package com.network.networkMonitor;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * 
 * This class takes care of opening the database if it exists, creating it if it
 * does not, and upgrading it as necessary
 * 
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "NetworkMap";
	private static final int DATABASE_VERSION = 1;
	static final String TABLE_BANDWIDTH_MAP = "bandwidth_map";

	/**
	 * SQL query to create the table {@code bandwidth_map} in the database
	 */
	final String sqlCreate_bandwidth_map = "CREATE TABLE IF NOT EXISTS [bandwidth_map] ("
	        + "[id] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,"
	        + "[utmZone] INTEGER  NOT NULL,"
	        + "[utmBand] VARCHAR(1)  NOT NULL,"
	        + "[utmEasting] INTEGER  NOT NULL,"
	        + "[utmNorthing] INTEGER  NOT NULL,"
	        + "[bandwidth] FLOAT  NOT NULL,"
	        + "[n_Samples] INTEGER  NOT NULL,"
	        + "[last_Sample] TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,"
	        + " UNIQUE(utmZone, utmBand, utmEasting, utmNorthing) ON CONFLICT FAIL);";

	/**
	 * This class contains the name of each field that compound the
	 * {@code bandwidth_map}
	 * 
	 * @author Alberto García
	 * 
	 */
	public static class table_bandwidth_map {
		public static final String ID = "id";
		public static final String UTM_ZONE = "utmZone";
		public static final String UTM_BAND = "utmBand";
		public static final String UTM_EASTING = "utmEasting";
		public static final String UTM_NORTHING = "utmNorthing";
		public static final String BANDWIDTH = "bandwidth";
		public static final String N_SAMPLES = "n_Samples";
		public static final String LAST_SAMPLE = "last_Sample";

	}
	
	public MySQLiteOpenHelper(Context context, String name,
	        CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	public MySQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create tables in the database
		try {
			db.execSQL(sqlCreate_bandwidth_map);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int formerVersion, int newVersion) {
		// Update the database with the new version
		// TODO: Add update rutine
	}
}