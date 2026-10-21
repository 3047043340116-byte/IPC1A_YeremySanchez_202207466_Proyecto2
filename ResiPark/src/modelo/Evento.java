package modelo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Evento {
    private String fechaHora;
    private String tipo;
    private String descripcion;
    private String garita;

    public Evento(String tipo, String descripcion, String garita) {
        this.fechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.garita = garita;
    }

    @Override
    public String toString() {
        return "[" + fechaHora + "] [" + tipo + "] " + (garita != null ? "(" + garita + ") " : "") + descripcion;
    }
}