package com.network.networkMonitor;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.network.networkMonitor.MySQLiteOpenHelper.table_bandwidth_map;

/**
 * This class managed the operations with the Database where the network
 * performance map is stored
 * 
 * @author Alberto Garc�a
 */
public class NetworkMapDataSource {

	private final String TAG = "NetworkMapDataSource";
	private SQLiteDatabase db;
	private MySQLiteOpenHelper dbHelper = null;

	public NetworkMapDataSource(Context context) {
		dbHelper = new MySQLiteOpenHelper(context);
	}

	/**
	 * Open a connection with the database
	 */
	public void open() {

		try {
			if (dbHelper != null) {
				db = dbHelper.getWritableDatabase();
				Log.d(TAG, "DB opened");
			}
		} catch (SQLiteException e) {

			e.printStackTrace();
		}
	}

	/**
	 * Close a connection with the database
	 */
	public void close() {
		// TODO
		// if (dbHelper != null) {
		// dbHelper.close();
		// dbHelper = null;
		// Log.d(TAG, "DB closed");
		// }

	}

	/**
	 * This method insert a new network performance sample into the table
	 * {@code bandwidth_map}
	 * 
	 * @param oLocation
	 *            The {@code UTMLocation} for which the network performance
	 *            sample was taken
	 * @param bandwidth
	 *            The network performance sample which is associated with the
	 *            location
	 */
	public void insertBWSample(UTMLocation oLocation, float bandwidth) {
		// INSERT INTO `bandwidth_map`( `UTMzone`, `UTMband`, `UTMnorthing`,
		// `UTMeasting`, `Bandwidth`, `N_Samples`) VALUES (?,?,?,?,?,?)

		// Set values for the fields of the table
		ContentValues values = new ContentValues();
		values.put(table_bandwidth_map.UTM_ZONE, oLocation.getZone());
		values.put(table_bandwidth_map.UTM_BAND, oLocation.getBand());
		values.put(table_bandwidth_map.UTM_EASTING, oLocation.getUTMe());
		values.put(table_bandwidth_map.UTM_NORTHING, oLocation.getUTMn());
		values.put(table_bandwidth_map.BANDWIDTH, bandwidth);
		values.put(table_bandwidth_map.N_SAMPLES, 1);

		insert(values);

	}

	public void insertBWSample(JSONObject jsonRow) {
		// Set values for the fields of the table
		ContentValues values = new ContentValues();
		try {
			values.put(table_bandwidth_map.UTM_ZONE,
					(String) jsonRow.get("UTM_ZONE"));
			values.put(table_bandwidth_map.UTM_BAND,
					(String) jsonRow.get("UTM_BAND"));
			values.put(table_bandwidth_map.UTM_EASTING,
					(String) jsonRow.get("UTM_EASTING"));
			values.put(table_bandwidth_map.UTM_NORTHING,
					(String) jsonRow.get("UTM_NORTHING"));
			values.put(table_bandwidth_map.BANDWIDTH,
					(String) jsonRow.get("BANDWIDTH"));
			values.put(table_bandwidth_map.N_SAMPLES,
					(String) jsonRow.get("N_SAMPLES"));
			values.put(table_bandwidth_map.LAST_SAMPLE,
					jsonRow.getString("LAST_SAMPLE"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		insert(values);
	}

	private void insert(ContentValues values) {
		// Insert values
		long idRow = db.insert(MySQLiteOpenHelper.TABLE_BANDWIDTH_MAP, null,
				values);
		Log.d(TAG, MySQLiteOpenHelper.TABLE_BANDWIDTH_MAP);
		Log.d(TAG, values.toString());
		if (idRow == -1) {
			Log.e(TAG, "SQL error in the method insertBWSample() ");
		} else {
			// Display what is stored in the database in LogCat
			Cursor c = db.rawQuery("SELECT * FROM bandwidth_map", null);
			c.moveToFirst();
			int count = c.getCount();
			for (int i = 0; i < count;) {
				Log.d(TAG,
						"ID = " + c.getString(0) + ", UTM_ZONE = "
								+ c.getString(1) + ", UTM_BAND = "
								+ c.getString(2) + ", UTM_EASTING = "
								+ c.getString(3) + ", UTM_NORTHING = "
								+ c.getString(4) + ", BANDWIDTH = "
								+ c.getString(5) + ", N_SAMPLES = "
								+ c.getString(6) + ", LAST_SAMPLE = "
								+ c.getString(7));
				c.moveToNext();
				i++;
			}
		}
	}

	/**
	 * This method update a network performance estimate with the info of the
	 * new sample into the table {@code bandwidth_map}
	 * 
	 * @param oLocation
	 *            The {@code UTMLocation} for which the network performance
	 *            sample was taken
	 * @param bandwidth
	 *            The network performance sample which is associated with the
	 *            location
	 */
	public void updateBWSample(UTMLocation oLocation, float bandwidth) {
		// Set the arguments for the where clause
		String[] whereArgs = new String[] {
				Integer.toString(oLocation.getZone()), oLocation.getBand(),
				Integer.toString(oLocation.getUTMe()),
				Integer.toString(oLocation.getUTMn()) };
		// Set sql query with bandwidth formula => BW = s x bandwidth + (1-s) BW
		// s=0.125
		double s = 0.125;
		String sql = "UPDATE bandwidth_map SET bandwidth= ("
				+ Float.toString(bandwidth)
				+ "*"
				+ s
				+ ")+((1-"
				+ s
				+ ")*bandwidth) , n_Samples= n_Samples+1 , last_Sample = CURRENT_TIMESTAMP "
				+ "WHERE `utmZone`=? AND `utmBand`=? AND `utmEasting`=? AND `utmNorthing`=? ";
		// Update the table
		try {
			db.execSQL(sql, whereArgs);
			// Display what is stored in the database in LogCat
			Cursor c = db.rawQuery("SELECT * FROM bandwidth_map", null);
			c.moveToFirst();
			int count = c.getCount();
			for (int i = 0; i < count;) {
				Log.d(TAG,
						"ID = " + c.getString(0) + ", UTM_ZONE = "
								+ c.getString(1) + ", UTM_BAND = "
								+ c.getString(2) + ", UTM_EASTING = "
								+ c.getString(3) + ", UTM_NORTHING = "
								+ c.getString(4) + ", BANDWIDTH = "
								+ c.getString(5) + ", N_SAMPLES = "
								+ c.getString(6) + ", LAST_SAMPLE = "
								+ c.getString(7));
				c.moveToNext();
				i++;
			}
			Log.d(TAG, "Successfully updated with method updateBWSample()");
		} catch (SQLiteException e) {
			Log.d(TAG, "Unable to update DB with updateBWSample()");
			e.printStackTrace();

		}

	}

	/**
	 * This method retrieves the data stored in the table {@code bandwidth_map}
	 * for the given location
	 * 
	 * @param oLocation
	 *            The {@code UTMLocation} for which the network performance
	 *            sample is retrieved
	 * 
	 * @return A Cursor which can be used to access the results of the query
	 */
	public Cursor selectBWSample(UTMLocation oLocation) {
		// SELECT `Bandwidth` FROM `bandwidth_map` WHERE `UTMzone`=? AND
		// `UTMband`=? AND `UTMeasting`=? AND `UTMnorthing`=?

		// Set the fields to retrieve
		String[] columns = new String[] { table_bandwidth_map.BANDWIDTH };

		// Set the where clause
		String whereClause = "`utmZone`=? AND `utmBand`=? AND `utmEasting`=? AND `utmNorthing`=? ";

		// Set the selection arguments to find the correct row
		String[] selectionArgs = new String[] {
				Integer.toString(oLocation.getZone()), oLocation.getBand(),
				Integer.toString(oLocation.getUTMe()),
				Integer.toString(oLocation.getUTMn()) };
		String groupBy = null;
		String having = null;
		String orderBy = null;

		// Execute the query
		Cursor cursor = db.query(MySQLiteOpenHelper.TABLE_BANDWIDTH_MAP,
				columns, whereClause, selectionArgs, groupBy, having, orderBy);
		return cursor;
	}

	/**
	 * This method retrieves the data stored in the table {@code bandwidth_map}
	 * for the location represented for each parameter that compound a UTM
	 * location
	 * 
	 * @param oZone
	 *            The location UTM zone
	 * @param oBand
	 *            The location UTM band
	 * @param oUTMe
	 *            The location UTM easting
	 * @param oUTMn
	 *            The location UTM northing
	 * 
	 * @return A Cursor which can be used to access the results of the query
	 */
	public Cursor selectBWSample(int oZone, String oBand, int oUTMe, int oUTMn) {
		// Set the fields to retrieve
		String[] columns = new String[] { table_bandwidth_map.BANDWIDTH };
		// Set the where clause
		String whereClause = "`utmZone`=? AND `utmBand`=? AND `utmEasting`=? AND `utmNorthing`=? ";
		// Set the selection arguments to find the correct row
		String[] selectionArgs = new String[] { Integer.toString(oZone), oBand,
				Integer.toString(oUTMe), Integer.toString(oUTMn) };
		String groupBy = null;
		String having = null;
		String orderBy = null;
		// Execute the query
		Cursor cursor = db.query(MySQLiteOpenHelper.TABLE_BANDWIDTH_MAP,
				columns, whereClause, selectionArgs, groupBy, having, orderBy);
		return cursor;
	}

	/**
	 * This method states whether or not exist a row into the table
	 * {@code bandwidth_map} for the given location
	 * 
	 * @param oLocation
	 *            The {@code UTMLocation} to check the existence of data
	 * 
	 * @return {@code TRUE} if there is a row otherwise {@code FALSE}
	 */
	public boolean existsBWSample(UTMLocation oLocation) {

		Cursor cur = selectBWSample(oLocation);

		if (cur.getCount() != 0) {
			cur.close();
			return true;
		} else {
			cur.close();
			return false;
		}
	}

	public Cursor getTableCursor() {
		Log.d(TAG, "Get cursor");
		Cursor c = db.rawQuery("SELECT * FROM bandwidth_map", null);
		Log.d(TAG, "Cursor gotten");
		return c;
	}

}