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
        this.gh.registerSensorListener();
    }

    ServerSocket sv = null;
    public void closeSocket()
    {
        try
        {
            gh.stopListener();
            gh = null;
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
        InputStream inputStream;
        PrintWriter out;

        try
        {
            sv = new ServerSocket(4444);
        }
        catch(IOException e)
        {
            Log.e(TAG, "Trouble creating server socket");
            run();
        }

        while(true && !sv.isClosed()) {
            try {
                client = sv.accept();
                client.setSoTimeout(5000);
                out = new PrintWriter(client.getOutputStream(), true);
                gh.addOutputPoint(out, client);
            } catch (IOException e)
            {
                Log.e(TAG, e.toString());
                //run();
            }
        }
    }

}
