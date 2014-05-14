package oybek.moodnote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class MoodSpinnerAdapter extends ArrayAdapter<String> implements Constants {

	private LayoutInflater mInflater;

	public MoodSpinnerAdapter(Context context, int textViewResourceId,
			String[] objects) {
		super(context, textViewResourceId, objects);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	public View getCustomView(int position, View convertView, ViewGroup parent) {

		View row = mInflater.inflate(R.layout.spinner_list_emoticon, parent, false);
		ImageView emoticon = (ImageView) row.findViewById(R.id.spinner_emo);
		emoticon.setImageResource(arr_emoticon[position]);

		return row;
	}
}
