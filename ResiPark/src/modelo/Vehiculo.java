package modelo;

public class Vehiculo {
    public enum Estado { FUERA, EN_COLA_ENTRADA, ESTACIONADO, EN_COLA_SALIDA }

    private String placa;
    private String marca;
    private String modelo;
    private String color;
    private String tipo; // automovil, motocicleta, pickup
    private Estado estado;
    private String idResidente; // ID del residente o "VISITANTE"

    public Vehiculo(String placa, String marca, String modelo, String color, String tipo, String idResidente) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.color = color;
        this.tipo = tipo;
        this.idResidente = idResidente;
        this.estado = Estado.FUERA;
    }

    public String getPlaca() { return placa; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public String getColor() { return color; }
    public String getTipo() { return tipo; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public String getIdResidente() { return idResidente; }
}
