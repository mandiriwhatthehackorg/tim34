package com.mandiri.whatthehack.onboarding.domain;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {
    private static final String keySharedPreferences = "WhatTheHack";

    public static float get(Context context, String key, float defaultValue) {
        return context.getSharedPreferences(keySharedPreferences, 0).getFloat(key, defaultValue);
    }

    public static boolean get(Context context, String key, boolean defaultValue) {
        return context.getSharedPreferences(keySharedPreferences, 0).getBoolean(key, defaultValue);
    }

    public static String get(Context context, String key, String defaultValue) {
        return context.getSharedPreferences(keySharedPreferences, 0).getString(key, defaultValue);
    }

    public static int get(Context context, String key, int defaultValue) {
        return context.getSharedPreferences(keySharedPreferences, 0).getInt(key, defaultValue);
    }

    public static long get(Context context, String key, long defaultValue) {
        return context.getSharedPreferences(keySharedPreferences, 0).getLong(key, defaultValue);
    }

    public static void set(Context context, String key, long value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(keySharedPreferences, 0).edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void set(Context context, String key, int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(keySharedPreferences, 0).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void set(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(keySharedPreferences, 0).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void set(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(keySharedPreferences, 0).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void set(Context context, String key, float value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(keySharedPreferences, 0).edit();
        editor.putFloat(key, value);
        editor.apply();
    }
}
