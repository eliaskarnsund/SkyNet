package com.network.wifiDirect;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class DataSender extends AsyncTask<Void, String, String> {

	private final Context context;
	private final TextView statusText;

	public DataSender(Context context, View statusText) {
		this.context = context;
		this.statusText = (TextView) statusText;
	}

	public void execute(NetworkHandler mReceiver) {
		// TODO Auto-generated method stub
		doInBackground();

	}

	@Override
	protected String doInBackground(Void... params) {
		try {

			/**
			 * Create a server socket and wait for client connections. This call
			 * blocks until a connection is accepted from a client
			 */

			Log.d("Datasender", "DataSender - Creating server socket");
			ServerSocket serverSocket = new ServerSocket(1234);
			Log.d("Datasender", "DataSender - skapat serversocket");
			Socket client = serverSocket.accept();
			Log.d("Datasender", "DataSender - serverSocket har accepterat");

			/**
			 * If this code is reached, a client has connected and transferred
			 * data Save the input stream from the client as a JPEG file
			 */
			Log.d("Datasender", "DataSender - Client has connected");
			// TODO skicka något vettigt
			final File f = new File(Environment.getExternalStorageDirectory()
					+ "/" + context.getPackageName() + "/wifip2pshared-"
					+ System.currentTimeMillis() + ".jpg");

			// TODO Ta bort
			File dirs = new File(f.getParent());
			if (!dirs.exists())
				dirs.mkdirs();
			f.createNewFile();

			InputStream inputstream = client.getInputStream();
			// TODO copyFile(inputstream, new FileOutputStream(f));
			serverSocket.close();
			return f.getAbsolutePath();
		} catch (IOException e) {
			// Log.e(WiFiDirectActivity.TAG, e.getMessage());
			return null;
		}
	}

	/**
	 * Start activity that can handle the JPEG image
	 */
	@Override
	protected void onPostExecute(String result) {
		if (result != null) {
			Log.d("HELLO", "DataSender - " + statusText);
			statusText.setText("File copied - " + result);
			Intent intent = new Intent();
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse("file://" + result), "image/*");
			context.startActivity(intent);
		}
	}
}
