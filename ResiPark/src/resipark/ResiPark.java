package resipark;

import static resipark.ModeloResiPark.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ResiPark - Proyecto IPC1, Segundo Semestre 2026.
 * Estructuras de datos implementadas manualmente con nodos.
 */
public class ResiPark extends JFrame {
    private final Sistema sistema = new Sistema();
    private final JPanel parkingGrid = new JPanel();
    private final JTextArea residentesVista = new JTextArea();
    private final JTextArea entradaVista = new JTextArea();
    private final JTextArea salidaVista = new JTextArea();
    private final JTextArea eventosVista = new JTextArea();
    private final JTextArea garitasVista = new JTextArea();
    private final JLabel estadoLabel = new JLabel("Iniciando...");
    private final JTextField idField = new JTextField(8), nombreField = new JTextField(14),
            casaField = new JTextField(8), placaField = new JTextField(9),
            marcaField = new JTextField(10), modeloField = new JTextField(10), colorField = new JTextField(10);
    private final JCheckBox socioBox = new JCheckBox("Residente socio");
    private final JComboBox<String> tipoVehiculo = new JComboBox<>(new String[]{"Automóvil", "Motocicleta", "Pickup"});
    private final JTextField visitanteNombre = new JTextField(12), visitantePlaca = new JTextField(9), visitanteResidente = new JTextField(8);
    private final JTextField entradaPlaca = new JTextField(10), salidaPlaca = new JTextField(10);
    private final DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public ResiPark() {
        super("ResiPark | Control de accesos y estacionamiento");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setSize(1180, 780);
        setMinimumSize(new Dimension(1000, 680));
        setLocationRelativeTo(null);
        sistema.cargar();
        construirInterfaz();
        sistema.setActualizador(() -> SwingUtilities.invokeLater(this::actualizarTodo));
        sistema.iniciarGaritas();
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                sistema.guardar();
                sistema.detener();
                dispose();
                System.exit(0);
            }
        });
        actualizarTodo();
    }

    private void construirInterfaz() {
        JPanel base = new JPanel(new BorderLayout(10, 10));
        base.setBorder(new EmptyBorder(12, 12, 12, 12));
        base.setBackground(new Color(242, 246, 250));
        JLabel titulo = new JLabel("ResiPark  |  Administración residencial");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setForeground(new Color(28, 54, 82));
        base.add(titulo, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Parqueo", crearPanelParqueo());
        tabs.addTab("Residentes y vehículos", crearPanelResidentes());
        tabs.addTab("Entrada", crearPanelEntrada());
        tabs.addTab("Salida", crearPanelSalida());
        tabs.addTab("Eventos", crearPanelEventos());
        tabs.addTab("Garitas / estado", crearPanelGaritas());
        base.add(tabs, BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        estadoLabel.setBorder(new EmptyBorder(5, 3, 0, 3));
        pie.add(estadoLabel, BorderLayout.CENTER);
        JButton actualizar = new JButton("Actualizar vista");
        actualizar.addActionListener(e -> actualizarTodo());
        pie.add(actualizar, BorderLayout.EAST);
        base.add(pie, BorderLayout.SOUTH);
        setContentPane(base);
    }

    private JPanel crearPanelParqueo() {
        JPanel panel = new JPanel(new BorderLayout(8,8));
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leyenda.add(new JLabel("Socios: A1-A25, B1-B25, C1-C25"));
        leyenda.add(new JLabel("     General/visitantes: E1-I15"));
        leyenda.add(new JLabel("     Verde = libre | Rojo = ocupado"));
        panel.add(leyenda, BorderLayout.NORTH);
        parkingGrid.setLayout(new BoxLayout(parkingGrid, BoxLayout.Y_AXIS));
        parkingGrid.setBorder(new EmptyBorder(8,8,8,8));
        panel.add(new JScrollPane(parkingGrid), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelResidentes() {
        JPanel panel = new JPanel(new BorderLayout(8,8));
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createTitledBorder("Registro y gestión"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4,4,4,4); c.anchor = GridBagConstraints.WEST;
        int row=0;
        addCampo(formulario,c,row++,"ID residente:",idField);
        addCampo(formulario,c,row++,"Nombre:",nombreField);
        addCampo(formulario,c,row++,"Casa / lote:",casaField);
        c.gridx=0; c.gridy=row; c.gridwidth=2; formulario.add(socioBox,c); row++; c.gridwidth=1;
        addCampo(formulario,c,row++,"Placa:",placaField);
        addCampo(formulario,c,row++,"Marca:",marcaField);
        addCampo(formulario,c,row++,"Modelo:",modeloField);
        addCampo(formulario,c,row++,"Color:",colorField);
        c.gridx=0; c.gridy=row; formulario.add(new JLabel("Tipo:"),c);
        c.gridx=1; formulario.add(tipoVehiculo,c); row++;
        JPanel botones = new JPanel(new GridLayout(0,2,5,5));
        JButton registrar = new JButton("Registrar residente");
        registrar.addActionListener(e -> registrarResidente());
        JButton buscar = new JButton("Buscar ID");
        buscar.addActionListener(e -> buscarResidente());
        JButton modificar = new JButton("Modificar datos");
        modificar.addActionListener(e -> modificarResidente());
        JButton eliminar = new JButton("Eliminar residente");
        eliminar.addActionListener(e -> eliminarResidente());
        JButton agregarVeh = new JButton("Agregar vehículo");
        agregarVeh.addActionListener(e -> agregarVehiculo());
        JButton eliminarVeh = new JButton("Eliminar vehículo");
        eliminarVeh.addActionListener(e -> eliminarVehiculo());
        JButton listarInverso = new JButton("Listar residentes al revés");
        listarInverso.addActionListener(e -> residentesVista.setText(sistema.listadoResidentesInverso()));
        botones.add(registrar); botones.add(buscar); botones.add(modificar); botones.add(eliminar); botones.add(agregarVeh); botones.add(eliminarVeh); botones.add(listarInverso);
        c.gridx=0;c.gridy=row;c.gridwidth=2; formulario.add(botones,c);
        panel.add(formulario,BorderLayout.WEST);
        residentesVista.setEditable(false); residentesVista.setFont(new Font(Font.MONOSPACED,Font.PLAIN,13));
        panel.add(new JScrollPane(residentesVista),BorderLayout.CENTER);
        return panel;
    }

    private void addCampo(JPanel p, GridBagConstraints c, int row, String label, JComponent field) {
        c.gridx=0;c.gridy=row;c.gridwidth=1;p.add(new JLabel(label),c);
        c.gridx=1;p.add(field,c);
    }

    private JPanel crearPanelEntrada() {
        JPanel panel = new JPanel(new BorderLayout(8,8));
        JPanel top = new JPanel(new GridBagLayout());
        top.setBorder(BorderFactory.createTitledBorder("Ingreso de vehículo registrado"));
        GridBagConstraints c = new GridBagConstraints(); c.insets=new Insets(5,5,5,5);
        c.gridx=0;c.gridy=0;top.add(new JLabel("Placa:"),c); c.gridx=1;top.add(entradaPlaca,c);
        JButton enviar = new JButton("Enviar a cola de entrada");
        enviar.addActionListener(e -> enviarEntrada());
        c.gridx=2;top.add(enviar,c);
        JPanel vis = new JPanel(new GridBagLayout());
        vis.setBorder(BorderFactory.createTitledBorder("Registrar visitante temporal"));
        c.gridx=0;c.gridy=0;vis.add(new JLabel("Nombre:"),c);c.gridx=1;vis.add(visitanteNombre,c);
        c.gridx=2;vis.add(new JLabel("Placa:"),c);c.gridx=3;vis.add(visitantePlaca,c);
        c.gridx=0;c.gridy=1;vis.add(new JLabel("ID residente visitado:"),c);c.gridx=1;vis.add(visitanteResidente,c);
        JButton registrarVisitante=new JButton("Registrar y enviar");
        registrarVisitante.addActionListener(e -> registrarVisitante());
        c.gridx=2;c.gridy=1;c.gridwidth=2;vis.add(registrarVisitante,c);
        JPanel norte = new JPanel(new BorderLayout(5,5)); norte.add(top,BorderLayout.NORTH);norte.add(vis,BorderLayout.CENTER);
        panel.add(norte,BorderLayout.NORTH);
        entradaVista.setEditable(false); entradaVista.setFont(new Font(Font.MONOSPACED,Font.PLAIN,14));
        panel.add(new JScrollPane(entradaVista),BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelSalida() {
        JPanel panel = new JPanel(new BorderLayout(8,8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(BorderFactory.createTitledBorder("Solicitar salida"));
        top.add(new JLabel("Placa estacionada:")); top.add(salidaPlaca);
        JButton salir=new JButton("Solicitar salida");
        salir.addActionListener(e -> solicitarSalida());
        JButton sugerir=new JButton("Ver estacionados");
        sugerir.addActionListener(e -> salidaPlaca.setText(sistema.placasEstacionadas()));
        top.add(salir);top.add(sugerir);
        panel.add(top,BorderLayout.NORTH);
        salidaVista.setEditable(false);salidaVista.setFont(new Font(Font.MONOSPACED,Font.PLAIN,14));
        panel.add(new JScrollPane(salidaVista),BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelEventos() {
        JPanel panel=new JPanel(new BorderLayout());
        JButton limpiarVista=new JButton("Actualizar historial");
        limpiarVista.addActionListener(e -> actualizarEventos());
        panel.add(limpiarVista,BorderLayout.NORTH);
        eventosVista.setEditable(false);eventosVista.setFont(new Font(Font.MONOSPACED,Font.PLAIN,13));
        panel.add(new JScrollPane(eventosVista),BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelGaritas() {
        JPanel panel=new JPanel(new BorderLayout());
        JLabel nota=new JLabel("Dos hilos de entrada comparten una cola FIFO; un hilo atiende la cola FIFO de salida.");
        nota.setBorder(new EmptyBorder(8,8,8,8));panel.add(nota,BorderLayout.NORTH);
        garitasVista.setEditable(false);garitasVista.setFont(new Font(Font.MONOSPACED,Font.PLAIN,14));
        panel.add(new JScrollPane(garitasVista),BorderLayout.CENTER);
        return panel;
    }

    private void registrarResidente() {
        String id=idField.getText().trim(), nombre=nombreField.getText().trim(), casa=casaField.getText().trim();
        if(!camposValidos(id,nombre,casa)) return;
        if(sistema.residentes.buscar(id)!=null){mensaje("Ya existe un residente con ese ID.");return;}
        Residente r=new Residente(id,nombre,casa,socioBox.isSelected());
        if(sistema.residentes.insertar(r)){sistema.evento("Residente registrado", "ID "+id+" - "+nombre, "-"); sistema.guardar(); limpiarCamposResidente(); actualizarTodo();}
    }
    private boolean camposValidos(String... valores) {
        for(String v:valores) {
            if(v==null||v.trim().isEmpty()||v.contains("|")) {mensaje("Complete los campos obligatorios. No se permite el carácter |.");return false;}
        }
        return true;
    }
    private void buscarResidente() {
        Residente r=sistema.residentes.buscar(idField.getText().trim());
        if(r==null){mensaje("No se encontró el residente.");return;}
        nombreField.setText(r.nombre);casaField.setText(r.casa);socioBox.setSelected(r.socio);
        residentesVista.setText(sistema.detalleResidente(r));
    }
    private void modificarResidente() {
        Residente r=sistema.residentes.buscar(idField.getText().trim());
        if(r==null){mensaje("Busque un residente existente por ID.");return;}
        if(!camposValidos(nombreField.getText().trim(),casaField.getText().trim()))return;
        if(r.socio!=socioBox.isSelected() && r.tieneVehiculosActivos()){mensaje("No se puede cambiar la condición de socio si alguno de sus vehículos no está FUERA.");return;}
        r.nombre=nombreField.getText().trim();r.casa=casaField.getText().trim();r.socio=socioBox.isSelected();
        sistema.evento("Residente modificado","ID "+r.id,"-");sistema.guardar();actualizarTodo();
    }
    private void eliminarResidente() {
        Residente r=sistema.residentes.buscar(idField.getText().trim());
        if(r==null){mensaje("No se encontró el residente.");return;}
        if(r.tieneVehiculosActivos()){mensaje("No se puede eliminar: tiene vehículos dentro o en cola.");return;}
        sistema.residentes.eliminar(r.id);sistema.evento("Residente eliminado","ID "+r.id,"-");sistema.guardar();actualizarTodo();
    }
    private void agregarVehiculo() {
        Residente r=sistema.residentes.buscar(idField.getText().trim());
        if(r==null){mensaje("Ingrese un ID de residente existente.");return;}
        String placa=placaField.getText().trim().toUpperCase(), marca=marcaField.getText().trim(), modelo=modeloField.getText().trim(), color=colorField.getText().trim();
        if(!camposValidos(placa,marca,modelo,color))return;
        if(sistema.placaExiste(placa)){mensaje("La placa ya existe en el sistema.");return;}
        if(r.vehiculos.cantidad()>=3){mensaje("Cada residente puede tener máximo 3 vehículos.");return;}
        Vehiculo v=new Vehiculo(placa,marca,modelo,color,(String)tipoVehiculo.getSelectedItem(),r,false);
        r.vehiculos.insertar(v);sistema.evento("Vehículo registrado","Placa "+placa+" para residente "+r.id,"-");sistema.guardar();actualizarTodo();
    }
    private void eliminarVehiculo() {
        String placa=placaField.getText().trim().toUpperCase();
        Vehiculo v=sistema.buscarVehiculo(placa);
        if(v==null||v.visitante){mensaje("No se encontró un vehículo registrado de residente.");return;}
        if(!"FUERA".equals(v.estado)){mensaje("Solo se puede eliminar un vehículo en estado FUERA.");return;}
        v.residente.vehiculos.eliminar(placa);sistema.evento("Vehículo eliminado","Placa "+placa,"-");sistema.guardar();actualizarTodo();
    }
    private void enviarEntrada() {
        Vehiculo v=sistema.buscarVehiculo(entradaPlaca.getText().trim().toUpperCase());
        if(v==null){mensaje("No existe esa placa registrada.");return;}
        if(sistema.encolarEntrada(v))actualizarTodo();
    }
    private void registrarVisitante() {
        String nombre=visitanteNombre.getText().trim(),placa=visitantePlaca.getText().trim().toUpperCase(),id=visitanteResidente.getText().trim();
        if(!camposValidos(nombre,placa,id))return;
        Residente r=sistema.residentes.buscar(id);
        if(r==null){mensaje("El ID del residente visitado no existe.");return;}
        if(sistema.placaExiste(placa)){mensaje("La placa ya existe en el sistema.");return;}
        Vehiculo v=new Vehiculo(placa,"Visitante","Temporal","No indicado","Automóvil",r,true);
        v.nombreVisitante=nombre;
        sistema.evento("Visitante registrado", "Visitante "+nombre+", placa "+placa+", visita al residente "+r.id, "-");
        if(sistema.encolarEntrada(v)){visitanteNombre.setText("");visitantePlaca.setText("");visitanteResidente.setText("");actualizarTodo();}
    }
    private void solicitarSalida() {
        Vehiculo v=sistema.buscarVehiculo(salidaPlaca.getText().trim().toUpperCase());
        if(v==null||!"ESTACIONADO".equals(v.estado)){mensaje("La placa debe corresponder a un vehículo actualmente estacionado.");return;}
        if(sistema.encolarSalida(v))actualizarTodo();
    }
    private void limpiarCamposResidente(){idField.setText("");nombreField.setText("");casaField.setText("");socioBox.setSelected(false);}
    private void mensaje(String s){JOptionPane.showMessageDialog(this,s,"ResiPark",JOptionPane.INFORMATION_MESSAGE);}

    private void actualizarTodo() {
        actualizarParqueo(); residentesVista.setText(sistema.listadoResidentes());
        entradaVista.setText(sistema.estadoEntrada());salidaVista.setText(sistema.estadoSalida());
        actualizarEventos();garitasVista.setText(sistema.estadoGaritas());
        estadoLabel.setText("Residentes: "+sistema.residentes.cantidad()+" | Espacios ocupados: "+sistema.parqueo.ocupados()+" / 150 | Cola entrada: "+sistema.entrada.cantidad()+" | Cola salida: "+sistema.salida.cantidad());
    }
    private void actualizarParqueo() {
        parkingGrid.removeAll();
        agregarFilaParqueo("A", 25, sistema.parqueo.socios);
        agregarFilaParqueo("B", 25, sistema.parqueo.socios);
        agregarFilaParqueo("C", 25, sistema.parqueo.socios);
        agregarFilaParqueo("E", 15, sistema.parqueo.general);
        agregarFilaParqueo("F", 15, sistema.parqueo.general);
        agregarFilaParqueo("G", 15, sistema.parqueo.general);
        agregarFilaParqueo("H", 15, sistema.parqueo.general);
        agregarFilaParqueo("I", 15, sistema.parqueo.general);
        parkingGrid.revalidate();parkingGrid.repaint();
    }
    private void agregarFilaParqueo(String letra, int cantidad, ListaCircular lista) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 3));
        fila.setOpaque(false);
        JLabel etiqueta = new JLabel("Fila " + letra + "  ");
        etiqueta.setPreferredSize(new Dimension(55, 34));
        fila.add(etiqueta);
        Espacio e = lista.primero;
        while (e != null && !e.id.startsWith(letra)) e = e.sigArea;
        if (e != null) {
            for (int i=0; i<cantidad; i++) { agregarBotonEspacio(fila, e); e=e.sigArea; }
        }
        parkingGrid.add(fila);
    }
    private void agregarBotonEspacio(JPanel fila, Espacio e) {
        JButton b=new JButton("<html><center>"+e.id+"<br>"+(e.ocupado?e.vehiculo.placa:"LIBRE")+"</center></html>");
        b.setPreferredSize(new Dimension(72, 38));
        b.setOpaque(true);b.setBorderPainted(false);b.setFont(new Font("SansSerif",Font.BOLD,9));
        b.setBackground(e.ocupado?new Color(220,92,92):new Color(117,204,145));
        b.setForeground(e.ocupado?Color.WHITE:new Color(25,55,35));
        b.setToolTipText(e.tipo+" - "+(e.ocupado?"Ocupado por "+e.vehiculo.placa:"Disponible"));
        fila.add(b);
    }
    private void actualizarEventos(){eventosVista.setText(sistema.eventos.comoTexto());}
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new ResiPark().setVisible(true)); }

}
