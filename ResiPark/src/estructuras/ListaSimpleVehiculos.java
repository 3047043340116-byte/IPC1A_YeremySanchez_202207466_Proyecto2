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

    // 1. INSERTAR (Máximo 3 vehículos por residente)
    public boolean insertar(Vehiculo v) {
        if (tamaño >= 3) return false;
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

    // 2. BUSCAR POR PLACA
    public Vehiculo buscarPorPlaca(String placa) {
        Nodo aux = cabeza;
        while (aux != null) {
            if (aux.vehiculo.getPlaca().equalsIgnoreCase(placa)) {
                return aux.vehiculo;
            }
            aux = aux.siguiente;
        }
        return null;
    }

    // 3. ELIMINAR (Validando que el vehículo esté en estado FUERA)
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

    // 4. RECORRER (Para iterar los nodos manualmente)
    public Nodo getCabeza() { return cabeza; }
    public int getTamaño() { return tamaño; }
}