package com.network.skynet;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.util.Log;

/**
 * This class implements the methods to take data which are used to determine
 * the network performance
 * 
 * @use To obtain a network performance sample is necesario to call the method
 *      {@code StartMonitoring()} just before start a download from the server
 *      to start monitoring the network received bytes. Upon the download ends,
 *      the method {@code StopMonitoring()} to finish the monitoring and pass
 *      the data to the alignment module. If the download is cancelled the
 *      monitoring should be cancelled by calling {@code CancelMonitoring()}.
 * 
 * @author Alberto Garc�a
 * 
 */

public class NetworkMonitor {

	/* Attributes */
	// Debug attributes
	private final String TAG = "NetworkMonitor";
	private Context mContext;
	// Monitoring attributes
	private long startBytes;
	private long startTime;
	private long endTime;
	private long endBytes;
	private UTMLocation locationOfMeasurement;
	private UTMLocation previousLocation;

	// Location attributes
	private LocationManager locationManager = null;
	private LocationListener locationListener;

	/**
	 * Constructor for the class
	 * 
	 * @param context
	 *            The application context
	 */
	public NetworkMonitor(Context context) {

		mContext = context;
		// Acquire a reference to the system Location Manager and set location
		// variables and location listener.
		locationManager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates

		locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location
				// provider.

				UTMLocation newUTMLocation = new UTMLocation(location,
						Config.getNetworkMapAccuracy());
				// When the devices changes its location, we compare the
				// previous
				// UTM location with the new UTM location
				if (!locationOfMeasurement.isEqual(newUTMLocation)) {
					// If we are in a new UTM Location, the devices has
					// performed a transition between UTM Location. Then we want
					// to measure the download speed of the location we just
					// left and store it.
					logMeasurement();
					previousLocation = new UTMLocation(locationOfMeasurement);

					locationOfMeasurement = new UTMLocation(newUTMLocation);
					Log.w(TAG,
							"Previous utm location: "
									+ previousLocation.toString()
									+ "| Next utm location: "
									+ locationOfMeasurement.toString());
				}

			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub

			}

		};
	}

	/**
	 * This method starts the monitoring of the download to get data from the
	 * network performance
	 */
	public void StartMonitoring() {
		Log.d(TAG, "Monitorization has started");
		/* Restart variables */
		startBytes = 0;
		startTime = 0;
		endBytes = 0;
		endTime = 0;
		String mProvider = null;

		// Sets the GPS to the provider of coordinates
		mProvider = LocationManager.GPS_PROVIDER;

		// Register the listener with the Location Manager to receive
		// location updates
		locationManager.requestLocationUpdates(mProvider, (2 * 1000), 500,
				locationListener);
		// Store current location in UTM coordinates
		Location x = locationManager.getLastKnownLocation(mProvider);
		if (x != null) {
			locationOfMeasurement = new UTMLocation(x,
					Config.getNetworkMapAccuracy());
		}

		// get start time to measure elapsed time between start and stop
		startTime = System.currentTimeMillis();
		// gets total bytes received before the download of data
		startBytes = TrafficStats.getTotalRxBytes();
	}

	/**
	 * This method stops the running monitoring and sends the data to the
	 * alignment module
	 */
	public void StopMonitoring() {
		logMeasurement();

		// Unregister the listener of location updates
		locationManager.removeUpdates(locationListener);

		Log.d(TAG, "Monitorization has stopped");
	}

	/**
	 * This method store measurement for the current speed and location.
	 */
	private void logMeasurement() {
		// get stop time to measure elapsed time between start and stop
		endTime = System.currentTimeMillis();
		// get total bytes received after the transmission of data
		endBytes = TrafficStats.getTotalRxBytes();

		// Send message to the service in charge of align the data
		Intent serviceData = new Intent(mContext, NetworkMapDataAlignment.class);
		serviceData.putExtra("START_TIME", startTime);
		serviceData.putExtra("END_TIME", endTime);
		serviceData.putExtra("START_BYTES", startBytes);
		serviceData.putExtra("END_BYTES", endBytes);
		serviceData.putExtra("LOCATION", locationOfMeasurement);
		mContext.startService(serviceData);

		// Updates the start time to measure elapsed time between start and stop
		startTime = System.currentTimeMillis();
		// Updates total bytes received at the start measurement point to
		// compare to end measurement.
		startBytes = TrafficStats.getTotalRxBytes();
	}

	/**
	 * This method cancels a running monitoring and discard the data already
	 * taken
	 */
	public void CancelMonitoring() {
		// Unregister the listener of location updates
		locationManager.removeUpdates(locationListener);
		/* Restart variables */
		startBytes = 0;
		startTime = 0;
		endBytes = 0;
		endTime = 0;
		Log.d(TAG, "Monitorization has been canceled");
	}
}