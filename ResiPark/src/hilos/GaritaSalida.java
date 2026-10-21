package hilos;

import estructuras.*;
import modelo.*;
import javax.swing.SwingUtilities;

public class GaritaSalida extends Thread {
    private ColaFIFO colaSalida;
    private ListaCircularParqueo parqueoSocios;
    private ListaCircularParqueo parqueoGeneral;
    private PilaEventos pilaEventos;
    private Runnable alActualizarGUI;

    public GaritaSalida(ColaFIFO cola, ListaCircularParqueo pSocios, ListaCircularParqueo pGeneral, PilaEventos pila, Runnable callback) {
        this.colaSalida = cola;
        this.parqueoSocios = pSocios;
        this.parqueoGeneral = pGeneral;
        this.pilaEventos = pila;
        this.alActualizarGUI = callback;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Vehiculo v = colaSalida.desencolar();
                Thread.sleep(2000);

                boolean liberado = parqueoSocios.liberarEspacio(v.getPlaca());
                if (!liberado) parqueoGeneral.liberarEspacio(v.getPlaca());

                v.setEstado(Vehiculo.Estado.FUERA);
                pilaEventos.apilar(new Evento("SALIDA", "Vehículo " + v.getPlaca() + " salió del residencial", "Garita Salida"));

                SwingUtilities.invokeLater(alActualizarGUI);
            } catch (InterruptedException e) { break; }
        }
    }
}