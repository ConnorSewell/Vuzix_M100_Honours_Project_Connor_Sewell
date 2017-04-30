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
import java.io.DataOutputStream;
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
 *
 * http://stackoverflow.com/questions/29695269/android-audiorecord-audiotrack-playing-recording-from-buffer
 * ^ Used to aid with bundling audio stream into byte array. Also used in phone application (also reference there)
 * ^ Accessed: 14/03/2017 @ 19:30
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
    private MediaRecorder audioRecorder;

    private long startTime;

    private ArrayList<PrintWriter> outputPoints = new ArrayList<PrintWriter>();
    private ArrayList<Socket> sockets = new ArrayList<Socket>();
    private long currTime = 0;

    public boolean ended = false;

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
                audioRecorder = new MediaRecorder();
                audioRecorder.setAudioSamplingRate(8000);
                audioRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                audioRecorder.setOutputFile("/dev/null");
                try
                {
                    audioRecorder.prepare();
                    audioRecorder.start();
                    mainActivity.setSensorReady();
                }
                catch(Exception e)
                {
                    Log.e(TAG, e.toString());
                }

                while (true && !ended)
                {
                    try
                    {
                        Thread.sleep(20);

                        int amplitude = audioRecorder.getMaxAmplitude();
                        currTime = System.nanoTime();
                        for(int i = 0; i < outputPoints.size(); i++)
                        {
                            try
                            {
                                outputPoints.get(i).println(String.valueOf(amplitude) + "," + (currTime - startTime));
                            }
                            catch(Exception e)
                            {
                                if(sockets.get(i).isClosed())
                                {
                                    outputPoints.remove(i);
                                    sockets.remove(i);
                                }
                            }
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
    public void addOutputPoint(PrintWriter out, Socket socket)
    {
        outputPoints.add(out);
        sockets.add(socket);
    }

    public void stopRecording()
    {
        try
        {
            audioRecorder.stop();
            audioRecorder = null;
            ended = true;
        }
        catch(Exception e)
        {
            Log.e(TAG, "Likely recording was not started...ERROR: " + e.toString());
        }
    }

    public void setStartTime(long startTime)
    {
        this.startTime = startTime;
    }

    /**
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
     */
}

