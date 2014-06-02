package com.google.android.gms.drive.sample.quickstart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import utils.Utils;

import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.DriveApi.ContentsResult;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class uploadService extends Service{

	public static boolean uploadedDrive = false;
	public static boolean uploadedDropBox = false;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * called only one time when the Service has to be created. 
	 * If the Service is already running this method won’t be called. We don’t call it directly but it is the OS that calls it.
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(this, "Creating UploadService", Toast.LENGTH_LONG).show(); 
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "Destroying UploadService", Toast.LENGTH_LONG).show();
	}


	/**
	 * OnStartCommand is the most important method because it is called when we required to start the Service. 
	 * In this method we have the Intent passed at time we run the Service, in this way we can exchange some information with the Service. 
	 * In this method, we implement our logic that can be execute directly inside this method if it isn’t time expensive otherwise we can create a thread
	 * if the integer returned is START_STICKY : Using this return value, if the OS kills our Service it will recreate it but the Intent that was sent to the Service isn’t redelivered. In this way the Service is always running
	 * if the integer returned is START_REDELIVER_INTENT: It is similar to the START_STICKY and in this case the Intent will be redelivered to the service.
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		showMessage("OnStartCommand");
		//SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


		if(sharedPrefs.getBoolean("prefGDrive", false) && sharedPrefs.getBoolean("prefDropbox", false))
		{
			if (!uploadedDrive) {

				Log.i("UploadService drive", "vai fazer saveFileToDrive");
				saveFilesToDrive();
			}

			if (!uploadedDropBox) {
				Log.i("UploadService dropbox", "vai fazer saveFileToDropBox");
				saveFileToDropBox();

			}
			//deleteFiles();
			if(sharedPrefs.getBoolean("prefSync", false)) {

			}
		}
		else if (sharedPrefs.getBoolean("prefGDrive", false) && !uploadedDrive) {
			Log.i("UploadService drive", "vai fazer saveFileToDrive");
			saveFilesToDrive();
			//deleteFiles();
		}
		else if(sharedPrefs.getBoolean("prefDropbox", false) && !uploadedDropBox)
		{
			Log.i("UploadService dropbox", "vai fazer saveFileToDropBox");
			saveFileToDropBox();
			//deleteFiles();
		}
		else
		{
			showMessage("Au Revoir!");
			//finish();
			this.stopSelf();
		}

		return START_REDELIVER_INTENT; 

	}


	private void deleteFiles() {

		final File dirsms = new File(Utils.SMSFolder);
		final File dirlogs = new File(Utils.CallLogFolder);

		if(dirsms.exists())
		{
			File[] listOfFiles = dirsms.listFiles();

			for(int i=0; i< listOfFiles.length; i++) {
				File file = new File(listOfFiles[i].getAbsolutePath());
				file.delete();	
			}
		}
		if(dirlogs.exists())
		{
			File[] listOfFiles = dirlogs.listFiles();

			for(int i=0; i< listOfFiles.length; i++) {
				File file = new File(listOfFiles[i].getAbsolutePath());
				file.delete();	
			}
		}
	}



	private void saveFilesToDrive() {
		final File dirsms = new File(Utils.SMSFolder);
		final File dirlogs = new File(Utils.CallLogFolder);

		if(dirsms.exists())
		{
			Log.i("saveFileToDropBox", "Antes Sdcard");
			File[] listOfFiles = dirsms.listFiles();

			//File sdcard = Environment.getExternalStorageDirectory();
			//Get the text file
			for(int i=0; i< listOfFiles.length; i++) {

				saveFileToDrive(listOfFiles[i].getName(),listOfFiles[i].getAbsolutePath());
				//File file = new File(dirsms,listOfFiles[i].getName());
			}
		}
		if(dirlogs.exists())
		{
			Log.i("saveFileToDropBox", "Antes Sdcard");
			File[] listOfFiles = dirlogs.listFiles();

			//File sdcard = Environment.getExternalStorageDirectory();
			//Get the text file
			for(int i=0; i< listOfFiles.length; i++) {

				saveFileToDrive(listOfFiles[i].getName(),listOfFiles[i].getAbsolutePath());
				//File file = new File(dirlogs,listOfFiles[i].getName());
			}
		}
	}
	
	/**
	 * Create a new file and save it to Drive.
	 */
	private void saveFileToDrive(String fname, String p) {
		// Start by creating a new contents, and setting a callback.
		
		final String filename = fname.substring(0,fname.length()-4);
		final String path = p;
		
		Log.i("saveFileToDrive", "Creating new contents.");
		Drive.DriveApi.newContents(MainActivity.mGoogleApiClient).setResultCallback(new ResultCallback<ContentsResult>() {

			@Override
			public void onResult(ContentsResult result) {
				// If the operation was not successful, we cannot do anything
				// and must
				// fail.
				Log.i("saveFileToDrive", "onResult");
				if (!result.getStatus().isSuccess()) {
					Log.i("saveFileToDrive", "Failed to create new contents.");
					return;
				}
				// Otherwise, we can write our data to the new contents.
				Log.i("saveFileToDrive", "New contents created.");

				MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
				.setTitle(filename)
				//.setMimeType("text/plain")
				.setMimeType("application/xml")
				.setStarred(true).build();

				//read nd write to final file
				transferContent(path, result);

				/*METHOD 1: SAVES TO DRIVE WITH PICKER AND OTHER OPTIONS*/
				// Create an intent for the file chooser, and start it.
				/*  IntentSender intentSender = Drive.DriveApi
                        .newCreateFileActivityBuilder()
                        .setInitialMetadata(changeSet)
                        .setInitialContents(result.getContents())
                        .build(mGoogleApiClient);
                try {
                    startIntentSenderForResult(
                            intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                } catch (SendIntentException e) {
                    Log.i(TAG, "Failed to launch file chooser.");
                }*/

				/*METHOD 2: SAVES FILE WITHOUT PICKER*/
				// create a file on root folder
				Drive.DriveApi.getRootFolder(MainActivity.mGoogleApiClient)
				.createFile(MainActivity.mGoogleApiClient, changeSet, result.getContents());

				showMessage("SaveToDrive");
				uploadedDrive  = true;
			}

		});

	}



	private void saveFileToDropBox() {

		new Thread(new Runnable() {

			@Override
			public void run() {

				final File dirsms = new File(Utils.SMSFolder);
				final File dirlogs = new File(Utils.CallLogFolder);

				if(dirsms.exists())
				{
					Log.i("saveFileToDropBox", "Antes Sdcard");
					File[] listOfFiles = dirsms.listFiles();

					//File sdcard = Environment.getExternalStorageDirectory();
					//Get the text file
					for(int i=0; i< listOfFiles.length; i++) {

						File file = new File(dirsms,listOfFiles[i].getName());

						Log.i("saveFileToDropBox", "file Criado");


						FileInputStream inputStream;
						try {
							inputStream = new FileInputStream(file);
							Log.i("saveFileToDropBox", "inputStream");
							Entry response;
							Log.i("saveFileToDropBox", "response declarada");
							response = MainActivity.mDBApi.putFile("/SMSFolder/"+listOfFiles[i].getName(), inputStream,
									file.length(), null, null);
							Log.i("DbExampleLog", "The uploaded file's rev is: " + response.rev);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (DropboxException e) {
							e.printStackTrace();
						}
					}
				}
				
				if(dirlogs.exists())
				{
					Log.i("saveFileToDropBox", "Antes Sdcard");
					File[] listOfFiles = dirlogs.listFiles();

					//File sdcard = Environment.getExternalStorageDirectory();
					//Get the text file
					for(int i=0; i< listOfFiles.length; i++) {

						File file = new File(listOfFiles[i].getAbsolutePath());

						Log.i("saveFileToDropBox", "file Criado");

						FileInputStream inputStream;
						try {
							inputStream = new FileInputStream(file);
							Log.i("saveFileToDropBox", "inputStream");
							Entry response;
							Log.i("saveFileToDropBox", "response declarada");
							response = MainActivity.mDBApi.putFileOverwrite("/CallLogFolder/"+listOfFiles[i].getName(), inputStream,
									file.length(), null);
							Log.i("DbExampleLog", "The uploaded file's rev is: " + response.rev);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (DropboxException e) {
							e.printStackTrace();
						}
					}
				}


			}
		}).start();

		uploadedDropBox = true;


	}






	//It needs the file in the SDcard
	private static boolean transferContent(String path, ContentsResult result) {

		//Find the directory for the SD Card using the API
		//*Don't* hardcode "/sdcard"
		//File sdcard = Environment.getExternalStorageDirectory();

		//Get the text file
		File file = new File(path);

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			OutputStream outputStream = result.getContents().getOutputStream();
			while ((line = br.readLine()) != null) {
				//  text.append(line);
				//text.append('\n');
				line += "\n";
				outputStream.write(line.getBytes());
			}
		}
		catch (IOException e) {
			//You'll need to add proper error handling here
		}


		return true;
	}



	protected void showMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}




}
