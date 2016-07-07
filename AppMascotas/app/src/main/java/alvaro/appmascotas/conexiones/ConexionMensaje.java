package alvaro.appmascotas.conexiones;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import alvaro.appmascotas.Ip;


public class ConexionMensaje extends AsyncTask<String,Void,String> {

    private Context context;

    Ip ip = new Ip();

    private String urlMensaje = "http://" + ip.getIp() +":8080/webService/rest/mensaje/";

    public ConexionMensaje(Context context) {
        this.context = context;
    }

    public JSONObject consultarMisMensajesId(int id){

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet del = new HttpGet(urlMensaje + "getMensajeByIdReceptorJSON/" + Integer.toString(id));
        del.setHeader("content-type", "application/json");

        JSONObject mensaje = null;

        try {
            HttpResponse resp = httpClient.execute(del);
            String respStr = EntityUtils.toString(resp.getEntity());
            if (!respStr.equals("null"))
                mensaje = new JSONObject(respStr);
        }
        catch(Exception ex) {
            Log.e("ServicioRest", "Error!", ex);
        }
        return mensaje;
    }

    public String consultarNumeroMensajesMios(int id){

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet del = new HttpGet(urlMensaje + "getNumeroMensajesByIdJSON/" + Integer.toString(id));
        del.setHeader("content-type", "application/json");

        String numeroMensajes = "0";

        try {
            HttpResponse resp = httpClient.execute(del);
            String respStr = EntityUtils.toString(resp.getEntity());

            numeroMensajes = respStr;
        }
        catch(Exception ex) {
            Log.e("ServicioRest", "Error!", ex);
        }
        return numeroMensajes;
    }

    public void mandarMensaje(int idEnvio, int idReceptor, String contenido, int idAnuncio, String anuncio, String nombreUsuarioEnvio, int telefonoUsuarioEnvio){

                contenido = contenido.replace(" ", "+");
                anuncio = anuncio.replace(" ", "+");
                nombreUsuarioEnvio = nombreUsuarioEnvio.replace(" ", "+");

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet del = new HttpGet(urlMensaje + "saveMensaje/" + idEnvio + "/" + idReceptor + "/" + contenido + "/" +
                idAnuncio + "/" + anuncio + "/" + nombreUsuarioEnvio + "/" + telefonoUsuarioEnvio);
        del.setHeader("content-type", "application/json");

        try {
            HttpResponse resp = httpClient.execute(del);
            String respStr = EntityUtils.toString(resp.getEntity());
        }
        catch(Exception ex) {
            Log.e("ServicioRest", "Error!", ex);
        }
    }

    protected void onPreExecute(){

    }

    @Override
    protected String doInBackground(String... arg0) {

        try {
            JSONObject datos = new JSONObject(arg0[0]);
            switch (datos.getString("motivo")){
                case "obtenerMisMensajes":
                    JSONObject mensajes = consultarMisMensajesId(datos.getInt("id"));
                    if (mensajes != null)
                        return mensajes.toString();
                    else
                        return "";
                case "mandarMensaje": mandarMensaje(datos.getInt("idEnvio"), datos.getInt("idReceptor"), datos.getString("contenido"), datos.getInt("idAnuncio"), datos.getString("anuncio"), datos.getString("nombreUsuarioEnvio"), datos.getInt("telefonoUsuarioEnvio"));
                    break;
                case "obtenerNumeroMensajes": return consultarNumeroMensajesMios(datos.getInt("id"));
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
