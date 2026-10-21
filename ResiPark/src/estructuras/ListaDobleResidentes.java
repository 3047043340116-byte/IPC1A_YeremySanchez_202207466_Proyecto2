package estructuras;

import modelo.Residente;
import modelo.Vehiculo;

public class ListaDobleResidentes {
    public class Nodo {
        public Residente residente;
        public Nodo anterior;
        public Nodo siguiente;
        public Nodo(Residente r) { this.residente = r; }
    }

    private Nodo cabeza;
    private Nodo cola;

    public boolean insertar(Residente r) {
        if (buscarPorId(r.getId()) != null) return false;
        Nodo nuevo = new Nodo(r);
        if (cabeza == null) {
            cabeza = cola = nuevo;
        } else {
            cola.siguiente = nuevo;
            nuevo.anterior = cola;
            cola = nuevo;
        }
        return true;
    }

    public Residente buscarPorId(String id) {
        Nodo aux = cabeza;
        while (aux != null) {
            if (aux.residente.getId().equalsIgnoreCase(id)) return aux.residente;
            aux = aux.siguiente;
        }
        return null;
    }

    public Vehiculo buscarVehiculoGlobal(String placa) {
        Nodo aux = cabeza;
        while (aux != null) {
            Vehiculo v = aux.residente.getVehiculos().buscarPorPlaca(placa);
            if (v != null) return v;
            aux = aux.siguiente;
        }
        return null;
    }

    public boolean eliminar(String id) {
        Nodo aux = cabeza;
        while (aux != null) {
            if (aux.residente.getId().equalsIgnoreCase(id)) {
                ListaSimpleVehiculos.Nodo nV = aux.residente.getVehiculos().getCabeza();
                while (nV != null) {
                    if (nV.vehiculo.getEstado() != Vehiculo.Estado.FUERA) return false;
                    nV = nV.siguiente;
                }
                if (aux.anterior != null) aux.anterior.siguiente = aux.siguiente;
                else cabeza = aux.siguiente;

                if (aux.siguiente != null) aux.siguiente.anterior = aux.anterior;
                else cola = aux.anterior;
                return true;
            }
            aux = aux.siguiente;
        }
        return false;
    }

    public Nodo getCabeza() { return cabeza; }
}