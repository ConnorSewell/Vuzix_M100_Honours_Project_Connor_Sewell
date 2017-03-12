package com.example.connor.vuzixm100_honoursproject;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Connor on 01/03/2017.
 *
 * Code for networking taken from: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 * ^ Accessed: 10/02/2017 @ 01:29
 */
public class AccelerometerStreamer  implements Runnable
{
    private AccelerometerHandler ah;
    private String TAG = "AccelerometerStreamer: ";
    private Context context;

    public AccelerometerStreamer(Context context, AccelerometerHandler ah)
    {
        this.context = context;
        this.ah = ah;
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
            sv = new ServerSocket(7777);
            client = sv.accept();
            out = new PrintWriter(client.getOutputStream(), true);
            ah.setOutputPoint(out);
            ah.registerSensorListener();
        }
        catch(IOException e)
        {
            Log.e(TAG, e.toString());
            run();
        }
    }
}
