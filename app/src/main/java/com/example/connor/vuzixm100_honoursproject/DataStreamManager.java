package com.example.connor.vuzixm100_honoursproject;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Connor on 10/02/2017.
 * Using: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 * ^ For all network related code. Accessed: 10/02/2017 @ 03:00
 */
public class DataStreamManager extends AsyncTask<Void, Void, Void>
{
    Socket socket;
    WifiP2pConfig config;

    String ip;

    public DataStreamManager(String ip)
    {
        //this.context = context;
        this.ip = ip;
        this.config = config;
        socket = new Socket();

        Log.i("Info... ", "Config is: " + ip);
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        try
        {
            socket.bind(null);
            //Phone, Port, Timeout
            socket.connect((new InetSocketAddress(ip, 8888)), 1000);
            Log.i("Success: ", "Connected to server!");
            //socket.close();
        }
        catch(Exception e)
        {
            Log.e("Error: ", e.toString());
            //System.out.println("Error here: " + e.toString());
        }

        return null;
    }

    private void streamData()
    {

    }


}
