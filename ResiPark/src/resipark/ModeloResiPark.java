package resipark;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Modelo y estructuras de datos manuales de ResiPark. */
final class ModeloResiPark {
static class NodoR { Residente dato; NodoR ant,sig; NodoR(Residente d){dato=d;} }
static class ListaResidentes {
    NodoR cabeza,cola; int size;
    synchronized boolean insertar(Residente r){if(buscar(r.id)!=null)return false;NodoR n=new NodoR(r);if(cabeza==null)cabeza=cola=n;else{cola.sig=n;n.ant=cola;cola=n;}size++;return true;}
    synchronized Residente buscar(String id){for(NodoR n=cabeza;n!=null;n=n.sig)if(n.dato.id.equalsIgnoreCase(id))return n.dato;return null;}
    synchronized boolean eliminar(String id){for(NodoR n=cabeza;n!=null;n=n.sig)if(n.dato.id.equalsIgnoreCase(id)){if(n.ant!=null)n.ant.sig=n.sig;else cabeza=n.sig;if(n.sig!=null)n.sig.ant=n.ant;else cola=n.ant;size--;return true;}return false;}
    synchronized int cantidad(){return size;}
    synchronized String listadoInverso(){StringBuilder s=new StringBuilder("LISTA DOBLE DE RESIDENTES (RECORRIDO INVERSO)\n");s.append("================================================\n");for(NodoR n=cola;n!=null;n=n.ant){Residente r=n.dato;s.append(r.id).append(" | ").append(r.nombre).append(" | Casa: ").append(r.casa).append(" | ").append(r.socio?"SOCIO":"NO SOCIO").append("\n");for(NodoV v=r.vehiculos.cabeza;v!=null;v=v.sig)s.append("   - ").append(v.dato.placa).append(" | ").append(v.dato.estado).append("\n");}return s.toString();}
    synchronized Vehiculo buscarVehiculo(String placa){for(NodoR n=cabeza;n!=null;n=n.sig){Vehiculo v=n.dato.vehiculos.buscar(placa);if(v!=null)return v;}return null;}
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
        if(v.residente.socio && !v.visitante){e=socios.buscarLibre();if(e==null)e=general.buscarLibre();}
        else e=general.buscarLibre();
        if(e!=null){e.ocupado=true;e.vehiculo=v;v.espacio=e;v.estado="ESTACIONADO";}
        return e;
    }
    synchronized void liberar(Vehiculo v){if(v.espacio!=null){v.espacio.ocupado=false;v.espacio.vehiculo=null;v.espacio=null;}v.estado="FUERA";}
    synchronized int ocupados(){return socios.ocupados()+general.ocupados();}
}
static class NodoCola {Vehiculo dato;NodoCola sig;NodoCola(Vehiculo v){dato=v;}}
static class ColaVehiculos {
    NodoCola frente,fin;int size;boolean cerrada;
    synchronized void encolar(Vehiculo v){if(cerrada)throw new IllegalStateException("La cola está cerrada");NodoCola n=new NodoCola(v);if(fin==null)frente=fin=n;else{fin.sig=n;fin=n;}size++;notifyAll();}
    synchronized Vehiculo desencolarEsperando() throws InterruptedException {while(frente==null&&!cerrada)wait();if(frente==null)return null;NodoCola n=frente;frente=n.sig;if(frente==null)fin=null;size--;return n.dato;}
    synchronized void cerrar(){cerrada=true;notifyAll();}
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
    Vehiculo buscarVehiculo(String placa){Vehiculo residente=residentes.buscarVehiculo(placa);return residente!=null?residente:buscarVisitanteActivo(placa);}
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
        while(activo){Vehiculo v;try{v=entrada.desencolarEsperando();}catch(InterruptedException ex){if(!activo)return;continue;}if(v==null)continue;
            if(g.equals("G1")){actualG1=v.placa;actualVehiculo1=v;}else{actualG2=v.placa;actualVehiculo2=v;}refrescar();
            try{Thread.sleep(1000+(long)(Math.random()*2001));}catch(InterruptedException ex){if(!activo)return;}
            synchronized(v){synchronized(parqueo){if(!"EN_COLA_ENTRADA".equals(v.estado))continue;Espacio e=parqueo.asignar(v);
                if(e==null){v.estado="FUERA";evento("Vehículo rechazado","Placa "+v.placa+": no hay espacios permitidos disponibles",g);if(v.visitante)evento("Visitante descartado","Placa "+v.placa+" rechazada",g);}
                else evento("Vehículo asignado a espacio","Placa "+v.placa+" asignada a "+e.id,g);}}
            if(g.equals("G1")){actualG1="Esperando";actualVehiculo1=null;}else{actualG2="Esperando";actualVehiculo2=null;}refrescar();
        }
    }
    private void atenderSalida(){
        while(activo){Vehiculo v;try{v=salida.desencolarEsperando();}catch(InterruptedException ex){if(!activo)return;continue;}if(v==null)continue;
            actualGS=v.placa;actualVehiculoSalida=v;refrescar();try{Thread.sleep(1000+(long)(Math.random()*2001));}catch(InterruptedException ex){if(!activo)return;}
            synchronized(v){synchronized(parqueo){if(!"EN_COLA_SALIDA".equals(v.estado))continue;parqueo.liberar(v);evento("Vehículo salió / espacio liberado","Placa "+v.placa+" salió del residencial", "GS");if(v.visitante)evento("Visitante descartado","Placa "+v.placa+" eliminada de memoria","GS");}}
            actualGS="Esperando";actualVehiculoSalida=null;refrescar();
        }
    }
    void detener(){activo=false;entrada.cerrar();salida.cerrar();}
    String listadoResidentes(){StringBuilder s=new StringBuilder("ID | Nombre | Casa | Tipo | Vehículos\n");s.append("---------------------------------------------------------------\n");for(NodoR n=residentes.cabeza;n!=null;n=n.sig){Residente r=n.dato;s.append(r.id).append(" | ").append(r.nombre).append(" | ").append(r.casa).append(" | ").append(r.socio?"SOCIO":"NO SOCIO").append("\n");for(NodoV v=r.vehiculos.cabeza;v!=null;v=v.sig)s.append("   - ").append(v.dato.placa).append(" ").append(v.dato.marca).append(" ").append(v.dato.modelo).append(" | ").append(v.dato.estado).append("\n");}return s.toString();}
    String listadoResidentesInverso(){return residentes.listadoInverso();}
    String detalleResidente(Residente r){StringBuilder s=new StringBuilder("Residente: "+r.nombre+" ("+r.id+")\n");for(NodoV n=r.vehiculos.cabeza;n!=null;n=n.sig)s.append(n.dato.placa+" | "+n.dato.marca+" "+n.dato.modelo+" | "+n.dato.estado+"\n");return s.toString();}
    String estadoEntrada(){return "COLA FIFO DE ENTRADA\n=====================\n"+entrada.texto()+"\nGarita G1 atiende: "+actualG1+"\nGarita G2 atiende: "+actualG2;}
    String estadoSalida(){return "COLA FIFO DE SALIDA\n===================\n"+salida.texto()+"\nGarita de salida atiende: "+actualGS+"\n\nLos vehículos conservan el espacio mientras esperan salida.";}
    String estadoGaritas(){return "GARITAS CONCURRENTES\n====================\nG1 entrada: "+actualG1+"\nG2 entrada: "+actualG2+"\nGS salida:  "+actualGS+"\n\nEntrada: "+entrada.cantidad()+" vehículo(s) en cola.\nSalida: "+salida.cantidad()+" vehículo(s) en cola.\nEspacios ocupados: "+parqueo.ocupados()+"/150\n\nLos tres procesos son hilos independientes; cada atención dura entre 1 y 3 segundos.";}
    String placasEstacionadas(){StringBuilder s=new StringBuilder();s.append("");Espacio e=parqueo.socios.primero;for(int i=0;i<75;i++,e=e.sigArea)if(e.ocupado)s.append(e.vehiculo.placa).append(" ");e=parqueo.general.primero;for(int i=0;i<75;i++,e=e.sigArea)if(e.ocupado)s.append(e.vehiculo.placa).append(" ");return s.toString().trim();}
    void guardar(){
        try(PrintWriter w=new PrintWriter(new OutputStreamWriter(new FileOutputStream("residentes.txt"), java.nio.charset.StandardCharsets.UTF_8))){for(NodoR n=residentes.cabeza;n!=null;n=n.sig){Residente r=n.dato;w.println(limpiar(r.id)+"|"+limpiar(r.nombre)+"|"+limpiar(r.casa)+"|"+(r.socio?"S":"N"));}}
        catch(IOException ex){evento("Error de persistencia","No se pudo guardar residentes.txt: "+ex.getMessage(),"-");}
        try(PrintWriter w=new PrintWriter(new OutputStreamWriter(new FileOutputStream("vehiculos.txt"), java.nio.charset.StandardCharsets.UTF_8))){for(NodoR n=residentes.cabeza;n!=null;n=n.sig)for(NodoV v=n.dato.vehiculos.cabeza;v!=null;v=v.sig){Vehiculo a=v.dato;w.println(limpiar(a.placa)+"|"+limpiar(a.marca)+"|"+limpiar(a.modelo)+"|"+limpiar(a.color)+"|"+a.residente.id);}}
        catch(IOException ex){evento("Error de persistencia","No se pudo guardar vehiculos.txt: "+ex.getMessage(),"-");}
    }
    private String limpiar(String s){return s.replace("|"," ").replace("\n"," ").replace("\r"," ");}
    void cargar(){
        File fr=new File("residentes.txt"),fv=new File("vehiculos.txt");
        if(fr.exists())try(BufferedReader br=new BufferedReader(new FileReader(fr))){String linea;int num=0;while((linea=br.readLine())!=null){num++;String[] p=linea.split("\\|",-1);if(p.length!=4){evento("Línea omitida","residentes.txt línea "+num+" inválida","-");continue;}if(p[0].trim().isEmpty()||p[1].trim().isEmpty()||p[2].trim().isEmpty()||residentes.buscar(p[0])!=null||(!"S".equalsIgnoreCase(p[3])&& !"N".equalsIgnoreCase(p[3]))){evento("Línea omitida","residentes.txt línea "+num+" inválida, socio debe ser S/N o ID duplicado","-");continue;}residentes.insertar(new Residente(p[0].trim(),p[1].trim(),p[2].trim(),"S".equalsIgnoreCase(p[3])));}}
        catch(IOException ex){evento("Error de persistencia","No se pudo leer residentes.txt","-");}
        if(fv.exists())try(BufferedReader br=new BufferedReader(new FileReader(fv))){String linea;int num=0;while((linea=br.readLine())!=null){num++;String[] p=linea.split("\\|",-1);if(p.length!=5){evento("Línea omitida","vehiculos.txt línea "+num+" inválida","-");continue;}Residente r=residentes.buscar(p[4]);if(r==null||r.vehiculos.cantidad()>=3||p[0].trim().isEmpty()||p[1].trim().isEmpty()||p[2].trim().isEmpty()||p[3].trim().isEmpty()||placaExiste(p[0].trim())){evento("Línea omitida","vehiculos.txt línea "+num+" inválida, residente inexistente, placa duplicada o supera 3 vehículos","-");continue;}r.vehiculos.insertar(new Vehiculo(p[0].trim().toUpperCase(),p[1].trim(),p[2].trim(),p[3].trim(),"Automóvil",r,false));}}
        catch(IOException ex){evento("Error de persistencia","No se pudo leer vehiculos.txt","-");}
    }
}
}
