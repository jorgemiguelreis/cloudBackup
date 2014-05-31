package com.google.android.gms.drive.sample.quickstart;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dropbox.android.sample.DropBoxService;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

public class MainActivity extends Activity {

	private static boolean runGDrive = true;
	private static boolean runDropbox = true;
	public static String operationGDrive = null;
	public static  String operationDropbox = null;
	public static String dropbox;
	
	final static private String APP_KEY = "8v9kx36fibrd7vr";
    final static private String APP_SECRET = "8zvnrqnrj2qg4hb";
    		
	final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
	
	public static DropboxAPI<AndroidAuthSession> mDBApi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		if (runGDrive) {
			//TODO AUTENTICAR AQUI drive
			
			operationGDrive = "upload";
			System.out.println("EM GDRIVE");
			Intent gdriveIntent = new Intent(this,GdriveService.class);
			startActivity(gdriveIntent);
			
		}
		
		if (runDropbox) {
			System.out.println("EM DROPBOX");
			
			AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
			AndroidAuthSession session = new AndroidAuthSession(appKeys);
			mDBApi = new DropboxAPI<AndroidAuthSession>(session);
			
			
			mDBApi.getSession().startOAuth2Authentication(MainActivity.this);
			
			
			
			
			
		}
	}
	
	protected void onResume() {
	    super.onResume();

	    if (runDropbox) {
	    	
	    	
		    	if (mDBApi.getSession().authenticationSuccessful()) 
		    	{
		    		try {
		    			// Required to complete auth, sets the access token on the session
		    			mDBApi.getSession().finishAuthentication();

		    			String accessToken = null;
		    			while (accessToken == null) {
		    				accessToken = mDBApi.getSession().getOAuth2AccessToken();
							
						}
		    			//accessToken = mDBApi.getSession().getOAuth2AccessToken();


		    			Context context = getBaseContext(); 
		    			Intent dropboxIntent = new Intent(context, DropBoxService.class);
		    			System.out.println("Intent declarado");

		    			dropboxIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    			System.out.println("flag added");
		    			dropboxIntent.putExtra(operationDropbox, "upload");
		    			System.out.println("operationDropbox is upload");
		    			
		    			
		    			context.startService(dropboxIntent);


		    		} catch (IllegalStateException e) {
		    			Log.i("DbAuthLog", "Error authenticating", e);
		    		}
			    }	
		}
	    
	}
}
