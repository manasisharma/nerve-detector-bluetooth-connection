package com.example.manasisharma.nim40;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.os.Bundle;

import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.List;
import android.widget.TextView;
import android.widget.ImageView;

import java.io.IOException;
import java.io.File;

import java.io.OutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;


import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.Toolbar;




public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.drawer_layout) DrawerLayout drawerlayout;
    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.drawer_recyclerView) RecyclerView drawerRecyclerView;

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothSocket mmSocket;
    private OutputStream mmOutStream;
    private InputStream mmInStream;



    TextView out;
    private  BluetoothDevice mmDevice;

    private BluetoothAdapter btAdapter;
    private ArrayList<BluetoothDevice> btDeviceList = new ArrayList<BluetoothDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);


        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,drawerlayout,toolbar,R.string.app_name,R.string.app_name);

        drawerlayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
        List<String> rows = new ArrayList<>();
        rows.add("Home");
        rows.add("Feedback");
        DrawerAdapter drawerAdapter = new DrawerAdapter(rows);
        drawerRecyclerView.setAdapter (drawerAdapter);
        drawerRecyclerView.hasFixedSize();
        drawerRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        out = (TextView) findViewById(R.id.out);
        out.setMovementMethod(new ScrollingMovementMethod());
        BluetoothProtocol.BluetoothHeader test;


        ((Button)findViewById(R.id.button6)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView image = (ImageView) findViewById(R.id.imageView);
                Bitmap bMap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getPath()+"/photo.jpg");
                image.setImageBitmap(bMap);
                File videoFile2Play = new File(Environment.getExternalStorageDirectory().getPath()+"/pic.jpg");


                Intent i = new Intent(MainActivity.this,PrintDialogActivity.class);
                i.setDataAndType(Uri.fromFile(videoFile2Play), "image/*");
                i.putExtra("title", "test");
                startActivity(i);


            }});



        ((Button)findViewById(R.id.button4)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] by = "snapshot".getBytes();

                try {
                    mmOutStream.write(by);
                    out.append("\n Requesting snapshot");

                } catch (IOException e) { }

                byte[] buffer = new byte[4000000];  // buffer store for the stream
                int bytes=0; // bytes returned from read()
                int bytecount=0;
                boolean index=true;
                int offset=0;
                long msize = 0;           // Size of Message #1
                int type = 0;             // Type of File #2
                int string_size = 0;     // string size #3
                String fname = "";         // Name of the file as String #5
                long fsize = 0;           // Size of the file #4
                boolean firsttime=true;
                BluetoothProtocol.BluetoothHeader test = new BluetoothProtocol.BluetoothHeader(msize,type,string_size,fsize,fname);
                try {
                    while (index) {

                        if(bytecount>=fsize&&bytecount>0)
                        {
                            index=false;
                            break;
                        }
                        bytes = mmInStream.read(buffer, 0, buffer.length);
                        bytecount+=bytes;
                        if (bytes > 0) {
                            if (firsttime) {
                                test = test.parseHeader(buffer);
                                fsize=test.fsize+24+test.string_size;
                                fname=test.fname;
                                out.append("\n "+fname+" is received");
                                offset = 24 + test.string_size;
                                firsttime = false;
                            }
                            test.parse(buffer, offset, bytes-offset, test.fname);
                            offset = 0;

                        } else {
                            index = false;
                        }

                    }
                }
                catch (Exception e){
                    out.append(e.getMessage()+"\n");
                }

            }



        });




        ((Button)findViewById(R.id.button7)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                out.setText("");
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                filter.addAction(BluetoothDevice.ACTION_UUID);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                registerReceiver(ActionFoundReceiver, filter); // Don't forget to unregister during onDestroy

                // Getting the Bluetooth adapter
                btAdapter = BluetoothAdapter.getDefaultAdapter();
              //  out.append("\n Bluetooth Started: " + btAdapter);

                CheckBTState();
            }
        });




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            CheckBTState();
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (btAdapter != null) {
            btAdapter.cancelDiscovery();
        }
        unregisterReceiver(ActionFoundReceiver);
    }

    private void CheckBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // If it isn't request to turn it on
        // List paired devices
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            out.append("\n Bluetooth not supported. Aborting!");
            return;
        } else {
            if (btAdapter.isEnabled()) {
                out.append("\n Bluetooth Enabled...");

                // Starting the device discovery
                btAdapter.startDiscovery();
            } else {
                Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }


    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
               // out.append("\n  Device: " + device.getName() + ", " + device);
                if (device.toString().equals("24:FD:52:33:44:7C")) { //James MAc address 5C:F3:70:60:3F:80

                    out.append("\n Found device .. Connecting");
                    btAdapter.cancelDiscovery();
                    pairDevice(device);


                }
                btDeviceList.add(device);
            }


        }

        private void pairDevice(BluetoothDevice device) {

            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            OutputStream tmpOut = null;
            InputStream tmpIn = null;

            BluetoothSocket tmp = null;
            mmDevice = device;


            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                out.append("\n Connected");
            } catch (IOException e) {
            }
            mmSocket = tmp;

            try {
                mmSocket.connect();


            } catch (Exception e) {

            }

            try {
                tmpIn = mmSocket.getInputStream();

                tmpOut = mmSocket.getOutputStream();


            } catch (IOException e) {
            }

            mmInStream = tmpIn;

            mmOutStream = tmpOut;




        }

    };

    @Override
    public void onBackPressed(){
        // Add data to your intent
      //  try {
        //    mmInStream.close();
       // } catch (IOException e) {
         //   e.printStackTrace();
        //}
        //out.setText(" ");
        //btAdapter=null;
        super.onBackPressed();

        //finish();
    }


}
