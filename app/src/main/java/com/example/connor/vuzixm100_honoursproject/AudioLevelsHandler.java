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
public class AudioLevelsHandler implements Runnable
{

    private String outputDirectory;
    private boolean streamMode;

    private FileWriter outputFileWriter;
    private File audioLevelTextFile;
    private BufferedWriter bufferedWriter;

    String TAG = "ALHandler: ";

    private AudioRecord ar = null;
    private int minSize;
    private Main activity;
    private Context context;
    private MediaRecorder mediaRecorder;

    private ArrayList<PrintWriter> outputPoints = new ArrayList<PrintWriter>();

    public AudioLevelsHandler(Main activity, String outputDirectory, boolean streamMode)
    {
        this.activity = activity;
        this.context = activity;
        this.outputDirectory = outputDirectory;
        this.streamMode = streamMode;
    }

    @Override
    public void run()
    {
        //int counter = 0;
        //int currentVal = 0;
        //double finalVal = 0;
        //long accumulatedTime = 0;

        int valsAdded = 0;
        int count = 0;
        int accumulatorCount = 0;
        long nanos = 0;
        int accumulator = 0;
        String test = null;

        if(streamMode)
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
            }
            catch(Exception e)
            {
                Log.e(TAG, e.toString());
            }

        }

        while (true)
        {
            try {
                Thread.sleep(20);

                int amplitude = mediaRecorder.getMaxAmplitude();
                if (amplitude > 0) {
                    accumulator = accumulator + amplitude;
                    nanos = nanos + System.nanoTime();
                    valsAdded++;
                }
                count++;
                if (count == 10)
                {
                    for(int i = 0; i < outputPoints.size(); i++)
                    {
                        outputPoints.get(i).println(String.valueOf(accumulator / valsAdded) + "," + String.valueOf(nanos / valsAdded));
                    }

                    count = 0;
                    nanos = 0;
                    accumulator = 0;
                    valsAdded = 0;
                }

                //counter++;
                //accumulatedTime = accumulatedTime + System.nanoTime();
                //if (counter == 20) {
                //    counter = 0;
                //    accumulatedTime = 0;
                // }

            }
            catch (Exception e)
            {
                Log.e(TAG, e.toString());
            }
        }
    }

    public void addOutputPoint(PrintWriter out)
    {
        outputPoints.add(out);
    }

}

