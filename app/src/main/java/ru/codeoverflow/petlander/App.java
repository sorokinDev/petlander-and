package ru.codeoverflow.petlander;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import ru.codeoverflow.petlander.util.SharedPrefUtil;

public class App extends Application {

    private static Application sApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
        SharedPrefUtil.init();
    }

    public static Application getApplication() {
        return sApplication;
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }


}
