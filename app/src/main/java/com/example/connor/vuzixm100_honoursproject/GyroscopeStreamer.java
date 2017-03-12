package com.example.connor.vuzixm100_honoursproject;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Connor on 03/03/2017.
 * Code for networking taken from: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 * ^ Accessed: 10/02/2017 @ 01:29
 */
public class GyroscopeStreamer implements Runnable
{
    private String TAG = "GyroscopeStreamer: ";
    private Context context;
    private GyroscopeHandler gh;

    public GyroscopeStreamer(Context context, GyroscopeHandler gh)
    {
        this.context = context;
        this.gh = gh;
    }

    @Override
    public void run()
    {
        ServerSocket sv;
        Socket client;
        InputStream inputStream;
        PrintWriter out;

        try
        {
            sv = new ServerSocket(4444);
            client = sv.accept();
            out = new PrintWriter(client.getOutputStream(), true);
            gh.setOutputPoint(out);
            gh.registerSensorListener();
        }
        catch(IOException e)
        {
            Log.e(TAG, e.toString());
            run();
        }
    }

}
