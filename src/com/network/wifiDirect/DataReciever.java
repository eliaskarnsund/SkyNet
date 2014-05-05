package com.network.wifiDirect;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

public class DataReciever extends AsyncTask<String, Void, String> {

	private EditText serverIp;
	private Button connectPhones;
	private final String serverIpAddress = "";
	private final boolean connected = false;
	private final Handler handler = new Handler();
	private Socket socket;

	@Override
	protected String doInBackground(String... ips) {

		try {
			InetAddress serverAddr = InetAddress.getByName(ips[0]);
			socket = new Socket(serverAddr, 1234);

			InputStream input = socket.getInputStream();
			String inputString = convertStreamToString(input);

			Log.d("BRA", inputString);

			// ServerSocket serverSocket = new ServerSocket(8888);
			// Socket client = serverSocket.accept();

		} catch (Exception e) {

		}
		return null;
	}

	static String convertStreamToString(java.io.InputStream is) {
		java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
}
