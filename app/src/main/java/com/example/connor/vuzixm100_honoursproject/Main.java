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
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.vuzix.hardware.GestureSensor;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

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
    private TextView textView;
    private TextView timerText;

    int sensorsReady = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_);
        surfaceView = (SurfaceView) findViewById(R.id.camera_preview);

        textView = (TextView) findViewById(R.id.statusText);
        textView.setText("Status: Ready");

        timerText = (TextView) findViewById(R.id.timerText);

        //setUpGestureSensor();

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new ClientServerManager(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        //Ref here...
        Time currTime = new Time(Time.getCurrentTimezone());
        currTime.setToNow();

        mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/" + currTime.monthDay + "-"
                + currTime.month + "-" + currTime.year + "--" + currTime.hour + ":" + currTime.minute + ":" + currTime.second);

        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                System.out.println("Failed to create directory...");
            }
        }

        String outputDirectory = mediaStorageDir.getPath();
        streamMode = false;

        VideoCapture vd = new VideoCapture(surfaceView, true, outputDirectory, streamMode, this);
        AccelerometerHandler ah = new AccelerometerHandler(this, outputDirectory, streamMode);
        GyroscopeHandler gh = new GyroscopeHandler(this, outputDirectory, streamMode);

        if(streamMode)
        {
          //  startStreamThreads(vd, ah, gh);
        }
        else
        {
            ah.registerSensorListener();
            gh.registerSensorListener();
        }

       // GPSHandler gps = new GPSHandler(this, this);
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

    public void setSensorReady()
    {
        sensorsReady++;
        if(sensorsReady == 3)
        {
            setTimer();
        }
    }

    private void setTimer()
    {
        //http://stackoverflow.com/questions/4597690/android-timer-how
        //^For basic timer code (Not including formatting or splitting into hours/mins/seconds). Accessed: 27/03/2017 @ 15:37
        Timer timer = new Timer();
        TimerTask t = new TimerTask()
        {   int seconds = 0;
            int minutes = 0;
            int hours = 0;

            String formattedSeconds;
            String formattedMinutes;
            String formattedHours;

            @Override
            public void run()
            {
                seconds++;

                if(seconds == 60)
                {
                    minutes++;
                    seconds = 0;
                }

                if(minutes == 60)
                {
                    hours++;
                    minutes = 0;
                }

                if(seconds < 10)
                {
                    formattedSeconds = "0" + seconds;
                }
                else
                {
                    formattedSeconds = String.valueOf(seconds);
                }

                if(minutes < 10)
                {
                    formattedMinutes = "0" + minutes;
                }
                else
                {
                    formattedMinutes = String.valueOf(minutes);
                }

                if(hours < 10)
                {
                    formattedHours = "0" + hours;
                }
                else
                {
                    formattedHours = String.valueOf(hours);
                }

                runOnUiThread(new Runnable(){
                    @Override
                    public void run()
                    {
                        timerText.setText(formattedHours + ":" + formattedMinutes + ":" + formattedSeconds);
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(t,1000,1000);
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






