package oybek.moodnote;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class MoodProvider extends ContentProvider {

	public static final String AUTHORITY = "oybek.moodnote.MoodProvider";

	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + DatabaseHelper.DATABASE_TABLE);

	private static final int MOODS = 1;
	private static final int MOODS_ID = 2;

	private DatabaseHelper dbHelper;

	private static final UriMatcher uriMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		uriMatcher.addURI(AUTHORITY, DatabaseHelper.DATABASE_TABLE, MOODS);
		uriMatcher.addURI(AUTHORITY, DatabaseHelper.DATABASE_TABLE + "/#",
				MOODS_ID);
	}

	@Override
	public boolean onCreate() {
		dbHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String orderBy) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		int match = uriMatcher.match(uri);
		switch (match) {
			case MOODS:
				qb.setTables(DatabaseHelper.DATABASE_TABLE);
				break;
			case MOODS_ID:
				qb.setTables(DatabaseHelper.DATABASE_TABLE);
				qb.appendWhere(DatabaseHelper.KEY_ROWID + "=");
				qb.appendWhere(uri.getLastPathSegment());
				break;
			default:
				throw new IllegalArgumentException("Unknown URL " + uri);
		}
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = qb.query(db, projection,
				selection, selectionArgs, null, null, DatabaseHelper.KEY_ROWID
						+ " DESC");
		if (cursor == null) {
			Log.e("Moods.query", "FAILED");
		} else {
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
		}

		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long rowId;
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (uriMatcher.match(uri)) {
			case MOODS:
				rowId = db.insert(DatabaseHelper.DATABASE_TABLE, null, values);
				break;
			default:
				throw new IllegalArgumentException("Cannot insert from URL: " + uri);
		}
		
		Uri uriResult = ContentUris.withAppendedId(CONTENT_URI, rowId);
		getContext().getContentResolver().notifyChange(uriResult, null);
		return uriResult;
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		int count;
		String primaryKey;
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (uriMatcher.match(uri)) {
			case MOODS:
				count = db.delete(DatabaseHelper.DATABASE_TABLE, where, whereArgs);
				break;
			case MOODS_ID:
				primaryKey = uri.getLastPathSegment();
				if (TextUtils.isEmpty(where)) {
					where = DatabaseHelper.KEY_ROWID + "=" + primaryKey;
				} else {
					where = DatabaseHelper.KEY_ROWID + "=" + primaryKey + " AND ("
							+ where + ")";
				}
				
				count = db.delete(DatabaseHelper.DATABASE_TABLE, where, whereArgs);
				break;
		default:
			throw new IllegalArgumentException("Cannot insert from URL: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		int count;
		String moodId;
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (uriMatcher.match(uri)) {
			case MOODS_ID:
				moodId = uri.getLastPathSegment();
				count = db.update(DatabaseHelper.DATABASE_TABLE, values,
						DatabaseHelper.KEY_ROWID + "=" + moodId, null);
				break;
			default: {
				throw new UnsupportedOperationException("Cannot update URL: " + uri);
			}
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		int match = uriMatcher.match(uri);
		switch (match) {
			case MOODS:
				return "vnd.android.cursor.dir/" + DatabaseHelper.DATABASE_TABLE;
			case MOODS_ID:
				return "vnd.android.cursor.item/" + DatabaseHelper.DATABASE_TABLE;
			default:
				throw new IllegalArgumentException("Unknown URL");
		}
	}

}