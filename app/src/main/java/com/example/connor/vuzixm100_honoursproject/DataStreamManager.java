package com.example.connor.vuzixm100_honoursproject;

import android.content.Context;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Connor on 10/02/2017.
 * Using: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 * ^ For all network related code. Accessed: 10/02/2017 @ 03:00
 */
public class DataStreamManager
{
    Context context;
    Socket socket;
    public DataStreamManager(Context context)
    {
        this.context = context;
        socket = new Socket();
    }

    private void streamData()
    {
        try
        {
            socket.bind(null);
            //Phone, Port, Timeout
            socket.connect((new InetSocketAddress("PhoneName", 0)), 1000);

            //socket.close();
        }
        catch(IOException e)
        {

        }
    }


}
