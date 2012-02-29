package de.akuz.android.utmumrechner.data;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocationDatabase {

	public final static int DATABASE_VERSION = 1;
	public final static String DATABASE_NAME = "locations.db";

	private final static String LOCATIONS_TABLE_NAME = "locations";
	private final static String LOCATIONS_ID = "_id";
	private final static String LOCATIONS_NAME = "name";
	private final static String LOCATIONS_DESCRIPTION = "description";
	private final static String LOCATIONS_COORDINATES = "coordinates";
	private final static String LOCATIONS_PICTURE = "picture_url";

	private final static String CREATE_DATABASE = "CREATE TABLE "
			+ LOCATIONS_TABLE_NAME + " (" + LOCATIONS_ID
			+ " integer primary key autoincrement," + LOCATIONS_NAME
			+ " text not null," + LOCATIONS_DESCRIPTION + " text not null,"
			+ LOCATIONS_COORDINATES + " text not null," + LOCATIONS_PICTURE
			+ " text);";

	public class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_DATABASE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

	}

	private DatabaseHelper myHelper;

	private SQLiteDatabase db;

	private String[] allColumns = { LOCATIONS_COORDINATES,
			LOCATIONS_DESCRIPTION, LOCATIONS_ID, LOCATIONS_NAME,
			LOCATIONS_PICTURE };

	public LocationDatabase(Context context) {
		myHelper = new DatabaseHelper(context);
	}

	public void open() throws SQLException {
		db = myHelper.getWritableDatabase();
	}

	public void close() {
		db.close();
	}

	public TargetLocation createTargetLocation(String name, String coordinates,
			String description) {
		ContentValues values = new ContentValues();
		values.put(LOCATIONS_NAME, name);
		values.put(LOCATIONS_COORDINATES, coordinates);
		values.put(LOCATIONS_DESCRIPTION, description);

		long insertId = db.insert(LOCATIONS_TABLE_NAME, null, values);

		return getLocationById(insertId);
	}

	public TargetLocation getLocationById(long id) {
		Cursor cursor = db.query(LOCATIONS_TABLE_NAME, allColumns, LOCATIONS_ID
				+ " = " + id, null, null, null, null);
		cursor.moveToFirst();

		TargetLocation location = cursorToLocation(cursor);
		cursor.close();
		return location;
	}

	private TargetLocation cursorToLocation(Cursor cursor) {
		int nameColumn = cursor.getColumnIndex(LOCATIONS_NAME);
		int coordinatesColumn = cursor.getColumnIndex(LOCATIONS_COORDINATES);
		int descriptionColumn = cursor.getColumnIndex(LOCATIONS_DESCRIPTION);
		int idColumn = cursor.getColumnIndex(LOCATIONS_ID);
		int pictureColumn = cursor.getColumnIndex(LOCATIONS_PICTURE);

		TargetLocation location = new TargetLocation();
		location.setDescription(cursor.getString(descriptionColumn));
		location.setId(cursor.getLong(idColumn));
		location.setMgrsCoordinate(cursor.getString(coordinatesColumn));
		location.setPictureUrl(cursor.getString(pictureColumn));
		location.setName(cursor.getString(nameColumn));
		return location;
	}

	public List<TargetLocation> getAllLocations() {
		List<TargetLocation> locations = new LinkedList<TargetLocation>();
		Cursor cursor = db.query(LOCATIONS_TABLE_NAME, allColumns, null, null,
				null, null, null);
		if (cursor.moveToFirst()) {
			do {
				locations.add(cursorToLocation(cursor));
			} while (cursor.moveToNext());
		}
		return Collections.unmodifiableList(locations);
	}

	public void deleteLocation(TargetLocation location) {
		long id = location.getId();
		db.delete(LOCATIONS_TABLE_NAME, LOCATIONS_ID + " = " + id, null);
	}

}
