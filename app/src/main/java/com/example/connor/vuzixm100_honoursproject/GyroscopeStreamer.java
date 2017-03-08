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
    private GyroscopeHandler gh;
    private String TAG = "GyroscopeStreamer: ";
    private Context context;

    public GyroscopeStreamer(Context context)
    {
        this.context = context;
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
            Log.i(TAG, "Socket opened...");
            sv = new ServerSocket(7777);
            client = sv.accept();
            Log.i(TAG, "Client accepted...");
            out = new PrintWriter(client.getOutputStream(), true);
            //gh = new GyroscopeStreamer(context, out);
            //while(true)
            //{
            //    try {
            //        Thread.sleep(200);
            //    }
            //    catch(Exception e){}
            //    out.write(ah.getCurrentValues());
            //}

            //sv.close();
        }
        catch(IOException e) {Log.e("Error: ", e.toString());}
    }

}
