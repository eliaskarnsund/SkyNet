// Copyright 2011 Google Inc. All Rights Reserved.

package com.network.wifidirect;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.network.networkMonitor.GlobalData;
import com.network.networkMonitor.NetworkMapDataSource;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class FileTransferService extends IntentService {

	private static final int SOCKET_TIMEOUT = 5000;
	public static final String ACTION_SEND_FILE = "com.example.android.wifidirect.SEND_FILE";
	public static final String EXTRAS_FILE_PATH = "file_url";
	public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
	public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";

	public FileTransferService(String name) {
		super(name);
	}

	public FileTransferService() {
		super("FileTransferService");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {

		// Ändra intent?
		if (intent.getAction().equals(ACTION_SEND_FILE)) {
			// String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
			String host = intent.getExtras().getString(
					EXTRAS_GROUP_OWNER_ADDRESS);
			Socket socket = new Socket();
			int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);

			try {
				Log.d(WiFiDirectFragment.TAG, "Opening client socket - ");
				socket.bind(null);
				socket.connect((new InetSocketAddress(host, port)),
						SOCKET_TIMEOUT);

				// TODO
				// String query = "select * from Student WHERE rownum = 2";
				// Cursor cursor = database.rawQuery(query, null);

				Log.d(WiFiDirectFragment.TAG,
						"Client socket - " + socket.isConnected());
				OutputStream stream = socket.getOutputStream();

				PrintStream printStream = new PrintStream(stream);

				String row = getRow();
				printStream.print(row);

				Log.d(WiFiDirectFragment.TAG, "Client: Data written");
			} catch (IOException e) {
				Log.e(WiFiDirectFragment.TAG, e.getMessage());
				Toast.makeText(getApplicationContext(), "Not connected",
						Toast.LENGTH_SHORT).show();
				Log.e("NY", "FEL");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (socket != null) {
					if (socket.isConnected()) {
						try {
							socket.close();
						} catch (IOException e) {
							// Give up
							e.printStackTrace();
						}
					}
				}
			}

		}
	}

	private String getRow() throws JSONException {
		final GlobalData global = ((GlobalData) getApplicationContext());
		NetworkMapDataSource networkMap = global.getDSNetworkMap();
		networkMap.open();
		Cursor cursor = networkMap.getTableCursor();
		cursor.moveToFirst();
		int count = cursor.getCount();
		// String row;

		JSONArray table = new JSONArray();
		for (int i = 0; i < count; i++) {
			JSONObject jsonRow = new JSONObject();
			jsonRow.put("ID", cursor.getString(0));
			jsonRow.put("UTM_ZONE", cursor.getString(1));
			jsonRow.put("UTM_BAND", cursor.getString(2));
			jsonRow.put("UTM_EASTING", cursor.getString(3));
			jsonRow.put("UTM_NORTHING", cursor.getString(4));
			jsonRow.put("BANDWIDTH", cursor.getString(5));
			jsonRow.put("N_SAMPLES", cursor.getString(6));
			jsonRow.put("LAST_SAMPLE", cursor.getString(7));

			cursor.moveToNext();
			table.put(jsonRow);
			jsonRow = null;
			// table[i - 1].put(jsonRow);
		}

		networkMap.close();
		return table.toString();
		// return row;

	}

	public Cursor getCursor() {
		final GlobalData global = ((GlobalData) getApplicationContext());
		NetworkMapDataSource networkMap = global.getDSNetworkMap();
		if (networkMap != null) {
			Cursor c = networkMap.getTableCursor();
			return c;
		} else {
			return null;
		}
	}
}
