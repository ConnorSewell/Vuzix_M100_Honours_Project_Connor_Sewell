package com.example.connor.vuzixm100_honoursproject;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Connor on 27/03/2017.
 */
public class AudioLevelsHandler
{

    private String outputDirectory;
    private boolean streamMode;

    private FileWriter outputFileWriter;
    private File audioLevelTextFile;
    private BufferedWriter bufferedWriter;

    String TAG = "ALHandler: ";

    private AudioRecord ar = null;
    private int minSize;
    public Main mainActivity;
    private Context context;
    private MediaRecorder mediaRecorder;

    private long startTime;

    private ArrayList<PrintWriter> outputPoints = new ArrayList<PrintWriter>();
    private long currTime = 0;

    public AudioLevelsHandler(Main activity, String outputDirectory, boolean streamMode)
    {
        this.mainActivity = activity;
        this.context = activity;
        this.outputDirectory = outputDirectory;
        this.streamMode = streamMode;

        new Thread(new Runnable()
        {
            public void run()
            {
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSamplingRate(8000);
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                mediaRecorder.setOutputFile("/dev/null");
                try
                {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                    mainActivity.setSensorReady();
                }
                catch(Exception e)
                {
                    Log.e(TAG, e.toString());
                }

                while (true)
                {
                    try
                    {
                        Thread.sleep(20);

                        int amplitude = mediaRecorder.getMaxAmplitude();
                        currTime = System.nanoTime();
                        for(int i = 0; i < outputPoints.size(); i++)
                        {
                            outputPoints.get(i).println(String.valueOf(amplitude) + "," + String.valueOf(currTime - startTime));
                        }
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, e.toString());
                    }
                }

            }
        }).start();
    }
    public void addOutputPoint(PrintWriter out)
    {
        outputPoints.add(out);
    }

    public void setStartTime(long startTime)
    {
        this.startTime = startTime;
    }
}

