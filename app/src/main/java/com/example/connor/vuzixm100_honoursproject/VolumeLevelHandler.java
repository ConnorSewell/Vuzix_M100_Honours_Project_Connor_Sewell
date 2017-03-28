package com.example.connor.vuzixm100_honoursproject;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.SystemClock;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Connor on 27/03/2017.
 */
public class VolumeLevelHandler implements Runnable
{

    String outputDirectory;
    boolean streamMode;

    //http://stackoverflow.com/questions/4777060/android-sample-microphone-without-recording-to-get-live-amplitude-level
    //^Used to get audio levels (majority of this class code).
    private AudioRecord ar = null;
    private int minSize;
    public void handleCurrentAmplitude()
    {
        short[] buffer = new short[minSize];
        ar.read(buffer, 0, minSize);
        int max = 0;
        double accumulator = 0;
        int count = 0;
        for (short byteInBuffer : buffer)
        {
            if (Math.abs(byteInBuffer) > max)
            {
                max = Math.abs(byteInBuffer);
            }
            accumulator += Math.abs(byteInBuffer);
            count++;
        }

        System.out.println("Max is: " + max + " At... " + SystemClock.elapsedRealtime());
        System.out.println("Accumulated val is: " + accumulator/count + " At... " + SystemClock.elapsedRealtime());
    }

    public VolumeLevelHandler(Main activity, String outputDirectory, boolean streamMode)
    {
        final Main activity2 = activity;
        this.outputDirectory = outputDirectory;
        this.streamMode = streamMode;

        minSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        ar = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,minSize);
        ar.startRecording();
}
    @Override
    public void run()
    {
        int count = 0;

        Timer timer = new Timer();
        TimerTask t = new TimerTask()
        {
            @Override
            public void run()
            {
                handleCurrentAmplitude();
            }
        };
        timer.scheduleAtFixedRate(t,200,200);
    }
}

