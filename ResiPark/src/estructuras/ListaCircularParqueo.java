package estructuras;

import modelo.EspacioParqueo;
import modelo.Vehiculo;

public class ListaCircularParqueo {
    public class Nodo {
        public EspacioParqueo espacio;
        public Nodo siguiente;
        public Nodo(EspacioParqueo e) { this.espacio = e; }
    }

    private Nodo ultimoAsignado;
    private int tamaño;

    public ListaCircularParqueo() {
        this.ultimoAsignado = null;
        this.tamaño = 0;
    }

    public void agregarEspacio(EspacioParqueo e) {
        Nodo nuevo = new Nodo(e);
        if (ultimoAsignado == null) {
            ultimoAsignado = nuevo;
            ultimoAsignado.siguiente = ultimoAsignado;
        } else {
            nuevo.siguiente = ultimoAsignado.siguiente;
            ultimoAsignado.siguiente = nuevo;
            ultimoAsignado = nuevo;
        }
        tamaño++;
    }

    public synchronized EspacioParqueo buscarYAsignarEspacioLibre(Vehiculo v) {
        if (ultimoAsignado == null) return null;
        Nodo actual = ultimoAsignado.siguiente;
        for (int i = 0; i < tamaño; i++) {
            if (!actual.espacio.isOcupado()) {
                actual.espacio.setOcupado(true);
                actual.espacio.setVehiculo(v);
                ultimoAsignado = actual; // Actualiza la referencia
                return actual.espacio;
            }
            actual = actual.siguiente;
        }
        return null;
    }

    public synchronized boolean liberarEspacio(String placa) {
        if (ultimoAsignado == null) return false;
        Nodo actual = ultimoAsignado.siguiente;
        for (int i = 0; i < tamaño; i++) {
            if (actual.espacio.isOcupado() && actual.espacio.getVehiculo().getPlaca().equalsIgnoreCase(placa)) {
                actual.espacio.setOcupado(false);
                actual.espacio.setVehiculo(null);
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    public Nodo getInicio() { return ultimoAsignado != null ? ultimoAsignado.siguiente : null; }
    public int getTamaño() { return tamaño; }
}