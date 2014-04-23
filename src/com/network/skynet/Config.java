package com.network.skynet;

/**
 * This class contains the configuration values for the network map
 * 
 * @author Alberto García
 * 
 */
public class Config {
	public static int NETWORK_MAP_ACCURACY = 1000;

	/**
	 * @param network_map_accuracy
	 *            the network_map_accuracy to set
	 */
	public static void setNetworkMapAccuracy(int network_map_accuracy) {
		NETWORK_MAP_ACCURACY = network_map_accuracy;
	}

	/**
	 * @return the networkMapAccuracy
	 */
	public static int getNetworkMapAccuracy() {
		return NETWORK_MAP_ACCURACY;
	}

}
