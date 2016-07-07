package alvaro.appmascotas;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class PopupFechaHoraActivity extends AppCompatActivity {

    private Button aceptar;

    private DatePicker fecha;
    private TimePicker hora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_fecha_hora);

        aceptar = (Button)findViewById(R.id.button_fechaHora);

        fecha = (DatePicker)findViewById(R.id.datePicker_fecha);
        hora = (TimePicker)findViewById(R.id.timePicker_hora);

        aceptar.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                //HAGO ESTO PORQUE EL MES DE ENERO ME LO TOMA COMO EL MES 0 Y QUIERO QUE SEA EL 1
                int fechaMes = fecha.getMonth() +1;
                String cad = fecha.getYear() + "-" + fechaMes + "-" + fecha.getDayOfMonth() + " " +
                        hora.getHour() + ":" + hora.getMinute() + ":00";
                Intent data = new Intent();
                data.setData(Uri.parse(cad));
                setResult(RESULT_OK, data);
                finish();
            }
        });



    }
}
