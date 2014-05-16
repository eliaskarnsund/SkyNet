// Copyright 2011 Google Inc. All Rights Reserved.

package com.network.wifidirect;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.network.networkMonitor.MySQLiteOpenHelper;

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

		Context context = getApplicationContext();
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
				MySQLiteOpenHelper sl = new MySQLiteOpenHelper(
						getApplicationContext());
				SQLiteDatabase db = sl.getWritableDatabase();
				// TODO
				// String query = "select * from Student WHERE rownum = 2";
				// Cursor cursor = database.rawQuery(query, null);

				Log.d(WiFiDirectFragment.TAG,
						"Client socket - " + socket.isConnected());
				OutputStream stream = socket.getOutputStream();

				// DeviceDetailFragment.copyFile(is, stream);
				// TODO SKICKA NÅGOT VETTIGT
				Random rand = new Random(500);
				PrintStream printStream = new PrintStream(stream);
				printStream.print("Hej " + rand.nextInt());

				Log.d(WiFiDirectFragment.TAG, "Client: Data written");
			} catch (IOException e) {
				Log.e(WiFiDirectFragment.TAG, e.getMessage());
				Toast.makeText(getApplicationContext(), "Not connected",
						Toast.LENGTH_SHORT).show();
				Log.e("NY", "FEL");
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
}
