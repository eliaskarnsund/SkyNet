package com.network.skynet;

import com.network.networkMonitor.NetworkMonitor;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends Activity {
	// App context
	final Context appContext = this;
	// Monitor used to measure throughput
	private NetworkMonitor mNetworkMonitor;
	// 1 MB file url to download
	private static String small_file_url = "http://vhost2.hansenet.de/1_mb_file.bin";
	// 100 MB file url to download
	private static String medium_file_url = "http://ipv4.download.thinkbroadband.com/100MB.zip";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Setup the NetworkMonitor
		mNetworkMonitor = new NetworkMonitor(getApplication());
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	/**
	 * Sets up test on a 1 MB file
	 * 
	 * @param v
	 */
	public void onClickSmallDownload(View v) {
		// Setup file download and start monitoring throughput
		mNetworkMonitor.StartMonitoring();
		DownloadFileFromURL download = new DownloadFileFromURL();
		download.setmNetworkMonitor(mNetworkMonitor);
		download.execute(small_file_url);
	}

	/**
	 * Sets up test on a 100 MB file
	 * 
	 * @param v
	 */
	public void onClickMediumDownload(View v) {
		// Setup file download and start monitoring throughput
		mNetworkMonitor.StartMonitoring();
		DownloadFileFromURL download = new DownloadFileFromURL();
		download.setmNetworkMonitor(mNetworkMonitor);
		download.execute(medium_file_url);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
