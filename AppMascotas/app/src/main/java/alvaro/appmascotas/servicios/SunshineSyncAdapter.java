package alvaro.appmascotas.servicios;

/**
 * Created by Alvaro on 22/6/16.
 */
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.Time;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import alvaro.appmascotas.BuildConfig;
import alvaro.appmascotas.R;
import alvaro.appmascotas.MainActivity;
import alvaro.appmascotas.conexiones.ConexionMensaje;

public class SunshineSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String LOG_TAG = SunshineSyncAdapter.class.getSimpleName();

    // Interval at which to sync with the weather, in milliseconds.
    // 60 seconds (1 minute)  180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 1;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;


    private NotificationManager notifyMgr;
    private Context context = null;
    private int id = 0;
    private int numeroActualMensajes = 0;

    public SunshineSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
        SharedPreferences pref = context.getSharedPreferences("misPreferencias", Context.MODE_PRIVATE);
        id = pref.getInt("id", 0);
        numeroActualMensajes = pref.getInt("numeroMensajes", 0);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "onPerformSync Called.");
        Log.d(LOG_TAG, "Starting sync");

        SharedPreferences pref = context.getSharedPreferences("misPreferencias", Context.MODE_PRIVATE);
        id = pref.getInt("id", 0);
        numeroActualMensajes = pref.getInt("numeroMensajes", 0);


        JSONObject datos = new JSONObject();

        String numeroMensajes = "0";

        try {
            datos.put("motivo", "obtenerNumeroMensajes");
            datos.put("id", this.id);

            numeroMensajes =  new ConexionMensaje(context).execute(datos.toString()).get();

            //System.out.println("El numero de mensajes inicial es: " +  numeroMensajes);

        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        int mensajesInt = Integer.parseInt(numeroMensajes);

        System.out.println("NumeroActualMensajes: " + numeroActualMensajes +  "\nMensajes en servidor: " + mensajesInt);

        if (numeroActualMensajes < mensajesInt){
            int numeroMensajesNuevos = mensajesInt - numeroActualMensajes;
            notifyMensaje(
                    1,
                    R.mipmap.fondo,
                    "PETPAD",
                    "¡Tiene " + numeroMensajesNuevos + "mensajes nuevos!"
            );
        }

        return;
    }

    private void notifyMensaje(int id, int iconId, String titulo, String contenido){

        Context context = getContext();

        notifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(iconId)
                        .setLargeIcon(BitmapFactory.decodeResource(
                                context.getResources(),
                                R.mipmap.ic_launcher
                                )
                        )
                        .setContentTitle(titulo)
                        .setContentText(contenido);


        // Construir la notificación y emitirla
        notifyMgr.notify(id, builder.build());

    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));


        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */

            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        SunshineSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
       /*
        * Finally, let's do a sync to get things started
        */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}