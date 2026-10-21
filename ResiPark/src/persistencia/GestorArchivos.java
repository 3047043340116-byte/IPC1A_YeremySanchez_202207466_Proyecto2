package persistencia;

import estructuras.ListaDobleResidentes;
import modelo.Residente;
import modelo.Vehiculo;
import java.io.*;

public class GestorArchivos {
    private static final String FILE_RESIDENTES = "residentes.txt";
    private static final String FILE_VEHICULOS = "vehiculos.txt";

    public static void guardarDatos(ListaDobleResidentes lista) {
        try (PrintWriter pwR = new PrintWriter(new FileWriter(FILE_RESIDENTES));
             PrintWriter pwV = new PrintWriter(new FileWriter(FILE_VEHICULOS))) {

            ListaDobleResidentes.Nodo auxR = lista.getCabeza();
            while (auxR != null) {
                Residente r = auxR.residente;
                pwR.println(r.getId() + "|" + r.getNombre() + "|" + r.getCasaLote() + "|" + (r.esSocio() ? "S" : "N"));

                var auxV = r.getVehiculos().getCabeza();
                while (auxV != null) {
                    Vehiculo v = auxV.vehiculo;
                    pwV.println(v.getPlaca() + "|" + v.getMarca() + "|" + v.getModelo() + "|" + v.getColor() + "|" + v.getTipo() + "|" + r.getId());
                    auxV = auxV.siguiente;
                }
                auxR = auxR.siguiente;
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void cargarDatos(ListaDobleResidentes lista) {
        File fR = new File(FILE_RESIDENTES);
        if (!fR.exists()) return;

        try (BufferedReader brR = new BufferedReader(new FileReader(fR))) {
            String linea;
            while ((linea = brR.readLine()) != null) {
                String[] p = linea.split("\\|");
                if (p.length == 4) {
                    lista.insertar(new Residente(p[0], p[1], p[2], p[3].equalsIgnoreCase("S")));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }

        File fV = new File(FILE_VEHICULOS);
        if (!fV.exists()) return;

        try (BufferedReader brV = new BufferedReader(new FileReader(fV))) {
            String linea;
            while ((linea = brV.readLine()) != null) {
                String[] p = linea.split("\\|");
                if (p.length == 6) {
                    Residente r = lista.buscarPorId(p[5]);
                    if (r != null) {
                        r.getVehiculos().insertar(new Vehiculo(p[0], p[1], p[2], p[3], p[4], p[5]));
                    }
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}