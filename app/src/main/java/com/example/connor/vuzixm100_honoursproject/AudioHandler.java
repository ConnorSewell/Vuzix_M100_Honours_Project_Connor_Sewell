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
        ar = new AudioRecord(5, 8000, 2, AudioFormat.ENCODING_PCM_16BIT, 1280);
        ar.startRecording();

        AudioTrack audioTrack = new  AudioTrack(AudioManager.STREAM_VOICE_CALL, 8000, 2, AudioFormat.ENCODING_DEFAULT, 2560, AudioTrack.MODE_STREAM);
        audioTrack.setPlaybackRate(8000);

        int otherBufferSize = AudioRecord.getMinBufferSize(8000, 2, AudioFormat.ENCODING_PCM_16BIT);
        Log.e("Other Buffer: ", String.valueOf(otherBufferSize));
        //if (audioTrack.STATE_INITIALIZED == 1)
        //{
            audioTrack.play();
        //}
        //else if(audioTrack.STATE_INITIALIZED == 0)
        //{
        //    Log.e(TAG, "NOT INITIALISED");
        //}
        //sendAudioStream();

        //boolean test = false;

        while(true)
        {
            byte[] audioBuffer = new byte[1280];
            ar.read(audioBuffer, 0, 1280);
            //audioTrack.setPlaybackRate(8000);
            //audioTrack.play();
            audioTrack.write(audioBuffer, 0, 1280);
       }
       // }
    }

    public void setOutputStream(DataOutputStream outputStream)
    {
        this.outputStream = outputStream;
    }
    public void startAudioStream()
    {
        while(true)
        {
            byte[] audioBuffer = new byte[1280];
            ar.read(audioBuffer, 0, 1280);
            try
            {
                outputStream.writeInt(1280);
                outputStream.write(audioBuffer, 0, 1280);
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
