package com.example.connor.vuzixm100_honoursproject;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Connor on 01/03/2017.
 */
public class AccelerometerStreamer  implements Runnable
{
    private AccelerometerHandler ah;
    private String TAG = "AccelerometerStreamer: ";
    private Context context;

    public AccelerometerStreamer(Context context)
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
            out = new PrintWriter(client.getOutputStream(), true);
            ah = new AccelerometerHandler(context, out);
            //sv.close();
        }
        catch(IOException e) {Log.e("Error: ", e.toString());}
    }
}
