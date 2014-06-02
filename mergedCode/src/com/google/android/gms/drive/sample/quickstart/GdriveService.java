/**
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.drive.sample.quickstart;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.ContentsResult;
import com.google.android.gms.drive.DriveApi.DriveIdResult;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveFolder.DriveFileResult;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

/**
 * Android Drive Quickstart activity. This activity takes a photo and saves it
 * in Google Drive. The user is prompted with a pre-made dialog which allows
 * them to choose the file location.
 */
public class GdriveService extends Service {

    private static final String TAG = "android-drive-quickstart";
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;

    
    /*
    private static boolean saved = false;
    private static boolean written = false;
    */
    
    private static boolean uploaded = false;
    private static boolean downloaded = false;
	
    
    /**
     * called only one time when the Service has to be created. 
     * If the Service is already running this method won’t be called. We don’t call it directly but it is the OS that calls it.
     */
    @Override
    public void onCreate() {        
        super.onCreate();
        Toast.makeText(this, "Creating GdriveService", Toast.LENGTH_LONG).show(); 
    }
    
    
    @Override
    public void onDestroy() {        
        super.onDestroy();
        Toast.makeText(this, "GdriveService Destroyed", Toast.LENGTH_LONG).show();
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
    	
    	 if (MainActivity.operationGDrive.equals("upload") && !uploaded) {
         	
         	saveFileToDrive();
 		}
         else if(MainActivity.operationGDrive.equals("download") && !downloaded)
         {
         	downloadFileFromDrive();
         }
         else
         {
         	showMessage("Au Revoir!");
         	//finish();
         	this.stopSelf();
         }
    	
    	
    	
    	
    	return START_REDELIVER_INTENT; 
    	
    }
    
    
    @Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	} 
    
    
    
    /**
     * Create a new file and save it to Drive.
     */
    private void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating new contents.");
        Drive.DriveApi.newContents(MainActivity.mGoogleApiClient).setResultCallback(new ResultCallback<ContentsResult>() {

            @Override
            public void onResult(ContentsResult result) {
                // If the operation was not successful, we cannot do anything
                // and must
                // fail.
                if (!result.getStatus().isSuccess()) {
                    Log.i(TAG, "Failed to create new contents.");
                    return;
                }
                // Otherwise, we can write our data to the new contents.
                Log.i(TAG, "New contents created.");
               
                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle("sms_backup")
                //.setMimeType("text/plain")
                .setMimeType("application/xml")
                .setStarred(true).build();
            
               //read nd write to final file
               transferContent("example.xml", result);
	
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
              uploaded  = true;
            }
           
        });
        
    }
    
    
    private void downloadFileFromDrive() {
    	Log.i(TAG, "Inside DownloadFileToDrive");

    	new Thread(new Runnable() {

    		@Override
    		public void run() {
    			DriveId driveId = Drive.DriveApi.getRootFolder(MainActivity.mGoogleApiClient)
    					.getDriveId();


    			DriveFolder folder = Drive.DriveApi.getFolder(MainActivity.mGoogleApiClient, driveId);

    			Query query = new Query.Builder().addFilter(Filters.and(
    					Filters.eq(SearchableField.MIME_TYPE, "application/xml"),
    					Filters.contains(SearchableField.TITLE, "sms_backup"))).build();


    			PendingResult<MetadataBufferResult> pendindResult = folder.queryChildren(MainActivity.mGoogleApiClient, query);

    			//pendindResult.await();

    			pendindResult.setResultCallback(new ResultCallback<MetadataBufferResult>(){

    				@Override
    				public void onResult(MetadataBufferResult arg0) {
    					// TODO Auto-generated method stu
    					Log.i(TAG, "Downloaded: " + arg0.getMetadataBuffer().get(0).getTitle());
    					
    					final long sizeFile = arg0.getMetadataBuffer().get(0).getFileSize();
    					
    					DriveId driveID = arg0.getMetadataBuffer().get(0).getDriveId();
    					DriveFile driveFile = Drive.DriveApi.getFile(MainActivity.mGoogleApiClient, driveID);
    					
    					//driveFile.openContents(mGoogleApiClient, arg1, arg2)
    					PendingResult<ContentsResult> contentResult = driveFile.openContents(MainActivity.mGoogleApiClient, driveFile.MODE_READ_ONLY, null);
    					
    					contentResult.setResultCallback(new ResultCallback<ContentsResult>(){

							@Override
							public void onResult(ContentsResult arg0) {
								// TODO Auto-generated method stub
								
								
								
								File sdcard = Environment.getExternalStorageDirectory();

						        //Get the text file
								String filename = "received.xml";
						        File file = new File(sdcard,filename);

						        try {
						            //BufferedReader br = new BufferedReader(new FileReader(file));
						            //String line;
						            InputStream inputStream = arg0.getContents().getInputStream();
						            OutputStream outputStream = new FileOutputStream(file);
						            
						            int content;
						            while((content = inputStream.read()) != -1)
						            {
						            	outputStream.write(content);
						            }
						            
						            /*while ((line = br.readLine()) != null) {
						              //  text.append(line);
						                //text.append('\n');
						          	  line += "\n";
						          	  
						                inputStream.read(line.getBytes());
						            }
						            */
						            Log.i(TAG, "Depois While ");
						           
						            /*
						            byte[] fileContent = new byte[(int) sizeFile];
						            
						            while(inputStream.read(fileContent) != -1)
						            {
						            	String s = new String(fileContent);
						            	Log.i(TAG, "Content is: " + s);
						            }
						            */
						            
						            
						            
						            downloaded = true;
						        }
						        catch (IOException e) {
						            //You'll need to add proper error handling here
						        }
								
								
								
								
								
								
							}
    						
    					});
    					
    					

    				}

    			});

    		}
    	}).start();

    };
    
    
  //It needs the file in the SDcard
    private static boolean transferContent(String filename, ContentsResult result) {

    	   //Find the directory for the SD Card using the API
        //*Don't* hardcode "/sdcard"
        File sdcard = Environment.getExternalStorageDirectory();

        //Get the text file
        File file = new File(sdcard,filename);

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
		// TODO Auto-generated method stub
    	 Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
    
    

   
}
