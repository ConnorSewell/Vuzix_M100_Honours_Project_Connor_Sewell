package com.example.connor.vuzixm100_honoursproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


//import java.nio.channels.Channel;

/**
 * Created by Connor on 08/02/2017.
 * Full class code taken from: https://developer.android.com/guide/topics/connectivity/wifip2p.html
 * ^ Accessed 08/02/2017 @ 14:55
 */
public class ClientServerManager extends BroadcastReceiver
{
    private Channel mChannel;
    private WifiP2pManager mManager;
    private Main_Activity mActivity;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    public ClientServerManager(WifiP2pManager manager, Channel channel, Main_Activity mActivity)
    {
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = mActivity;

    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();

        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {}
        else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {}
        else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {}
        else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {}


    }



}


