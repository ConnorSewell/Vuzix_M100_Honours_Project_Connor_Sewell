package com.example.connor.vuzixm100_honoursproject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.vuzix.hardware.GestureSensor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    //private MediaRecorder mr = new MediaRecorder();
    private WifiP2pManager mManager;
    private Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private File mediaStorageDir;
    public boolean streamMode = false;
    private MyGestureSensor mGS;
    private TextView modeText;
    private TextView timerText;

    int sensorsStreamReadyReady = 0;
    int sensorsStoreReadyReady = 0;

    private FileWriter outputFileWriter;
    private File videoStartTextFile;
    private BufferedWriter bufferedWriter;

    private boolean mediaStarted;

    private VideoCapture vc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_);


        modeText = (TextView) findViewById(R.id.modeText);
        timerText = (TextView) findViewById(R.id.timerText);
        TextView lolTest = (TextView) findViewById(R.id.loltest);

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

        surfaceView = (SurfaceView) findViewById(R.id.camera_preview);
        vc = new VideoCapture(surfaceView, true, outputDirectory, true, this);
        //setDirectory();

        //startStream();
        GPSHandler gps = new GPSHandler(this, this, lolTest);

    }

    public void setModeText(String text)
    {
        modeText.setText(text);
    }

    String outputDirectory;
    public void setDirectory() {
        Time currTime = new Time(Time.getCurrentTimezone());
        currTime.setToNow();

        mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/" + currTime.monthDay + 1 + "#"
                + currTime.month + "#" + currTime.year + "--" + currTime.hour + "-" + currTime.minute + "-" + currTime.second);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                System.out.println("Failed to create directory...");
            }
        }

        outputDirectory = mediaStorageDir.getPath();
    }

    public void startStream()
    {
        vc.initialiseStreamProperties();
        AccelerometerHandler ah = new AccelerometerHandler(this, outputDirectory, true);
        GyroscopeHandler gh = new GyroscopeHandler(this, outputDirectory, true);
        AudioLevelsHandler alh = new AudioLevelsHandler(this, outputDirectory, true);

        ah.registerSensorListener();
        gh.registerSensorListener();
        setSensorReady();

        startStreamThreads(vc, ah, gh, alh);
        //Thread videoSocketListener = new Thread(vc, "Thread: Video");
        //videoSocketListener.start();
    }

    private void setVideoThread(VideoCapture vc)
    {
        //Thread videoThread = new Thread(vc, "Thread: Video");
        //videoThread.start();
    }

    public void startRecording()
    {
        VideoCapture vc = new VideoCapture(surfaceView, true, outputDirectory, false, this);
        vc.init();
        AccelerometerHandler ah = new AccelerometerHandler(this, outputDirectory, false);
        GyroscopeHandler gh = new GyroscopeHandler(this, outputDirectory, false);
        //AudioLevelsHandler alh = new AudioLevelsHandler(this, outputDirectory, false);

        setVideoThread(vc);

        ah.registerSensorListener();
        gh.registerSensorListener();
        //startStreamThreads(vc, ah, gh, alh);
    }

    MediaRecorder mr = new MediaRecorder();
    public MediaRecorder getMediaRecorder()
    {
        return mr;
    }

    private void setUpGestureSensor()
    {
        if(GestureSensor.isOn() == false)
        {
            mGS = null;
            Toast.makeText(this, "Gesture Sensor Is Not On", Toast.LENGTH_LONG).show();
            return;
        }

        mGS = new MyGestureSensor(this, this);

        if(mGS == null)
        {
            Toast.makeText(this, "Gesture Sensor Not Calibrated", Toast.LENGTH_LONG).show();
            return;
        }

        mGS.register();
    }

    public void stopStream()
    {
        //hnvideoSocketListener.ter
    }

    VideoStreamer csm;
    AccelerometerStreamer as;
    GyroscopeStreamer gs;
    AudioLevelsStreamer als;
    Thread videoSocketListener,accelerometerSocketListener, gyroscopeSocketListener, audioLevelsListener;

    private void startStreamThreads(VideoCapture vc, AccelerometerHandler ah, GyroscopeHandler gh, AudioLevelsHandler alh)
    {
        long currTime = System.nanoTime();
        ah.setStartTime(currTime);
        gh.setStartTime(currTime);
        alh.setStartTime(currTime);

        csm = new VideoStreamer(vc);
        videoSocketListener = new Thread(csm, "Thread: Video");
        videoSocketListener.start();

        as = new AccelerometerStreamer(this, ah);
        accelerometerSocketListener = new Thread(as, "Thread: Accelerometer");
        accelerometerSocketListener.start();

        gs = new GyroscopeStreamer(this, gh);
        gyroscopeSocketListener = new Thread(gs, "Thread: Gyroscope");
        gyroscopeSocketListener.start();

        als = new AudioLevelsStreamer(alh);
        audioLevelsListener = new Thread(als, "Thread: Audio Levels");
        audioLevelsListener.start();

        //AudioHandler audioH = new AudioHandler();
        //AudioStreamer audioStreamer = new AudioStreamer(this, audioH);
        //Thread audioTester = new Thread(audioStreamer, "Thread: Audio");
        //audioTester.start();
    }

    int sensorsReady = 0;
    public void setSensorReady()
    {
        sensorsReady++;
        if(sensorsReady == 4 && streamMode)
        {
            setTimer();
        }
        else if(sensorsReady == 3 && !streamMode)
        {
            setTimer();
        }
    }

    public void resetTimer()
    {
        timerTask.cancel();
        timer.cancel();
        timerText.setText("00:00:00");
    }

    TimerTask timerTask;
    Timer timer = new Timer();
    private void setTimer()
    {
        //http://stackoverflow.com/questions/4597690/android-timer-how
        //^For basic timer code (Not including formatting or splitting into hours/mins/seconds). Accessed: 27/03/2017 @ 15:37

        timerTask = new TimerTask()
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
        timer.scheduleAtFixedRate(timerTask,1000,1000);
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






