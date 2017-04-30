package com.example.connor.vuzixm100_honoursproject;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.provider.MediaStore;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Connor on 14/03/2017.
 * NOT USED - Class originally intended to accept clients, and stream raw audio to them.
 *
 * http://stackoverflow.com/questions/29695269/android-audiorecord-audiotrack-playing-recording-from-buffer
 * ^ Used to aid with bundling audio stream into byte array. Also used in phone application (also reference there)
 * ^ Accessed: 14/03/2017 @ 19:30
 */
public class RawAudioHandler implements Runnable
{
    Context context;
    AudioRecord ar;
    String TAG = "RawAudioHandler: ";
    DataOutputStream outputStream;

    public RawAudioHandler()
    {
        this.context = context;
    }

    @Override
    public void run()
    {
        ar = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, 7104);

        ar.startRecording();
        while(true)
        {
            byte[] audioBuffer = new byte[7104];
            short[] buffer = new short[7104];
            ar.read(buffer, 0, 7104);
            int max = 0;
            double accumulator = 0;
            int count = 0;
            long timePoint = 0;
            for (short byteInBuffer : buffer)
            {
                if (Math.abs(byteInBuffer) > max)
                {
                    max = Math.abs(byteInBuffer);
                    timePoint = System.nanoTime();
                }
            }

            System.out.println(max);
        }

    }

    public void setOutputStream(DataOutputStream outputStream, Socket socket)
    {
        this.outputStream = outputStream;

    }
    public void startAudioStream()
    {
        ar.startRecording();
        while(true)
        {
            byte[] audioBuffer = new byte[7104];
            ar.read(audioBuffer, 0, 7104);
            try
            {
                outputStream.writeInt(128);
                outputStream.write(audioBuffer, 0, 7104);
                outputStream.flush();
            }
            catch(IOException e)
            {
                Log.e(TAG, " ERROR when writing to output stream: " + e.toString());
                break;
            }
        }
    }


}
