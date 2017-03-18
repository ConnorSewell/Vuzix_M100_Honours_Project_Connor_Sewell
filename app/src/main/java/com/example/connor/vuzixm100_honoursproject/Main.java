package com.example.connor.vuzixm100_honoursproject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.widget.Toast;

import com.vuzix.hardware.GestureSensor;

import java.io.File;

//Gesture Sensor code taken from official docs: http://files.vuzix.com/Content/Upload/Driver_File_GestureSensorSDK_20160317210116857.pdf
//^ Accessed: 18/03/2017 @ 02:00
//
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
    private MyGestureSensor mGS;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_);
        surfaceView = (SurfaceView) findViewById(R.id.camera_preview);

        setUpGestureSensor();

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new ClientServerManager(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        //Ref here...
        mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Connor");

        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                System.out.println("Failed to create directory...");
            }
        }

        String outputDirectory = mediaStorageDir.getPath();
        streamMode = false;

        VideoCapture vd = new VideoCapture(surfaceView, true, outputDirectory, streamMode);
        AccelerometerHandler ah = new AccelerometerHandler(this, outputDirectory, streamMode);
        GyroscopeHandler gh = new GyroscopeHandler(this, outputDirectory, streamMode);

        if(streamMode)
        {
            startStreamThreads(vd, ah, gh);
        }
        else
        {
            ah.registerSensorListener();
            gh.registerSensorListener();
        }

        //GPSHandler gps = new GPSHandler(this, this);
    }

    private void setUpGestureSensor()
    {
        if(GestureSensor.isOn() == false)
        {
            mGS = null;
            Toast.makeText(this, "Gesture Sensor Is Not On", Toast.LENGTH_LONG).show();
            return;
        }

        mGS = new MyGestureSensor(this);

        if(mGS == null)
        {
            Toast.makeText(this, "Gesture Sensor Not Calibrated", Toast.LENGTH_LONG).show();
            return;
        }

        mGS.register();
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

        AudioHandler audioH = new AudioHandler();
        AudioStreamer audioStreamer = new AudioStreamer(this, audioH);
        Thread audioTester = new Thread(audioStreamer, "Thread: Audio");
        audioTester.start();

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);

        if(mGS != null)
            mGS.register();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(mReceiver);

        if(mGS != null)
            mGS.unregister();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        System.out.println("On Destroy...");
        
        if(mGS != null)
            mGS.unregister();
    }

}




