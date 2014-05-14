package oybek.moodnote;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * Custom cursor adapter.
 */
public class MoodCursorAdapter extends CursorAdapter implements Constants {
	private LayoutInflater inflater = null;

	public MoodCursorAdapter(Context context, Cursor cursor, int flags) {
		super(context, cursor, 0);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return inflater.inflate(R.layout.listview_mood, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ImageView icon = (ImageView) view.findViewById(R.id.item_emoticon);
		icon.setImageResource(arr_emoticon[cursor
				.getInt(DatabaseHelper.COL_EMOTICON)]);

		TextView message = (TextView) view.findViewById(R.id.item_note);
		message.setText(cursor.getString(DatabaseHelper.COL_NOTE));

		TextView date = (TextView) view.findViewById(R.id.item_date);
		date.setText(cursor.getString(DatabaseHelper.COL_DATE));
	}
}
