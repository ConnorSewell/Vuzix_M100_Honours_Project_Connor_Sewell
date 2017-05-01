package com.example.connor.vuzixm100_honoursproject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.text.TextUtils;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
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
    private String TAG = "Main: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_);

        getActionBar().setIcon(android.R.color.transparent);

        modeText = (TextView) findViewById(R.id.modeText);
        timerText = (TextView) findViewById(R.id.timerText);

        setUpGestureSensor();

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new ClientServerManager(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        surfaceView = (SurfaceView) findViewById(R.id.camera_preview);
        vc = new VideoCapture(surfaceView, true, true, this);
    }

    public void setModeText(String text)
    {
        modeText.setText(text);
    }

    String outputDirectory;
    public void setDirectory()
    {
       Calendar calender = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        //int day = calender.get(Calendar.MONTH) + 1;
        int hours = calender.get(Calendar.HOUR_OF_DAY) + 1;
        int minutes = calender.get(Calendar.MINUTE);
        int month = calender.get(Calendar.DAY_OF_MONTH);
        int seconds = calender.get(Calendar.SECOND);
        int currentMonth = calender.get(Calendar.MONTH) + 1;

        //String dayString = null;
        String hoursString = null;
        String minutesString = null;
        String monthString = null;
        String secondsString = null;
        String currentMonthString = null;

        if(hours < 10)
        {
            hoursString = "0" + hours;
        }
        else
        {
            hoursString = String.valueOf(hours);
        }

        if(minutes < 10)
        {
            minutesString = "0" + minutes;
        }
        else
        {
            minutesString = String.valueOf(minutes);
        }

        if(month < 10)
        {
            monthString = "0" + month;
        }
        else
        {
            monthString = String.valueOf(month);
        }

        if(seconds < 10)
        {
            secondsString = "0" + seconds;
        }
        else
        {
            secondsString = String.valueOf(seconds);
        }

        if(currentMonth < 10)
        {
            currentMonthString = "0" + currentMonth;
        }
        else
        {
            currentMonthString = String.valueOf(currentMonth);
        }

        mediaStorageDir = new File("/mnt/ext_sdcard" + "/" + month + "#"
                + currentMonthString + "#" + calender.get(Calendar.YEAR) + "--" +
                hoursString + "-" + minutesString + "-" +
                secondsString);

        System.out.println("Dir: " + mediaStorageDir.getPath());
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                System.out.println("Failed to create directory...");
            }
        }

        outputDirectory = mediaStorageDir.getPath();
    }

    public void startStream()
    {
        if(vc.surfaceIsCreated)
        {
            vc.initialiseStreamProperties();
            ah = new AccelerometerHandler(this, outputDirectory, true);
            gh = new GyroscopeHandler(this, outputDirectory, true);

            alh = new AudioLevelsHandler(this, outputDirectory, true);
            setSensorReady();
            startStreamThreads();
        }
        else
        {
            Toast.makeText(this, "Surface hasn't been created. Please try again, or rester", Toast.LENGTH_LONG).show();
        }
    }

    AccelerometerHandler ah;
    GyroscopeHandler gh;
    AudioLevelsHandler alh;
    public void startRecording()
    {
        if(vc.surfaceIsCreated)
        {
            setDirectory();
            ah = new AccelerometerHandler(this, outputDirectory, false);
            gh = new GyroscopeHandler(this, outputDirectory, false);
            ah.setFileOutput(outputDirectory);
            gh.setFileOutput(outputDirectory);
            vc.setDirectory(outputDirectory);
            ah.registerSensorListener();
            gh.registerSensorListener();
        }
        else
        {
            Toast.makeText(this, "Surface hasn't been created. Please try again, or rester", Toast.LENGTH_LONG).show();
        }

    }

    int sensorsReceivingData = 0;
    public void videoRequirementsToStart()
    {
        sensorsReceivingData++;
        if(sensorsReceivingData == 2)
        {
            vc.init();
            sensorsReceivingData = 0;
        }
    }

    public void stopRecording()
    {
        vc.stopRecording();
        ah.stopListener();
        ah.closeBuffer();
        gh.stopListener();
        gh.closeBuffer();
        ah = null;
        gh = null;

        Toast.makeText(this, "Storing Stopped", Toast.LENGTH_LONG).show();
        sensorsReady = 0;
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
        try {

            csm.closeSocket();
            as.closeSocket();
            gs.closeSocket();
            als.closeSocket();
            videoSocketListener.interrupt();
            videoSocketListener = null;
            accelerometerSocketListener.interrupt();
            accelerometerSocketListener = null;
            gyroscopeSocketListener.interrupt();
            gyroscopeSocketListener = null;
            audioLevelsListener.interrupt();
            audioLevelsListener = null;

            csm = null;
            as = null;
            gs = null;
            als = null;

            ah.stopListener();
            gh.stopListener();
            ah = null;
            gh = null;
            alh = null;

            sensorsReady = 0;

            Toast.makeText(this, "Stream Stopped", Toast.LENGTH_LONG).show();
        }
        catch(Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    VideoStreamer csm;
    AccelerometerStreamer as;
    GyroscopeStreamer gs;
    AudioLevelsStreamer als;
    Thread videoSocketListener,accelerometerSocketListener, gyroscopeSocketListener, audioLevelsListener;

    private void startStreamThreads()
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
        sensorsReady = 0;
    }

    TimerTask timerTask;
    Timer timer;
    private void setTimer()
    {
        //http://stackoverflow.com/questions/4597690/android-timer-how
        //^For basic timer code (Not including formatting or splitting into hours/mins/seconds). Accessed: 27/03/2017 @ 15:37
        timer = new Timer();

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
        vc.stopRecording();
        vc = null;
        alh = null;

        if(!streamMode && ah!=null && gh!=null)
        {
            Log.i(TAG, "Closing Buffers");
            ah.stopListener();
            gh.stopListener();
            ah.closeBuffer();
            gh.closeBuffer();
        }

        if(mGS != null)
            mGS.unregister();

        gh = null;
        ah = null;
    }

}






