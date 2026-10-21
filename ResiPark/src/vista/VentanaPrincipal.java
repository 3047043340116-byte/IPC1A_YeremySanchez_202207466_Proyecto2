package vista;

import estructuras.*;
import modelo.*;
import hilos.*;
import persistencia.GestorArchivos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class VentanaPrincipal extends JFrame {
    private ListaDobleResidentes listaResidentes = new ListaDobleResidentes();
    private ListaCircularParqueo parqueoSocios = new ListaCircularParqueo();
    private ListaCircularParqueo parqueoGeneral = new ListaCircularParqueo();
    private ColaFIFO colaEntrada = new ColaFIFO();
    private ColaFIFO colaSalida = new ColaFIFO();
    private PilaEventos pilaEventos = new PilaEventos();

    private JPanel panelCuadrículaSocios = new JPanel(new GridLayout(3, 25, 2, 2));
    private JPanel panelCuadrículaGeneral = new JPanel(new GridLayout(5, 15, 2, 2));
    private JTextArea areaEventos = new JTextArea();

    public VentanaPrincipal() {
        setTitle("ResiPark - Control de Accesos y Estacionamiento");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarParqueo();
        GestorArchivos.cargarDatos(listaResidentes);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Parqueo", crearPanelParqueo());
        tabbedPane.addTab("Residentes y Vehículos", crearPanelResidentes());
        tabbedPane.addTab("Entrada", crearPanelEntrada());
        tabbedPane.addTab("Salida", crearPanelSalida());
        tabbedPane.addTab("Eventos", crearPanelEventos());

        add(tabbedPane);

        // Inicialización de Hilos
        GaritaEntrada g1 = new GaritaEntrada("Garita 1", colaEntrada, parqueoSocios, parqueoGeneral, listaResidentes, pilaEventos, this::refrescarGUI);
        GaritaEntrada g2 = new GaritaEntrada("Garita 2", colaEntrada, parqueoSocios, parqueoGeneral, listaResidentes, pilaEventos, this::refrescarGUI);
        GaritaSalida gs = new GaritaSalida(colaSalida, parqueoSocios, parqueoGeneral, pilaEventos, this::refrescarGUI);

        g1.start();
        g2.start();
        gs.start();

        // Persistencia al cerrar
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                GestorArchivos.guardarDatos(listaResidentes);
            }
        });
    }

    private void inicializarParqueo() {
        String[] filasSocios = {"A", "B", "C"};
        for (String f : filasSocios) {
            for (int i = 1; i <= 25; i++) {
                parqueoSocios.agregarEspacio(new EspacioParqueo(f + i, "SOCIO"));
            }
        }
        String[] filasGen = {"E", "F", "G", "H", "I"};
        for (String f : filasGen) {
            for (int i = 1; i <= 15; i++) {
                parqueoGeneral.agregarEspacio(new EspacioParqueo(f + i, "GENERAL"));
            }
        }
    }

    private JPanel crearPanelParqueo() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel panelSociosContainer = new JPanel(new BorderLayout());
        panelSociosContainer.setBorder(BorderFactory.createTitledBorder("Área Socios (A1-C25)"));
        panelSociosContainer.add(panelCuadrículaSocios, BorderLayout.CENTER);

        JPanel panelGenContainer = new JPanel(new BorderLayout());
        panelGenContainer.setBorder(BorderFactory.createTitledBorder("Área General (E1-I15)"));
        panelGenContainer.add(panelCuadrículaGeneral, BorderLayout.CENTER);

        panel.add(panelSociosContainer, BorderLayout.NORTH);
        panel.add(panelGenContainer, BorderLayout.CENTER);
        refrescarParqueo();
        return panel;
    }

    private JPanel crearPanelResidentes() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        
        // Formulario Residente
        JPanel pR = new JPanel(new FlowLayout());
        JTextField txtId = new JTextField(5);
        JTextField txtNombre = new JTextField(10);
        JTextField txtCasa = new JTextField(5);
        JCheckBox chkSocio = new JCheckBox("Socio");
        JButton btnAddR = new JButton("Registrar Residente");

        pR.add(new JLabel("ID:")); pR.add(txtId);
        pR.add(new JLabel("Nombre:")); pR.add(txtNombre);
        pR.add(new JLabel("Casa/Lote:")); pR.add(txtCasa);
        pR.add(chkSocio); pR.add(btnAddR);

        btnAddR.addActionListener(e -> {
            if (txtId.getText().contains("|") || txtNombre.getText().contains("|")) {
                JOptionPane.showMessageDialog(this, "No se permite el carácter '|'");
                return;
            }
            Residente r = new Residente(txtId.getText(), txtNombre.getText(), txtCasa.getText(), chkSocio.isSelected());
            if (listaResidentes.insertar(r)) {
                pilaEventos.apilar(new Evento("REGISTRO", "Residente registrado: " + r.getNombre(), null));
                JOptionPane.showMessageDialog(this, "Residente registrado exitosamente.");
            } else {
                JOptionPane.showMessageDialog(this, "ID Duplicado.");
            }
        });

        // Formulario Vehículo
        JPanel pV = new JPanel(new FlowLayout());
        JTextField txtPlaca = new JTextField(6);
        JTextField txtMarca = new JTextField(8);
        JTextField txtModelo = new JTextField(8);
        JTextField txtColor = new JTextField(6);
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"automovil", "motocicleta", "pickup"});
        JTextField txtIdRes = new JTextField(5);
        JButton btnAddV = new JButton("Registrar Vehículo");

        pV.add(new JLabel("Placa:")); pV.add(txtPlaca);
        pV.add(new JLabel("Marca:")); pV.add(txtMarca);
        pV.add(new JLabel("Modelo:")); pV.add(txtModelo);
        pV.add(new JLabel("Color:")); pV.add(txtColor);
        pV.add(cbTipo);
        pV.add(new JLabel("ID Residente:")); pV.add(txtIdRes);
        pV.add(btnAddV);

        btnAddV.addActionListener(e -> {
            Residente r = listaResidentes.buscarPorId(txtIdRes.getText());
            if (r != null) {
                Vehiculo v = new Vehiculo(txtPlaca.getText(), txtMarca.getText(), txtModelo.getText(), txtColor.getText(), cbTipo.getSelectedItem().toString(), r.getId());
                if (r.getVehiculos().insertar(v)) {
                    pilaEventos.apilar(new Evento("REGISTRO", "Vehículo registrado: " + v.getPlaca(), null));
                    JOptionPane.showMessageDialog(this, "Vehículo registrado exitosamente.");
                } else {
                    JOptionPane.showMessageDialog(this, "Límite de 3 vehículos alcanzado o error.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Residente no encontrado.");
            }
        });

        panel.add(pR);
        panel.add(pV);
        return panel;
    }

    private JPanel crearPanelEntrada() {
        JPanel panel = new JPanel(new FlowLayout());
        JTextField txtPlaca = new JTextField(8);
        JButton btnEntrar = new JButton("Enviar a Cola Entrada");

        panel.add(new JLabel("Placa del Vehículo:"));
        panel.add(txtPlaca);
        panel.add(btnEntrar);

        btnEntrar.addActionListener(e -> {
            Vehiculo v = listaResidentes.buscarVehiculoGlobal(txtPlaca.getText());
            if (v != null && v.getEstado() == Vehiculo.Estado.FUERA) {
                v.setEstado(Vehiculo.Estado.EN_COLA_ENTRADA);
                colaEntrada.encolar(v);
                pilaEventos.apilar(new Evento("COLA_ENTRADA", "Vehículo enviado a cola: " + v.getPlaca(), null));
                JOptionPane.showMessageDialog(this, "Vehículo puesto en cola de entrada.");
            } else {
                JOptionPane.showMessageDialog(this, "Vehículo no disponible para ingresar.");
            }
        });

        return panel;
    }

    private JPanel crearPanelSalida() {
        JPanel panel = new JPanel(new FlowLayout());
        JTextField txtPlaca = new JTextField(8);
        JButton btnSalir = new JButton("Solicitar Salida");

        panel.add(new JLabel("Placa del Vehículo:"));
        panel.add(txtPlaca);
        panel.add(btnSalir);

        btnSalir.addActionListener(e -> {
            Vehiculo v = listaResidentes.buscarVehiculoGlobal(txtPlaca.getText());
            if (v != null && v.getEstado() == Vehiculo.Estado.ESTACIONADO) {
                v.setEstado(Vehiculo.Estado.EN_COLA_SALIDA);
                colaSalida.encolar(v);
                pilaEventos.apilar(new Evento("COLA_SALIDA", "Vehículo solicitó salida: " + v.getPlaca(), null));
                JOptionPane.showMessageDialog(this, "Vehículo puesto en cola de salida.");
            } else {
                JOptionPane.showMessageDialog(this, "El vehículo no está estacionado.");
            }
        });

        return panel;
    }

    private JPanel crearPanelEventos() {
        JPanel panel = new JPanel(new BorderLayout());
        areaEventos.setEditable(false);
        panel.add(new JScrollPane(areaEventos), BorderLayout.CENTER);
        return panel;
    }

    private void refrescarGUI() {
        refrescarParqueo();
        refrescarEventos();
    }

    private void refrescarParqueo() {
        panelCuadrículaSocios.removeAll();
        var nodoS = parqueoSocios.getInicio();
        if (nodoS != null) {
            for (int i = 0; i < parqueoSocios.getTamaño(); i++) {
                EspacioParqueo ep = nodoS.espacio;
                JLabel lbl = new JLabel(ep.getIdEspacio(), SwingConstants.CENTER);
                lbl.setOpaque(true);
                lbl.setBackground(ep.isOcupado() ? Color.RED : Color.GREEN);
                lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                panelCuadrículaSocios.add(lbl);
                nodoS = nodoS.siguiente;
            }
        }

        panelCuadrículaGeneral.removeAll();
        var nodoG = parqueoGeneral.getInicio();
        if (nodoG != null) {
            for (int i = 0; i < parqueoGeneral.getTamaño(); i++) {
                EspacioParqueo ep = nodoG.espacio;
                JLabel lbl = new JLabel(ep.getIdEspacio(), SwingConstants.CENTER);
                lbl.setOpaque(true);
                lbl.setBackground(ep.isOcupado() ? Color.RED : Color.GREEN);
                lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                panelCuadrículaGeneral.add(lbl);
                nodoG = nodoG.siguiente;
            }
        }

        panelCuadrículaSocios.revalidate();
        panelCuadrículaGeneral.revalidate();
        panelCuadrículaSocios.repaint();
        panelCuadrículaGeneral.repaint();
    }

    private void refrescarEventos() {
        StringBuilder sb = new StringBuilder();
        var nodoE = pilaEventos.getTope();
        while (nodoE != null) {
            sb.append(nodoE.evento.toString()).append("\n");
            nodoE = nodoE.siguiente;
        }
        areaEventos.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}