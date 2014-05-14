package oybek.moodnote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = "DBAdapter";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_EMOTICON = "emoticon";
	public static final String KEY_NOTE = "message";
	public static final String KEY_DATE = "date";
	public static final int COL_ROWID = 0;
	public static final int COL_EMOTICON = 1;
	public static final int COL_NOTE = 2;
	public static final int COL_DATE = 3;

	public static final String[] ALL_KEYS = new String[] { KEY_ROWID,
			KEY_EMOTICON, KEY_NOTE, KEY_DATE };

	public static final String DATABASE_NAME = "mnDB";
	public static final String DATABASE_TABLE = "mainTable";
	public static final int DATABASE_VERSION = 4;

	private static final String DATABASE_CREATE_SQL = "create table "
			+ DATABASE_TABLE
			+ " ("
			+ KEY_ROWID
			+ " integer primary key autoincrement, "

			// TODO: Place your fields here!
			// + KEY_{...} + " {type} not null"
			// - Key is the column name you created above.
			// - {type} is one of: text, integer, real, blob
			// (http://www.sqlite.org/datatype3.html)
			// - "not null" means it is a required field (must be given a
			// value).
			// NOTE: All must be comma separated (end of line!) Last one must
			// have NO comma!!
			+ KEY_EMOTICON + " integer not null, " + KEY_NOTE
			+ " text not null, " + KEY_DATE + " text not null"

			+ ");";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase _db) {
		_db.execSQL(DATABASE_CREATE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading application's database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data!");

		_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
		onCreate(_db);
	}
}
