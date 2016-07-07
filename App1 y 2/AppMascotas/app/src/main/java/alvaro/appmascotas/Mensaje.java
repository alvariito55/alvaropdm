package alvaro.appmascotas;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Alvaro on 28/4/16.
 */
public class Mensaje {

    private int id;
    private int id_envio;
    private int id_receptor;
    private String contenido;
    private int id_anuncio;
    private String anuncio;
    private String nombre_usuario_envio;
    private int telefono_usuario_envio;
    private String reservado;

    public Mensaje(int id, int idEnvio, int idReceptor, int idAnuncio, String anuncio, String contenido, String nombreUsuarioEnvio, int telefonoUsuarioEnvio){

        this.id = id;
        this.id_envio = idEnvio;
        this.id_receptor = idReceptor;
        this.id_anuncio = idAnuncio;
        this.anuncio = anuncio;
        this.contenido = contenido;
        this.nombre_usuario_envio = nombreUsuarioEnvio;
        this.telefono_usuario_envio = telefonoUsuarioEnvio;
    }

    public Mensaje(JSONObject jsonObject) {
        try {
            this.id = jsonObject.getInt("id");
            this.id_envio = jsonObject.getInt("idEnvio");
            this.id_receptor = jsonObject.getInt("idReceptor");
            this.id_anuncio = jsonObject.getInt("idAnuncio");
            this.anuncio = jsonObject.getString("anuncio");
            this.contenido = jsonObject.getString("contenido");
            this.nombre_usuario_envio = jsonObject.getString("nombreUsuarioEnvio");
            this.telefono_usuario_envio = jsonObject.getInt("telefonoUsuarioEnvio");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString(){
        return "\nSoy: " + getNombre_usuario_envio() +
                "\n\nMensaje: " +
                getContenido() +
                "\nMi telefono es: " + getTelefono_usuario_envio() +
                "\nServicio: " + getAnuncio() +
                "\nConfirmado: " + getReservado();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_envio() {
        return id_envio;
    }

    public void setId_envio(int id_envio) {
        this.id_envio = id_envio;
    }

    public int getId_receptor() {
        return id_receptor;
    }

    public void setId_receptor(int id_receptor) {
        this.id_receptor = id_receptor;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public int getId_anuncio() {
        return id_anuncio;
    }

    public void setId_anuncio(int id_anuncio) {
        this.id_anuncio = id_anuncio;
    }

    public String getAnuncio() {
        return anuncio;
    }

    public void setAnuncio(String anuncio) {
        this.anuncio = anuncio;
    }

    public String getNombre_usuario_envio() {
        return nombre_usuario_envio;
    }

    public void setNombre_usuario_envio(String nombre_usuario_envio) {
        this.nombre_usuario_envio = nombre_usuario_envio;
    }

    public int getTelefono_usuario_envio() {
        return telefono_usuario_envio;
    }

    public void setTelefono_usuario_envio(int telefono_usuario_envio) {
        this.telefono_usuario_envio = telefono_usuario_envio;
    }

    public String getReservado() {
        return reservado;
    }

    public void setReservado(Boolean reservado) {
        if (reservado)
            this.reservado = "Si";
        else
            this.reservado = "No";
    }
}
