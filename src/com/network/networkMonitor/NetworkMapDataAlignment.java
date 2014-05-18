package com.network.networkMonitor;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;

/**
 * 
 * 
 * 
 * This class acts as a Service which is listening for Intents with the data of
 * a network performance sample and processes this data to obtain a new network
 * performance measurement which is properly stored in the network map
 * 
 * @use The intent received by this class should contain an extra bundle with
 *      the next fields {@code IntentExtra
 *      ["START_TIME"->long, "END_TIME"->long, "START_BYTES"->long,
 *      "END_BYTES"->long, "LOCATION"->parcelabe(UTMLocation)]}
 * 
 */
public class NetworkMapDataAlignment extends IntentService {

	private NetworkMapDataSource DSNetworkMap = null;
	// public static final String DB_GET_ROW = "Row row";
	private final String TAG = "NetworkMapDataAlignment";

	public NetworkMapDataAlignment() {
		super("NetworkMapDataAlignment");
		DSNetworkMap = new NetworkMapDataSource(this);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		setGlobal();
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		// if (intent.getAction().equals(DB_GET_ROW)) {
		//
		// }

		// Process data to be stored in our Network Map
		Bundle extra = intent.getExtras();
		long startTime = extra.getLong("START_TIME", 0);
		long endTime = extra.getLong("END_TIME", 0);
		long startBytes = extra.getLong("START_BYTES", 0);
		long endBytes = extra.getLong("END_BYTES", 0);
		UTMLocation locationOfMeasurement = extra.getParcelable("LOCATION");
		// setGlobal();
		ProcessData(startTime, endTime, startBytes, endBytes,
				locationOfMeasurement);
	}

	public void setGlobal() {
		final GlobalData global = ((GlobalData) getApplicationContext());
		global.setDSNetworkMap(DSNetworkMap);
	}

	/**
	 * This method processes the data obtained in a sample to calculate the
	 * performance of the network and store this new information in the network
	 * map with the correct format
	 * 
	 * @param startTime
	 *            The start time of the sample {@code (milliseconds)}
	 * @param endTime
	 *            The end time of the sample {@code (milliseconds)}
	 * @param startBytes
	 *            The number of bytes received at start time {@code (Bytes)}
	 * @param endBytes
	 *            The number of bytes received at end time {@code (Bytes)}
	 * @param MeasuredLocation
	 *            The {@code UTMLocation} where the sample was taken
	 */
	private void ProcessData(long startTime, long endTime, long startBytes,
			long endBytes, UTMLocation MeasuredLocation) {
		// Difference of time in milliseconds
		float DifTime = (endTime - startTime);
		Log.d(TAG, "DifTime");
		// Difference of Rx data in Bytes
		float DifBytes = endBytes - startBytes;
		Log.d(TAG, "DifBytes");
		// Available bandwidth in Kbps
		float availableBW = (DifBytes * 8) / DifTime;
		Log.d(TAG, "availableBW");

		Log.w(TAG,
				"Time stamp:" + Long.toString(System.currentTimeMillis())
						+ "|Location:" + MeasuredLocation.toString()
						+ "|Start time:" + Long.toString(startTime)
						+ "|Start Bytes:" + Long.toString(startBytes)
						+ "|End Time:" + Long.toString(endTime) + "|End Bytes:"
						+ Long.toString(endBytes) + "|Dif Time:"
						+ Float.toString(DifTime) + "|Dif Bytes:"
						+ Float.toString(DifBytes) + "|Available BW:"
						+ Float.toString(availableBW) + "Kbps");
		// Store the new network performance sample
		try {
			if (availableBW > -1) {
				if (DSNetworkMap != null) {
					DSNetworkMap.open();
					// If already exist a estimate for the location, update it.
					if (DSNetworkMap.existsBWSample(MeasuredLocation)) {
						Log.d(TAG, "UpdateBWSample");
						DSNetworkMap.updateBWSample(MeasuredLocation,
								availableBW);
					} else {
						Log.d(TAG, "InsertBWSample");
						// Otherwise create a new network performance estimate
						// in the map
						DSNetworkMap.insertBWSample(MeasuredLocation,
								availableBW);
					}
					if (DSNetworkMap != null) {
						DSNetworkMap.close();
						DSNetworkMap = null;
					}

				}
			}

		} catch (SQLiteException e) {
			e.printStackTrace();
			if (DSNetworkMap != null) {
				DSNetworkMap.close();
				DSNetworkMap = null;
			}
			Log.e(TAG,
					"Error it could not perfom the action against the database");
		}
	}

	@Override
	public void onDestroy() {

		// If the connection is open, proceed to close it
		if (DSNetworkMap != null) {
			DSNetworkMap.close();
			DSNetworkMap = null;
		}
		super.onDestroy();
	}

}
