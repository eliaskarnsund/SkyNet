package com.network.wifiDirect;

import java.util.Collection;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
import android.widget.Toast;

public class Peerlistener implements PeerListListener {

	private WifiP2pDeviceList peers;
	private WifiP2pDevice device;
	private Activity activity;
	private WifiP2pManager mManager;
	private Channel mChannel;

	public Peerlistener(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void onPeersAvailable(WifiP2pDeviceList peers) {
		this.peers = peers;
		
		// ConnectToDeviceAsync runner = new ConnectToDeviceAsync();
		// Log.d("HELLO", "Kör AsyncTask.execute");
		// runner.execute(mReceiver);
		
		// TODO do something in separate thread?
		Collection<WifiP2pDevice> devs = peers.getDeviceList();
		for (WifiP2pDevice wifiP2pDevice : devs) {
			Log.d("HELLO", wifiP2pDevice.deviceAddress);

			// TODO this is saved for test purpose
			// device = wifiP2pDevice;

		}
		Log.d("HELLO", String.valueOf(devs.isEmpty()));

	}

	public WifiP2pDevice getDevice() {
		if (device!= null) {
			Log.d("HELLO", "Hämtad adress " + device.deviceAddress);
		}else {
			Log.d("HELLO", "Device är null");
		}
		return device;
	}

	private void makeToast(CharSequence text) {
		Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
	}

	public WifiP2pDeviceList getPeers() {
		return peers;
	}

}
