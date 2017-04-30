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
 * Waits for incoming connections. When connection received, creates a writer to the client and passes it to accelerometer handler
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
        this.ah.registerSensorListener();
    }

    ServerSocket sv = null;
    public void closeSocket()
    {
        try
        {
            ah.stopListener();
            ah = null;
            sv.close();
        }
        catch(Exception e)
        {
            System.out.println("Error");
        }
    }

    @Override
    public void run() {
        Socket client;
        InputStream inputStream;
        PrintWriter out;
        try
        {
            sv = new ServerSocket(7777);
        }
        catch(IOException e)
        {
            Log.e(TAG, "Trouble creating server socket");
            run();
        }

        while (true && !sv.isClosed())
        {
            try
            {
                client = sv.accept();
                client.setSoTimeout(5000);
                Log.i(TAG, "IP: " + client.getInetAddress());
                out = new PrintWriter(client.getOutputStream(), true);
                ah.setOutputPoint(out, client);
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        }
    }
}
