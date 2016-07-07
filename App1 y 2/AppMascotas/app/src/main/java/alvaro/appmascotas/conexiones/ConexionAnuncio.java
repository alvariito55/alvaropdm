package alvaro.appmascotas.conexiones;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.SystemDefaultCredentialsProvider;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import alvaro.appmascotas.Ip;


public class ConexionAnuncio extends AsyncTask<String,Void,String> {

    private Context context;

    Ip ip = new Ip();

    //PONER LA IP QUE SE USA EN CADA RED PORQUE SINO NO FUNCIONA.
    //Parece que la la IP 10.0.2.2 es como si fuera el localhost en el emulador
    //private String urlAnuncio = "http://10.0.2.2:8080/webService/rest/anuncio/";
    //private String urlAnuncio = "http://192.168.1.11:8080/webService/rest/anuncio/";

    private String urlAnuncio = "http://" + ip.getIp() +":8080/webService/rest/anuncio/";

    public ConexionAnuncio(Context context) {
        this.context = context;
    }

    public void insertarAnuncio(int id_anunciante, String nombre_anunciante, String ciudad, int telefono_anunciante,
                                String anuncio, String fecha_inicio, String fecha_fin){

        HttpClient httpClient = new DefaultHttpClient();

        nombre_anunciante = nombre_anunciante.replace(" ", "+");
        ciudad = ciudad.replace(" ", "+");
        anuncio = anuncio.replace(" ", "+");
        fecha_inicio = fecha_inicio.replace(" ", "+");
        fecha_fin = fecha_fin.replace(" ", "+");

        String urlString = "saveNewAnuncio/" + id_anunciante + "/" + nombre_anunciante + "/"+ ciudad + "/" + telefono_anunciante + "/" + anuncio + "/" + fecha_inicio + "/" + fecha_fin;

        HttpGet del = new HttpGet(urlAnuncio + urlString);
        try {
            del.setHeader("content-type", "application/json");
            HttpResponse resp = httpClient.execute(del);
        }
        catch(Exception ex) {
            Log.e("ServicioRest", "Error!", ex);
        }
    }

    public JSONObject consultarAnunciosId(int id){

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet del = new HttpGet(urlAnuncio + "getAnuncioByIdAnuncianteJSON/" + Integer.toString(id));
        del.setHeader("content-type", "application/json");

        JSONObject anuncio = null;

        try {
            HttpResponse resp = httpClient.execute(del);
            String respStr = EntityUtils.toString(resp.getEntity());
            if (!respStr.equals("null"))
                anuncio = new JSONObject(respStr);
        }
        catch(Exception ex) {
            Log.e("ServicioRest", "Error!", ex);
        }
        return anuncio;
    }

    public JSONObject consultarAnunciosCiudad(int id, String ciudad){

        if (ciudad.contains(" ")){
            ciudad = ciudad.replace(" ", "+");
        }

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet del = new HttpGet(urlAnuncio + "getAnuncioByCiudadJSON/" + id + "/" + ciudad);
        del.setHeader("content-type", "application/json");

        JSONObject anuncio = null;

        System.out.println(ciudad);

        try {
            HttpResponse resp = httpClient.execute(del);
            String respStr = EntityUtils.toString(resp.getEntity());
            if (!respStr.equals("null"))
                anuncio = new JSONObject(respStr);
        }
        catch(Exception ex) {
            Log.e("ServicioRest", "Error!", ex);
        }
        return anuncio;
    }

    public JSONObject borrarAnuncioConId(int id){

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet del = new HttpGet(urlAnuncio + "deleteAnuncio/" + id);
        del.setHeader("content-type", "application/json");

        JSONObject respuesta = null;

        try {
            HttpResponse resp = httpClient.execute(del);
            String respStr = EntityUtils.toString(resp.getEntity());
            respuesta = new JSONObject(respStr);
        }
        catch(Exception ex) {
            Log.e("ServicioRest", "Error!", ex);
        }
        return respuesta;
    }

    public void actualizarMensajeAReservado(int id_anuncio, int id_contratante){

        HttpClient httpClient = new DefaultHttpClient();

        String ocupado = "true";
        String urlString = "updateAnuncio/" + id_anuncio + "/" + ocupado + "/" + id_contratante;

        HttpGet del = new HttpGet(urlAnuncio + urlString);
        try {
            del.setHeader("content-type", "application/json");
            HttpResponse resp = httpClient.execute(del);
        }
        catch(Exception ex) {
            Log.e("ServicioRest", "Error!", ex);
        }
    }

    public JSONObject obtenerAnuncioId(int id){

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet del = new HttpGet(urlAnuncio + "getAnuncioByIdJSON/" + id);
        del.setHeader("content-type", "application/json");

        JSONObject anuncio = null;

        try {
            HttpResponse resp = httpClient.execute(del);
            String respStr = EntityUtils.toString(resp.getEntity());
            if (!respStr.equals("null"))
                anuncio = new JSONObject(respStr);
        }
        catch(Exception ex) {
            Log.e("ServicioRest", "Error!", ex);
        }
        return anuncio;
    }

    protected void onPreExecute(){

    }

    @Override
    protected String doInBackground(String... arg0) {
        try {
            JSONObject datos = new JSONObject(arg0[0]);
            switch (datos.getString("motivo")){
                case "insertar": insertarAnuncio(datos.getInt("id_anunciante"), datos.getString("nombre_anunciante"),datos.getString("ciudad"),datos.getInt("telefono_anunciante"),datos.getString("anuncio"), datos.getString("fecha_inicio"), datos.getString("fecha_fin"));
                    break;
                case "obtenerMisAnuncios":
                    JSONObject anuncios = consultarAnunciosId(datos.getInt("id"));
                    if (anuncios != null)
                        return anuncios.toString();
                    else
                        return "";
                    //break;
                case "obtenerAnunciosCiudad": JSONObject anunciosCiudad = consultarAnunciosCiudad(datos.getInt("id"), datos.getString("ciudad"));
                    if (anunciosCiudad != null)
                        return anunciosCiudad.toString();
                    else
                        return "";
                case "borrarAnuncio": JSONObject respuesta = borrarAnuncioConId(datos.getInt("id"));
                    if (respuesta.toString().contains("not"))
                        return "No se ha borrado el anuncio";
                    else
                        return "Se ha borrado correctamente el anuncio";
                case "actualizarMensajeAReservado":
                    System.out.println("EL QUE CONTRATA ES: " + datos.getInt("idContratante"));
                    actualizarMensajeAReservado(datos.getInt("idAnuncio"), datos.getInt("idContratante"));
                    break;
                case "obtenerAnuncioId": JSONObject anuncioId = obtenerAnuncioId(datos.getInt("id"));
                    if (anuncioId != null)
                        return anuncioId.toString();
                    else
                        return "";
            };
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";



    };

    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[] {};
                    }
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}}
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(String result){
        //System.out.println("result: " + result);

        /*Intent intent = new Intent();

        startActivity(intent);
        finish();*/
    }
}
