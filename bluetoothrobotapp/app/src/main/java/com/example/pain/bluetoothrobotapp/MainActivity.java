/**
 * Classe principale (ecran paramètres)
*@author mathias martinez et theo paris
*/
package com.example.pain.bluetoothrobotapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.sql.SQLClientInfoException;
import java.util.List;

import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
import static android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;

public class MainActivity extends AppCompatActivity implements BluetoothCallback{
    //declaration of the UI components
    ToggleButton toggleButtonBluetooth,toggleButtonVisible;
    ProgressBar spinnerDiscovering;
    Button buttonConnect, about;
    SeekBar seekbar;//selection luminosité manuel
    CheckBox check;//case pour le mode de luminosité
    ListView list;//liste des appareil bluetooth a proximité

    private int progress = 0;//etat de la barre
    private boolean mode;//mode luminosité
    //private float currentlum;
    private Context mContext;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();
        //initialize the bluetooth with the activity,
        // an UUID THAT YOU MUST GENERATE IN https://www.uuidgenerator.net/
        //and a unique name (that you also must change it!!!
        //BluetoothManager.getInstance().initializeBluetooth(this, "00001101-0000-1000-8000-00805F9B34FB","HEALTH_MODULE_1");
        BluetoothManager.getInstance().initializeBluetooth(this, "00001101-0000-1000-8000-00805F9B34FB","RNBT-B71E");


        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M)
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1001);//this for 6.0.1


        //all the functions from BluetoothManager must be accessible by using getInstance in
        // order to use the singleton

        //Hint: If you are connecting to a Bluetooth serial board then try using the well-known
        // SPP UUID 00001101-0000-1000-8000-00805F9B34FB.
        // However if you are connecting to an Android peer then please generate your own unique UUID.


        //pick up the UI components
        buttonConnect = (Button)findViewById(R.id.buttonConnect);
        toggleButtonBluetooth = (ToggleButton)findViewById(R.id.toggleButtonBluetooth);
        spinnerDiscovering = (ProgressBar)findViewById(R.id.progressBarDiscover);
        toggleButtonVisible = (ToggleButton)findViewById(R.id.toggleButtonVisible);

        about = (Button)findViewById(R.id.about);
        check = (CheckBox)findViewById(R.id.check);
        list = (ListView)findViewById(R.id.list);

        int brightness = getScreenBrightness();//recupère la luminosité actuelle de l'ecran
        seekbar = (SeekBar)findViewById(R.id.seekbar);
        seekbar.setMax(255);//regle le maximum de la barre
        seekbar.setProgress(brightness);//regle le curseur à la luminosité actuelle

        //set the toogleButton that will show how is the state of the bluetooth to the correct state
        toggleButtonBluetooth.setChecked(BluetoothManager.getInstance().isBluetoothOn());

        //on the click will turn on or off the bluetooth device
        toggleButtonBluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    BluetoothManager.getInstance().turnOnBluetooth(MainActivity.this);
                }else{
                    BluetoothManager.getInstance().turnOffBluetooth();
                }
            }
        });

        //on the click will turn on the discovery mode on the device for 10 seconds
        toggleButtonVisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!BluetoothManager.getInstance().isBluetoothOn()){
                        Toast.makeText(MainActivity.this,"The BT device is OFF!",
                                Toast.LENGTH_SHORT).show();
                        toggleButtonVisible.setChecked(false);
                        return;
                    }
                    BluetoothManager.getInstance().makeBluetoothDiscoverable(MainActivity
                            .this,10,MainActivity
                            .this);
                    toggleButtonVisible.setEnabled(false);
                    spinnerDiscovering.setVisibility(View.VISIBLE);
                    buttonConnect.setVisibility(View.INVISIBLE);
                }else{

                }
            }
        });

        //on the click will start the connect mode
        //one device must be on the Discovey mode... and another in this connect mode!
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!BluetoothManager.getInstance().isBluetoothOn()) {
                    Toast.makeText(MainActivity.this, "The BT device is OFF!", Toast.LENGTH_SHORT).show();
                    return;
                }
                BluetoothManager.getInstance().startDiscover(MainActivity.this);
                Log.d("BT","startDiscover");

                spinnerDiscovering.setVisibility(View.VISIBLE);

            }
        });
///////////////////////////////////////////////////////////////////////////////////////

        /**
         * bouton à propos
         *
         *
         */
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(MainActivity.this,Main2Activity.class);
                startActivity(intent2);// lance l'activité main2activity (à propos)
            }
        });

        //this.listedescpateurs();//affiche la liste des capteurs de l'appareil
        this.getsytemlum();//recupère la luminosité actuelle de l'ecran




        check.setClickable(this.islightexist());//rend cliquable ou pas la case à cocher en fonction de si le capteur de luminosité existe

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //choix du mode
                mode = check.isChecked();//true->automatique;false->manuel
                if(check.isChecked()==true){
                    check.setText("manuel");
                    seekbar.setVisibility(0);//si on est en mode automatique, la case est cachée
                    lummode(mode,getContentResolver());

                }else{
                    check.setText("auto");
                    seekbar.setVisibility(100);//sinon elle est visible
                    lummode(mode,getContentResolver());
                }
                refreshBrightness();//met à jour la luminosité

            }
        });


        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                //changelum(getContentResolver(),progress);
                //Toast.makeText(MainActivity.this, "Changing seekbar's progress"+progress, Toast.LENGTH_SHORT).show();


                setScreenBrightness(i);//recupére la position du curseur et le passe en argument
                refreshBrightness();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(MainActivity.this, "Started tracking seekbar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Toast.makeText(MainActivity.this, "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
            }

        });




    }

    //on the destroy we make sure to close the connections that we made.
    @Override
    protected void onDestroy() {
        BluetoothManager.getInstance().closeBluetooth(this);
        super.onDestroy();
    }

    //on onActivityResult we must implement the CheckActivityResult of the BluetoothManager that
    // will check results about some request that were made about discovering and on/off
    // relations to bluetooth.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int resultBluetooth = BluetoothManager.getInstance().CheckActivityResult(requestCode,
                resultCode);

        if(resultBluetooth == BluetoothManager.BLUETOOTH_ON){
            toggleButtonBluetooth.setChecked(true);
        }else if(resultBluetooth == BluetoothManager.BLUETOOTH_OFF){
            toggleButtonBluetooth.setChecked(false);
        }else if(resultBluetooth == BluetoothManager.BLUETOOTH_DISCOVERY_LISTEN){
            toggleButtonVisible.setEnabled(false);
            spinnerDiscovering.setVisibility(View.VISIBLE);
            buttonConnect.setVisibility(View.INVISIBLE);
        }else if(resultBluetooth == BluetoothManager.BLUETOOTH_DISCOVERY_CANCELED){
            toggleButtonVisible.setEnabled(true);
            toggleButtonVisible.setChecked(false);
            spinnerDiscovering.setVisibility(View.INVISIBLE);
            buttonConnect.setVisibility(View.VISIBLE);
        }

        super.onActivityResult(requestCode,resultCode,data);
    }

    //callback for when the device is connected or an error occurred. When the connection os ok,
    // it starts a new Activity for the message exchange
    @Override
    public void onBluetoothConnection(int returnCode) {
        if(returnCode == BluetoothManager.BLUETOOTH_CONNECTED){
            Toast.makeText(MainActivity.this, "Connected",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ActivityConnected.class);

            startActivity(intent);
        }else if(returnCode == BluetoothManager.BLUETOOTH_CONNECTED_ERROR){
            Toast.makeText(MainActivity.this, "ConnectionError",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //callback for when the device is discoverable or if it ended the discoverable mode.
    @Override
    public void onBluetoothDiscovery(int returnCode) {
        if(returnCode == BluetoothManager.BLUETOOTH_DISCOVERABLE){
            toggleButtonVisible.setEnabled(false);
            spinnerDiscovering.setVisibility(View.VISIBLE);
            buttonConnect.setVisibility(View.INVISIBLE);

        }else if(returnCode == BluetoothManager.BLUETOOTH_CONNECTABLE ||
                returnCode == BluetoothManager.BLUETOOTH_NOT_CONNECTABLE){
            toggleButtonVisible.setEnabled(true);
            toggleButtonVisible.setChecked(false);
            spinnerDiscovering.setVisibility(View.INVISIBLE);
            buttonConnect.setVisibility(View.VISIBLE);
        }
    }

    //callback for when the bluetooth receive some that, that we are going to deal in a separated
    // activity
    @Override
    public void onReceiveData(String data) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {


        switch (requestCode) {
            case 1001: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // --->
                    Log.d("BT", "permisionGranted");

                } else {
                    //TODO re-request
                    Log.d("BT", "permisionNOTGranted");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);


                }
                break;
            }
        }
    }
////////////////////////////////////////

   /** public void listedescpateurs () {

        SensorManager mysens = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> listecapteur = mysens.getSensorList(Sensor.TYPE_ALL);
        TextView tv = (TextView)findViewById(R.id.tv);
        for(Sensor sensor : listecapteur){
            tv.append("-"+sensor.getType()+"\t : \t "+sensor.getName()+"\n");
        }

    }**/
    /**
     * test si le capteur de luminosité est present
     *
     *
     *
     *
     * @return true si le capteur existe
     */
    public boolean islightexist(){
        boolean cap=true;

        SensorManager mysens = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor light = mysens.getDefaultSensor(Sensor.TYPE_LIGHT);

        if(light == null){
            Toast.makeText(this, "pas de lightsensor!", Toast.LENGTH_LONG).show();
            cap=false;

        }else{
            Toast.makeText(this, "le capteur light existe!", Toast.LENGTH_LONG).show();
            cap=true;
        }

        return cap;
    }

    /**
     * change le mode de reglage de la luminosité
     *
     *
     *
     * @param mode true si manual
     * @param res
     */

    public  void lummode(boolean mode, ContentResolver res){
        if (mode) {
            Settings.System.putInt(res, SCREEN_BRIGHTNESS_MODE, SCREEN_BRIGHTNESS_MODE_MANUAL);
        } else {
            Settings.System.putInt(res, SCREEN_BRIGHTNESS_MODE, SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        }
    }

    /**
     * recuperer la luminosité actuelle et la stock dans le singleton (datamodel)
     *
     *
     *
     *
     * @return la luminosité actuelle
     */
    public float getsytemlum(){

        try {
            DataModel.getInstance().currentlum = Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS);
            Toast.makeText(this, "lum actuelle"+DataModel.getInstance().currentlum , Toast.LENGTH_LONG).show();
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return DataModel.getInstance().currentlum;
    }

    /**
     * regle la luminosté
     *
     *
     *
     *
     * @param brightnessValue valeur de reglage
     */
    public void setScreenBrightness(int brightnessValue){

        // Make sure brightness value between 0 to 255
        if(brightnessValue >= 0 && brightnessValue <= 255){
            Settings.System.putInt(
                    mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS,
                    brightnessValue
            );
        }
    }

    /**
     *
     *
     *
     * @deprecated
     * @return la valeur de la luminosité
     */
    public int getScreenBrightness(){

        int brightnessValue = Settings.System.getInt(
                mContext.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,
                0
        );
        return brightnessValue;
    }

    /**
     * mets à jour la luminosité
     *
     *
     *
     */
    private void refreshBrightness() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        getWindow().setAttributes(lp);
    }

   /** @Override
    protected  void onResume(){
        super.onResume();
        updateList();

    }
    void updateList(){

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_2, android.R.id.text1, DataModel.getInstance().Listname );
        list.setAdapter(adapter);


    }**/
    /**this.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {//edit
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {





        }
    });**/

}
