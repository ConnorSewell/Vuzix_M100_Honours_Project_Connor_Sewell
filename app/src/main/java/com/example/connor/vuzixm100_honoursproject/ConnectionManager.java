package com.example.connor.vuzixm100_honoursproject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Connor on 18/02/2017.
 * Code for networking taken from: https://developer.android.com/guide/topics/connectivity/wifip2p.html#creating-app
 * ^ Accessed: 10/02/2017 @ 01:29
 */
class ConnectionManager extends AsyncTask<Void, Void, String>
{
    Context context;

    public ConnectionManager() {}

    @Override
    protected String doInBackground(Void... params)
    {
        ServerSocket sv;
        Socket client;
        InputStream inputStream;
        try
        {
            Log.i("Connection State: ", "Waiting for connection...");
            sv = new ServerSocket(8888);
            client = sv.accept();
            InputStream is = client.getInputStream();
            String address = client.getInetAddress().toString();
            return address;
        }
        catch(IOException e) {return e.toString();}
    }

    @Override
    protected void onPostExecute(String exitResult)
    {
        if (!exitResult.equals("Transaction Completed")) {Log.e("Error: ", exitResult);}
        else {Log.i("INFO: ", exitResult);}
    }
}
