package com.example.connor.vuzixm100_honoursproject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.File;


//https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
//^Used for network related code (WifiP2pManager, Channel, BroadcastReceiver...). Accessed 08/02/2017 @ 14:55
public class Main_Activity extends Activity
{
    private SurfaceView surfaceView;
    MediaRecorder mr = new MediaRecorder();
    WifiP2pManager mManager;
    Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;

    public String inetAddress;
    File mediaStorageDir;

    boolean streamMode;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_);
        surfaceView = (SurfaceView) findViewById(R.id.camera_preview);


        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new ClientServerManager(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        //Ref here...
        mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/ACELP");

        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                System.out.println("Failed to create directory...");
            }
        }

        String outputDirectory = mediaStorageDir.getPath();

        streamMode = true;

        VideoCapture vd = new VideoCapture(surfaceView, true, outputDirectory, streamMode);
        AccelerometerHandler ah = new AccelerometerHandler(this, outputDirectory, streamMode);
        GyroscopeHandler gh = new GyroscopeHandler(this, outputDirectory, streamMode);
       // ah.register();
        //vd.init();

        if(streamMode) {
          startStreamThreads(vd, ah, gh);
        }
        GPSHandler gps = new GPSHandler(this, this);

    }

    private void startStreamThreads(VideoCapture vc, AccelerometerHandler ah, GyroscopeHandler gh)
    {
        VideoStreamer csm = new VideoStreamer(vc);
        Thread videoSocketListener = new Thread(csm, "Thread: Video");
        videoSocketListener.start();

        AccelerometerStreamer as = new AccelerometerStreamer(this, ah);
        Thread accelerometerSocketListener = new Thread(as, "Thread: Accelerometer");
        accelerometerSocketListener.start();

        GyroscopeStreamer gs = new GyroscopeStreamer(this, gh);
        Thread gyroscopeSocketListener = new Thread(gs, "Thread: Gyroscope");
        gyroscopeSocketListener.start();
    }

    public void addClient(String address)
    {
        inetAddress = address;
        Toast.makeText(this, "Result: " + inetAddress, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        //registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        //unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}




