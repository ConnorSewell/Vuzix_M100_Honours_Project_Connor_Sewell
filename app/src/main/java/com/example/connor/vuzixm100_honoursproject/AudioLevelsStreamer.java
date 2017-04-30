package com.example.connor.vuzixm100_honoursproject;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Connor on 01/04/2017.
 * Waits for incoming connections. When connection received, creates a writer to the client and passes it to audiolevelshandler
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

    ServerSocket sv = null;
    public void closeSocket()
    {
        try
        {
            alh.stopRecording();
            alh = null;
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
        PrintWriter out = null;

        OutputStream os;
        DataOutputStream dos;

        try
        {
            sv = new ServerSocket(1111);
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
                alh.addOutputPoint(out, client);
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                //run();
            }
        }

    }
}
