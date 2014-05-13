package com.network.wifiDirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;
import android.widget.Toast;

import com.network.skynet.MainActivity;

public class NetworkHandler extends BroadcastReceiver {

	private final WifiP2pManager mManager;
	private final Channel        mChannel;
	// the activity that this broadcast receiver will be registered in
	private final MainActivity   mActivity;
	private Actionlistener       actionListener;
	private final Peerlistener   myPeerListListener;
	DataSender                   sender;
	WifiP2pManager.ConnectionInfoListener connListener;

	public NetworkHandler(WifiP2pManager manager, Channel channel,
	    MainActivity activity) {
		super();
		this.mManager = manager;
		this.mChannel = channel;
		this.mActivity = activity;
		setupActionlistener();
		myPeerListListener = new Peerlistener(mActivity);
		connListener = new WifiP2pManager.ConnectionInfoListener() {

	    @Override
	    public void onConnectionInfoAvailable(WifiP2pInfo info) {
		    Log.d("TAG", "ConnctionInfo " + info.toString());
	    }
    };

	}

	public void discover() {
		mManager.discoverPeers(mChannel, actionListener);
	}

	public void setupActionlistener() {
		actionListener = new Actionlistener(mActivity);
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();

		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
			// Check to see if Wi-Fi is enabled and notify appropriate activity
			checkstate(intent);
		} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
			// request available peers from the wifi p2p manager. This is an
			// asynchronous call and the calling activity is notified with a
			// callback on PeerListListener.onPeersAvailable()
			if (mManager != null) {
				mManager.requestPeers(mChannel, myPeerListListener);
				makeToast("List of peers retrieved");
				// TODO maybe should be verified somehow by onPeersAvailable()
				// verkar köras innan en enhet hittats, körs av knapp istället
				// just nu
				// onPeersAvailable()
				// connect();
			}

		} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
			Log.d("HELLO", "CONNECTION CHANGED ACTION " + action.toString());
			NetworkInfo netInfo = intent
			    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
			Log.d("TAG", "Is connected " + netInfo.isConnected());
			// new DataReciever().execute("10:68:3f:41:7b:1a");

			// från exempel
			if (mActivity == null) {
				return;
			}

			NetworkInfo networkInfo = (NetworkInfo) intent
			    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

			if (networkInfo.isConnected()) {

				// we are connected with the other device, request connection
				// info to find group owner IP

				//DeviceDetailFragment fragment = (DeviceDetailFragment) activity
				// .getFragmentManager().findFragmentById(R.id.frag_detail);
				mManager.requestConnectionInfo(mChannel, connListener);
			} else {
				// It's a disconnect
				// activity.resetData();
			}
			// Respond to new connection or disconnections
			// TODO
		} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
		    .equals(action)) {
			// Respond to this device's wifi state changing
			// TODO
		}
	}

	public void connect() {

		Log.d("HELLO", "NetworkHandler - connect startar");
		// obtain a peer from the WifiP2pDeviceList
		WifiP2pDevice device = myPeerListListener.getDevice();

		// TODO FULHAX-connectar till plattan (fungerar)
		if (device == null) {
			Log.d("HELLO", "NetworkHandler - device är null och får hårdkodad adress");
			device = new WifiP2pDevice();
			device.deviceAddress = "32:85:a9:4a:7d:4d";
			// DET HÄR FUNGERAR!!!
		}
		Log.d("HELLO",
		    "NetworkHandler - Device är " + device.deviceAddress.toString());
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = device.deviceAddress;
		mManager.connect(mChannel, config, new ActionListener() {

			@Override
			public void onSuccess() {
				// success logic
				makeToast("Connected");
				sender = new DataSender(mActivity, null);
				sender.execute();
			}

			@Override
			public void onFailure(int reason) {
				// failure logic
			}
		});

	}

	private void checkstate(Intent intent) {
		int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
		if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
			// TODO
			// Wifi P2P is enabled
			makeToast("Wifi P2P enabled");
		} else {
			// TODO
			// Wi-Fi P2P is not enabled
			makeToast("Wifi P2P NOT enabled");
		}
	}

	private void makeToast(CharSequence text) {
		Toast.makeText(mActivity, text, Toast.LENGTH_SHORT).show();
	}
}
