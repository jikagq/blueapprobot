package com.example.a8673294.bttest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    Switch mode;
    Button st;

    ImageButton av,rc,dr,ga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        //start the thread responsible of receiving the data from the other device
        BluetoothManager.getInstance().startReadingData(this);

        //pick up the UI components
        editTextToSend = (EditText)findViewById(R.id.editTextToSend);
        buttonSend = (Button)findViewById(R.id.buttonSend);

        mode = (Switch)findViewById(R.id.mode);

        st = (Button)findViewById(R.id.st);

        av = (ImageButton)findViewById(R.id.av);
        rc = (ImageButton)findViewById(R.id.rc);
        dr = (ImageButton)findViewById(R.id.dr);
        ga = (ImageButton)findViewById(R.id.ga);


        //when the button is clicked send the data from the EditText
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothManager.getInstance().sendData(ActivityConnected.this,
                        editTextToSend.getText().toString());
            }
        });

        //mettre le switch


        av.setOnClickListener(new View.OnClickListener() {//avance
            @Override
            public void onClick(View view) {
                String avancer = "f1a";//f1a\0
                //BluetoothManager.getInstance().sendData(ActivityConnected.this, editTextToSend.getText().toString());
                BluetoothManager.getInstance().sendData(ActivityConnected.this, avancer);
            }
        });

        rc.setOnClickListener(new View.OnClickListener() {//recule
            @Override
            public void onClick(View view) {
                String reculer = "f1r";
                BluetoothManager.getInstance().sendData(ActivityConnected.this, reculer);
            }
        });

        dr.setOnClickListener(new View.OnClickListener() {//droite
            @Override
            public void onClick(View view) {
                String droite = "f1d";
                BluetoothManager.getInstance().sendData(ActivityConnected.this, droite);
            }
        });

        ga.setOnClickListener(new View.OnClickListener() {//gauche
            @Override
            public void onClick(View view) {
                String gauche = "f1g";
                BluetoothManager.getInstance().sendData(ActivityConnected.this, gauche);
            }
        });


        st.setOnClickListener(new View.OnClickListener() {//stop
            @Override
            public void onClick(View view) {
                String stop = "f1s";
                BluetoothManager.getInstance().sendData(ActivityConnected.this, stop);
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