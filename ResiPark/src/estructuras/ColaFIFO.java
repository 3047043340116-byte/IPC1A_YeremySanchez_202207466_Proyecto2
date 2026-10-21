package estructuras;

import modelo.Vehiculo;

public class ColaFIFO {
    public class Nodo {
        public Vehiculo vehiculo;
        public Nodo siguiente;
        public Nodo(Vehiculo v) { this.vehiculo = v; }
    }
    private Nodo frente, fin;

    public synchronized void encolar(Vehiculo v) {
        Nodo nuevo = new Nodo(v);
        if (fin == null) { frente = fin = nuevo; }
        else { fin.siguiente = nuevo; fin = nuevo; }
        notifyAll(); // Notifica a los hilos de garitas
    }

    public synchronized Vehiculo desencolar() throws InterruptedException {
        while (frente == null) {
            wait(); // Espera sincronizada si la cola está vacía
        }
        Vehiculo v = frente.vehiculo;
        frente = frente.siguiente;
        if (frente == null) fin = null;
        return v;
    }

    public Nodo getFrente() { return frente; }
}