package com.example.weather.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * sharepreference封装
 */
public class PrefUtils {

	private static SharedPreferences mPref;

	private static SharedPreferences getPreferences(Context ctx) {
		if (mPref == null) {//只初始化一次
			mPref = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
		}

		return mPref;
	}

	public static boolean getBoolean(Context ctx, String key, boolean defValue) {
		return getPreferences(ctx).getBoolean(key, defValue);
	}

	public static void putBoolean(Context ctx, String key, boolean value) {
		getPreferences(ctx).edit().putBoolean(key, value).commit();
	}

	public static String getString(Context ctx, String key, String defValue) {
		return getPreferences(ctx).getString(key, defValue);
	}

	public static void putString(Context ctx, String key, String value) {
		getPreferences(ctx).edit().putString(key, value).commit();
	}

	public static int getInt(Context ctx, String key, int defValue) {
		return getPreferences(ctx).getInt(key, defValue);
	}

	public static void putInt(Context ctx, String key, int value) {
		getPreferences(ctx).edit().putInt(key, value).commit();
	}
	public static long getLong(Context ctx, String key, long defValue) {
		return getPreferences(ctx).getLong(key, defValue);
	}

	public static void putLong(Context ctx, String key, long value) {
		getPreferences(ctx).edit().putLong(key, value).commit();
	}

	public static void remove(Context ctx, String key) {
		getPreferences(ctx).edit().remove(key).commit();
	}

}
