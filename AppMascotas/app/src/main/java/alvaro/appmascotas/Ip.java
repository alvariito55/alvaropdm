package alvaro.appmascotas;

/**
 * Created by Alvaro on 17/5/16.
 */
public class Ip {
    String ip;

    public Ip(){
        //Ip cuando se instale la aplicacion en un dispositivo y se quiera acceder a la misma red.
        //ip = "192.168.1.11";

        //Ip para cuando quiera usar el simulador que esta en el emulador.
        ip = "10.0.2.2";
    }

    public String getIp(){
        return ip;
    }
}
