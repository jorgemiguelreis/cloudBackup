package com.google.android.gms.drive.sample.quickstart;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class UserSettingActivity extends PreferenceActivity 
{
             
             @Override
             public void onCreate(Bundle savedInstanceState) 
             {
                     super.onCreate(savedInstanceState);
                     Log.i("UserSettingActivity", "Em UserSettingActivity");
                     addPreferencesFromResource(R.xml.user_settings);
                     
             }
}
