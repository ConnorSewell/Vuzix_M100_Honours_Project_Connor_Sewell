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
public class Main extends Activity
{
    private String inetAddress;
    private SurfaceView surfaceView;
    private MediaRecorder mr = new MediaRecorder();
    private WifiP2pManager mManager;
    private Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private File mediaStorageDir;
    private boolean streamMode;

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

        if(streamMode)
        {
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

    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

}




