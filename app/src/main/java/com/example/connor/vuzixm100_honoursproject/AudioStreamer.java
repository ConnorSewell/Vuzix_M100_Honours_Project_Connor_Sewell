package com.example.connor.vuzixm100_honoursproject;

import android.content.Context;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
    private RawAudioHandler ah;
    private String TAG = "AudioStreamer: ";
    private Context context;


    public AudioStreamer(Context context, RawAudioHandler aud)
    {
        this.context = context;
        ah = aud;
    }

    ServerSocket sv = null;
    public void closeSocket()
    {
        try
        {

            sv.close();
        }
        catch(Exception e)
        {
            System.out.println("Error");
        }
    }

    @Override
    public void run()
    {
        Socket client;
        OutputStream os;
        DataOutputStream dos;

        try
        {
            sv = new ServerSocket(3333);
            client = sv.accept();
            client.setSoTimeout(5000);
            os = client.getOutputStream();
            dos = new DataOutputStream(os);
            ah.setOutputStream(dos, client);
            ah.startAudioStream();
        }
        catch(IOException e)
        {
            Log.e(TAG, e.toString());
            run();
        }
    }
}
