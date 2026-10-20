package resipark;

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
        botones.add(registrar); botones.add(buscar); botones.add(modificar); botones.add(eliminar); botones.add(agregarVeh); botones.add(eliminarVeh);
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
            Espacio inicio = e;
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

    // ---------------- MODELO Y ESTRUCTURAS PROPIAS ----------------
    static class NodoR { Residente dato; NodoR ant,sig; NodoR(Residente d){dato=d;} }
    static class ListaResidentes {
        NodoR cabeza,cola; int size;
        synchronized boolean insertar(Residente r){if(buscar(r.id)!=null)return false;NodoR n=new NodoR(r);if(cabeza==null)cabeza=cola=n;else{cola.sig=n;n.ant=cola;cola=n;}size++;return true;}
        synchronized Residente buscar(String id){for(NodoR n=cabeza;n!=null;n=n.sig)if(n.dato.id.equalsIgnoreCase(id))return n.dato;return null;}
        synchronized boolean eliminar(String id){for(NodoR n=cabeza;n!=null;n=n.sig)if(n.dato.id.equalsIgnoreCase(id)){if(n.ant!=null)n.ant.sig=n.sig;else cabeza=n.sig;if(n.sig!=null)n.sig.ant=n.ant;else cola=n.ant;size--;return true;}return false;}
        synchronized int cantidad(){return size;}
    }
    static class NodoV {Vehiculo dato;NodoV sig;NodoV(Vehiculo v){dato=v;}}
    static class ListaVehiculos {
        NodoV cabeza;int size;
        synchronized void insertar(Vehiculo v){NodoV n=new NodoV(v);if(cabeza==null)cabeza=n;else{NodoV a=cabeza;while(a.sig!=null)a=a.sig;a.sig=n;}size++;}
        synchronized Vehiculo buscar(String placa){for(NodoV n=cabeza;n!=null;n=n.sig)if(n.dato.placa.equalsIgnoreCase(placa))return n.dato;return null;}
        synchronized boolean eliminar(String placa){NodoV ant=null,n=cabeza;while(n!=null){if(n.dato.placa.equalsIgnoreCase(placa)){if(ant==null)cabeza=n.sig;else ant.sig=n.sig;size--;return true;}ant=n;n=n.sig;}return false;}
        synchronized int cantidad(){return size;}
    }
    static class Residente {
        final String id;String nombre,casa;boolean socio;final ListaVehiculos vehiculos=new ListaVehiculos();
        Residente(String i,String n,String c,boolean s){id=i;nombre=n;casa=c;socio=s;}
        boolean tieneVehiculosActivos(){for(NodoV n=vehiculos.cabeza;n!=null;n=n.sig)if(!"FUERA".equals(n.dato.estado))return true;return false;}
    }
    static class Vehiculo {
        final String placa,marca,modelo,color,tipo;final Residente residente;final boolean visitante;
        String estado="FUERA",nombreVisitante="";Espacio espacio;
        Vehiculo(String p,String ma,String mo,String co,String t,Residente r,boolean vis){placa=p;marca=ma;modelo=mo;color=co;tipo=t;residente=r;visitante=vis;}
        String propietario(){return visitante?"Visitante: "+nombreVisitante:residente.nombre;}
    }
    static class Espacio {
        final String id,tipo;boolean ocupado;Vehiculo vehiculo;
        Espacio sigArea,arriba,abajo,izquierda,derecha;
        Espacio(String i,String t){id=i;tipo=t;}
    }
    static class ListaCircular {
        Espacio primero,ultimo,ultimoAsignado;int size;
        synchronized void agregar(Espacio e){if(primero==null){primero=ultimo=e;e.sigArea=e;}else{e.sigArea=primero;ultimo.sigArea=e;ultimo=e;}size++;}
        synchronized Espacio buscarLibre(){if(primero==null)return null;Espacio inicio=ultimoAsignado==null?primero:ultimoAsignado.sigArea;Espacio e=inicio;do{if(!e.ocupado){ultimoAsignado=e;return e;}e=e.sigArea;}while(e!=inicio);return null;}
        synchronized int ocupados(){int n=0;if(primero!=null){Espacio e=primero;do{if(e.ocupado)n++;e=e.sigArea;}while(e!=primero);}return n;}
    }
    static class Parqueo {
        final ListaCircular socios=new ListaCircular(),general=new ListaCircular();
        Parqueo(){
            crearFila(socios,"A",25,"SOCIO");crearFila(socios,"B",25,"SOCIO");crearFila(socios,"C",25,"SOCIO");
            crearFila(general,"E",15,"GENERAL");crearFila(general,"F",15,"GENERAL");crearFila(general,"G",15,"GENERAL");crearFila(general,"H",15,"GENERAL");crearFila(general,"I",15,"GENERAL");
            // Enlaces ortogonales: cada fila es horizontal y las filas consecutivas se enlazan verticalmente.
            enlazarFilas(new String[]{"A","B","C"},25); enlazarFilas(new String[]{"E","F","G","H","I"},15);
        }
        private void crearFila(ListaCircular l,String fila,int total,String tipo){for(int i=1;i<=total;i++)l.agregar(new Espacio(fila+i,tipo));}
        private void enlazarFilas(String[] filas,int columnas){
            for(int f=0;f<filas.length;f++)for(int c=1;c<=columnas;c++){
                Espacio actual=buscar(filas[f]+c);
                if(c>1){Espacio izq=buscar(filas[f]+(c-1));actual.izquierda=izq;izq.derecha=actual;}
                if(f>0){Espacio arriba=buscar(filas[f-1]+c);actual.arriba=arriba;arriba.abajo=actual;}
            }
        }
        synchronized Espacio buscar(String id){Espacio e=buscarEn(socios,id);return e!=null?e:buscarEn(general,id);}
        private Espacio buscarEn(ListaCircular l,String id){if(l.primero==null)return null;Espacio e=l.primero;do{if(e.id.equalsIgnoreCase(id))return e;e=e.sigArea;}while(e!=l.primero);return null;}
        synchronized Espacio asignar(Vehiculo v){
            Espacio e=null;
            if(v.residente.socio){e=socios.buscarLibre();if(e==null)e=general.buscarLibre();}
            else e=general.buscarLibre();
            if(e!=null){e.ocupado=true;e.vehiculo=v;v.espacio=e;v.estado="ESTACIONADO";}
            return e;
        }
        synchronized void liberar(Vehiculo v){if(v.espacio!=null){v.espacio.ocupado=false;v.espacio.vehiculo=null;v.espacio=null;}v.estado="FUERA";}
        synchronized int ocupados(){return socios.ocupados()+general.ocupados();}
    }
    static class NodoCola {Vehiculo dato;NodoCola sig;NodoCola(Vehiculo v){dato=v;}}
    static class ColaVehiculos {
        NodoCola frente,fin;int size;
        synchronized void encolar(Vehiculo v){NodoCola n=new NodoCola(v);if(fin==null)frente=fin=n;else{fin.sig=n;fin=n;}size++;notifyAll();}
        synchronized Vehiculo desencolarEsperando() throws InterruptedException {while(frente==null)wait();NodoCola n=frente;frente=n.sig;if(frente==null)fin=null;size--;return n.dato;}
        synchronized boolean contiene(Vehiculo v){for(NodoCola n=frente;n!=null;n=n.sig)if(n.dato==v)return true;return false;}
        synchronized int cantidad(){return size;}
        synchronized String texto(){StringBuilder s=new StringBuilder();int i=1;for(NodoCola n=frente;n!=null;n=n.sig)s.append(i++).append(". ").append(n.dato.placa).append(" - ").append(n.dato.estado).append(" - ").append(n.dato.propietario()).append("\n");return s.length()==0?"(vacía)\n":s.toString();}
    }
    static class Evento {String fecha,tipo,descripcion,garita;Evento siguiente;Evento(String t,String d,String g){fecha=LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));tipo=t;descripcion=d;garita=g;}}
    static class PilaEventos {
        Evento tope;int size;
        synchronized void push(Evento e){e.siguiente=tope;tope=e;size++;}
        synchronized String comoTexto(){StringBuilder s=new StringBuilder();for(Evento e=tope;e!=null;e=e.siguiente)s.append("[").append(e.fecha).append("] ").append(e.tipo).append("\n  ").append(e.descripcion).append(" | Garita: ").append(e.garita).append("\n\n");return s.length()==0?"Sin eventos todavía.":s.toString();}
    }

    static class Sistema {
        final ListaResidentes residentes=new ListaResidentes();
        final Parqueo parqueo=new Parqueo();
        final ColaVehiculos entrada=new ColaVehiculos(),salida=new ColaVehiculos();
        final PilaEventos eventos=new PilaEventos();
        volatile boolean activo=true;volatile String actualG1="Esperando",actualG2="Esperando",actualGS="Esperando";
        volatile Vehiculo actualVehiculo1, actualVehiculo2, actualVehiculoSalida;
        Runnable actualizador=()->{};
        void setActualizador(Runnable r){actualizador=r;}
        void refrescar(){actualizador.run();}
        synchronized void evento(String t,String d,String g){eventos.push(new Evento(t,d,g));}
        Vehiculo buscarVehiculo(String placa){synchronized(residentes){for(NodoR n=residentes.cabeza;n!=null;n=n.sig){Vehiculo v=n.dato.vehiculos.buscar(placa);if(v!=null)return v;}}return buscarVisitanteActivo(placa);}
        private Vehiculo buscarVisitanteActivo(String placa){if(actualVehiculo1!=null&&actualVehiculo1.placa.equalsIgnoreCase(placa))return actualVehiculo1;if(actualVehiculo2!=null&&actualVehiculo2.placa.equalsIgnoreCase(placa))return actualVehiculo2;if(actualVehiculoSalida!=null&&actualVehiculoSalida.placa.equalsIgnoreCase(placa))return actualVehiculoSalida;synchronized(entrada){for(NodoCola n=entrada.frente;n!=null;n=n.sig)if(n.dato.visitante&&n.dato.placa.equalsIgnoreCase(placa))return n.dato;}synchronized(salida){for(NodoCola n=salida.frente;n!=null;n=n.sig)if(n.dato.visitante&&n.dato.placa.equalsIgnoreCase(placa))return n.dato;}synchronized(parqueo){Espacio e=parqueo.socios.primero;for(int i=0;i<75;i++,e=e.sigArea)if(e.ocupado&&e.vehiculo.visitante&&e.vehiculo.placa.equalsIgnoreCase(placa))return e.vehiculo;e=parqueo.general.primero;for(int i=0;i<75;i++,e=e.sigArea)if(e.ocupado&&e.vehiculo.visitante&&e.vehiculo.placa.equalsIgnoreCase(placa))return e.vehiculo;}return null;}
        boolean placaExiste(String p){return buscarVehiculo(p)!=null;}
        boolean encolarEntrada(Vehiculo v){
            synchronized(v){if(!"FUERA".equals(v.estado)){mensajeEstatico("El vehículo debe estar en estado FUERA.");return false;}
                v.estado="EN_COLA_ENTRADA";entrada.encolar(v);}
            evento("Vehículo en cola de entrada","Placa "+v.placa,"-");refrescar();return true;
        }
        boolean encolarSalida(Vehiculo v){
            synchronized(v){if(!"ESTACIONADO".equals(v.estado)){mensajeEstatico("Solo un vehículo ESTACIONADO puede solicitar salida.");return false;}
                v.estado="EN_COLA_SALIDA";salida.encolar(v);}
            evento("Vehículo en cola de salida","Placa "+v.placa+" conserva su espacio hasta ser atendido","-");refrescar();return true;
        }
        private void mensajeEstatico(String s){SwingUtilities.invokeLater(()->JOptionPane.showMessageDialog(null,s,"ResiPark",JOptionPane.INFORMATION_MESSAGE));}
        void iniciarGaritas(){new Thread(()->atenderEntrada("G1"),"Garita-Entrada-1").start();new Thread(()->atenderEntrada("G2"),"Garita-Entrada-2").start();new Thread(this::atenderSalida,"Garita-Salida").start();}
        private void atenderEntrada(String g){
            while(activo){Vehiculo v;try{v=entrada.desencolarEsperando();}catch(InterruptedException ex){if(!activo)return;continue;}
                if(g.equals("G1")){actualG1=v.placa;actualVehiculo1=v;}else{actualG2=v.placa;actualVehiculo2=v;}refrescar();
                try{Thread.sleep(1000+(long)(Math.random()*2001));}catch(InterruptedException ex){if(!activo)return;}
                synchronized(v){synchronized(parqueo){if(!"EN_COLA_ENTRADA".equals(v.estado))continue;Espacio e=parqueo.asignar(v);
                    if(e==null){v.estado="FUERA";evento("Vehículo rechazado","Placa "+v.placa+": no hay espacios permitidos disponibles",g);if(v.visitante)evento("Visitante descartado","Placa "+v.placa+" rechazada",g);}
                    else evento("Vehículo asignado a espacio","Placa "+v.placa+" asignada a "+e.id,g);}}
                if(g.equals("G1")){actualG1="Esperando";actualVehiculo1=null;}else{actualG2="Esperando";actualVehiculo2=null;}refrescar();
            }
        }
        private void atenderSalida(){
            while(activo){Vehiculo v;try{v=salida.desencolarEsperando();}catch(InterruptedException ex){if(!activo)return;continue;}
                actualGS=v.placa;actualVehiculoSalida=v;refrescar();try{Thread.sleep(1000+(long)(Math.random()*2001));}catch(InterruptedException ex){if(!activo)return;}
                synchronized(v){synchronized(parqueo){if(!"EN_COLA_SALIDA".equals(v.estado))continue;parqueo.liberar(v);evento("Vehículo salió / espacio liberado","Placa "+v.placa+" salió del residencial", "GS");if(v.visitante)evento("Visitante descartado","Placa "+v.placa+" eliminada de memoria","GS");}}
                actualGS="Esperando";actualVehiculoSalida=null;refrescar();
            }
        }
        void detener(){activo=false;synchronized(entrada){entrada.notifyAll();}synchronized(salida){salida.notifyAll();}}
        String listadoResidentes(){StringBuilder s=new StringBuilder("ID | Nombre | Casa | Tipo | Vehículos\n");s.append("---------------------------------------------------------------\n");for(NodoR n=residentes.cabeza;n!=null;n=n.sig){Residente r=n.dato;s.append(r.id).append(" | ").append(r.nombre).append(" | ").append(r.casa).append(" | ").append(r.socio?"SOCIO":"NO SOCIO").append("\n");for(NodoV v=r.vehiculos.cabeza;v!=null;v=v.sig)s.append("   - ").append(v.dato.placa).append(" ").append(v.dato.marca).append(" ").append(v.dato.modelo).append(" | ").append(v.dato.estado).append("\n");}return s.toString();}
        String detalleResidente(Residente r){StringBuilder s=new StringBuilder("Residente: "+r.nombre+" ("+r.id+")\n");for(NodoV n=r.vehiculos.cabeza;n!=null;n=n.sig)s.append(n.dato.placa+" | "+n.dato.marca+" "+n.dato.modelo+" | "+n.dato.estado+"\n");return s.toString();}
        String estadoEntrada(){return "COLA FIFO DE ENTRADA\n=====================\n"+entrada.texto()+"\nGarita G1 atiende: "+actualG1+"\nGarita G2 atiende: "+actualG2;}
        String estadoSalida(){return "COLA FIFO DE SALIDA\n===================\n"+salida.texto()+"\nGarita de salida atiende: "+actualGS+"\n\nLos vehículos conservan el espacio mientras esperan salida.";}
        String estadoGaritas(){return "GARITAS CONCURRENTES\n====================\nG1 entrada: "+actualG1+"\nG2 entrada: "+actualG2+"\nGS salida:  "+actualGS+"\n\nEntrada: "+entrada.cantidad()+" vehículo(s) en cola.\nSalida: "+salida.cantidad()+" vehículo(s) en cola.\nEspacios ocupados: "+parqueo.ocupados()+"/150\n\nLos tres procesos son hilos independientes; cada atención dura entre 1 y 3 segundos.";}
        String placasEstacionadas(){StringBuilder s=new StringBuilder();s.append("");Espacio e=parqueo.socios.primero;for(int i=0;i<75;i++,e=e.sigArea)if(e.ocupado)s.append(e.vehiculo.placa).append(" ");e=parqueo.general.primero;for(int i=0;i<75;i++,e=e.sigArea)if(e.ocupado)s.append(e.vehiculo.placa).append(" ");return s.toString().trim();}
        void guardar(){
            try(PrintWriter w=new PrintWriter(new FileWriter("residentes.txt"))){for(NodoR n=residentes.cabeza;n!=null;n=n.sig){Residente r=n.dato;w.println(limpiar(r.id)+"|"+limpiar(r.nombre)+"|"+limpiar(r.casa)+"|"+(r.socio?"S":"N"));}}
            catch(IOException ex){evento("Error de persistencia","No se pudo guardar residentes.txt: "+ex.getMessage(),"-");}
            try(PrintWriter w=new PrintWriter(new FileWriter("vehiculos.txt"))){for(NodoR n=residentes.cabeza;n!=null;n=n.sig)for(NodoV v=n.dato.vehiculos.cabeza;v!=null;v=v.sig){Vehiculo a=v.dato;w.println(limpiar(a.placa)+"|"+limpiar(a.marca)+"|"+limpiar(a.modelo)+"|"+limpiar(a.color)+"|"+a.residente.id);}}
            catch(IOException ex){evento("Error de persistencia","No se pudo guardar vehiculos.txt: "+ex.getMessage(),"-");}
        }
        private String limpiar(String s){return s.replace("|"," ").replace("\n"," ").replace("\\r"," ");}
        void cargar(){
            File fr=new File("residentes.txt"),fv=new File("vehiculos.txt");
            if(fr.exists())try(BufferedReader br=new BufferedReader(new FileReader(fr))){String linea;int num=0;while((linea=br.readLine())!=null){num++;String[] p=linea.split("\\|",-1);if(p.length!=4){evento("Línea omitida","residentes.txt línea "+num+" inválida","-");continue;}if(p[0].isEmpty()||p[1].isEmpty()||p[2].isEmpty()||residentes.buscar(p[0])!=null){evento("Línea omitida","residentes.txt línea "+num+" inválida o ID duplicado","-");continue;}residentes.insertar(new Residente(p[0],p[1],p[2],"S".equalsIgnoreCase(p[3])));}}
            catch(IOException ex){evento("Error de persistencia","No se pudo leer residentes.txt","-");}
            if(fv.exists())try(BufferedReader br=new BufferedReader(new FileReader(fv))){String linea;int num=0;while((linea=br.readLine())!=null){num++;String[] p=linea.split("\\|",-1);if(p.length!=5){evento("Línea omitida","vehiculos.txt línea "+num+" inválida","-");continue;}Residente r=residentes.buscar(p[4]);if(r==null||r.vehiculos.cantidad()>=3||p[0].isEmpty()||placaExiste(p[0])){evento("Línea omitida","vehiculos.txt línea "+num+" referencia residente inexistente, placa duplicada o supera 3 vehículos","-");continue;}r.vehiculos.insertar(new Vehiculo(p[0],p[1],p[2],p[3],"Automóvil",r,false));}}
            catch(IOException ex){evento("Error de persistencia","No se pudo leer vehiculos.txt","-");}
        }
    }
}
