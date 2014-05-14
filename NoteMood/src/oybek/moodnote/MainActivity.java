package oybek.moodnote;

import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity implements Constants,
		OnClickListener, OnItemLongClickListener,
		LoaderManager.LoaderCallbacks<Cursor> {

	private static final int LOADER_ID = 1;
	private DatabaseHelper dbHelper;
	private ListView moodList;
	private MoodCursorAdapter mAdapter;
	private long moodId;
	protected Object mActionMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		dbHelper = new DatabaseHelper(this);
		mAdapter = new MoodCursorAdapter(this, null, 0);
		moodList = (ListView) findViewById(R.id.moodsListView);
		moodList.setAdapter(mAdapter);
		moodList.setOnItemLongClickListener(this);

		ImageButton btn = (ImageButton) findViewById(R.id.addBtn);
		btn.setOnClickListener(this);

		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		spinner.setAdapter(new MoodSpinnerAdapter(MainActivity.this,
				R.layout.spinner_list_emoticon, arr_emoticon_strings));

		getLoaderManager().initLoader(LOADER_ID, null, this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		dbHelper.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(MainActivity.this, MoodSettings.class));
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		if (mActionMode != null) {
			return false;
		}
		
		Log.i("item id from list", "" + id);
		Log.i("item position from list", "" + position);
		
		moodId = id;
		mActionMode = MainActivity.this.startActionMode(mActionModeCallback);
		view.setSelected(true);
		return true;
	}

	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.context_menu, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false; // Return false if nothing is done
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.menu_delete:
				boolean deleted = Mood.deleteMood(getContentResolver(), moodId);
				if (deleted) {
					Toast.makeText(MainActivity.this, "Deleted",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(MainActivity.this, "Failed to delete",
							Toast.LENGTH_SHORT).show();
				}

				mode.finish();
				return true;

			case R.id.menu_copy:
				ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				String copiedNote = Mood.getMood(getContentResolver(), moodId).note;
				ClipData clip = ClipData.newPlainText("Note", copiedNote);
				clipboard.setPrimaryClip(clip);

				mode.finish();
				return true;

			case R.id.menu_share:
				Mood mood = Mood.getMood(getContentResolver(), moodId);
				Intent shareNote = new Intent();
				shareNote.setAction(Intent.ACTION_SEND);
				shareNote.putExtra(Intent.EXTRA_TEXT, mood.note);
				shareNote.setType("text/plain");
				Intent openInChooser = Intent.createChooser(shareNote,
						"Share with...");
				startActivityForResult(openInChooser, 0);

				mode.finish();
				return true;

			default:
				return false;
			}
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
		}
	};

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
		return Mood.getMoodsCursorLoader(this);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.changeCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.changeCursor(null);
	}

	@Override
	public void onClick(View v) {
		// add to db
		Mood mood = new Mood(getEmoticon(), getNote(), getFormatDateAndTime());
		Mood.addMood(getContentResolver(), mood);

		// play sound
		MediaPlayer mediaplayer = MediaPlayer.create(this, R.raw.bottlepop);
		if (mediaplayer == null) {
			Log.v("onClick()", "Create() on MediaPlayer failed.");
		} else {
			mediaplayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mediaplayer) {
					mediaplayer.stop();
					mediaplayer.release();
				}
			});
			mediaplayer.start();
		}

		// refresh
		EditText editText = (EditText) findViewById(R.id.editNote);
		editText.setText("");
		moodList.smoothScrollToPosition(0);

	}

	private int getEmoticon() {
		Spinner spinner = (Spinner) findViewById(R.id.spinner);
		int emoticon = spinner.getSelectedItemPosition();
		return emoticon;
	}

	private String getNote() {
		EditText editText = (EditText) findViewById(R.id.editNote);
		String note = editText.getText().toString();
		return note;
	}

	// Convert the current date into string
	// Format is varied depending on region
	private String getFormatDateAndTime() {
		Calendar calendar = Calendar.getInstance();

		if (Locale.getDefault().equals(Locale.US)) {
			// format example: Jan 9, 2014 - 7:20 PM
			int minute = calendar.get(Calendar.MINUTE);
			String formatMinute;
			if (minute < 10) {
				formatMinute = "0" + minute;
			} else {
				formatMinute = String.valueOf(minute);
			}

			int formatHour = calendar.get(Calendar.HOUR);
			if (formatHour == 0)
				formatHour = 12;
			String date = calendar.getDisplayName(Calendar.MONTH,
					Calendar.SHORT, Locale.US)
					+ " "
					+ calendar.get(Calendar.DAY_OF_MONTH)
					+ ", "
					+ calendar.get(Calendar.YEAR)
					+ " - "
					+ formatHour
					+ ":"
					+ formatMinute
					+ " "
					+ calendar.getDisplayName(Calendar.AM_PM, Calendar.SHORT,
							Locale.US);
			return date;

		} else {
			// format example: 9 January, 2014 - 19:20
			int minute = calendar.get(Calendar.MINUTE);
			String formatMinute;
			if (minute < 10) {
				formatMinute = "0" + minute;
			} else {
				formatMinute = String.valueOf(minute);
			}

			String date = calendar.get(Calendar.DAY_OF_MONTH)
					+ " "
					+ calendar.getDisplayName(Calendar.MONTH, Calendar.LONG,
							Locale.US) + ", " + calendar.get(Calendar.YEAR)
					+ " - " + calendar.get(Calendar.HOUR_OF_DAY) + ":"
					+ formatMinute;

			return date;
		}
	}
}
