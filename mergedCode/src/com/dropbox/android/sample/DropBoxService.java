/*
 * Copyright (c) 2010-11 Dropbox, Inc.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.dropbox.android.sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.IntentService;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.media.audiofx.LoudnessEnhancer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.google.android.gms.drive.sample.quickstart.MainActivity;
//import com.google.android.gms.drive.sample.quickstart.R;
import com.google.android.gms.drive.sample.quickstart.R;


//public class DropBoxService extends Activity {
public class DropBoxService extends Service {
    

	private static final String TAG = "DBRoulette";

    ///////////////////////////////////////////////////////////////////////////
    //                      Your app-specific settings.                      //
    ///////////////////////////////////////////////////////////////////////////

    // Replace this with your app key and secret assigned by Dropbox.
    // Note that this is a really insecure way to do this, and you shouldn't
    // ship code which contains your key & secret in such an obvious way.
    // Obfuscation is good.
    final static private String APP_KEY = "8v9kx36fibrd7vr";
    final static private String APP_SECRET = "8zvnrqnrj2qg4hb";

    ///////////////////////////////////////////////////////////////////////////
    //                      End app-specific settings.                       //
    ///////////////////////////////////////////////////////////////////////////

    // You don't need to change these, leave them alone.
    final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    private static final boolean USE_OAUTH1 = false;

    private static DropboxAPI<AndroidAuthSession> dropbox;
    //private DropboxAPI<AndroidAuthSession> mDBApi;

    private boolean mLoggedIn;

    // Android widgets
    private Button mSubmit;
    private LinearLayout mDisplay;
    private Button mPhoto;
    private Button mRoulette;

    private ImageView mImage;

    private final String PHOTO_DIR = "/Photos/";

    final static private int NEW_PICTURE = 1;
    private String mCameraFileName;
    private static String operation;
    
    private static boolean uploaded = false;
    private static boolean downloaded = false;
	
    
    /**
     * called only one time when the Service has to be created. 
     * If the Service is already running this method won’t be called. We don’t call it directly but it is the OS that calls it.
     */
    @Override
    public void onCreate() {        
        super.onCreate();
        Toast.makeText(this, "Creating DropBoxService", Toast.LENGTH_LONG).show();
        
        
        
    }

    
    
    @Override
    public void onDestroy() {        
        super.onDestroy();
        Toast.makeText(this, "DropBoxService Destroyed", Toast.LENGTH_LONG).show();
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
        //return super.onStartCommand(intent, flags, startId);
    	Toast.makeText(this, "DropBoxService Started", Toast.LENGTH_LONG).show();
    	
    	//operation = intent.getExtras().getString(com.google.android.gms.drive.sample.quickstart.MainActivity.operationDropbox);
    	operation = MainActivity.operationDropbox;
    	Log.i(TAG, "Extra operationDropbox is: " + operation);
    	
    	//dropbox = MainActivity.mDBApi;
    	
    	//dropbox.getSession().finishAuthentication();
    	MainActivity.mDBApi.getSession().finishAuthentication();
    	
    	//String accessToken = dropbox.getSession().getOAuth2AccessToken();
    	
    	
    	Log.i(TAG, "AfterDropBox");
    	
    	//TODO iniciarSessao
    	//TODO verificar se o user esta logado
		    	//Se sim verificar qual a operacao a executar
		    		//Upload
		    		//download
		    	
    		
    				if (operation.equals("upload") && !uploaded) {
				    	new Thread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								
				
				            	Log.i(TAG, "Antes Sdcard");
				            	File sdcard = Environment.getExternalStorageDirectory();
				            	//Get the text file
				            	File file = new File(sdcard,"example.xml");
				            	
				            	Log.i(TAG, "file Criado");
				
				
				            	FileInputStream inputStream;
				            	try {
				            		inputStream = new FileInputStream(file);
				            		Log.i(TAG, "inputStream");
				            		Entry response;
				            		Log.i(TAG, "response declarada");
				            		response = MainActivity.mDBApi.putFile("/euzinho.xml", inputStream,
				            				file.length(), null, null);
				            		Log.i("DbExampleLog", "The uploaded file's rev is: " + response.rev);
				            	} catch (FileNotFoundException e) {
				            		e.printStackTrace();
				            	} catch (DropboxException e) {
				            		e.printStackTrace();
				            	}
							}
						}).start();
				    	
				    	uploaded = true;
						
					
				}
    			else if (operation.equals("download") && !downloaded) 
    			{
		    		
		    		
							
			    	new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							
							FileOutputStream outputStream;
			        		File sdcard = Environment.getExternalStorageDirectory();
			            	//Get the text file
			            	File file_smartphone = new File(sdcard,"magnum-opus.xml");
			        		try {
			        			outputStream = new FileOutputStream(file_smartphone);
			        			DropboxFileInfo info = MainActivity.mDBApi.getFile("/example.xml", null, outputStream, null);
			        			Log.i("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);
			        		} catch (FileNotFoundException e) {
			        			e.printStackTrace();
			        		} catch (DropboxException e) {
			        			e.printStackTrace();
			        		}
							
						}
					}).start();
							
					downloaded = true;
					
				}
    			else
    			{
    				this.stopSelf();
    			}
		    	
		    	//stopself()
    	
    	
    	return START_REDELIVER_INTENT;
    	
    }
    
    @Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
    
    
    private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }
    
    
 
    
    
    

}
