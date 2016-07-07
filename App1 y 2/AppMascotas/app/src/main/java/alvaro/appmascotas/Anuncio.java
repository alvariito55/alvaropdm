package alvaro.appmascotas;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Alvaro on 28/4/16.
 */
public class Anuncio {

    private int id;
    private int id_anunciante;
    private String nombre_anunciante;
    private String ciudad;
    private int telefono_anunciante;
    private String anuncio;
    private String fecha_inicio;
    private String fecha_final;
    private Boolean ocupado;

    public Anuncio(int id_anunciante, String nombre, String ciudad, int telefono, String anuncio, String fechaInicio, String fechaFinal, Boolean ocupado){
        this.id_anunciante = id_anunciante;
        this.nombre_anunciante = nombre;
        this.ciudad = ciudad;
        this.telefono_anunciante = telefono;
        this.anuncio = anuncio;
        this.fecha_inicio = fechaInicio;
        this.fecha_final = fechaFinal;
        this.ocupado = ocupado;
    }

    public Anuncio(JSONObject jsonObject) {
        try {
            this.id = jsonObject.getInt("id");
            this.id_anunciante = jsonObject.getInt("idAnunciante");
            this.nombre_anunciante = jsonObject.getString("nombreAnunciante");
            this.ciudad = jsonObject.getString("ciudad");
            this.telefono_anunciante = jsonObject.getInt("telefonoAnunciante");
            this.anuncio = jsonObject.getString("anuncio");
            this.fecha_inicio = jsonObject.getString("fechaInicio");
            this.fecha_final = jsonObject.getString("fechaFin");
            this.ocupado = jsonObject.getBoolean("ocupado");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString(){
        String reservado;
        if (getOcupado())
            reservado = "Si";
        else
            reservado = "No";

        return "\nAnunciante: " + getNombre_anunciante() + "\n\t\t" +
                "Anuncio: " + getAnuncio() + "\n\t\t" +
                "Ciudad: " + getCiudad() + "\n\t\t" +
                "Fecha de inicio: " + getFecha_inicio() + "\n\t\t" +
                "Fecha de fin: " + getFecha_final() + "\n" +
                "Reservado:  " + reservado + "\n";

    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_anunciante() {
        return id_anunciante;
    }

    public void setId_anunciante(int id_anunciante) {
        this.id_anunciante = id_anunciante;
    }

    public String getNombre_anunciante() {
        return nombre_anunciante;
    }

    public void setNombre_anunciante(String nombre_anunciante) {
        this.nombre_anunciante = nombre_anunciante;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public int getTelefono_anunciante() {
        return telefono_anunciante;
    }

    public void setTelefono_anunciante(int telefono_anunciante) {
        this.telefono_anunciante = telefono_anunciante;
    }

    public String getAnuncio() {
        return anuncio;
    }

    public void setAnuncio(String anuncio) {
        this.anuncio = anuncio;
    }

    public String getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(String fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public String getFecha_final() {
        return fecha_final;
    }

    public void setFecha_final(String fecha_final) {
        this.fecha_final = fecha_final;
    }

    public Boolean getOcupado() {
        return ocupado;
    }

    public void setOcupado(Boolean ocupado) {
        this.ocupado = ocupado;
    }
}
