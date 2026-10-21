package hilos;

import estructuras.*;
import modelo.*;
import javax.swing.SwingUtilities;

public class GaritaEntrada extends Thread {
    private String nombreGarita;
    private ColaFIFO colaEntrada;
    private ListaCircularParqueo parqueoSocios;
    private ListaCircularParqueo parqueoGeneral;
    private ListaDobleResidentes listaResidentes;
    private PilaEventos pilaEventos;
    private Runnable alActualizarGUI;

    public GaritaEntrada(String nombre, ColaFIFO cola, ListaCircularParqueo pSocios, ListaCircularParqueo pGeneral, ListaDobleResidentes residentes, PilaEventos pila, Runnable callback) {
        this.nombreGarita = nombre;
        this.colaEntrada = cola;
        this.parqueoSocios = pSocios;
        this.parqueoGeneral = pGeneral;
        this.listaResidentes = residentes;
        this.pilaEventos = pila;
        this.alActualizarGUI = callback;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Vehiculo v = colaEntrada.desencolar();
                Thread.sleep(2000); // Simulación de tiempo de atención

                Residente r = listaResidentes.buscarPorId(v.getIdResidente());
                boolean esSocio = (r != null && r.esSocio());

                EspacioParqueo asignado = null;
                if (esSocio) {
                    asignado = parqueoSocios.buscarYAsignarEspacioLibre(v);
                    if (asignado == null) asignado = parqueoGeneral.buscarYAsignarEspacioLibre(v);
                } else {
                    asignado = parqueoGeneral.buscarYAsignarEspacioLibre(v);
                }

                if (asignado != null) {
                    v.setEstado(Vehiculo.Estado.ESTACIONADO);
                    pilaEventos.apilar(new Evento("ASIGNACION", "Vehículo " + v.getPlaca() + " asignado a " + asignado.getIdEspacio(), nombreGarita));
                } else {
                    v.setEstado(Vehiculo.Estado.FUERA);
                    pilaEventos.apilar(new Evento("RECHAZO", "Vehículo " + v.getPlaca() + " rechazado (Parqueo lleno)", nombreGarita));
                }

                SwingUtilities.invokeLater(alActualizarGUI); // Actualización segura en EDT
            } catch (InterruptedException e) { break; }
        }
    }
}