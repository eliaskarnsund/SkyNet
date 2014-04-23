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
import android.widget.TextView;

public class MainActivity extends Activity {
	// App context
	final Context appContext = this;
	// label to display messages
	public TextView lblMessage;
	// Button to start the test
//	private Button startButton;
	private NetworkMonitor mNetworkMonitor;
	// File url to download
	private static String file_url = "http://vhost2.hansenet.de/1_mb_file.bin";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		lblMessage = (TextView) findViewById(R.id.textView1);
//		startButton = (Button) findViewById(R.id.startButton);
		mNetworkMonitor = new NetworkMonitor(getApplication());
//		//Start monitoring throughput
		mNetworkMonitor.StartMonitoring();
		DownloadFileFromURL download = new DownloadFileFromURL();
		download.setmNetworkMonitor(mNetworkMonitor);
		download.execute(file_url);
//		setupStartButton();
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
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

