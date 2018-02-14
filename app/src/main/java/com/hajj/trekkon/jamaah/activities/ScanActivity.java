package com.hajj.trekkon.jamaah.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.hajj.trekkon.jamaah.R;
import com.hajj.trekkon.jamaah.adapter.RV_bt;
import com.hajj.trekkon.jamaah.model.BTDeviceModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ScanActivity extends AppCompatActivity {

    private BluetoothAdapter btAdapter;
    protected static final int DISCOVERY_REQUEST = 1;
    private List<BTDeviceModel> btDeviceModels ;
    BTDeviceModel btDeviceModel;
    RecyclerView rv;
    RV_bt rv_bt;


    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA};



    public TextView statusUpdate, tvJumlah;
    public Button connect, sambung;
    public Button disconnect;
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

        btDeviceModels = new ArrayList<>();
        rv.setLayoutManager(new LinearLayoutManager(ScanActivity.this));
        rv_bt = new RV_bt(btDeviceModels, R.layout.bt, ScanActivity.this);
        rv.setAdapter(rv_bt);

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }



    }

    private void setupUI(){

        tvJumlah = (TextView)findViewById(R.id.tvJumlah);
        statusUpdate = (TextView)findViewById(R.id.statusUpdate);
        connect=(Button)findViewById(R.id.connect);
        disconnect = (Button)findViewById(R.id.disconnect);
        rv=(RecyclerView)findViewById(R.id.rv);


        //connect.setVisibility(View.GONE);
        disconnect.setVisibility(View.GONE);


        btAdapter=BluetoothAdapter.getDefaultAdapter();
        if (btAdapter.isEnabled()){
            String address = btAdapter.getAddress();
            String name = btAdapter.getName();
            String statusText = name + " : " + address;
            statusUpdate.setText(statusText);

            disconnect.setVisibility(View.VISIBLE);
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

            //String remoteDeviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
            //BluetoothDevice remoteDevice;
            //remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            btDeviceModel = new BTDeviceModel();
            btDeviceModel.setEXTRANAME(intent.getStringExtra(BluetoothDevice.EXTRA_NAME));
            btDeviceModel.setEXTRAUUID(intent.getStringExtra(BluetoothDevice.EXTRA_UUID));
            btDeviceModel.setBONDBONDED(intent.getIntExtra(String.valueOf(BluetoothDevice.BOND_BONDED),0) );

            btDeviceModels.add(btDeviceModel);

            //toastText = "Ditemukan : " + remoteDeviceName;
            //Toast.makeText(ScanActivity.this, btDeviceModel.getEXTRANAME() + " " + btDeviceModel.getBONDBONDED().toString() , Toast.LENGTH_SHORT).show();

            rv_bt.notifyDataSetChanged();
            tvJumlah.setText(String.valueOf(btDeviceModels.size()));


        }
    };

    private String getLastUsedRemoteDevice(){
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String result = prefs.getString("LAST_REMOTE_DEVICE_ADDRESS", null);
        return  result;
    }

    @Override
    protected void onDestroy() {


        unregisterReceiver(bluetoothState);
        unregisterReceiver(discoveryResult);
    }




    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
