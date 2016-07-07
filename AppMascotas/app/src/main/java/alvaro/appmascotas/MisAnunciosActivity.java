package alvaro.appmascotas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

public class MisAnunciosActivity extends AppCompatActivity
            implements NavigationView.OnNavigationItemSelectedListener{

    private ListView li;
    private ArrayList<Anuncio> lista = new ArrayList<>();

    private TextView nombre;
    private TextView correo;


    //PARA EL POPUP
    LayoutInflater layoutInflater;
    View popupView;
    PopupWindow popupWindow;
    Button btn_si;
    Button btn_no;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_misanuncios);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPrueba);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), SubirAnuncioActivity.class);
                startActivity(i);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        li = (ListView)findViewById(R.id.listView_mis_anuncios);


        ArrayAdapter<Anuncio> adap = null;
        try {
            adap = new ArrayAdapter<Anuncio>(getApplicationContext(), R.layout.list_item, cargarListView());
            li.setAdapter(adap);
        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        li.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, final int posicion, long id) {

                li.setEnabled(false);

                    layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    popupView = layoutInflater.inflate(R.layout.activity_pop_up_borrar, null);
                    popupWindow = new PopupWindow(popupView, RadioGroup.LayoutParams.WRAP_CONTENT,
                            RadioGroup.LayoutParams.WRAP_CONTENT);

                    btn_si = (Button)popupView.findViewById(R.id.id_si);
                    btn_si.setOnClickListener(new Button.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        JSONObject datos = new JSONObject();
                        Anuncio a = (Anuncio)li.getAdapter().getItem(posicion);
                        String respuesta = null;
                        try {
                            datos.put("motivo", "borrarAnuncio");
                            datos.put("id", a.getId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            respuesta = new ConexionAnuncio(getApplicationContext()).execute(datos.toString()).get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                        if (respuesta == null){
                            System.out.println("Fallo en la aplicacion al borrar un anuncio");
                        }
                        else if(respuesta.contains("not")){
                            Toast.makeText(getApplicationContext(), "No se ha podido borrar el anuncio", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Se ha borrado correctamente el anuncio", Toast.LENGTH_LONG).show();
                        }
                        ArrayAdapter<Anuncio> adap = null;
                        try {
                            adap = new ArrayAdapter<Anuncio>(getApplicationContext(), R.layout.list_item, cargarListView());
                            li.setAdapter(adap);
                        } catch (JSONException | ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        popupWindow.dismiss();
                        li.setEnabled(true);
                    }});

                btn_no = (Button)popupView.findViewById(R.id.id_no);
                btn_no.setOnClickListener(new Button.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        //Cierra el popup
                        popupWindow.dismiss();
                        li.setEnabled(true);
                    }});

                popupWindow.showAsDropDown(btn_no,50, 400, Gravity.BOTTOM);


            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        ArrayAdapter<Anuncio> adap = null;
        try {
            adap = new ArrayAdapter<Anuncio>(getApplicationContext(), R.layout.list_item, cargarListView());
            li.setAdapter(adap);
        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onStart(){
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("misPreferencias", Context.MODE_PRIVATE);

        if (!prefs.getBoolean("registrado",false)){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }
    }

    private List<Anuncio> cargarListView() throws JSONException, ExecutionException, InterruptedException {

        JSONObject datos = new JSONObject();
        SharedPreferences pref = getSharedPreferences("misPreferencias", Context.MODE_PRIVATE);

            datos.put("motivo", "obtenerMisAnuncios");
            datos.put("id", pref.getInt("id", 0));

            String anunciosString = new ConexionAnuncio(getApplicationContext()).execute(datos.toString()).get();
            if (anunciosString.equals("")){
                Toast.makeText(getApplicationContext(), "Aún no tiene ningún anuncio, pulse en el icono \"+\" para añadir el primero", Toast.LENGTH_LONG).show();
            }
            else {
                lista.clear();
                JSONObject anunciosJson = new JSONObject(anunciosString);

                JSONArray anunciosArray = null;
                if (anunciosJson.getString("anuncios").contains("[") && anunciosJson.getString("anuncios").contains("]")){
                    anunciosArray = new JSONArray(anunciosJson.getString("anuncios"));
                }
                else {
                    anunciosArray = new JSONArray();
                    anunciosArray.put(anunciosJson.get("anuncios"));
                }

                for (int i = 0; i < anunciosArray.length(); i++) {
                    Anuncio a = new Anuncio(anunciosArray.getJSONObject(i));
                    lista.add(a);
                }
            }

        return lista;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SharedPreferences pref = getSharedPreferences("misPreferencias", Context.MODE_PRIVATE);
        nombre = (TextView) findViewById(R.id.textView2_nombre_fijo);
        correo = (TextView) findViewById(R.id.textView_correo_fijo);

        if (!pref.getBoolean("registrado", false)){
            nombre.setText("invitado");
            correo.setText("correo@desconocido");
        }
        else {
            nombre.setText(pref.getString("nombre", "invitado"));
            correo.setText(pref.getString("correo", "correo@desconocido"));
        }

        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.mis_anuncios) {
            Intent i = new Intent(getApplicationContext(), MisAnunciosActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.perfil) {

        } else if (id == R.id.salir){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);

            SharedPreferences pref = getSharedPreferences("misPreferencias", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();

            edit.putBoolean("registrado", false);
            edit.putBoolean("dentro", false);
            edit.putString("nombre", "");
            edit.putString("correo", "");
            edit.putInt("telefono", 0);
            edit.putInt("id",0);
            edit.commit();
            startActivity(i);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
