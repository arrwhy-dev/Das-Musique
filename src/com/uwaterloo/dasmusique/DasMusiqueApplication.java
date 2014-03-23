package com.uwaterloo.dasmusique;

import android.app.Application;
import android.content.Context;

public class DasMusiqueApplication extends Application {

	public static Context mContext;

	@Override
	public void onCreate() {

		mContext = getApplicationContext();

		super.onCreate();
	}

}
