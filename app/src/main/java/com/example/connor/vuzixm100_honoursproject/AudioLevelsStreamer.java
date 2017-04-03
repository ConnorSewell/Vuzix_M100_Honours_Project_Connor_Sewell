package com.example.connor.vuzixm100_honoursproject;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Connor on 01/04/2017.
 *
 * Code for networking taken from: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 * ^ Accessed: 10/02/2017 @ 01:29
 */
public class AudioLevelsStreamer implements Runnable
{

    private AudioLevelsHandler alh;

    String TAG = "AudLevelsStreamer";

    public AudioLevelsStreamer(AudioLevelsHandler alh)
    {
        this.alh = alh;
    }

    @Override
    public void run()
    {
        ServerSocket sv = null;
        Socket client;
        InputStream inputStream;
        PrintWriter out = null;

        try
        {
            sv = new ServerSocket(1111);
        }
        catch(IOException e)
        {
            Log.e(TAG, "Trouble creating server socket");
            run();
        }

        while (true)
        {
            try
            {
                client = sv.accept();
                Log.i(TAG, "IP: " + client.getInetAddress());
                out = new PrintWriter(client.getOutputStream(), true);
                alh.addOutputPoint(out);
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                //run();
            }
        }

    }
}
