package alvaro.appmascotas;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import alvaro.appmascotas.conexiones.ConexionUsuario;
import alvaro.appmascotas.servicios.SunshineSyncAdapter;
import alvaro.appmascotas.servicios.SunshineSyncService;


public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private TextView registro;
    private TextView entrarSinRegistro;
    private Button entrar;

    private EditText correo;
    private EditText password;


    /*private View vista;
    private static final int SOLICITUD_PERMISO_LOCALIZACION = 0;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_main);

        registro = (TextView) findViewById(R.id.textView_registro);
        entrarSinRegistro = (TextView) findViewById(R.id.textView_noRegistro);
        entrar = (Button) findViewById(R.id.button_entrar);
        correo = (EditText) findViewById(R.id.editText_correo);
        password = (EditText) findViewById(R.id.editText_password);

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegistroActivity.class);

                startActivity(i);
            }
        });

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isEmpty(correo) || isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "No ha introducido los datos de acceso", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        JSONObject datos = new JSONObject();

                        datos.put("motivo", "login");
                        datos.put("correo", correo.getText().toString());
                        //Comparo la contraseña con la misma pero cifrada
                        datos.put("password", MD5.getMD5(password.getText().toString()));
                        //datos.put("password", password.getText().toString());

                        String existeUsuario = new ConexionUsuario(getApplicationContext()).execute(datos.toString()).get();

                        if (!existeUsuario.equals("")) {

                            //i.putExtra("usuario", existeUsuario);

                            SharedPreferences prefs = getSharedPreferences("misPreferencias", Context.MODE_PRIVATE);

                            //if (!prefs.getBoolean("dentro", false)) {
                                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(i);
                            //}

                            JSONObject usuario = new JSONObject(existeUsuario);


                            SharedPreferences.Editor edit = prefs.edit();
                            edit.putBoolean("registrado", true);
                            edit.putBoolean("dentro", true);
                            edit.putString("nombre", usuario.getString("nombre"));
                            edit.putString("correo", usuario.getString("correo"));
                            edit.putInt("telefono", usuario.getInt("telefono"));
                            edit.putInt("id", usuario.getInt("id"));
                            edit.commit();

                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "El usuario no existe o no ha introducido correctamente la contraseña", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        entrarSinRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), HomeActivity.class);

                SharedPreferences prefs = getSharedPreferences("misPreferencias", Context.MODE_PRIVATE);

                startActivity(i);


                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("registrado", false);
                edit.putBoolean("dentro", true);
                edit.putString("nombre", "invitado");
                edit.putString("correo", "invitado");
                edit.putInt("telefono", 0);
                edit.putInt("id", 0);
                edit.commit();


                finish();
            }
        });
        SunshineSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public void onStart(){
        Log.e(LOG_TAG, "onStart");
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("misPreferencias", Context.MODE_PRIVATE);

        if (!prefs.contains("nombre")){
            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(i);
            finish();
        }
        else if(prefs.getBoolean("registrado", false)){
            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onResume(){
        Log.e(LOG_TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause(){
        Log.e(LOG_TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop(){
        Log.e(LOG_TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        Log.e(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    static public boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();

        SharedPreferences prefs = getSharedPreferences("misPreferencias", Context.MODE_PRIVATE);
        if (prefs.getBoolean("dentro",false)){
            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(i);
            finish();
        }else{
            finish();
        }
    }
}
