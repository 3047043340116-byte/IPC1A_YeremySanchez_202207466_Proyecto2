package modelo;

import estructuras.ListaSimpleVehiculos;

public class Residente {
    private String id;
    private String nombre;
    private String casaLote;
    private boolean esSocio;
    private ListaSimpleVehiculos vehiculos;

    public Residente(String id, String nombre, String casaLote, boolean esSocio) {
        this.id = id;
        this.nombre = nombre;
        this.casaLote = casaLote;
        this.esSocio = esSocio;
        this.vehiculos = new ListaSimpleVehiculos();
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCasaLote() { return casaLote; }
    public void setCasaLote(String casaLote) { this.casaLote = casaLote; }
    public boolean esSocio() { return esSocio; }
    public void setEsSocio(boolean esSocio) { this.esSocio = esSocio; }
    public ListaSimpleVehiculos getVehiculos() { return vehiculos; }
}