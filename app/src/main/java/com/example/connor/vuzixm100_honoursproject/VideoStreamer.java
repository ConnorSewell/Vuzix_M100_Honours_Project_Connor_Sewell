package com.example.connor.vuzixm100_honoursproject;

import android.content.Context;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Connor on 18/02/2017.
 * Waits for incoming connections. When connection received, creates a writer to the client and passes it video capture
 *
 * Code for networking taken from: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 * ^ Accessed: 10/02/2017 @ 01:29
 */
public class VideoStreamer implements Runnable
{
    private VideoCapture vd;
    private String TAG = "VideoStreamer: ";
    ServerSocket sv = null;
    public VideoStreamer(VideoCapture vd)
    {
        this.vd = vd;
    }

    public void closeSocket()
    {
        try
        {
            vd.releaseBuffer();
            sv.close();
        }
        catch(Exception e)
        {}
    }

    @Override
    public void run()
    {
        Socket client;
        OutputStream os;
        DataOutputStream dos;

        try
        {
            sv = new ServerSocket(8888);
            sv.setSoTimeout(5000);
            sv.setPerformancePreferences(0, 1, 0);
        }
        catch(IOException e)
        {
            run();
        }
        byte[] receive = new byte[8];
        while(true && !sv.isClosed())
        {
            try
            {
                client = sv.accept();
                client.setTcpNoDelay(true);
                client.setSoTimeout(5000);
                os = client.getOutputStream();
                dos = new DataOutputStream(new BufferedOutputStream(os));
                vd.addOutputPoint(dos, client);

            } catch (Exception e) {
                Log.e("Error: ", e.toString());
            }
        }
    }
}
