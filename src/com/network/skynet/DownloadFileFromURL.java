package com.network.skynet;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import com.network.networkMonitor.NetworkMonitor;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

/**
 * Background Async Task to download file
 * */
class DownloadFileFromURL extends AsyncTask<String, String, String> {

	private NetworkMonitor mNetworkMonitor;
	
	/**
	 * Before starting background thread Show Progress Bar Dialog
	 * */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	/**
	 * Downloading file in background thread
	 * */
	@Override
	protected String doInBackground(String... f_url) {
		int count;
		try {
			URL url = new URL(f_url[0]);
			URLConnection conection = url.openConnection();
			conection.connect();

			// download the file
			InputStream input = new BufferedInputStream(url.openStream(), 8192);

			// Output stream
			OutputStream output = new FileOutputStream(Environment
					.getExternalStorageDirectory().toString() + "/2011.kml");

			byte data[] = new byte[1024];

			while ((count = input.read(data)) != -1) {
				// writing data to file
				output.write(data, 0, count);
			}
			
			// Stop monitoring throughput
			mNetworkMonitor.StopMonitoring();
			
			// flushing output
			output.flush();

			// closing streams
			output.close();
			input.close();

		} catch (Exception e) {
			Log.e("Error: ", e.getMessage());
		}

		return null;
	}

	public void setmNetworkMonitor(NetworkMonitor mNetworkMonitor) {
		this.mNetworkMonitor = mNetworkMonitor;
	}
}