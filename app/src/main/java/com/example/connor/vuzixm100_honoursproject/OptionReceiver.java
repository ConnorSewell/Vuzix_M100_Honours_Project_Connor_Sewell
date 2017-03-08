package com.example.connor.vuzixm100_honoursproject;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Connor on 08/03/2017.
 */
public class OptionReceiver implements Runnable
{
    Main_Activity ma;

    public OptionReceiver(Main_Activity ma)
    {
        this.ma = ma;
    }

    @Override
    public void run()
    {
        ServerSocket sv;
        Socket client;
        InputStream is;
        BufferedReader bin;

        try
        {
            sv = new ServerSocket(6666);
            client = sv.accept();
            is = client.getInputStream();
            bin = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while(true)
            {
                String line = bin.readLine();
                if(line.equals("0"))
                {
                    //Start Stream
                }
                else if(line.equals("1"))
                {
                    //Stop Stream
                }
            }
        }
        catch(Exception e) {Log.e("Error: ", e.toString());}
    }

}
