package estructuras;

import modelo.Evento;

public class PilaEventos {
    public class Nodo {
        public Evento evento;
        public Nodo siguiente;
        public Nodo(Evento e) { this.evento = e; }
    }
    private Nodo tope;

    public synchronized void apilar(Evento e) {
        Nodo nuevo = new Nodo(e);
        nuevo.siguiente = tope;
        tope = nuevo;
    }

    public Nodo getTope() { return tope; }
}