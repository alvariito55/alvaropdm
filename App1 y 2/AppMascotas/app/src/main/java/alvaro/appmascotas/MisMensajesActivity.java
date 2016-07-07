package alvaro.appmascotas;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import alvaro.appmascotas.conexiones.ConexionAnuncio;
import alvaro.appmascotas.conexiones.ConexionMensaje;

public class MisMensajesActivity extends AppCompatActivity {

    private ListView li;
    private ArrayList<Mensaje> lista = new ArrayList<>();

    private NotificationManager notifyMgr;

    //PARA EL POPUP
    LayoutInflater layoutInflater;
    View popupView;
    PopupWindow popupWindow;
    Button btn_si;
    Button btn_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_mensajes);

        li = (ListView)findViewById(R.id.listView_mis_mensajes);


        ArrayAdapter<Mensaje> adap = null;
        try {
            adap = new ArrayAdapter<Mensaje>(getApplicationContext(), R.layout.list_item, cargarListView());
            li.setAdapter(adap);
        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        li.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, final int posicion, long id) {


                li.setEnabled(false);

                layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                popupView = layoutInflater.inflate(R.layout.activity_pop_up_confirmar_servicio, null);
                popupWindow = new PopupWindow(popupView, RadioGroup.LayoutParams.WRAP_CONTENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT);


                btn_si = (Button) popupView.findViewById(R.id.id_si_deseo);
                btn_si.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        JSONObject datos = new JSONObject();
                        String respuesta = null;
                        try {
                            Mensaje m = (Mensaje) li.getAdapter().getItem(posicion);
                            datos.put("motivo", "actualizarMensajeAReservado");
                            datos.put("idAnuncio", m.getId_anuncio());
                            datos.put("idContratante", m.getId_envio());
                            System.out.println("El contratante es: " + m.getId_envio());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try{
                            new ConexionAnuncio(getApplicationContext()).execute(datos.toString()).get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }

                        popupWindow.dismiss();

                        ArrayAdapter<Mensaje> adap = null;
                        try {
                            adap = new ArrayAdapter<Mensaje>(getApplicationContext(), R.layout.list_item, cargarListView());
                            li.setAdapter(adap);
                        } catch (JSONException | ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }

                        li.setEnabled(true);
                    }
                });

                btn_no = (Button) popupView.findViewById(R.id.id_no_deseo);
                btn_no.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //Cierra el popup
                        popupWindow.dismiss();
                        li.setEnabled(true);
                    }
                });

                popupWindow.showAsDropDown(btn_no, 50, 300, Gravity.BOTTOM);
            }

        });

    }

    private List<Mensaje> cargarListView() throws JSONException, ExecutionException, InterruptedException {

        JSONObject datos = new JSONObject();
        SharedPreferences pref = getSharedPreferences("misPreferencias", Context.MODE_PRIVATE);

        datos.put("motivo", "obtenerMisMensajes");
        datos.put("id", pref.getInt("id", 0));

        String mensajesString = new ConexionMensaje(getApplicationContext()).execute(datos.toString()).get();
        if (mensajesString.equals("")){
            Toast.makeText(getApplicationContext(), "Aún no tiene ningún mensaje de sus anuncios.", Toast.LENGTH_LONG).show();
        }
        else {
            lista.clear();
            JSONObject mensajesJson = new JSONObject(mensajesString);

            JSONArray mensajesArray = null;
            if (mensajesJson.getString("mensajes").contains("[") && mensajesJson.getString("mensajes").contains("]")){
                mensajesArray = new JSONArray(mensajesJson.getString("mensajes"));
            }
            else {
                mensajesArray = new JSONArray();
                mensajesArray.put(mensajesJson.get("mensajes"));
            }

            for (int i = 0; i < mensajesArray.length(); i++) {
                Mensaje m = new Mensaje(mensajesArray.getJSONObject(i));
                JSONObject datos2 = new JSONObject();
                datos2.put("motivo", "obtenerAnuncioId");
                datos2.put("id", m.getId_anuncio());
                String anuncioId = new ConexionAnuncio(getApplicationContext()).execute(datos2.toString()).get();
                System.out.println("El anuncio es: " + anuncioId);
                JSONObject anuncioJson = new JSONObject(anuncioId);
                if (m.getId_envio() == anuncioJson.getInt("idContratado"))
                    m.setReservado(anuncioJson.getBoolean("ocupado"));
                else{
                    m.setReservado(false);
                }

                lista.add(m);
            }
            JSONObject datos3 = new JSONObject();
            datos3.put("motivo", "obtenerNumeroMensajes");
            datos3.put("id", pref.getInt("id",0));
            String numeroMensajes = new ConexionMensaje(getApplicationContext()).execute(datos3.toString()).get();
            SharedPreferences.Editor edit = pref.edit();
            edit.putInt("numeroMensajes", Integer.parseInt(numeroMensajes));
            edit.commit();
        }
        return lista;
    }
}
