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

    // Estructura para mantener el control dinámico de vehículos en el parqueo / cola
    private ListaSimpleVehiculos visitantesEnProceso = new ListaSimpleVehiculos();

    private JPanel panelCuadrículaSocios = new JPanel(new GridLayout(3, 25, 2, 2));
    private JPanel panelCuadrículaGeneral = new JPanel(new GridLayout(5, 15, 2, 2));
    private JTextArea areaEventos = new JTextArea();

    private ListaDobleResidentes.Nodo nodoResidenteActual = null;

    public VentanaPrincipal() {
        setTitle("ResiPark - Control de Accesos y Estacionamiento");
        setSize(1080, 750);
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

        // Hilos de Garitas
        GaritaEntrada g1 = new GaritaEntrada("Garita 1", colaEntrada, parqueoSocios, parqueoGeneral, listaResidentes, pilaEventos, this::refrescarGUI);
        GaritaEntrada g2 = new GaritaEntrada("Garita 2", colaEntrada, parqueoSocios, parqueoGeneral, listaResidentes, pilaEventos, this::refrescarGUI);
        GaritaSalida gs = new GaritaSalida(colaSalida, parqueoSocios, parqueoGeneral, pilaEventos, this::refrescarGUI);

        g1.start();
        g2.start();
        gs.start();

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
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));

        // 1. RESIDENTES
        JPanel pR = new JPanel(new FlowLayout());
        pR.setBorder(BorderFactory.createTitledBorder("Gestión de Residentes"));

        JTextField txtId = new JTextField(5);
        JTextField txtNombre = new JTextField(10);
        JTextField txtCasa = new JTextField(5);
        JCheckBox chkSocio = new JCheckBox("Socio");

        JButton btnAddR = new JButton("Registrar");
        JButton btnBuscarR = new JButton("Buscar ID");
        JButton btnUpdateR = new JButton("Actualizar");
        JButton btnEliminarR = new JButton("Eliminar");

        pR.add(new JLabel("ID:")); pR.add(txtId);
        pR.add(new JLabel("Nombre:")); pR.add(txtNombre);
        pR.add(new JLabel("Casa/Lote:")); pR.add(txtCasa);
        pR.add(chkSocio);
        pR.add(btnAddR); pR.add(btnBuscarR); pR.add(btnUpdateR); pR.add(btnEliminarR);

        btnAddR.addActionListener(e -> {
            if (txtId.getText().contains("|") || txtNombre.getText().contains("|") || txtCasa.getText().contains("|")) {
                JOptionPane.showMessageDialog(this, "No se permite el carácter '|'");
                return;
            }
            String id = txtId.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar un ID válido.");
                return;
            }
            Residente r = new Residente(id, txtNombre.getText().trim(), txtCasa.getText().trim(), chkSocio.isSelected());
            if (listaResidentes.insertar(r)) {
                pilaEventos.apilar(new Evento("REGISTRO", "Residente registrado: " + r.getNombre(), null));
                JOptionPane.showMessageDialog(this, "Residente registrado exitosamente.");
            } else {
                JOptionPane.showMessageDialog(this, "ID Duplicado.");
            }
        });

        btnBuscarR.addActionListener(e -> {
            Residente r = listaResidentes.buscarPorId(txtId.getText().trim());
            if (r != null) {
                txtNombre.setText(r.getNombre());
                txtCasa.setText(r.getCasaLote());
                chkSocio.setSelected(r.esSocio());
                JOptionPane.showMessageDialog(this, "Residente encontrado.");
            } else {
                JOptionPane.showMessageDialog(this, "Residente no encontrado.");
            }
        });

        btnUpdateR.addActionListener(e -> {
            boolean exito = listaResidentes.actualizar(txtId.getText().trim(), txtNombre.getText().trim(), txtCasa.getText().trim(), chkSocio.isSelected());
            if (exito) {
                pilaEventos.apilar(new Evento("ACTUALIZACION", "Residente " + txtId.getText() + " actualizado", null));
                JOptionPane.showMessageDialog(this, "Residente actualizado correctamente.");
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar (No existe o tiene vehículos activos).");
            }
        });

        btnEliminarR.addActionListener(e -> {
            boolean exito = listaResidentes.eliminar(txtId.getText().trim());
            if (exito) {
                pilaEventos.apilar(new Evento("ELIMINACION", "Residente " + txtId.getText() + " eliminado", null));
                JOptionPane.showMessageDialog(this, "Residente eliminado correctamente.");
                txtId.setText(""); txtNombre.setText(""); txtCasa.setText(""); chkSocio.setSelected(false);
            } else {
                JOptionPane.showMessageDialog(this, "No se puede eliminar: El residente no existe o tiene vehículos activos.");
            }
        });

        // 2. RECORRIDO LISTA DOBLE
        JPanel pNav = new JPanel(new FlowLayout());
        pNav.setBorder(BorderFactory.createTitledBorder("Recorrido Lista Doble (Anterior / Siguiente)"));

        JButton btnInicio = new JButton("<< Inicio");
        JButton btnAnterior = new JButton("< Anterior");
        JLabel lblInfoNav = new JLabel("Seleccione Inicio para navegar");
        JButton btnSiguiente = new JButton("Siguiente >");
        JButton btnFin = new JButton("Fin >>");

        pNav.add(btnInicio); pNav.add(btnAnterior); pNav.add(lblInfoNav); pNav.add(btnSiguiente); pNav.add(btnFin);

        Runnable actualizarVistaNav = () -> {
            if (nodoResidenteActual != null) {
                Residente r = nodoResidenteActual.residente;
                lblInfoNav.setText("ID: " + r.getId() + " | " + r.getNombre() + " | Casa: " + r.getCasaLote() + " | " + (r.esSocio() ? "Socio" : "General"));
                txtId.setText(r.getId());
                txtNombre.setText(r.getNombre());
                txtCasa.setText(r.getCasaLote());
                chkSocio.setSelected(r.esSocio());
            } else {
                lblInfoNav.setText("Lista vacía");
            }
        };

        btnInicio.addActionListener(e -> { nodoResidenteActual = listaResidentes.getCabeza(); actualizarVistaNav.run(); });
        btnFin.addActionListener(e -> { nodoResidenteActual = listaResidentes.getCola(); actualizarVistaNav.run(); });
        btnAnterior.addActionListener(e -> {
            if (nodoResidenteActual != null && nodoResidenteActual.anterior != null) {
                nodoResidenteActual = nodoResidenteActual.anterior;
                actualizarVistaNav.run();
            }
        });
        btnSiguiente.addActionListener(e -> {
            if (nodoResidenteActual != null && nodoResidenteActual.siguiente != null) {
                nodoResidenteActual = nodoResidenteActual.siguiente;
                actualizarVistaNav.run();
            }
        });

        // 3. VEHÍCULOS DE RESIDENTES
        JPanel pV = new JPanel(new FlowLayout());
        pV.setBorder(BorderFactory.createTitledBorder("Gestión de Vehículos por Residente (Lista Simple)"));

        JTextField txtPlaca = new JTextField(6);
        JTextField txtMarca = new JTextField(6);
        JTextField txtModelo = new JTextField(6);
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
            String placa = txtPlaca.getText().trim();
            String idRes = txtIdRes.getText().trim();

            if (placa.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar una placa válida.");
                return;
            }

            // Validar que la placa sea única globalmente en residentes y visitantes
            if (listaResidentes.existePlacaGlobal(placa) || visitantesEnProceso.buscarPorPlaca(placa) != null) {
                JOptionPane.showMessageDialog(this, "Error: La placa " + placa + " ya existe en el sistema.", "Placa Duplicada", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Residente r = listaResidentes.buscarPorId(idRes);
            if (r != null) {
                Vehiculo v = new Vehiculo(placa, txtMarca.getText().trim(), txtModelo.getText().trim(), txtColor.getText().trim(), cbTipo.getSelectedItem().toString(), r.getId());
                if (r.getVehiculos().insertar(v)) {
                    pilaEventos.apilar(new Evento("REGISTRO", "Vehículo registrado: " + v.getPlaca(), null));
                    JOptionPane.showMessageDialog(this, "Vehículo registrado exitosamente.");
                    txtPlaca.setText(""); txtMarca.setText(""); txtModelo.setText(""); txtColor.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Error: Límite de 3 vehículos alcanzado para este residente.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Residente no encontrado.");
            }
        });

        panel.add(pR);
        panel.add(pNav);
        panel.add(pV);
        return panel;
    }

    private JPanel crearPanelEntrada() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));

        // Entrada de Vehículo Registrado (Residente)
        JPanel pRes = new JPanel(new FlowLayout());
        pRes.setBorder(BorderFactory.createTitledBorder("Ingreso de Vehículo Registrado (Residente)"));
        JTextField txtPlacaRes = new JTextField(8);
        JButton btnEntrarRes = new JButton("Enviar a Cola Entrada");

        pRes.add(new JLabel("Placa:")); pRes.add(txtPlacaRes); pRes.add(btnEntrarRes);

        btnEntrarRes.addActionListener(e -> {
            String placa = txtPlacaRes.getText().trim();
            Vehiculo v = listaResidentes.buscarVehiculoGlobal(placa);
            if (v != null && v.getEstado() == Vehiculo.Estado.FUERA) {
                v.setEstado(Vehiculo.Estado.EN_COLA_ENTRADA);
                colaEntrada.encolar(v);
                pilaEventos.apilar(new Evento("COLA_ENTRADA", "Vehículo enviado a cola: " + v.getPlaca(), null));
                JOptionPane.showMessageDialog(this, "Vehículo de residente puesto en cola.");
                txtPlacaRes.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Vehículo no disponible para ingresar o ya está dentro/en cola.");
            }
        });

        // Entrada de Vehículo Visitante
        JPanel pVis = new JPanel(new FlowLayout());
        pVis.setBorder(BorderFactory.createTitledBorder("Ingreso de Vehículo Visitante"));
        JTextField txtPlacaVis = new JTextField(6);
        JTextField txtMarcaVis = new JTextField(6);
        JTextField txtModeloVis = new JTextField(6);
        JTextField txtColorVis = new JTextField(6);
        JComboBox<String> cbTipoVis = new JComboBox<>(new String[]{"automovil", "motocicleta", "pickup"});
        JButton btnEntrarVis = new JButton("Ingresar Visitante");

        pVis.add(new JLabel("Placa:")); pVis.add(txtPlacaVis);
        pVis.add(new JLabel("Marca:")); pVis.add(txtMarcaVis);
        pVis.add(new JLabel("Modelo:")); pVis.add(txtModeloVis);
        pVis.add(new JLabel("Color:")); pVis.add(txtColorVis);
        pVis.add(cbTipoVis);
        pVis.add(btnEntrarVis);

        btnEntrarVis.addActionListener(e -> {
            String placa = txtPlacaVis.getText().trim();
            if (placa.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese una placa válida.");
                return;
            }

            // Validar unicidad de placa
            if (listaResidentes.existePlacaGlobal(placa) || visitantesEnProceso.buscarPorPlaca(placa) != null) {
                JOptionPane.showMessageDialog(this, "Error: La placa " + placa + " ya está registrada o dentro del residencial.", "Placa Duplicada", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Vehiculo vVis = new Vehiculo(placa, txtMarcaVis.getText().trim(), txtModeloVis.getText().trim(), txtColorVis.getText().trim(), cbTipoVis.getSelectedItem().toString(), "VISITANTE");
            vVis.setEstado(Vehiculo.Estado.EN_COLA_ENTRADA);
            visitantesEnProceso.insertar(vVis);
            colaEntrada.encolar(vVis);

            pilaEventos.apilar(new Evento("COLA_ENTRADA", "Visitante enviado a cola: " + vVis.getPlaca(), null));
            JOptionPane.showMessageDialog(this, "Vehículo visitante enviado a cola de entrada.");
            txtPlacaVis.setText(""); txtMarcaVis.setText(""); txtModeloVis.setText(""); txtColorVis.setText("");
        });

        panel.add(pRes);
        panel.add(pVis);
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
            String placa = txtPlaca.getText().trim();
            Vehiculo v = listaResidentes.buscarVehiculoGlobal(placa);
            if (v == null) {
                v = visitantesEnProceso.buscarPorPlaca(placa);
            }

            if (v != null && v.getEstado() == Vehiculo.Estado.ESTACIONADO) {
                v.setEstado(Vehiculo.Estado.EN_COLA_SALIDA);
                colaSalida.encolar(v);
                pilaEventos.apilar(new Evento("COLA_SALIDA", "Vehículo solicitó salida: " + v.getPlaca(), null));
                JOptionPane.showMessageDialog(this, "Vehículo puesto en cola de salida.");
                txtPlaca.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "El vehículo no se encuentra estacionado.");
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