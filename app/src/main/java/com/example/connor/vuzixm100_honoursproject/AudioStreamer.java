package com.example.connor.vuzixm100_honoursproject;

import android.content.Context;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Connor on 14/03/2017.
 *
 * Code for networking taken from: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 * ^ Accessed: 10/02/2017 @ 01:29
 */
public class AudioStreamer implements Runnable
{
    private AudioHandler ah;
    private String TAG = "AudioStreamer: ";
    private Context context;


    public AudioStreamer(Context context, AudioHandler audioH)
    {
        this.context = context;
        ah = new AudioHandler();
    }

    @Override
    public void run()
    {
        ServerSocket sv;
        Socket client;
        OutputStream os;
        DataOutputStream dos;

        try
        {
            sv = new ServerSocket(3333);
            client = sv.accept();
            os = client.getOutputStream();
            dos = new DataOutputStream(os);
            ah.setOutputStream(dos);
            ah.startAudioStream();
            //out = new PrintWriter(client.getOutputStream(), true);
        }
        catch(IOException e)
        {
            Log.e(TAG, e.toString());
            run();
        }
    }
}
