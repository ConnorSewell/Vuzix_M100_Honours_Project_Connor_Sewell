package com.example.connor.vuzixm100_honoursproject;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Connor on 14/03/2017.
 *
 * http://stackoverflow.com/questions/29695269/android-audiorecord-audiotrack-playing-recording-from-buffer
 * ^ Used to aid with bundling audio stream into byte array. Also used in phone application (also reference there)
 * ^ Accessed: 14/03/2017 @ 19:30
 */
public class AudioHandler
{
    Context context;
    AudioRecord ar;
    String TAG = "AudioHandler: ";
    DataOutputStream outputStream;

    public AudioHandler()
    {
        this.context = context;
        ar = new AudioRecord(5, 44100, 2, AudioFormat.ENCODING_PCM_16BIT, 7104);
    }

    public void setOutputStream(DataOutputStream outputStream)
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
