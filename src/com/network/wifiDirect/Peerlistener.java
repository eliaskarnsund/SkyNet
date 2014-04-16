package com.network.wifiDirect;

import java.util.Collection;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

public class Peerlistener implements PeerListListener {
	
	public Peerlistener(Activity activity){
		
	}

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		// TODO do something in separate thread?
		Collection<WifiP2pDevice> devs = peers.getDeviceList();
		for (WifiP2pDevice wifiP2pDevice : devs) {
			Log.d("HELLO",wifiP2pDevice.deviceAddress);
		}
		
	}

}
