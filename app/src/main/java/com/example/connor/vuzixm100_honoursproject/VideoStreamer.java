package com.example.connor.vuzixm100_honoursproject;

import android.content.Context;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Connor on 18/02/2017.
 * Code for networking taken from: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 * ^ Accessed: 10/02/2017 @ 01:29
 */
public class VideoStreamer implements Runnable
{
    private VideoCapture vd;
    private String TAG = "VideoStreamer: ";

    public VideoStreamer(VideoCapture vd)
    {
        this.vd = vd;
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
            sv = new ServerSocket(8888);
            client = sv.accept();
            os = client.getOutputStream();
            dos = new DataOutputStream(os);
            vd.addOutputPoint(dos);
        }
        catch(Exception e)
        {
            Log.e("Error: ", e.toString());
            run();
        }
    }
}
