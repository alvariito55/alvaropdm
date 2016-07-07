package alvaro.appmascotas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import alvaro.appmascotas.conexiones.ConexionUsuario;

public class RegistroActivity extends AppCompatActivity {

    private Button registrarme;
    private EditText nombre;
    private EditText correo;
    private EditText telefono;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        registrarme = (Button)findViewById(R.id.button_registrarme);

        nombre = (EditText)findViewById(R.id.editText_nombre_registro);
        correo = (EditText)findViewById(R.id.editText_correo_registro);
        telefono = (EditText)findViewById(R.id.editText2_telefono);
        password = (EditText)findViewById(R.id.editText_password_registro);


        registrarme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);

                if (isEmpty(nombre) || isEmpty(correo) || isEmpty(telefono) || isEmpty(password)){

                    Toast.makeText(getApplicationContext(), "Ha dejado alguno de los campos vacios", Toast.LENGTH_LONG).show();

                    System.out.println("Ha dejado alguno de los campos vacios");
                }
                else {
                    try {
                        JSONObject datos = new JSONObject();
                        datos.put("motivo", "consultarCorreo");

                        datos.put("nombre", nombre.getText().toString());
                        datos.put("correo", correo.getText().toString());
                        datos.put("telefono", telefono.getText().toString());
                        //Meto la contraseña en la BD cifrada.
                        datos.put("password", MD5.getMD5(password.getText().toString()));
                        //datos.put("password", password.getText().toString());

                        String existeUsuario = new ConexionUsuario(getApplicationContext()).execute(datos.toString()).get();

                        if (existeUsuario.equals("")){
                            datos.put("motivo", "insertar");
                            new ConexionUsuario(getApplicationContext()).execute(datos.toString());
                            Toast.makeText(getApplicationContext(), "Se Ha registrado correctamente", Toast.LENGTH_LONG).show();
                            startActivity(i);
                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "El correo elegido ya existe, elija otro porfavor", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }
    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}
