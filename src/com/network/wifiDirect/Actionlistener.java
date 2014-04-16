package com.network.wifiDirect;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

public class Actionlistener implements WifiP2pManager.ActionListener {

	Activity mActivity;

	public Actionlistener(Activity activity) {
		super();
		this.mActivity = activity;
	}

	@Override
	public void onFailure(int reason) {
		// TODO Auto-generated method stub
		// notifies that the discovery process succeeded and does
		// not provide any information about the actual peers that it
		// discovered
		// the system broadcasts the WIFI_P2P_PEERS_CHANGED_ACTION
		// intent
		makeToast("Peers found");

	}

	@Override
	public void onSuccess() {
		// TODO Auto-generated method stub
		makeToast("No peers found");

	}

	private void makeToast(CharSequence text) {
		Toast.makeText(mActivity, text, Toast.LENGTH_SHORT).show();
	}

}
