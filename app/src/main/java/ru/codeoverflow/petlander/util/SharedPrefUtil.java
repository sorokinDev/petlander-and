package ru.codeoverflow.petlander.util;

import android.content.Context;
import android.content.SharedPreferences;

import ru.codeoverflow.petlander.App;

public class SharedPrefUtil {

    private static final String SHARED_PREF_NAME = "petlanderPref";
    private interface Key {
        String IS_AUTHORIZED = "is_authorized";
    }

    private static SharedPreferences sharedPref;

    public static void init() {
        sharedPref = App.getContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor() {
        return sharedPref.edit();
    }

    public static boolean isUserAuthorized() {
        return sharedPref.getBoolean(Key.IS_AUTHORIZED, false);
    }

    public static void setUserAuthorized() {
        getEditor().putBoolean(Key.IS_AUTHORIZED, true).commit();
    }

    public static void setUserNotAuthorized() {
        getEditor().putBoolean(Key.IS_AUTHORIZED, false).commit();
    }
}
