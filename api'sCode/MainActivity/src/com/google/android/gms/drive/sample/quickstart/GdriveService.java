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
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
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
public class GdriveService extends Activity implements ConnectionCallbacks,
        OnConnectionFailedListener {

    private static final String TAG = "android-drive-quickstart";
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;

    private GoogleApiClient mGoogleApiClient;
    private Drive service;
    private Bitmap mBitmapToSave;
    private static boolean saved = false;
    private static boolean written = false;
    private static String operation; 

    /**
     * Create a new file and save it to Drive.
     */
    private void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating new contents.");
        final Bitmap image = mBitmapToSave;
        Drive.DriveApi.newContents(mGoogleApiClient).setResultCallback(new ResultCallback<ContentsResult>() {

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
              Drive.DriveApi.getRootFolder(mGoogleApiClient)
                .createFile(mGoogleApiClient, changeSet, result.getContents());
          
              showMessage("SaveToDrive");
              saved  = true;
            }
           
        });
        
    }
    
    
    private void downloadFileFromDrive() {
    	Log.i(TAG, "Inside DownloadFileToDrive");

    	new Thread(new Runnable() {

    		@Override
    		public void run() {
    			DriveId driveId = Drive.DriveApi.getRootFolder(mGoogleApiClient)
    					.getDriveId();


    			DriveFolder folder = Drive.DriveApi.getFolder(mGoogleApiClient, driveId);

    			Query query = new Query.Builder().addFilter(Filters.and(
    					Filters.eq(SearchableField.MIME_TYPE, "application/xml"),
    					Filters.contains(SearchableField.TITLE, "sms_backup"))).build();


    			PendingResult<MetadataBufferResult> pendindResult = folder.queryChildren(mGoogleApiClient, query);

    			//pendindResult.await();

    			pendindResult.setResultCallback(new ResultCallback<MetadataBufferResult>(){

    				@Override
    				public void onResult(MetadataBufferResult arg0) {
    					// TODO Auto-generated method stu
    					Log.i(TAG, "Downloaded: " + arg0.getMetadataBuffer().get(0).getTitle());
    					
    					final long sizeFile = arg0.getMetadataBuffer().get(0).getFileSize();
    					
    					DriveId driveID = arg0.getMetadataBuffer().get(0).getDriveId();
    					DriveFile driveFile = Drive.DriveApi.getFile(mGoogleApiClient, driveID);
    					
    					//driveFile.openContents(mGoogleApiClient, arg1, arg2)
    					PendingResult<ContentsResult> contentResult = driveFile.openContents(mGoogleApiClient, driveFile.MODE_READ_ONLY, null);
    					
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
						            
						            
						            
						            written = true;
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
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if (mGoogleApiClient == null) {
    		// Create the API client and bind it to an instance variable.
            // We use this instance as the callback for connection and connection
            // failures.
            // Since no account name is passed, the user is prompted to choose.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
    	}
        // Connect the client. Once connected, the camera is launched.
        mGoogleApiClient.connect();
    }

    protected void showMessage(String message) {
		// TODO Auto-generated method stub
    	 Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	@Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
     /*  switch (requestCode) {
            case REQUEST_CODE_CAPTURE_IMAGE:
                // Called after a photo has been taken.
                if (resultCode == Activity.RESULT_OK) {
                    // Store the image data as a bitmap for writing later.
                    mBitmapToSave = (Bitmap) data.getExtras().get("data");
                }
                break;
            case REQUEST_CODE_CREATOR:
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Image successfully saved.");
                    mBitmapToSave = null;
                    // Just start the camera again for another photo.
                    startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                            REQUEST_CODE_CAPTURE_IMAGE);
                }
                break;
        }*/
    	
    	
    	showMessage("onActivityResult");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
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
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "API client connected.");
       // if (mBitmapToSave == null) {
        	// This activity has no UI of its own. Just start the camera.
           // startActivityForResult(new Intent("android.intent.action.MAIN"),
                  //7  REQUEST_CODE_CAPTURE_IMAGE);
          //  return;
      //  }
        /*
        if(!saved)
        	saveFileToDrive();
        else
        {
        	showMessage("Au Revoir!");
        	finish();
        	
        }
        */
        
        if (MainActivity.operationGDrive.equals("upload") && !saved) {
        	
        	saveFileToDrive();
		}
        else if(MainActivity.operationGDrive.equals("download") && !written)
        {
        	downloadFileFromDrive();
        }
        else
        {
        	showMessage("Au Revoir!");
        	finish();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }
}
