package alvaro.appmascotas.conexiones;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import alvaro.appmascotas.Ip;


public class ConexionUsuario extends AsyncTask<String,Void,String> {

    private Context context;

    Ip ip = new Ip();

    //PONER LA IP QUE SE USA EN CADA RED PORQUE SINO NO FUNCIONA.
    //Parece que la la IP 10.0.2.2 es como si fuera el localhost en el emulador
    //private String urlUsuario = "http://10.0.2.2:8080/webService/rest/usuario/";
    //private String urlUsuario = "http://192.168.1.11:8080/webService/rest/usuario/";

    private String urlUsuario = "http://" + ip.getIp() +":8080/webService/rest/usuario/";




    public ConexionUsuario(Context context) {
        this.context = context;
    }

    public void insertarUsuario(String nombre, String correo, int telefono, String password){

        if (nombre.contains(" "))
            nombre = nombre.replace(" ", "+");

        HttpClient httpClient = new DefaultHttpClient();
        String tel = Integer.toString(telefono);
        HttpGet del = new HttpGet(urlUsuario + "saveUsuario/" + nombre + "/" + correo + "/" + tel + "/" + password);
        del.setHeader("content-type", "application/json");
        try {
            HttpResponse resp = httpClient.execute(del);
        }
        catch(Exception ex) {
            Log.e("ServicioRest", "Error!", ex);
        }
    }

    public JSONObject consultarUsuarioId(int id){

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet del = new HttpGet(urlUsuario + "getUsuarioByIdJSON/" + Integer.toString(id));
        del.setHeader("content-type", "application/json");

        JSONObject usuario = null;

        try {
            HttpResponse resp = httpClient.execute(del);
            String respStr = EntityUtils.toString(resp.getEntity());
            usuario = new JSONObject(respStr);
        }
        catch(Exception ex) {
            Log.e("ServicioRest", "Error!", ex);
        }
        return usuario;
    }

    public JSONObject consultarUsuarioCorreo(String correo){

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet del = new HttpGet(urlUsuario + "getUsuarioByCorreoJSON/" + correo);
        del.setHeader("content-type", "application/json");

        JSONObject usuario = null;

        try {
            HttpResponse resp = httpClient.execute(del);
            if (resp.getEntity() != null) {
                String respStr = EntityUtils.toString(resp.getEntity());
                usuario = new JSONObject(respStr);
            }
        }
        catch(Exception ex) {
            Log.e("ServicioRest", "Error!", ex);
        }
        return usuario;
    }

    public JSONObject comprobarCorreoConPassword(String correo, String password){

        JSONObject usuario = null;

        usuario = consultarUsuarioCorreo(correo);

        if (usuario == null){
            System.out.println("No existe ningun usuario con ese correo");
        }
        else{
            try {
                if (!usuario.getString("password").equals(password)){
                    System.out.println("El correo no coincide con la contraseña");
                    usuario = null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return usuario;
    }

    protected void onPreExecute(){

    }

    @Override
    protected String doInBackground(String... arg0) {

        try {
            JSONObject datos = new JSONObject(arg0[0]);
            switch (datos.getString("motivo")){
                case "insertar": insertarUsuario(datos.getString("nombre"), datos.getString("correo"),datos.getInt("telefono"),datos.getString("password"));
                    break;
                case "consultarId": JSONObject usuario = consultarUsuarioId(datos.getInt("id"));
                    System.out.println("Nombre: " + usuario.getString("nombre") + ", Correo: " + usuario.getString("correo"));
                    break;
                case "consultarCorreo": JSONObject existeusuario = consultarUsuarioCorreo(datos.getString("correo"));
                    if (existeusuario == null)
                        return "";
                    else
                        return existeusuario.toString();
                case "login": JSONObject usuarioYPassword = comprobarCorreoConPassword(datos.getString("correo"), datos.getString("password"));
                    if (usuarioYPassword == null)
                        return "";
                    else
                        return usuarioYPassword.toString();
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
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[] {};
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
    }
}
