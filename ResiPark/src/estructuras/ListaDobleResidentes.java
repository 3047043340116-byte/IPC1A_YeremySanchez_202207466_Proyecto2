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

    // 1. INSERTAR RESIDENTE
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

    // 2. BUSCAR RESIDENTE POR ID
    public Residente buscarPorId(String id) {
        Nodo aux = cabeza;
        while (aux != null) {
            if (aux.residente.getId().equalsIgnoreCase(id)) return aux.residente;
            aux = aux.siguiente;
        }
        return null;
    }

    // 3. VALIDACIÓN DE PLACA ÚNICA EN TODO EL SISTEMA (Incluye Residentes y Visitantes)
    public boolean existePlacaGlobal(String placa) {
        Nodo aux = cabeza;
        while (aux != null) {
            if (aux.residente.getVehiculos().buscarPorPlaca(placa) != null) {
                return true; // La placa ya pertenece a un residente
            }
            aux = aux.siguiente;
        }
        return false;
    }

    // Búsqueda global de vehículo registrado por residente
    public Vehiculo buscarVehiculoGlobal(String placa) {
        Nodo aux = cabeza;
        while (aux != null) {
            Vehiculo v = aux.residente.getVehiculos().buscarPorPlaca(placa);
            if (v != null) return v;
            aux = aux.siguiente;
        }
        return null;
    }

    // 4. ACTUALIZAR RESIDENTE
    public boolean actualizar(String id, String nuevoNombre, String nuevaCasa, boolean nuevoEsSocio) {
        Nodo aux = cabeza;
        while (aux != null) {
            if (aux.residente.getId().equalsIgnoreCase(id)) {
                if (aux.residente.esSocio() != nuevoEsSocio) {
                    ListaSimpleVehiculos.Nodo nV = aux.residente.getVehiculos().getCabeza();
                    while (nV != null) {
                        if (nV.vehiculo.getEstado() != Vehiculo.Estado.FUERA) {
                            return false; // No permite cambiar si tiene vehículos dentro
                        }
                        nV = nV.siguiente;
                    }
                }
                aux.residente.setNombre(nuevoNombre);
                aux.residente.setCasaLote(nuevaCasa);
                aux.residente.setEsSocio(nuevoEsSocio);
                return true;
            }
            aux = aux.siguiente;
        }
        return false;
    }

    // 5. ELIMINAR RESIDENTE
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
    public Nodo getCola() { return cola; }
}