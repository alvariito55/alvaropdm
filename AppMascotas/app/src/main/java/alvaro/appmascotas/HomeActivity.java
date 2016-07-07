package alvaro.appmascotas;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import alvaro.appmascotas.conexiones.ConexionAnuncio;
import alvaro.appmascotas.conexiones.ConexionMensaje;
import alvaro.appmascotas.servicios.SunshineSyncAdapter;
import alvaro.appmascotas.servicios.SunshineSyncService;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

        private TextView nombre;
        private TextView correo;

        private EditText ciudad;

        private Button buscar;
        private Button buscar_ubicacion;


        private ListView li;
        private ArrayList<Anuncio> lista = new ArrayList<>();
        private ArrayAdapter adap = null;

        private View vista;
        private static final int SOLICITUD_PERMISO_LOCALIZACION = 0;



    //PARA EL POPUP
    LayoutInflater layoutInflater;
    View popupView;
    PopupWindow popupWindow;
    Button btn_si;
    Button btn_no;
    TextView mensaje;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Para el icono del mensaje
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences("misPreferencias", Context.MODE_PRIVATE);

                if (prefs.getBoolean("registrado", false)){
                    Intent i = new Intent(getApplicationContext(), MisMensajesActivity.class);
                    startActivity(i);
                }
                else {
                    Snackbar.make(view, "Registrese para acceder a sus mensajes", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        vista = findViewById(R.id.content_home);

        li = (ListView)findViewById(R.id.listView_todos_los_anuncios);
        ciudad = (EditText)findViewById(R.id.editText_buscar_anuncio);

        buscar_ubicacion = (Button)findViewById(R.id.button_buscar_ubicacion);

        buscar_ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chekearPermisosYObtenerLocalizacionYLista();
            }
        });



        buscar = (Button)findViewById(R.id.button_buscar);
        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(ciudad)){
                    Toast.makeText(getApplicationContext(), "Introduce una ciudad para buscar anuncios.", Toast.LENGTH_LONG).show();
                }
                else{
                    adap = null;
                    try {
                        adap = new ArrayAdapter<Anuncio>(getApplicationContext(), R.layout.list_item, cargarListView(ciudad.getText().toString()));
                        li.setAdapter(adap);
                    } catch (JSONException | ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        });

        SharedPreferences prefs = getSharedPreferences("misPreferencias", Context.MODE_PRIVATE);

        if (!prefs.contains("nombre")){
            //Si no existe esta variable, creare todas, esto se ejecuta la primera vez que se instale la app.
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("registrado", false);
            edit.putBoolean("dentro", true);
            edit.putString("nombre", "invitado");
            edit.putString("correo", "correo@desconocido.com");
            edit.putInt("telefono", 0);
            edit.putInt("id", 0);
            edit.putInt("numeroMensajes", 0);
            edit.commit();
        }



            li.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, final int posicion, long id) {

                    SharedPreferences prefs = getSharedPreferences("misPreferencias", Context.MODE_PRIVATE);

                    if (prefs.getBoolean("registrado", false)) {
                        Anuncio a = (Anuncio) li.getAdapter().getItem(posicion);
                        if (!a.getOcupado()) {
                            li.setEnabled(false);

                            layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                            popupView = layoutInflater.inflate(R.layout.activity_pop_up_mandar_mensaje, null);
                            popupWindow = new PopupWindow(popupView, RadioGroup.LayoutParams.WRAP_CONTENT,
                                    RadioGroup.LayoutParams.WRAP_CONTENT);

                            mensaje = (TextView) popupView.findViewById(R.id.textView_desea_mandarMensaje);

                            //SharedPreferences prefs = getSharedPreferences("misPreferencias", Context.MODE_PRIVATE);

                            mensaje.setText("Desea mandar el siguiente mensaje al usuario: " + a.getNombre_anunciante() + "\nSoy: " +
                                    prefs.getString("nombre", "invitado") + "\n\n" +
                                    "Pongase en contacto conmigo en el siguiente telefono para acordar las condiciones del servicio.\n" +
                                    "Telefono: " + prefs.getInt("telefono", 0) + "\n" + "" +
                                    "Servicio: " + a.getAnuncio());

                            btn_si = (Button) popupView.findViewById(R.id.id_mandarMensaje);
                            btn_si.setOnClickListener(new Button.OnClickListener() {

                                @Override
                                public void onClick(View v) {

                                    JSONObject datos = new JSONObject();
                                    String respuesta = null;
                                    try {
                                        Anuncio a = (Anuncio) li.getAdapter().getItem(posicion);
                                        SharedPreferences prefs = getSharedPreferences("misPreferencias", Context.MODE_PRIVATE);
                                        String contenido = "Pongase en contacto conmigo en el siguiente telefono para acordar las condiciones del servicio.";
                                        datos.put("motivo", "mandarMensaje");
                                        datos.put("idEnvio", prefs.getInt("id", 0));
                                        datos.put("idReceptor", a.getId_anunciante());
                                        datos.put("idAnuncio", a.getId());
                                        datos.put("anuncio", a.getAnuncio());
                                        datos.put("contenido", contenido);
                                        datos.put("nombreUsuarioEnvio", prefs.getString("nombre", "invitado"));
                                        datos.put("telefonoUsuarioEnvio", prefs.getInt("telefono", 0));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        new ConexionMensaje(getApplicationContext()).execute(datos.toString()).get();
                                    } catch (InterruptedException | ExecutionException e) {
                                        e.printStackTrace();
                                    }

                                    popupWindow.dismiss();
                                    li.setEnabled(true);
                                }
                            });

                            btn_no = (Button) popupView.findViewById(R.id.id_noMandarMensaje);
                            btn_no.setOnClickListener(new Button.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    //Cierra el popup
                                    popupWindow.dismiss();
                                    li.setEnabled(true);
                                }
                            });
                            popupWindow.showAsDropDown(btn_no, 50, 100, Gravity.BOTTOM);
                        } else {
                            Toast.makeText(getApplicationContext(), "Este servicio ya esta reservado por otro usuario", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);

                        System.out.println("INTENTA VOLVER AL MAIN");
                        startActivity(i);
                    }
                }

            });
    }

    private void chekearPermisosYObtenerLocalizacionYLista() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Localizacion localizacion = new Localizacion(getApplicationContext());

            localizacion.setView(ciudad);

            adap = null;
            try {
                adap = new ArrayAdapter<Anuncio>(getApplicationContext(), R.layout.list_item, cargarListView(ciudad.getText().toString()));
                li.setAdapter(adap);

            } catch (JSONException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            solicitarPermisoLocalizacion();
        }
    }

    private List<Anuncio> cargarListView(String ciudad) throws JSONException, ExecutionException, InterruptedException {
        JSONObject datos = new JSONObject();
        lista.clear();
        SharedPreferences pref = getSharedPreferences("misPreferencias", Context.MODE_PRIVATE);
            datos.put("motivo", "obtenerAnunciosCiudad");
            datos.put("ciudad", ciudad);
            datos.put("id", pref.getInt("id",0));

            String anunciosString = new ConexionAnuncio(getApplicationContext()).execute(datos.toString()).get();
            if (anunciosString.equals("")){
                Toast.makeText(getApplicationContext(), "No se ha encontrado ningún anuncio en su zona.", Toast.LENGTH_LONG).show();
            }
            else {
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
    public void onResume(){
        super.onResume();
    }

    static boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.mis_anuncios) {
            SharedPreferences pref = getSharedPreferences("misPreferencias", Context.MODE_PRIVATE);

            if (pref.getBoolean("registrado", false)){
                Intent i = new Intent(getApplicationContext(), MisAnunciosActivity.class);
                startActivity(i);
                finish();
            }
            else{
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }

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
    private void solicitarPermisoLocalizacion() {

        ActivityCompat.requestPermissions(HomeActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                SOLICITUD_PERMISO_LOCALIZACION);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == SOLICITUD_PERMISO_LOCALIZACION) {
            if (grantResults.length== 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Localizacion localizacion = new Localizacion(getApplicationContext());

                localizacion.setView(ciudad);


                adap = null;
                try {
                    adap = new ArrayAdapter<Anuncio>(getApplicationContext(), R.layout.list_item, cargarListView(ciudad.getText().toString()));
                    li.setAdapter(adap);
                } catch (JSONException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

            } else {
                Snackbar.make(vista, "Sin el permiso, no puedo acceder a la localización", Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}
