package com.network.skynet;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * This class represent a Location defined by UTM coordinates and implements the
 * methods necessary to handle this locations
 * 
 * @author Alberto García
 */
public class UTMLocation implements Parcelable {

	private final String TAG = "UTMLocation";
	/* Coordinates UTM */
	private int Zone;
	private String Band;
	private int UTMn;
	private int UTMe;
	private int Granularity = 1; // Default accuracy of 1 meter by 1 meter
	// Values of granularity allowed
	public static final int ACCURACY_1M = 1; // UTM square of 1m by 1m
	public static final int ACCURACY_10M = 10; // UTM square of 10m by 10 m
	public static final int ACCURACY_100M = 100; // UTM square of 100m by 100m
	public static final int ACCURACY_1000M = 1000; // UTM square of 1,000m by
													// 1,000m
	public static final int ACCURACY_10000M = 10000; // UTM square of 10,000m by
														// 10,000m
	public static final int ACCURACY_100000M = 100000; // UTM square of 100,000m
														// by 100,000m

	// Constructor
	public UTMLocation(Location location) {
		LocationToUTM(location);
	}

	// Constructor
	public UTMLocation(Location location, int granularity) {
		setGranularity(granularity);
		LocationToUTM(location);
	}

	// Constructor copy
	public UTMLocation(UTMLocation oLocation) {
		copy(oLocation);
	}

	/*
	 * Part of the Parcelable interface of the object
	 */
	public UTMLocation(Parcel in) {
		UTMe = in.readInt();
		UTMn = in.readInt();
		Zone = in.readInt();
		Band = in.readString();
		Granularity = in.readInt();
	}

	/**
	 * This method transform a latitude/longitude coordinates location to an UTM
	 * coordinates location
	 * 
	 * @param location
	 *            in latitude/longitude coordinates
	 */
	public void LocationToUTM(Location location) {
		// Converter from Lat /Long
		// to UTM (WGS84 / NAD84)

		final double knu = 0.9996; // scale along central meridian of zone
		final double a = 6378137.0; // Equatorial radius in meters
		final double b = 6356752.3142; // Polar radius in meters

		double lat = Math.toRadians(location.getLatitude()); // Latitude in
		@SuppressWarnings("unused")
		// radians
		double lng = Math.toRadians(location.getLongitude()); // Longitude in
																// radians
		double lngdeg = location.getLongitude(); // Longitude in "normal" form.
													// dd.dd
		double latdeg = location.getLatitude(); // Latitude in "normal" form.
												// dd.dd

		double e = Math.sqrt(1 - (b * b) / (a * a)); // e = the eccentricity of
														// the earth's
														// elliptical
														// cross-section
		double e2 = e * e / (1 - (e * e)); // The quantile e' only occurs in
											// even powers
		double n = (a - b) / (a + b);

		Zone = (int) (31 + (lngdeg / 6)); // Calculating UTM zone using
											// Longitude in dd.dd form as
											// supplied
											// by the GPS
		Band = calculateBand(latdeg);

		double pi = 6 * Zone - 183; // Central meridian of zone
		double pii = (lngdeg - pi) * Math.PI / 180; // Difference between
													// Longitude and central
													// meridian of zone
		@SuppressWarnings("unused")
		double rho = a
				* (1 - e * e)
				/ Math.pow((1 - (e * e) * (Math.sin(lat) * (Math.sin(lat)))),
						(3.0 / 2.0)); // The radius of the curvature of the
										// earth in meridian plane

		double nu = a
				/ (Math.pow((1 - (e * e * (Math.sin(lat)) * (Math.sin(lat)))),
						(1.0 / 2.0))); // The radius of the curvature of the
										// earth perpendicular to the meridian
										// plane

		/*
		 * A0 - E0 is used for calculating the Meridional arc through the given
		 * point (lat long) The distance from the earth's surface form the
		 * equator. All angles are in radians
		 */

		double A0 = a
				* (1 - n + (5 / 4) * (Math.pow(n, 2) - Math.pow(n, 3)) + (81 / 64)
						* (Math.pow(n, 4) - Math.pow(n, 5)));
		double B0 = (3 * a * n / 2)
				* (1 - n - (7 * n * n / 8) * (1 - n) + (55 / 64)
						* (Math.pow(n, 4) - Math.pow(n, 5)));
		double C0 = (15 * a * n * n / 16) * (1 - n + (3 * n * n / 4) * (1 - n));
		double D0 = (35 * a * Math.pow(n, 3) / 48) * (1 - n + 11 * n * n / 16);
		double E0 = (315 * a * Math.pow(n, 4) / 51) * (1 - n);

		// Calculation of the Meridional Arc
		double S = A0 * lat - B0 * Math.sin(2 * lat) + C0 * Math.sin(4 * lat)
				- D0 * Math.sin(6 * lat) + E0 * Math.sin(8 * lat);

		/*
		 * y = northing = Ki+ Kii*p^2 + Kiii*p^4
		 */

		double Ki = S * knu;
		double Kii = knu * nu * Math.sin(lat) * Math.cos(lat) / 2;
		double Kiii = (knu * nu * Math.sin(lat) * Math.pow(Math.cos(lat), 3) / 24)
				* (5 - Math.pow(Math.tan(lat), 2) + 9 * Math.pow(e2, 2)
						* Math.pow(Math.cos(lat), 2) + 4 * Math.pow(e2, 2)
						* Math.pow(Math.cos(lat), 4));

		/*
		 * x = easting = Kiv*pii + Kv*pii^3 +
		 */
		double Kiv = knu * nu * Math.cos(lat);
		double Kv = knu
				* Math.pow(Math.cos(lat), 3)
				* (nu / 6)
				* (1 - Math.pow(Math.tan(lat), 2) + e2
						* Math.pow(Math.cos(lat), 2));

		double UTMni = (Ki + Kii * Math.pow(pii, 2) + Kiii * Math.pow(pii, 4));// Northing
		double UTMei = 500000 + (Kiv * pii + Kv * Math.pow(pii, 3));
		// Easting is relative to the central meridian. For conventional UTM
		// Easting add 5000000 meters to x

		this.setUTMn((int) UTMni); // Northing, rounded to closest integer
		this.setUTMe((int) UTMei); // Easting, rounded to closest integer

	}

	/**
	 * This method compare two {@code UTMLocation}
	 * 
	 * @param oLocation
	 *            {@code UTMLocation} to compare with
	 * 
	 * @return {@code TRUE} if the locations are equal and {@code FALSE} if not
	 *         are equal
	 */
	public boolean isEqual(UTMLocation oLocation) {

		int oZone = oLocation.getZone();
		int oUTMn = oLocation.getUTMn();
		int oUTMe = oLocation.getUTMe();
		String oBand = oLocation.getBand();
		if (oZone == Zone && oUTMn == UTMn && oUTMe == UTMe
				&& oBand.equalsIgnoreCase(Band)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method copy a given {@code UTMLocation} into the current
	 * {@code UTMLocation}
	 * 
	 * @param oLocation
	 *            {@code UTMLocation} to be copied
	 */
	public void copy(UTMLocation oLocation) {

		setZone(oLocation.getZone());
		setBand(oLocation.getBand());
		setUTMe(oLocation.getUTMe());
		setUTMn(oLocation.getUTMn());
		// Copy granularity the last in order to avoid affect the UTMe and UTMn
		// dividing it again by the granularity
		setGranularity(oLocation.getGranularity());
	}

	/**
	 * @return the zone
	 */
	public int getZone() {
		return Zone;
	}

	/**
	 * @param zone
	 *            the zone to set
	 */
	protected void setZone(int zone) {
		Zone = zone;
	}

	/**
	 * @return the uTMn
	 */
	public int getUTMn() {
		return UTMn;
	}

	/**
	 * @param uTMn
	 *            the uTMn to set
	 */
	protected void setUTMn(int uTMn) {
		UTMn = uTMn / Granularity;
	}

	/**
	 * @return the uTMe
	 */
	public int getUTMe() {
		return UTMe;
	}

	/**
	 * @param uTMe
	 *            the uTMe to set
	 */
	protected void setUTMe(int uTMe) {
		UTMe = uTMe / Granularity;
	}

	/**
	 * @return the band
	 */
	public String getBand() {
		return Band;
	}

	/**
	 * @param oBand
	 *            the band to set
	 */
	protected void setBand(String oBand) {
		Band = oBand;
	}

	/**
	 * @return the granularity
	 */
	public int getGranularity() {
		return Granularity;
	}

	/**
	 * @param oGranularity
	 *            the granularity to set
	 */
	public void setGranularity(int oGranularity) {
		if (oGranularity == 1 || oGranularity == 10 || oGranularity == 100
				|| oGranularity == 1000 || oGranularity == 10000
				|| oGranularity == 100000) {
			Granularity = oGranularity;
		} else {
			Log.w(TAG,
					"Granularity passed is not a correct value for this atribute");
		}
	}

	/**
	 * This method calculates the UTM Band for the UTM Location from a given
	 * latitude coordinate
	 * 
	 * @param oLatdeg
	 *            Latitude in degrees
	 * @return The UTM Band
	 */
	private String calculateBand(double oLatdeg) {

		if (oLatdeg > -80 && oLatdeg < -72)
			return "C";
		if (oLatdeg > -72 && oLatdeg < -64)
			return "D";
		if (oLatdeg > -64 && oLatdeg < -56)
			return "E";
		if (oLatdeg > -56 && oLatdeg < -48)
			return "F";
		if (oLatdeg > -48 && oLatdeg < -40)
			return "G";
		if (oLatdeg > -40 && oLatdeg < -32)
			return "H";
		if (oLatdeg > -32 && oLatdeg < -24)
			return "J";
		if (oLatdeg > -24 && oLatdeg < -16)
			return "K";
		if (oLatdeg > -16 && oLatdeg < -8)
			return "L";
		if (oLatdeg > -8 && oLatdeg < 0)
			return "M";
		if (oLatdeg > 0 && oLatdeg < 8)
			return "N";
		if (oLatdeg > 8 && oLatdeg < 16)
			return "P";
		if (oLatdeg > 16 && oLatdeg < 24)
			return "Q";
		if (oLatdeg > 24 && oLatdeg < 32)
			return "R";
		if (oLatdeg > 32 && oLatdeg < 40)
			return "S";
		if (oLatdeg > 40 && oLatdeg < 48)
			return "T";
		if (oLatdeg > 48 && oLatdeg < 56)
			return "U";
		if (oLatdeg > 56 && oLatdeg < 64)
			return "V";
		if (oLatdeg > 64 && oLatdeg < 72)
			return "W";
		if (oLatdeg > 72 && oLatdeg < 84)
			return "X";
		return null;
	}

	/**
	 * This method format the UTM Location to create a human readable string
	 * 
	 * @return String with a human readable UTM Location
	 */
	public String toString() {

		String x = Integer.toString(Zone) + Band + " " + Integer.toString(UTMe)
				+ Integer.toString(UTMn);
		return x;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#describeContents() Part of the Parcelable
	 * interface of the object
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int) Part of
	 * the Parcelable interface of the object
	 */
	@Override
	public void writeToParcel(Parcel out, int flag) {
		out.writeInt(UTMe);
		out.writeInt(UTMn);
		out.writeInt(Zone);
		out.writeString(Band);
		out.writeInt(Granularity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable Part of the Parcelable interface of the object
	 */
	public static final Parcelable.Creator<UTMLocation> CREATOR = new Parcelable.Creator<UTMLocation>() {
		public UTMLocation createFromParcel(Parcel in) {
			return new UTMLocation(in);
		}

		@Override
		public UTMLocation[] newArray(int size) {
			return new UTMLocation[size];
		}
	};

}