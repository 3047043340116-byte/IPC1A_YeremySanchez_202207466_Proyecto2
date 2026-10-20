package resipark;

import static resipark.ModeloResiPark.*;

/** Pruebas manuales automatizadas de las estructuras principales. Ejecutar desde IDE o terminal. */
public class PruebasEstructuras {
    public static void main(String[] args) throws Exception {
        probarCantidadYEnlacesDelParqueo();
        probarColaFIFO();
        probarListaDobleYRecorridoInverso();
        probarCierreDeCola();
        probarVisitanteSoloEnAreaGeneral();
        System.out.println("Todas las pruebas de estructuras terminaron correctamente.");
    }

    private static void probarCantidadYEnlacesDelParqueo() {
        Parqueo p = new Parqueo();
        exigir(p.ocupados() == 0, "El parqueo debe iniciar vacío");
        exigir(contar(p.socios.primero) == 75, "El área de socios debe tener 75 espacios");
        exigir(contar(p.general.primero) == 75, "El área general debe tener 75 espacios");
        Espacio a1 = p.buscar("A1"), a2 = p.buscar("A2"), b1 = p.buscar("B1");
        exigir(a1 != null && a1.derecha == a2 && a2.izquierda == a1, "Enlace horizontal incorrecto");
        exigir(a1.abajo == b1 && b1.arriba == a1, "Enlace vertical incorrecto");
    }

    private static int contar(Espacio inicio) {
        if (inicio == null) return 0;
        int n = 0; Espacio e = inicio;
        do { n++; e = e.sigArea; } while (e != inicio && n <= 200);
        return n;
    }

    private static void probarColaFIFO() throws Exception {
        ColaVehiculos cola = new ColaVehiculos();
        Residente r = new Residente("R1", "Prueba", "1", false);
        Vehiculo a = new Vehiculo("AAA111", "Marca", "Modelo", "Rojo", "Automóvil", r, false);
        Vehiculo b = new Vehiculo("BBB222", "Marca", "Modelo", "Azul", "Automóvil", r, false);
        cola.encolar(a); cola.encolar(b);
        exigir(cola.desencolarEsperando() == a, "La cola no respetó FIFO para el primer vehículo");
        exigir(cola.desencolarEsperando() == b, "La cola no respetó FIFO para el segundo vehículo");
    }

    private static void probarListaDobleYRecorridoInverso() {
        ListaResidentes lista = new ListaResidentes();
        lista.insertar(new Residente("R1", "Primero", "1", false));
        lista.insertar(new Residente("R2", "Segundo", "2", true));
        exigir(lista.cabeza.dato.id.equals("R1"), "Cabeza incorrecta");
        exigir(lista.cola.dato.id.equals("R2"), "Cola incorrecta");
        exigir(lista.cola.ant == lista.cabeza && lista.cabeza.sig == lista.cola, "Enlaces dobles incorrectos");
        exigir(lista.listadoInverso().indexOf("R2") < lista.listadoInverso().indexOf("R1"), "Recorrido inverso incorrecto");
    }

    private static void probarCierreDeCola() throws Exception {
        ColaVehiculos cola = new ColaVehiculos();
        cola.cerrar();
        exigir(cola.desencolarEsperando() == null, "Una cola cerrada y vacía debe liberar al hilo");
    }

    private static void probarVisitanteSoloEnAreaGeneral() {
        Parqueo p = new Parqueo();
        Residente socio = new Residente("RS", "Socio", "1", true);
        Vehiculo visitante = new Vehiculo("VIS123", "Visitante", "Temporal", "Negro", "Automóvil", socio, true);
        Espacio asignado = p.asignar(visitante);
        exigir(asignado != null, "Debe existir espacio general disponible");
        exigir(asignado.tipo.equals("GENERAL"), "El visitante no debe ocupar área de socios aunque visite a un socio");
        exigir(visitante.estado.equals("ESTACIONADO"), "El estado debe cambiar a ESTACIONADO tras asignarse");
    }

    private static void exigir(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }
}
