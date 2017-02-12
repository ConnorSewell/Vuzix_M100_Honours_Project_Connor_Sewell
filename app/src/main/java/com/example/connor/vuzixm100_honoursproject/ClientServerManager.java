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
public class ClientServerManager extends BroadcastReceiver {
    private Channel mChannel;
    private WifiP2pManager mManager;
    private Main_Activity mActivity;

    String errorLogTag = "ERROR LOG: ";
    String infoLogTag = "INFO: ";

    public ClientServerManager(WifiP2pManager manager, Channel channel, Main_Activity mActivity) {
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = mActivity;


        mManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(infoLogTag, "Successful");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.e(errorLogTag, "Failed. Reason Code = " + String.valueOf(reasonCode));
            }
        });

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            mManager.requestPeers(mChannel, peerListListener);
            Log.i(infoLogTag, action);
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
        }
    }

    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList)
        {
            Log.i(infoLogTag, "No of peers: " + String.valueOf(peerList.getDeviceList().size()));
            peers.clear();
            peers.addAll(peerList.getDeviceList());

            if (peers.size() == 0) {
                Log.i(infoLogTag, "No devices found");
                return;
            }
        }


    };
}



