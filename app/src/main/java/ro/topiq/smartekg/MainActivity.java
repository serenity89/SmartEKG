package ro.topiq.smartekg;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PAIRED_DEVICE = 2;
    private static final int REQUEST_EKG_FILE = 3;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /** Called when the activity is first created. */
    Button btnListBTDevices, btnListEKGFiles;
    TextView stateText;
    BluetoothAdapter bluetoothAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnListBTDevices = (Button)findViewById(R.id.listBTdevices);
        btnListEKGFiles = (Button)findViewById(R.id.listEKGfiles);

        stateText = (TextView)findViewById(R.id.bluetoothstate);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        CheckBlueToothState();

        btnListBTDevices.setOnClickListener(btnListPairedDevicesOnClickListener);
        btnListEKGFiles.setOnClickListener(btnListEkgFilesOnClickListener);

    }

    private void CheckBlueToothState(){
        if (bluetoothAdapter == null){
            stateText.setText("Bluetooth NOT support");
        }else{
            if (bluetoothAdapter.isEnabled()){
                if(bluetoothAdapter.isDiscovering()){
                    stateText.setText("Bluetooth is currently in device discovery process.");
                }else{
                    stateText.setText("Bluetooth is Enabled.");
                    btnListBTDevices.setEnabled(true);
                }
            }else{
                stateText.setText("Bluetooth is NOT Enabled!");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    private Button.OnClickListener btnListPairedDevicesOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, ListPairedDevicesActivity.class);
            startActivityForResult(intent, REQUEST_PAIRED_DEVICE);
        }};

    private Button.OnClickListener btnListEkgFilesOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, ListEkgFilesActivity.class);
            startActivityForResult(intent, REQUEST_EKG_FILE);
        }};

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent = new Intent();

        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK) {
                intent.setClass(MainActivity.this, ListPairedDevicesActivity.class);
                startActivityForResult(intent, REQUEST_PAIRED_DEVICE);
            }
        }
        if (requestCode == REQUEST_PAIRED_DEVICE){
            if(resultCode == RESULT_OK){
                // switch to Draw EKG activity
                intent.setClass(MainActivity.this, DrawEKG.class);
                startActivity(intent);
            }
        }
        if( requestCode == REQUEST_EKG_FILE) {
            if(resultCode == RESULT_OK) {
                // switch to Draw EKG activity
                intent.setData(data.getData());
                intent.setClass(MainActivity.this, DrawEKG.class);
                startActivity(intent);
            }
        }
    }

}
