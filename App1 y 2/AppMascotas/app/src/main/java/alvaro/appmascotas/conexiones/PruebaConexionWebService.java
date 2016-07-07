package alvaro.appmascotas.conexiones;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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
import org.json.JSONObject;


/**
 * Created by Alvaro on 15/4/16.
 */
    public class PruebaConexionWebService extends AsyncTask<String,Void,String> {

        private TextView statusField;
        private Context context;

        public PruebaConexionWebService(Context context) {
            this.context = context;
        }

        protected void onPreExecute(){

        }

    @Override
    protected String doInBackground(String... arg0) {

        //Para hacer mediante WebService
        HttpClient httpClient = new DefaultHttpClient();
        //String id = txtId.getText().toString();
        HttpGet del = new HttpGet("http://192.168.1.18:8080/webService/rest/usuario/getUsuarioByIdJSON/3");
        del.setHeader("content-type", "application/json");
        try {
            HttpResponse resp = httpClient.execute(del);
            String respStr = EntityUtils.toString(resp.getEntity());
            JSONObject respJSON = new JSONObject(respStr);
            int idCli = respJSON.getInt("id");
            String nombCli = respJSON.getString("nombre");
            int telefCli = respJSON.getInt("telefono");
            System.out.println("" + idCli + "-" + nombCli + "-" + telefCli);
            //lblResultado.setText("" + idCli + "-" + nombCli + "-" + telefCli);
        }
        catch(Exception ex) {
            Log.e("ServicioRest", "Error!", ex);
        }
        return "";


        /*HttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://localhost:8080/webService/usuario/getAllUsuariosInJson");
        post.setHeader("content-type", "application/json");

        JSONObject dato = new JSONObject();
        dato.put("nombre", "alvaro");
        dato.put("Telefono", Integer.parseInt(txtTelefono.getText().toString()));
        StringEntity entity = new StringEntity(dato.toString());
        post.setEntity(entity);
        */


/*
        final String URL = "jdbc:mysql://localhost:8080/webService/rest/usuario/getAllUsuariosInJson";
        final String USER = "root";
        final String PASSWORD = "root";

        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        }catch(Exception e){
            System.err.println("Cannot create connection");
        }
        try{
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement statement = connection.createStatement();

            System.out.println("Se ha conectado bien.");
            String query = "SELECT nombre, password FROM usuarios";// WHERE Name = ";
            //query = query +"'" +"test"+"'";
            ResultSet result = statement.executeQuery(query);
            // data es el objeto ResultSet
            while(result.next()) {

                // Ya dentro del while obtener los datos
                System.out.println("Nombre: " + result.getString("nombre"));
                System.out.println("Pass: " + result.getString("password"));
            }
        }catch(Exception e){
            System.err.println("Error: " + e.getMessage().toString());
        }
        return "";
*/

        //PARA HACER MEDIANTE EL DRIVER Y sin webService
        /*final String URL = "jdbc:mysql://192.168.1.18:8889/appMascotas";
        final String USER = "root";
        final String PASSWORD = "root";
        //final String username = (String)arg0[0];
        //final String password = (String)arg0[1];
        //final String urlWeb = (String)arg0[2];

        Connection connection = null;

        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        }catch(Exception e){
            System.err.println("Cannot create connection");
        }
        try{
            //Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement statement = connection.createStatement();

            String query = "SELECT nombre, password FROM usuarios";// WHERE Name = ";
            //query = query +"'" +"test"+"'";
            ResultSet result = statement.executeQuery(query);
            // data es el objeto ResultSet
            while(result.next()) {

                // Ya dentro del while obtener los datos
                System.out.println("Nombre: " + result.getString("nombre"));
                System.out.println("Pass: " + result.getString("password"));
            }
            }catch(Exception e){
                System.err.println("Error: " + e.getMessage().toString());
            }
        return "";

        }

        // always verify the host - dont check for certificate
        final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }*/
        };

        /**
         * Trust every server - dont check for any certificate
         */
        private static void trustAllHosts() {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[] {};
                }

                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }
            } };

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
            System.out.println("result: " + result);

        /*Intent intent = new Intent();

        
        startActivity(intent);
        finish();*/
        }
    }