package alvaro.appmascotas;

import android.Manifest;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.DateTimeKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import alvaro.appmascotas.conexiones.ConexionAnuncio;

public class SubirAnuncioActivity extends AppCompatActivity {

    private EditText fechaHora_inicio;
    private EditText fechaHora_final;
    private Button aniadir;
    private EditText anuncio;
    private EditText ciudad;

    int request_code = 1;

    private Boolean fecha_inicio = false;
    private Boolean fecha_fin = false;

    private View vista;
    private static final int SOLICITUD_PERMISO_LOCALIZACION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_anuncio);


        fechaHora_inicio = (EditText) findViewById(R.id.editText_fecha_inicio);
        fechaHora_final = (EditText) findViewById(R.id.editText_fecha_fin);
        aniadir = (Button)findViewById(R.id.button_aniadir);
        anuncio = (EditText) findViewById(R.id.editText_anuncio);
        ciudad = (EditText) findViewById(R.id.editText_ciudad);

        vista = findViewById(R.id.content_subir_anuncios);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            Localizacion localizacion = new Localizacion(getApplicationContext());

            localizacion.setView(findViewById(R.id.editText_ciudad));

        } else {
            System.out.println("Esta entrando por donde no da permiso");
            solicitarPermisoLocalizacion();
        }


        fechaHora_inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fecha_inicio = true;
                fecha_fin = false;

                Intent i = new Intent(getApplicationContext(), PopupFechaHoraActivity.class);

                startActivityForResult(i, request_code);
                System.out.println("El codigo antes de mandarlo es: " + request_code);
            }
        });

        fechaHora_final.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fecha_inicio = false;
                fecha_fin = true;

                Intent i = new Intent(getApplicationContext(), PopupFechaHoraActivity.class);

                startActivityForResult(i, request_code);
            }
        });

        aniadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), HomeActivity.class);


                if (MainActivity.isEmpty(fechaHora_inicio) || MainActivity.isEmpty(fechaHora_final) ||
                        MainActivity.isEmpty(ciudad) || MainActivity.isEmpty(anuncio)){
                    Toast.makeText(getApplicationContext(), "Debe rellernar todos los campos", Toast.LENGTH_LONG).show();
                }
                else{
                    SharedPreferences pref = getSharedPreferences("misPreferencias", Context.MODE_PRIVATE);
                    JSONObject datos = new JSONObject();
                    //SimpleDateFormat formatoDeFecha = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
                    try {
                        datos.put("motivo", "insertar");
                        datos.put("id_anunciante", pref.getInt("id",0));
                        datos.put("nombre_anunciante", pref.getString("nombre", "invitado"));
                        datos.put("ciudad", ciudad.getText());
                        datos.put("telefono_anunciante", pref.getInt("telefono", 0));
                        datos.put("anuncio", anuncio.getText());
                        datos.put("fecha_inicio", fechaHora_inicio.getText());
                        datos.put("fecha_fin", fechaHora_final.getText());

                        //formatoDeFecha.format(new Date()) //Para conseguir una fecha en String.
                        new ConexionAnuncio(getApplicationContext()).execute(datos.toString());

                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == request_code) && (resultCode == RESULT_OK)){
            if (fecha_inicio){
                fechaHora_inicio.setText(data.getDataString());
            }
            else if (fecha_fin){
                fechaHora_final.setText(data.getDataString());
            }
        }
        else{
            if (fecha_inicio){
                fechaHora_inicio.setText(data.getDataString());
            }
            else if (fecha_fin){
                fechaHora_final.setText(data.getDataString());
            }
        }
    }

    private void solicitarPermisoLocalizacion() {

        System.out.println("Ha entrado en dar permisos");

        ActivityCompat.requestPermissions(SubirAnuncioActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                SOLICITUD_PERMISO_LOCALIZACION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == SOLICITUD_PERMISO_LOCALIZACION) {
            if (grantResults.length== 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Snackbar.make(vista, "Sin el permiso, no puedo acceder a la localización", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

}
