package com.example.connor.vuzixm100_honoursproject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Connor on 18/02/2017.
 * Code for networking taken from: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 * ^ Accessed: 10/02/2017 @ 01:29
 */
public class ConnectionManager implements Runnable
{
    Context context;
    VideoAudio vd;

    public ConnectionManager(VideoAudio vd)
    {
        this.vd = vd;
    }

    @Override
    public void run()
    {
        ServerSocket sv;
        Socket client;
        InputStream inputStream;
        try
        {
            Log.i("Connection State: ", "Socket opened...");
            sv = new ServerSocket(8888);
            client = sv.accept();
            Log.i("Connection State: ", "Connected... Socket Accepted");
            vd.init(client);
            sv.close();
        }
        catch(IOException e) {Log.e("Error: ", e.toString());}
    }
}
