package oybek.moodnote;

public interface Constants {
	
	public static final String[] arr_emoticon_strings = { "smile", "happy", "inlove", "angry", "crying",
			"hurts", "dizzy", "sick", "music", "omg", "seriously", "drunk",
			"slow", "snooty", "want", "wtf", "wut", "xd", "yuush", "zzz" };
	
	public static final int arr_emoticon[] = { R.drawable.smile, R.drawable.happy, R.drawable.inlove,
			R.drawable.angry, R.drawable.crying, R.drawable.hurts,
			R.drawable.dizzy, R.drawable.sick, R.drawable.music,
			R.drawable.omg, R.drawable.seriously, R.drawable.drunk,
			R.drawable.slow, R.drawable.snooty, R.drawable.want,
			R.drawable.wtf, R.drawable.wut, R.drawable.xd, R.drawable.yuush,
			R.drawable.zzz };
	
	public static final String SETTINGS = "settings";
	public static final String ADVICE_NOTICE = "advice_notice";
	public static final String KEY_ENABLE_NOTIFICATION = "enable_notification";
	public static final String KEY_SET_TIME = "set_time";
	public static final String KEY_SHARE = "key_share";
	public static final String KEY_RATE = "key_rate";
	public static final String KEY_DELETE_ALL_NOTES = "key_delete_all_notes";
	public static final String KEY_ABOUT = "key_about";
	public static final String KEY_HOUR_OF_DAY = "hour_of_day";
	public static final String KEY_HOUR = "hour";
	public static final String KEY_MINUTE = "minute";
	public static final int DEFAULT_HOUR = 9;
	public static final int DEFAULT_HOUR_OF_DAY = 21;
	public static final int DEFAULT_MINUTE = 0;
	
	public static final String NOTIFICATION_CONTENT_TITLE = "NoteMood notification";
	public static final String NOTIFICATION_CONTENT_TEXT = "It is time to take a note of your mood";
	
	public static final String SHARE_SUBJECT = "Check out NoteMood!";
	public static final String SHARE_TEXT = "Download NoteMood app and take notes of your mood. " +
			"http://play.google.com/store/apps/details?id=oybek.moodnote";
	
	public static final String MARKET_LINK = "market://details?id=oybek.moodnote";

}
