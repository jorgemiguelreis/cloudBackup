package com.google.android.gms.drive.sample.quickstart;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import data.CallLogData;
import data.SMSData;
import utils.Utils;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.android.sample.DropBoxService;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;

public class MainActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener  {

	/*
	public static boolean runGDrive = true;
	public static boolean runDropbox = true;
	 */
	public static boolean driveConnected = false;

	public static String operationGDrive = "download";
	public static  String operationDropbox = "download";
	//public static String dropbox;

	private static final String TAG = "android-drive-quickstart";

	final static private String APP_KEY = "8v9kx36fibrd7vr";
	final static private String APP_SECRET = "8zvnrqnrj2qg4hb";

	final static private String ACCOUNT_PREFS_NAME = "prefs";
	final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
	final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";

	public static DropboxAPI<AndroidAuthSession> mDBApi;
	public static GoogleApiClient mGoogleApiClient;

	private static final int REQUEST_CODE_RESOLUTION = 3;

	private static final int SETTINGS_RESULT = 1;

	public static final String mainFolder = "/CloudBackupFolder/";
	public static boolean xmlCreated = false;
	Button settingButton;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		

		final File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + mainFolder);


		if(!dir.exists())
		{
			//TODO CRIAR servico de criar XML e iniciar se pasta da aplicacao nao existe
			/*
                 	Context context = getBaseContext(); 
	    			Intent createXMLIntent = new Intent(context, XMLCreateService.class);
	    			System.out.println("Intent XML declarado");

	    			createXMLIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    			System.out.println("flag added");
	    			context.startService(createXMLIntent);
			 */
			Log.i("MainActivity", "Pasta aplicacao nao existe");
			
			dir.mkdirs();   
			
			//TODO Criar e chamar servico que cria SMS e CallLog XML 
			
			Intent i = new Intent(getApplicationContext(), CreateXMLService.class);
			startActivity(i);
			
			/*
			createSMSXML();
			createCallLogXML();
			*/
		}



		Button btnSettings=(Button)findViewById(R.id.buttonSettings);
		// start the UserSettingActivity when user clicks on Button
		btnSettings.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(getApplicationContext(), UserSettingActivity.class);
				startActivityForResult(i, SETTINGS_RESULT);
			}
		});

		Button btnSms =(Button)findViewById(R.id.ButtonSMS);

		btnSms.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//TODO chamar activity

				Intent intent = new Intent(getApplicationContext(), FragmentLayout.class);
	            startActivity(intent);
				
			}
		});

		
		Button btnLogs =(Button)findViewById(R.id.ButtonLogs);

		btnLogs.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//TODO chamar activity

				Intent intent = new Intent(getApplicationContext(), FragmentLayoutLogs.class);
	            startActivity(intent);
				
			}
		});
		

		Button btnUpLoad =(Button)findViewById(R.id.ButtonUpload);

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);



		//if(sharedPrefs.getBoolean("prefDropbox", false) || sharedPrefs.getBoolean("prefGDrive", false))
		//{

		Log.i("MainActivity", "Clouds not null");

		// if ((mGoogleApiClient!= null && mGoogleApiClient.isConnected()) || (mDBApi!= null && mDBApi.getSession().isLinked())) 
		//{
		Log.i("MainActivity", "clients not null");
		btnUpLoad.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//TODO chamar servico de upload
				Log.i("MainActivity", "Dentro de onClickupload");
				Context context = getBaseContext(); 
				Intent uploadIntent = new Intent(context, uploadService.class);
				System.out.println("Uploadservice Intent declarado");
				uploadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				System.out.println("uploadintent flag added");
				context.startService(uploadIntent);
			}
		});
		// }


		//}


	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode==SETTINGS_RESULT)
		{
			//displayUserSettings();
			Log.i("MainActivity", "vai fazer verifyLogins");
			verifyLogins();
		}

	}




	private void verifyLogins() 
	{
		Log.i("MainActivity", "Dentro de verifyLogins");

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		/*
                 String  settings = "";

                 settings=settings+"Password: " + sharedPrefs.getString("prefUserPassword", "NOPASSWORD");

                 settings=settings+"\nRemind For Update:"+ sharedPrefs.getBoolean("prefLockScreen", false);

                 settings=settings+"\nUpdate Frequency: "
                         + sharedPrefs.getString("prefUpdateFrequency", "NOUPDATE");


		 */

		Log.i("MainActivity verifyLogins", "Depois de sharedPreferences");

		if(sharedPrefs.getBoolean("prefDropbox", false))
		{
			//if dropbox !loggedIn
			//fazer login

			Log.i("MainActivity verifyLogins", "Execute dropbox");

			if (mDBApi == null) 
			{
				System.out.println("EM DROPBOX");

				AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
				AndroidAuthSession session = new AndroidAuthSession(appKeys);
				mDBApi = new DropboxAPI<AndroidAuthSession>(session);


				mDBApi.getSession().startOAuth2Authentication(MainActivity.this);
			}


		}

		if(sharedPrefs.getBoolean("prefGDrive", false))
		{
			//if gdrive !loggedIn
			//fazer login
			/*
                	 Log.i("MainActivity verifyLogins", "Execute Gdrive");

                	 if (mGoogleApiClient == null) {
                		 // Create the API client and bind it to an instance variable.
                		 // We use this instance as the callback for connection and connection
                		 // failures.
                		 // Since no account name is passed, the user is prompted to choose.

                		 Log.i("MainActivity verifyLogins", "mGoogleApiClient null");
                		 mGoogleApiClient = new GoogleApiClient.Builder(this)
                		 .addApi(Drive.API)
                		 .addScope(Drive.SCOPE_FILE)
                		 .addConnectionCallbacks(this)
                		 .addOnConnectionFailedListener(this)
                		 .build();
                	 }
                	 mGoogleApiClient.connect();
			 */

			if (mGoogleApiClient == null) 
			{


				// Create the API client and bind it to an instance variable.
				// We use this instance as the callback for connection and connection
				// failures.
				// Since no account name is passed, the user is prompted to choose.

				Log.i("MainActivity verifyLogins", "mGoogleApiClient null");
				mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(Drive.API)
				.addScope(Drive.SCOPE_FILE)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();
			}
			mGoogleApiClient.connect();



		}


	}






	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		// Called whenever the API client fails to connect.
		Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
		if (!result.hasResolution()) {
			// show the localized error dialog.
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
			return;
		}
		// The failure has a resolution. Resolve it.
		// Called typically when the app is not yet authorized, and an
		// authorization
		// dialog is displayed to the user.
		try {
			result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
		} catch (SendIntentException e) {
			Log.e(TAG, "Exception while starting resolution activity", e);
		}

	}




	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		//System.out.println("EM GDRIVE");
		Log.i("MainActivity onConnected", "Gdrive connected");

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		driveConnected = true;


		if (sharedPrefs.getBoolean("prefGDrive", false))
		{
			Log.i("MainActivity", "mGoogleApiClient.isConnected()");
			/*Context context = getBaseContext(); 
					Intent uploadIntent = new Intent(context, uploadService.class);
					System.out.println("Uploadservice Intent declarado");
					uploadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					System.out.println("uploadintent flag added");
					context.startService(uploadIntent);
			 */
			Toast.makeText(getBaseContext(), "Logado com sucesso gDrive", Toast.LENGTH_SHORT);

		}





	}




	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		Log.i(TAG, "GoogleApiClient connection suspended");

	}



	protected void onResume() {
		super.onResume();

		Log.i("MainActivity", "Dentro de onResume");
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		Log.i("MainActivity", "Dentro de onResume After SharedPreferences");


		if (sharedPrefs.getBoolean("prefDropbox", false)) 
		{

			Log.i("MainActivity", "So drop");

			if (mDBApi == null) 
			{
				System.out.println("EM DROPBOX");

				AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
				AndroidAuthSession session = new AndroidAuthSession(appKeys);
				mDBApi = new DropboxAPI<AndroidAuthSession>(session);


				mDBApi.getSession().startOAuth2Authentication(MainActivity.this);
			}

			if (mDBApi.getSession().authenticationSuccessful())
			{
				Log.i("MainActivity", "drop");
				try {
					// Required to complete auth, sets the access token on the session
					mDBApi.getSession().finishAuthentication();

					String accessToken = null;
					while (accessToken == null) {
						accessToken = mDBApi.getSession().getOAuth2AccessToken();

					}
					//accessToken = mDBApi.getSession().getOAuth2AccessToken();

					/*
		    			Context context = getBaseContext(); 
		    			Intent uploadIntent = new Intent(context, uploadService.class);
		    			System.out.println("Uploadservice Intent declarado");
		    			uploadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    			System.out.println("uploadintent flag added");
		    			context.startService(uploadIntent);
					 */



				} catch (IllegalStateException e) {
					Log.i("DbAuthLog", "Error authenticating", e);
				}
			}

		}


		if (sharedPrefs.getBoolean("prefGDrive", false)) 
		{
			Log.i("MainActivity", "antes mGoogleApiClient.isConnected()");


			if (mGoogleApiClient == null) 
			{


				// Create the API client and bind it to an instance variable.
				// We use this instance as the callback for connection and connection
				// failures.
				// Since no account name is passed, the user is prompted to choose.

				Log.i("MainActivity verifyLogins", "mGoogleApiClient null");
				mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(Drive.API)
				.addScope(Drive.SCOPE_FILE)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();
			}
			mGoogleApiClient.connect();


			/*
		    	if (mGoogleApiClient.isConnected()) 
		    	{
		    		Log.i("MainActivity", "mGoogleApiClient.isConnected()");
		    		Context context = getBaseContext(); 
	    			Intent uploadIntent = new Intent(context, uploadService.class);
	    			System.out.println("Uploadservice Intent declarado");
	    			uploadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    			System.out.println("uploadintent flag added");
	    			context.startService(uploadIntent);

				}
			 */

		}

		/*
		    if (sharedPrefs.getBoolean("prefDropbox", false) && sharedPrefs.getBoolean("prefGDrive", false)) 
		    {
		    	Log.i("MainActivity", "Dentro de onResume AmBos");



		    	if (mDBApi.getSession().authenticationSuccessful() && mGoogleApiClient.isConnected()) 
		    	{
		    		Log.i("MainActivity", "Dentro de onResume AmBos conetados");
		    		try {
		    			// Required to complete auth, sets the access token on the session
		    			mDBApi.getSession().finishAuthentication();

		    			String accessToken = null;
		    			while (accessToken == null) {
		    				accessToken = mDBApi.getSession().getOAuth2AccessToken();

		    			}
		    			//accessToken = mDBApi.getSession().getOAuth2AccessToken();


		    			Context context = getBaseContext(); 
		    			Intent uploadIntent = new Intent(context, uploadService.class);
		    			System.out.println("Uploadservice Intent declarado");
		    			uploadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    			System.out.println("uploadintent flag added");
		    			context.startService(uploadIntent);




		    		} catch (IllegalStateException e) {
		    			Log.i("DbAuthLog", "Error authenticating", e);
		    		}
		    	}	
		    }
		    else if(sharedPrefs.getBoolean("prefDropbox", false))
		    {
		    	Log.i("MainActivity", "So drop");

		    	if (mDBApi.getSession().authenticationSuccessful())
		    	{
		    		Log.i("MainActivity", "drop");
		    		try {
		    			// Required to complete auth, sets the access token on the session
		    			mDBApi.getSession().finishAuthentication();

		    			String accessToken = null;
		    			while (accessToken == null) {
		    				accessToken = mDBApi.getSession().getOAuth2AccessToken();

		    			}
		    			//accessToken = mDBApi.getSession().getOAuth2AccessToken();


		    			Context context = getBaseContext(); 
		    			Intent uploadIntent = new Intent(context, uploadService.class);
		    			System.out.println("Uploadservice Intent declarado");
		    			uploadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    			System.out.println("uploadintent flag added");
		    			context.startService(uploadIntent);




		    		} catch (IllegalStateException e) {
		    			Log.i("DbAuthLog", "Error authenticating", e);
		    		}
		    	}

		    }
		    else if(sharedPrefs.getBoolean("prefGDrive", false))
		    {
		    	if (mGoogleApiClient.isConnected()) 
		    	{
		    		Context context = getBaseContext(); 
	    			Intent uploadIntent = new Intent(context, uploadService.class);
	    			System.out.println("Uploadservice Intent declarado");
	    			uploadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    			System.out.println("uploadintent flag added");
	    			context.startService(uploadIntent);

				}
		    }
		 */

	}

	
/*	
    public void createSMSXML ()
    {
    	List<SMSData> smsList = new ArrayList<SMSData>();
		 
    	String[] reqCols = new String[]{"_id", "address", "body", "date", "type"};

        Uri inboxURI = Uri.parse("content://sms");

        ContentResolver cr = getContentResolver();

        // Fetch Inbox SMS Message from Built-in Content Provider
        Cursor c = cr.query(inboxURI, reqCols, null, null, null);

        if(c.moveToFirst()) {
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(Long.parseLong(c.getString(c.getColumnIndex("date"))));
            String dayOfYear1 = date.get(Calendar.YEAR)+"-"+ (date.get(Calendar.MONTH)+1) +"-"+date.get(Calendar.DAY_OF_MONTH);
            do
            {
                int type = c.getInt(c.getColumnIndex("type"));
                String number = c.getString(c.getColumnIndex("address"));
                date = Calendar.getInstance();
                date.setTimeInMillis(Long.parseLong(c.getString(c.getColumnIndex("date"))));
                String dayOfYear = date.get(Calendar.YEAR)+"-"+ (date.get(Calendar.MONTH)+1) +"-"+date.get(Calendar.DAY_OF_MONTH);
                String body = c.getString(c.getColumnIndex("body"));
                SMSData sms = new SMSData(number, body, date, type);
                sms.setPerson(Utils.getContactName(this, number));

                if(dayOfYear.equals(dayOfYear1))
                    smsList.add(sms);
                else
                {
                    Utils.createXml(getBaseContext(), smsList, dayOfYear1);
                    smsList.clear();
                    dayOfYear1 = dayOfYear;
                    smsList.add(sms);
                }
            }while (c.moveToNext());
            Utils.createXml(getBaseContext(), smsList, dayOfYear1);
        }
    }
    
    public void createCallLogXML ()
    {
		 List<CallLogData> callLogList = new ArrayList<CallLogData>();
		 String[] reqCols = new String[]{"_id", CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION};

         Uri callsURI = CallLog.Calls.CONTENT_URI;

         ContentResolver cr = getContentResolver();

         Cursor c = cr.query(callsURI, reqCols, null, null, null);

         if(c.moveToFirst()) {
             Calendar date = Calendar.getInstance();
             date.setTimeInMillis(Long.parseLong(c.getString(c.getColumnIndex("date"))));
             String dayOfYear1 = date.get(Calendar.YEAR)+"-"+ (date.get(Calendar.MONTH)+1) +"-"+date.get(Calendar.DAY_OF_MONTH);
             do
             {
                 int type = c.getInt(c.getColumnIndex(CallLog.Calls.TYPE));
                 String number = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));
                 date = Calendar.getInstance();
                 date.setTimeInMillis(Long.parseLong(c.getString(c.getColumnIndex(CallLog.Calls.DATE))));
                 String dayOfYear = date.get(Calendar.YEAR)+"-"+ (date.get(Calendar.MONTH)+1) +"-"+date.get(Calendar.DAY_OF_MONTH);
                 String duration = c.getString(c.getColumnIndex(CallLog.Calls.DURATION));

                 CallLogData callLog = new CallLogData(number, duration, date, type);
                // String p = Utils.getContactName(this, number);
                 //if(p != null)
                   //  callLog.setPerson(Utils.getContactName(this, number));
                 if(dayOfYear.equals(dayOfYear1))
                     callLogList.add(callLog);
                 else
                 {
                     Utils.createCallLogXML(getBaseContext(), callLogList, dayOfYear1);
                     callLogList.clear();
                     dayOfYear1 = dayOfYear;
                     callLogList.add(callLog);
                 }
             }while (c.moveToNext());
             Utils.createCallLogXML(getBaseContext(), callLogList, dayOfYear1);
         }
    }
  */
}
