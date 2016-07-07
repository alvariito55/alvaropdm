package alvaro.appmascotas;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Permission;
import java.util.List;

/**
 * Created by Alvaro on 5/5/16.
 */
public class Localizacion extends Service implements LocationListener {

    private final Context ctx;
    double latitud;
    double longitud;
    Location location;
    boolean gpsActivo;
    TextView texto;
    EditText textoCiudad;
    String ciudad;
    LocationManager locationManager;

    public Localizacion() {
        super();
        this.ctx = this.getApplicationContext();
    }

    public Localizacion(Context c) {
        super();
        this.ctx = c;
        getLocation();

    }

    public void setView(View v) {
        //textoCiudad = (EditText) v;
        //textoCiudad.setText(ciudad);
        texto = (TextView) v;
        texto.setText(ciudad);
        //texto.setText("Coordenadas: " + latitud + "," + longitud);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void getLocation() {
        try {
            locationManager = (LocationManager) this.ctx.getSystemService(LOCATION_SERVICE);
            gpsActivo = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gpsActivo) {

            //10 = a obtener cada vez que me mueva 10 metros

            //Esto es lo que va a disparar los eventos de cada metodo debajo.

            //if (Manifest.permission.ACCESS_FINE_LOCATION)
            /*locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,
                    1000 * 60,
                    10,this);*/
            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER,
                    0,
                    0,this);

            //location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);

            String direccionText = null;

            if (location != null) {
                latitud = location.getLatitude();
                longitud = location.getLongitude();
            }
            else {
                //LATITUD SOLO PARA CUANDO ESTÁ EN EL EMULADOR YA QUE NO CONSEGUIRÁ SABER CUAL ES.
                latitud = 37.175333;
                longitud = -3.599169;
            }

            setCiudad();
        }
    }

    private void setCiudad(){
        Geocoder geocoder = new Geocoder(ctx);
        List<Address> direcciones = null;

        try {
            direcciones = geocoder.getFromLocation(latitud, longitud,1);
        } catch (Exception e) {
            Log.d("Error", "Error en geocoder:"+e.toString());
        }

        if(direcciones != null && direcciones.size() > 0 ){

            // Creamos el objeto address
            Address direccion = direcciones.get(0);
            // Creamos el string a partir del elemento direccion
                String direccionText = String.format("%s, %s, %s",
                        direccion.getMaxAddressLineIndex() > 0 ? direccion.getAddressLine(0) : "",
                        direccion.getLocality(),
                        direccion.getCountryName());


            ciudad = direccion.getLocality();

            if (ciudad == null){
                ciudad = direccion.getAddressLine(0);
            }
        }
        else{
            ciudad = "Granada";
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


}
