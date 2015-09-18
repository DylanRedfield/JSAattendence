package me.dylanredfield.jsaattendence;

import android.app.Application;

import com.parse.Parse;

public class JSAattendence extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, Keys.APP_ID, Keys.CLIENT_KEY);
    }
}
