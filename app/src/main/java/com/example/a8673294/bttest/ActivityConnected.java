package com.example.a8673294.bttest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

public class ActivityConnected extends AppCompatActivity implements BluetoothCallback {
    //declaration of the UI components
    Button buttonSend;
    EditText editTextToSend;


    Button st,mode;

    ImageButton av,rc,dr,ga;

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

        mode.setOnClickListener(new View.OnClickListener() {//avance
            @Override
            public void onClick(View view) {


                if(i==0){
                    mode.setText("Mode manuel");
                    Log.d("BT", "mode manuel");
                    Toast.makeText(ActivityConnected.this, "Passage en manuel", Toast.LENGTH_SHORT).show();
                    //BluetoothManager.getInstance().sendData(ActivityConnected.this, avancer);
                    i=1;
                }else{
                    mode.setText("Mode auto");
                    Log.d("BT", "mode automatique");
                    Toast.makeText(ActivityConnected.this, "Passage en auto", Toast.LENGTH_SHORT).show();
                    //BluetoothManager.getInstance().sendData(ActivityConnected.this, avancer);
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
            }
        });

        rc.setOnClickListener(new View.OnClickListener() {//recule
            @Override
            public void onClick(View view) {
                String reculer = "f1r"+'\0';
                BluetoothManager.getInstance().sendData(ActivityConnected.this, reculer);
                Log.d("BT", "recule !!!");
            }
        });

        dr.setOnClickListener(new View.OnClickListener() {//droite
            @Override
            public void onClick(View view) {
                String droite = "f1d"+'\0';
                BluetoothManager.getInstance().sendData(ActivityConnected.this, droite);
                Log.d("BT", "droite !!!");
            }
        });

        ga.setOnClickListener(new View.OnClickListener() {//gauche
            @Override
            public void onClick(View view) {
                String gauche = "f1g"+'\0';
                BluetoothManager.getInstance().sendData(ActivityConnected.this, gauche);
                Log.d("BT", "gauche !!!");
            }
        });


        st.setOnClickListener(new View.OnClickListener() {//stop
            @Override
            public void onClick(View view) {
                String stop = "f1s"+'\0';
                BluetoothManager.getInstance().sendData(ActivityConnected.this, stop);
                Log.d("BT", "stop !!!");
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
}