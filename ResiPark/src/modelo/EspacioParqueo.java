package modelo;

public class EspacioParqueo {
    private String idEspacio;
    private boolean ocupado;
    private Vehiculo vehiculo;
    private String tipoArea; // "SOCIO" o "GENERAL"

    public EspacioParqueo(String idEspacio, String tipoArea) {
        this.idEspacio = idEspacio;
        this.tipoArea = tipoArea;
        this.ocupado = false;
        this.vehiculo = null;
    }

    public String getIdEspacio() { return idEspacio; }
    public boolean isOcupado() { return ocupado; }
    public void setOcupado(boolean ocupado) { this.ocupado = ocupado; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }
    public String getTipoArea() { return tipoArea; }
}