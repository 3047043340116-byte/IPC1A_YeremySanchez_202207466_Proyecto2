package estructuras;

import modelo.Vehiculo;

public class ListaSimpleVehiculos {
    public class Nodo {
        public Vehiculo vehiculo;
        public Nodo siguiente;
        public Nodo(Vehiculo v) { this.vehiculo = v; this.siguiente = null; }
    }

    private Nodo cabeza;
    private int tamaño;

    public ListaSimpleVehiculos() {
        this.cabeza = null;
        this.tamaño = 0;
    }

    public boolean insertar(Vehiculo v) {
        if (tamaño >= 3) return false; // Máximo 3 vehículos por residente
        Nodo nuevo = new Nodo(v);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            Nodo aux = cabeza;
            while (aux.siguiente != null) {
                aux = aux.siguiente;
            }
            aux.siguiente = nuevo;
        }
        tamaño++;
        return true;
    }

    public Vehiculo buscarPorPlaca(String placa) {
        Nodo aux = cabeza;
        while (aux != null) {
            if (aux.vehiculo.getPlaca().equalsIgnoreCase(placa)) return aux.vehiculo;
            aux = aux.siguiente;
        }
        return null;
    }

    public boolean eliminar(String placa) {
        if (cabeza == null) return false;
        if (cabeza.vehiculo.getPlaca().equalsIgnoreCase(placa)) {
            if (cabeza.vehiculo.getEstado() != Vehiculo.Estado.FUERA) return false;
            cabeza = cabeza.siguiente;
            tamaño--;
            return true;
        }
        Nodo aux = cabeza;
        while (aux.siguiente != null) {
            if (aux.siguiente.vehiculo.getPlaca().equalsIgnoreCase(placa)) {
                if (aux.siguiente.vehiculo.getEstado() != Vehiculo.Estado.FUERA) return false;
                aux.siguiente = aux.siguiente.siguiente;
                tamaño--;
                return true;
            }
            aux = aux.siguiente;
        }
        return false;
    }

    public int getTamaño() { return tamaño; }
    public Nodo getCabeza() { return cabeza; }
}