package com.hajj.trekkon.jamaah.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hajj.trekkon.jamaah.R;

import java.util.Set;

public class ScanActivity extends AppCompatActivity {

    private BluetoothAdapter btAdapter;
    protected static final int DISCOVERY_REQUEST = 1;

    public TextView statusUpdate;
    public Button connect, sambung;
    public Button disconnect;
    public ImageView logo;
    public String toastText = "";
    private BluetoothDevice remoteDevice;

    BroadcastReceiver bluetoothState = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String prevStateExtra = BluetoothAdapter.EXTRA_PREVIOUS_STATE;
            String stateExtra = BluetoothAdapter.EXTRA_STATE;
            int state = intent.getIntExtra(stateExtra,-1);
            int previousState = intent.getIntExtra(prevStateExtra,-1);
            String toastText = "";
            switch (state){

                case (BluetoothAdapter.STATE_TURNING_ON):
                {
                    toastText = "Bluetooth sedang dihidupkan";
                    Toast.makeText(ScanActivity.this, toastText, Toast.LENGTH_SHORT).show();
                    break;
                }

                case (BluetoothAdapter.STATE_TURNING_OFF):
                {
                    toastText="Bluetooth sedang dimatikan";
                    Toast.makeText(ScanActivity.this, toastText, Toast.LENGTH_SHORT).show();
                    break;
                }

                case (BluetoothAdapter.STATE_ON):
                {
                    toastText="Bluetooth sudah hidup";
                    Toast.makeText(ScanActivity.this, toastText, Toast.LENGTH_SHORT).show();
                    setupUI();
                    break;
                }

                case (BluetoothAdapter.STATE_OFF):
                {
                    toastText="Bluetooth sudah mati";
                    Toast.makeText(ScanActivity.this, toastText, Toast.LENGTH_SHORT).show();
                    break;
                }



            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        setupUI();
    }

    private void setupUI(){
        statusUpdate = (TextView)findViewById(R.id.statusUpdate);
        connect=(Button)findViewById(R.id.connect);
        disconnect = (Button)findViewById(R.id.disconnect);
        logo = (ImageView)findViewById(R.id.logo);
        sambung = (Button)findViewById(R.id.sambung);

        sambung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

                IntentFilter i = new IntentFilter();
                i.addAction(BluetoothDevice.ACTION_FOUND);
                i.addAction(BluetoothDevice.ACTION_DIS);
            }
        });

        //connect.setVisibility(View.GONE);
        disconnect.setVisibility(View.GONE);
        logo.setVisibility(View.GONE);

        btAdapter=BluetoothAdapter.getDefaultAdapter();
        if (btAdapter.isEnabled()){
            String address = btAdapter.getAddress();
            String name = btAdapter.getName();
            String statusText = name + " : " + address;
            statusUpdate.setText(statusText);

            disconnect.setVisibility(View.VISIBLE);
            logo.setVisibility(View.VISIBLE);
            connect.setVisibility(View.GONE);

        }else{
            connect.setVisibility(View.VISIBLE);
            statusUpdate.setText("Bluetooth sedang mati");
        }

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //String actionStateChanged = BluetoothAdapter.ACTION_STATE_CHANGED;
                //String actionRequestEnable = BluetoothAdapter.ACTION_REQUEST_ENABLE;
                //IntentFilter filter = new IntentFilter(actionStateChanged);
                //registerReceiver(bluetoothState, filter);
                //startActivityForResult(new Intent(actionRequestEnable),0);

                String scanModeChanged = BluetoothAdapter.ACTION_SCAN_MODE_CHANGED;
                String beDiscoverable = BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE;
                IntentFilter filter = new IntentFilter(scanModeChanged);
                registerReceiver(bluetoothState, filter);
                startActivityForResult(new Intent(beDiscoverable), DISCOVERY_REQUEST);



            }
        });

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btAdapter.disable();
                disconnect.setVisibility(View.GONE);
                logo.setVisibility(View.GONE);
                connect.setVisibility(View.VISIBLE);
                statusUpdate.setText("Bluetooth dimatikan");

            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == DISCOVERY_REQUEST){
            Toast.makeText(ScanActivity.this, "Sedang melakukan discovery", Toast.LENGTH_SHORT).show();
            setupUI();
            findDevices();
        }

    }

    private void findDevices() {


        String lastUsedRemoteDevice = getLastUsedRemoteDevice();
        if (lastUsedRemoteDevice != null){
            toastText = "Memeriksa device, bernama : " + lastUsedRemoteDevice;
            Toast.makeText(ScanActivity.this, toastText, Toast.LENGTH_SHORT).show();

            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
            for (BluetoothDevice pairedDevice : pairedDevices){
                if (pairedDevice.getAddress().equals(lastUsedRemoteDevice)){
                    toastText = "Found device : " + pairedDevice.getName() + "@" + lastUsedRemoteDevice;
                    Toast.makeText(ScanActivity.this, toastText, Toast.LENGTH_SHORT).show();
                    remoteDevice = pairedDevice;
                }
            }
        }

        if (remoteDevice == null){
            toastText = "Starting discovery for remote devices...";
            Toast.makeText(ScanActivity.this, toastText, Toast.LENGTH_SHORT).show();

            if (btAdapter.startDiscovery()){
                toastText = "Memulai discovery...";
                Toast.makeText(ScanActivity.this, toastText, Toast.LENGTH_SHORT).show();
                registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
        }


    }


    BroadcastReceiver discoveryResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String remoteDeviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
            BluetoothDevice remoteDevice;
            remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            toastText = "Ditemukan : " + remoteDeviceName;
            Toast.makeText(ScanActivity.this, toastText, Toast.LENGTH_SHORT).show();


        }
    };

    private String getLastUsedRemoteDevice(){
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String result = prefs.getString("LAST_REMOTE_DEVICE_ADDRESS", null);
        return  result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(bluetoothState);
        unregisterReceiver(discoveryResult);
    }
}
