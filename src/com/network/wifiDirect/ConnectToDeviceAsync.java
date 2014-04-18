package com.network.wifiDirect;

import android.os.AsyncTask;
import android.util.Log;

public class ConnectToDeviceAsync extends AsyncTask<String, String, String> {

	private String resp;
	private NetworkHandler hand;

	@Override
	protected String doInBackground(String... params) {
		// publishProgress("Sleeping..."); // Calls onProgressUpdate()
		Log.d("HELLO", "connect startas av AsyncTask");
		hand.connect();
		
		resp = "hello";
		return resp;
	}

	public void execute(NetworkHandler mReceiver) {
		// TODO Auto-generated method stub
		hand = mReceiver;
		resp = "test";
		doInBackground(resp);
		
	}
}