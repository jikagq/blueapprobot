/**
 * @author mathias martinez et theo paris
 *
 * classe permettant de piloter le robot
 *
 *
 *
 *
 */
package com.example.pain.bluetoothrobotapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class ActivityConnected extends AppCompatActivity implements BluetoothCallback {
    //declaration of the UI components
    Button buttonSend;
    EditText editTextToSend;


    Button st,mode;

    ImageButton av,rc,dr,ga;// bouton directionnel

    //RequeteHttp rec = new RequeteHttp();

    //MainActivity ma = new MainActivity();

    private int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        //start the thread responsible of receiving the data from the other device
        BluetoothManager.getInstance().startReadingData(this);

        //pick up the UI components
        editTextToSend = (EditText)findViewById(R.id.editTextToSend);
        buttonSend = (Button)findViewById(R.id.buttonSend);

        mode = (Button) findViewById(R.id.mode);

        st = (Button)findViewById(R.id.st);

        av = (ImageButton)findViewById(R.id.av);
        rc = (ImageButton)findViewById(R.id.rc);
        dr = (ImageButton)findViewById(R.id.dr);
        ga = (ImageButton)findViewById(R.id.ga);



        mode.setText("Mode manuel");

        //when the button is clicked send the data from the EditText
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothManager.getInstance().sendData(ActivityConnected.this,
                        editTextToSend.getText().toString());
            }
        });

        mode.setOnClickListener(new View.OnClickListener() {//change le mode de déplacement du robot
            @Override
            public void onClick(View view) {
                String manu = "f1m0;"+'\0';//f1a\0
                String auto = "f1m1;"+'\0';//f1a\0

                if(i==0){
                    mode.setText("Mode manuel");
                    Log.d("BT", "mode manuel");
                    Toast.makeText(ActivityConnected.this, "Passage en manuel", Toast.LENGTH_SHORT).show();
                    BluetoothManager.getInstance().sendData(ActivityConnected.this, manu);
                    i=1;
                }else{
                    mode.setText("Mode auto");
                    Log.d("BT", "mode automatique");
                    Toast.makeText(ActivityConnected.this, "Passage en auto", Toast.LENGTH_SHORT).show();
                        BluetoothManager.getInstance().sendData(ActivityConnected.this, auto);
                    i=0;
                }

            }
        });


        av.setOnClickListener(new View.OnClickListener() {//avance
            @Override
            public void onClick(View view) {
                String avancer = "f1a"+'\0';//f1a\0
                //BluetoothManager.getInstance().sendData(ActivityConnected.this, editTextToSend.getText().toString());
                BluetoothManager.getInstance().sendData(ActivityConnected.this, avancer);
                Log.d("BT", "avance !!!");
                action("avancer");//envoi l'action pour la methode d'envoi sur la base de donnée
            }
        });

        rc.setOnClickListener(new View.OnClickListener() {//recule
            @Override
            public void onClick(View view) {
                String reculer = "f1r"+'\0';
                BluetoothManager.getInstance().sendData(ActivityConnected.this, reculer);
                Log.d("BT", "recule !!!");
                action("reculer");
            }
        });

        dr.setOnClickListener(new View.OnClickListener() {//droite
            @Override
            public void onClick(View view) {
                String droite = "f1d"+'\0';
                BluetoothManager.getInstance().sendData(ActivityConnected.this, droite);
                Log.d("BT", "droite !!!");
                action("droite");
            }
        });

        ga.setOnClickListener(new View.OnClickListener() {//gauche
            @Override
            public void onClick(View view) {
                String gauche = "f1g"+'\0';
                BluetoothManager.getInstance().sendData(ActivityConnected.this, gauche);
                Log.d("BT", "gauche !!!");
                action("gauche");
            }
        });


        st.setOnClickListener(new View.OnClickListener() {//stop
            @Override
            public void onClick(View view) {
                String stop = "f1s"+'\0';
                BluetoothManager.getInstance().sendData(ActivityConnected.this, stop);
                Log.d("BT", "stop !!!");
                action("stop");
            }
        });




    }

    @Override
    protected void onDestroy() {
        //stop the thread responsible for reading the data.
        BluetoothManager.getInstance().stopReadingData();
        super.onDestroy();
    }


    @Override
    public void onBluetoothConnection(int returnCode) {

    }

    @Override
    public void onBluetoothDiscovery(int returnCode) {

    }

    @Override
    public void onReceiveData(String data) {
        //if it receives a new data, put on a toast on the UIThread
        final String finalData = data;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), finalData,
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * genere l'url a charger pour envoyé sur la base
     *
     *
     *
     *
     *
     * @param action recupère l'action effectuer
     */
    public void action(String action){
        String chaineaenvoyer;
        String lum="0";
        String id="46";
        String timestamp;

        Long tsLong = System.currentTimeMillis()/1000;//recupère l'heure
        timestamp = tsLong.toString();//convertie l'heure en chaine

        //ma.getsytemlum();

        lum = Float.toString(DataModel.getInstance().currentlum);//recupère la luminosité et la convertie en chaine
        Log.d("BT", "test "+lum);
        chaineaenvoyer = "http://cabani.free.fr/ise/adddata.php?idproject="+id+"&lux="+lum+"&timestamp="+timestamp+"&action="+action;//creation de l'url
        new RequeteHttp().execute(chaineaenvoyer);//appele de la fonction pour envoyer


    }


}