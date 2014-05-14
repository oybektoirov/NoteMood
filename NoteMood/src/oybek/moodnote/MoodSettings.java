package oybek.moodnote;

import java.util.Calendar;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

public class MoodSettings extends PreferenceActivity implements Constants,
		OnPreferenceChangeListener, OnPreferenceClickListener {

	private static Preference prefSetTime;
	private static int mHour, mHourOfDay, mMinute;
	private static SharedPreferences settings;
	private static SharedPreferences.Editor prefEditor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		settings = getSharedPreferences(SETTINGS, MODE_PRIVATE);
		prefEditor = settings.edit();
		boolean state = settings.getBoolean(KEY_ENABLE_NOTIFICATION, false);

		CheckBoxPreference checkBoxPref = (CheckBoxPreference) findPreference(KEY_ENABLE_NOTIFICATION);
		checkBoxPref.setChecked(state);
		checkBoxPref.setOnPreferenceChangeListener(this);

		prefSetTime = (Preference) findPreference(KEY_SET_TIME);
		prefSetTime.setEnabled(state);
		setTimeSummary();
		prefSetTime.setOnPreferenceClickListener(this);
		prefSetTime.setOnPreferenceChangeListener(this);

		Preference prefShare = (Preference) findPreference(KEY_SHARE);
		prefShare.setOnPreferenceClickListener(this);

		Preference prefRate = (Preference) findPreference(KEY_RATE);
		prefRate.setOnPreferenceClickListener(this);

		Preference prefDeleteAllNotes = (Preference) findPreference(KEY_DELETE_ALL_NOTES);
		prefDeleteAllNotes.setOnPreferenceClickListener(this);

		Preference prefAbout = (Preference) findPreference(KEY_ABOUT);
		prefAbout.setOnPreferenceClickListener(this);
		getListView().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.custom_background));
	}

	public static class TimePickerFragment extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Log.e("onCreateDialog", mHourOfDay + " " + mMinute);
			return new TimePickerDialog(getActivity(), this, mHourOfDay,
					mMinute, DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Log.e("onTimeSet", "" + hourOfDay);

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
			calendar.set(Calendar.MINUTE, minute);

			prefEditor.putInt(KEY_HOUR_OF_DAY, hourOfDay);
			prefEditor.putInt(KEY_HOUR, calendar.get(Calendar.HOUR));
			prefEditor.putInt(KEY_MINUTE, minute);

			boolean settingsSaved = prefEditor.commit();
			if (settingsSaved) {
				setTimeSummary();

				Intent intent = new Intent(getActivity(),
						MoodBroadcastReceiver.class);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						getActivity(), 0, intent, 0);
				AlarmManager alarmManager = (AlarmManager) getActivity()
						.getSystemService(ALARM_SERVICE);
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
						calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
						pendingIntent);
				Toast.makeText(getActivity(), "Notification time is set",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), "Failed to set time",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public boolean onPreferenceChange(Preference pref, Object newValue) {
		if (pref.getKey().equals(KEY_ENABLE_NOTIFICATION)) {
			boolean isOn = Boolean.parseBoolean(newValue.toString());
			if (isOn) {
				Log.i("On", "" + isOn);
				prefSetTime.setEnabled(true);
				prefEditor.putBoolean(KEY_ENABLE_NOTIFICATION, true);
				prefEditor.apply();

				mHourOfDay = settings.getInt(KEY_HOUR_OF_DAY,
						DEFAULT_HOUR_OF_DAY);
				mMinute = settings.getInt(KEY_MINUTE, DEFAULT_MINUTE);

				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.HOUR_OF_DAY, mHourOfDay);
				calendar.set(Calendar.MINUTE, mMinute);

				Intent intent = new Intent(this, MoodBroadcastReceiver.class);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
						0, intent, 0);
				AlarmManager alarmManager = (AlarmManager) this
						.getSystemService(ALARM_SERVICE);
				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
						calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
						pendingIntent);
				Toast.makeText(this, "Notification time is set",
						Toast.LENGTH_SHORT).show();
			} else {
				Log.i("Off", "" + isOn);
				prefSetTime.setEnabled(false);
				prefEditor.putBoolean(KEY_ENABLE_NOTIFICATION, false);
				prefEditor.apply();

				// Cancel notification alarm
				Intent intent = new Intent(this, MoodBroadcastReceiver.class);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
						0, intent, 0);
				AlarmManager alarmManager = (AlarmManager) this
						.getSystemService(ALARM_SERVICE);
				alarmManager.cancel(pendingIntent);
			}
		}

		return true;
	}

	@Override
	public boolean onPreferenceClick(Preference pref) {
		if (pref.getKey().equals(KEY_SET_TIME)) {
			DialogFragment newFragment = new TimePickerFragment();
			newFragment.show(getFragmentManager(), "timePicker");
		} else if (pref.getKey().equals(KEY_SHARE)) {
			Intent shareApp = new Intent();
			shareApp.setAction(Intent.ACTION_SEND);
			shareApp.putExtra(Intent.EXTRA_SUBJECT, SHARE_SUBJECT);
			shareApp.putExtra(
					Intent.EXTRA_TEXT, SHARE_TEXT);
			shareApp.setType("text/plain");
			Intent openInChooser = Intent.createChooser(shareApp,
					"Share with...");
			startActivityForResult(openInChooser, 0);
		} else if (pref.getKey().equals(KEY_RATE)) {
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(MARKET_LINK));
				startActivity(intent);
			} catch (Exception e) {
				Toast.makeText(this, "Failed. Check your Internet connection.", 
						Toast.LENGTH_SHORT).show();
			}

		} else if (pref.getKey().equals(KEY_DELETE_ALL_NOTES)) {
			deleteAllNotes();
		} else if (pref.getKey().equals(KEY_ABOUT)) {
			showAbout();
		}

		return true;
	}

	private static String getAM_PM() {
		Calendar time = Calendar.getInstance();
		time.set(Calendar.HOUR_OF_DAY, mHourOfDay);
		String am_pm = "";
		if (time.get(Calendar.AM_PM) == Calendar.AM) {
			am_pm = "AM";
		} else if (time.get(Calendar.AM_PM) == Calendar.PM) {
			am_pm = "PM";
		}

		return am_pm;
	}

	private static void setTimeSummary() {
		mHourOfDay = settings.getInt(KEY_HOUR_OF_DAY, DEFAULT_HOUR_OF_DAY);
		mHour = settings.getInt(KEY_HOUR, DEFAULT_HOUR);
		mMinute = settings.getInt(KEY_MINUTE, DEFAULT_MINUTE);

		Log.i("setSummary", "mHour: " + mHour + ", mHourOfDay: " + mHourOfDay);

		String minute = "";
		if (mMinute < 10) {
			minute = "0" + mMinute;
		} else {
			minute = String.valueOf(mMinute);
		}

		// Set time format for summary based on locale
		if (Locale.getDefault().equals(Locale.US)) {
			// Android bug: midnight comes as 0
			if (mHour == 0) {
				mHour = 12;
			}
			prefSetTime.setSummary(mHour + ":" + minute + " " + getAM_PM());
		} else {
			prefSetTime.setSummary(mHourOfDay + ":" + minute);
		}
	}

	private void deleteAllNotes() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.dialog_delete_all_title)
				.setMessage(R.string.dialog_delete_all_message)
				.setPositiveButton(R.string.dialog_delete_all_positive,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								boolean allDeleted = Mood
										.deleteMoods(getContentResolver());
								if (allDeleted) {
									Toast.makeText(MoodSettings.this,
											"All notes are deleted",
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(
											MoodSettings.this,
											"Nothing to delete or failed to delete",
											Toast.LENGTH_SHORT).show();
								}
							}
						})
				.setNegativeButton(R.string.dialog_delete_all_negative,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// just ignore
							}
						}).show();
	}

	private void showAbout() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.pref_about_title)
				.setMessage(R.string.pref_about_message)
				.setNeutralButton(R.string.pref_about_close,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).show();
	}
}
