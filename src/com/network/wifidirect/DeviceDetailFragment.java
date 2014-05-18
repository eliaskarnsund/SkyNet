/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.network.wifidirect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.network.networkMonitor.GlobalData;
import com.network.networkMonitor.NetworkMapDataSource;
import com.network.networkMonitor.UTMLocation;
import com.network.skynet.R;
import com.network.wifidirect.DeviceListFragment.DeviceActionListener;

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment extends Fragment implements
		ConnectionInfoListener {

	protected static final int CHOOSE_FILE_RESULT_CODE = 20;
	private View mContentView = null;
	private WifiP2pDevice device;
	private WifiP2pInfo info;
	ProgressDialog progressDialog = null;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mContentView = inflater.inflate(R.layout.device_detail, null);
		mContentView.findViewById(R.id.btn_connect).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						WifiP2pConfig config = new WifiP2pConfig();
						config.deviceAddress = device.deviceAddress;
						config.wps.setup = WpsInfo.PBC;
						if (progressDialog != null
								&& progressDialog.isShowing()) {
							progressDialog.dismiss();
						}
						progressDialog = ProgressDialog.show(getActivity(),
								"Press back to cancel", "Connecting to :"
										+ device.deviceAddress, true, true

						);
						final WiFiDirectFragment fragment = (WiFiDirectFragment) getFragmentManager()
								.findFragmentById(R.id.frag_main);
						((DeviceActionListener) fragment).connect(config);

					}
				});

		mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						final WiFiDirectFragment fragment = (WiFiDirectFragment) getFragmentManager()
								.findFragmentById(R.id.frag_main);
						((DeviceActionListener) fragment).disconnect();
					}
				});

		mContentView.findViewById(R.id.btn_start_client).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// Allow user to pick an image from Gallery or other
						// registered apps
						// TODO
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.setType("image/*");
						// startActivityForResult(intent,
						// CHOOSE_FILE_RESULT_CODE);
						onActivityResult(1, 2, intent);
					}
				});

		return mContentView;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		// User has picked an image. Transfer it to group owner i.e peer using
		// FileTransferService.
		// Uri uri = data.getData();
		TextView statusText = (TextView) mContentView
				.findViewById(R.id.status_text);
		statusText.setText("Sending: något");
		Log.d(WiFiDirectFragment.TAG, "Intent----------- ");
		Intent serviceIntent = new Intent(getActivity(),
				FileTransferService.class);
		serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
		// serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH,
		// uri.toString());
		serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
				info.groupOwnerAddress.getHostAddress());
		serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT,
				8988);
		getActivity().startService(serviceIntent);
	}

	@Override
	public void onConnectionInfoAvailable(final WifiP2pInfo info) {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		this.info = info;
		this.getView().setVisibility(View.VISIBLE);

		// The owner IP is now known.
		TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
		view.setText(getResources().getString(R.string.group_owner_text)
				+ ((info.isGroupOwner == true) ? getResources().getString(
						R.string.yes) : getResources().getString(R.string.no)));

		// InetAddress from WifiP2pInfo struct.
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText("Group Owner IP - "
				+ info.groupOwnerAddress.getHostAddress());

		// After the group negotiation, we assign the group owner as the file
		// server. The file server is single threaded, single connection server
		// socket.

		if (info.groupFormed && info.isGroupOwner) {
			// TODO FEL Activity
			new FileServerAsyncTask(getActivity().getApplicationContext(),
					mContentView.findViewById(R.id.status_text)).execute();
		} else if (info.groupFormed) {
			// The other device acts as the client. In this case, we enable the
			// get file button.
			mContentView.findViewById(R.id.btn_start_client).setVisibility(
					View.VISIBLE);
			((TextView) mContentView.findViewById(R.id.status_text))
					.setText(getResources().getString(R.string.client_text));
		}

		// hide the connect button
		mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
	}

	/**
	 * Updates the UI with device data
	 * 
	 * @param device
	 *            the device to be displayed
	 */
	public void showDetails(WifiP2pDevice device) {
		this.device = device;
		this.getView().setVisibility(View.VISIBLE);
		TextView view = (TextView) mContentView
				.findViewById(R.id.device_address);
		view.setText(device.deviceAddress);
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText(device.toString());

	}

	/**
	 * Clears the UI fields after a disconnect or direct mode disable operation.
	 */
	public void resetViews() {
		mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
		TextView view = (TextView) mContentView
				.findViewById(R.id.device_address);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.device_info);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.group_owner);
		view.setText(R.string.empty);
		view = (TextView) mContentView.findViewById(R.id.status_text);
		view.setText(R.string.empty);
		mContentView.findViewById(R.id.btn_start_client).setVisibility(
				View.GONE);
		this.getView().setVisibility(View.GONE);
	}

	/**
	 * A simple server socket that accepts connection and writes some data on
	 * the stream.
	 */
	public static class FileServerAsyncTask extends
			AsyncTask<Void, Void, JSONArray> {

		private final Context context;
		private final TextView statusText;

		/**
		 * @param context
		 * @param statusText
		 */
		public FileServerAsyncTask(Context context, View statusText) {
			this.context = context;
			this.statusText = (TextView) statusText;
		}

		@Override
		protected JSONArray doInBackground(Void... params) {
			try {
				ServerSocket serverSocket = new ServerSocket(8988);
				Log.d(WiFiDirectFragment.TAG, "Server: Socket opened");
				Socket client = serverSocket.accept();
				Log.d(WiFiDirectFragment.TAG, "Server: connection done");

				// Converts inputstream to charset
				InputStream inputstream = client.getInputStream();
				BufferedReader r = new BufferedReader(new InputStreamReader(
						inputstream));
				StringBuilder total = new StringBuilder();
				String line;
				while ((line = r.readLine()) != null) {
					total.append(line);
				}
				Log.d("wifidirectdemo", total.toString());
				JSONArray table = new JSONArray(total.toString());
				inputstream.close();

				Log.d("NY", "mottaget " + total);
				serverSocket.close();
				return table;
			} catch (IOException | JSONException e) {
				Log.e(WiFiDirectFragment.TAG, e.getMessage());
				return null;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(JSONArray table) {
			if (table != null) {

				UTMLocation loc = null;
				Float bandwidth = null;
				JSONObject result = null;

				final GlobalData global = ((GlobalData) context);
				NetworkMapDataSource networkMap = global.getDSNetworkMap();
				networkMap.open();
				try {
					for (int i = 0; i < table.length(); i++) {

						result = new JSONObject(table.get(i).toString());
						if (table.get(i) == null) {
							break;
						}
						loc = new UTMLocation((String) result.get("UTM_ZONE"),
								(String) result.get("UTM_BAND"),
								(String) result.get("UTM_NORTHING"),
								(String) result.get("UTM_EASTING"));
						bandwidth = Float.parseFloat((String) result
								.get("BANDWIDTH"));
						if (!networkMap.existsBWSample(loc)) {
							Log.d(WiFiDirectFragment.TAG,
									"Finns inte i databas");
							networkMap.insertBWSample(result);
						} else {
							Log.d(WiFiDirectFragment.TAG,
									"Finns redan i databas");
							networkMap.updateBWSample(loc, bandwidth);
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				statusText.setText("La till: " + table.length() + " objekt");

			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			statusText.setText("Opening a server socket");
		}

	}

}
