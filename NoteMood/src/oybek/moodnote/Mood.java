package oybek.moodnote;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class Mood {

	public static final long INVALID_ID = -1;

	public static ContentValues createContentValues(Mood mood) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.KEY_EMOTICON, mood.emoticon);
		values.put(DatabaseHelper.KEY_NOTE, mood.note);
		values.put(DatabaseHelper.KEY_DATE, mood.date);
		return values;
	}

	public static Intent createIntent(String action, long moodId) {
		return new Intent(action).setData(getUri(moodId));
	}

	public static Intent createIntent(Context context, Class<?> cls,
			long moodId) {
		return new Intent(context, cls).setData(getUri(moodId));
	}

	public static Uri getUri(long moodId) {
		return ContentUris.withAppendedId(MoodProvider.CONTENT_URI, moodId);
	}

	public static long getId(Uri contentUri) {
		return ContentUris.parseId(contentUri);
	}

	/**
	 * Get mood cursor loader for all moods.
	 * 
	 * @param context
	 *            to query the database.
	 * @return cursor loader with all the moods.
	 */
	public static CursorLoader getMoodsCursorLoader(Context context) {
		return new CursorLoader(context, MoodProvider.CONTENT_URI,
				DatabaseHelper.ALL_KEYS, null, null, null);
	}

	/**
	 * Get mood by id.
	 * 
	 * @param contentResolver
	 *            to perform the query on.
	 * @param moodId
	 *            for the desired mood.
	 * @return mood if found, null otherwise
	 */
	public static Mood getMood(ContentResolver contentResolver, long moodId) {
		String s = ""+getUri(moodId);
		Log.e("getMoodUri", s);
		Cursor cursor = contentResolver.query(getUri(moodId),
				DatabaseHelper.ALL_KEYS, null, null, null);
		Mood result = null;
		if (cursor == null) {
			return result;
		}

		try {
			if (cursor.moveToFirst()) {
				result = new Mood(cursor);
				
			}
		} finally {
			cursor.close();
		}

		return result;
	}

	/**
	 * Get all moods given conditions.
	 * 
	 * @param contentResolver
	 *            to perform the query on.
	 * @param selection
	 *            A filter declaring which rows to return, formatted as an SQL
	 *            WHERE clause (excluding the WHERE itself). Passing null will
	 *            return all rows for the given URI.
	 * @param selectionArgs
	 *            You may include ?s in selection, which will be replaced by the
	 *            values from selectionArgs, in the order that they appear in
	 *            the selection. The values will be bound as Strings.
	 * @return list of moods matching where clause or empty list if none found.
	 */
	public static List<Mood> getMoods(ContentResolver contentResolver,
			String selection, String... selectionArgs) {
		Cursor cursor = contentResolver.query(MoodProvider.CONTENT_URI,
				DatabaseHelper.ALL_KEYS, selection, selectionArgs, null);
		List<Mood> result = new LinkedList<Mood>();
		if (cursor == null) {
			return result;
		}

		try {
			if (cursor.moveToFirst()) {
				do {
					result.add(new Mood(cursor));
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
		}

		return result;
	}

	public static Mood addMood(ContentResolver contentResolver, Mood mood) {
		ContentValues values = createContentValues(mood);
		Uri uri = contentResolver.insert(MoodProvider.CONTENT_URI, values);
		mood.id = getId(uri); 
		return mood;
	}

	public static boolean deleteMood(ContentResolver contentResolver, long moodId) {
		if (moodId == INVALID_ID)
			return false;
		int deletedRows = contentResolver.delete(getUri(moodId), "", null);
		return deletedRows == 1;
	}
	
	public static boolean deleteMoods(ContentResolver contentResolver) {
		Cursor cursor = contentResolver.query(MoodProvider.CONTENT_URI,
				DatabaseHelper.ALL_KEYS, null, null, null);
		if (cursor == null) {
			return false;
		}

		try {
			if (cursor.moveToFirst()) {
				do {
					long rowId = cursor.getLong(DatabaseHelper.COL_ROWID);
					contentResolver.delete(getUri(rowId), "", null);
				} while (cursor.moveToNext());
			} else {
				return false;
			}
		} finally {
			cursor.close();
		}
		
		return true;
	}
	
//	//Not used for now
//	public static boolean updateMood(ContentResolver contentResolver, Mood mood) {
//		if (mood.id == Mood.INVALID_ID)
//			return false;
//		ContentValues values = createContentValues(mood);
//		long rowsUpdated = contentResolver.update(getUri(mood.id), values,
//				null, null);
//		return rowsUpdated == 1;
//	}

	public long id;
	public int emoticon;
	public String note;
	public String date;

	public Mood(int emoticon, String note, String date) {
		super();
		this.emoticon = emoticon;
		this.note = note;
		this.date = date;
	}

	public Mood(Cursor c) {
		id = c.getLong(DatabaseHelper.COL_ROWID);
		emoticon = c.getInt(DatabaseHelper.COL_EMOTICON);
		note = c.getString(DatabaseHelper.COL_NOTE);
		date = c.getString(DatabaseHelper.COL_DATE);
	}
}
