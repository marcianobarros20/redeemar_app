/*===============================================================================
Copyright (c) 2016 PTC Inc. All Rights Reserved.


Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.tier5.redeemar.RedeemarConsumerApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tier5.redeemar.RedeemarConsumerApp.SampleAppMenu.SampleAppMenu;
import com.tier5.redeemar.RedeemarConsumerApp.SampleAppMenu.SampleAppMenuGroup;
import com.tier5.redeemar.RedeemarConsumerApp.SampleAppMenu.SampleAppMenuInterface;
import com.tier5.redeemar.RedeemarConsumerApp.async.DownloadImageTask;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.BrandVideo;
import com.tier5.redeemar.RedeemarConsumerApp.pojo.Offer;
import com.tier5.redeemar.RedeemarConsumerApp.utils.Constants;
import com.tier5.redeemar.RedeemarConsumerApp.utils.UrlEndpoints;
import com.tier5.redeemar.SampleApplication.SampleApplicationControl;
import com.tier5.redeemar.SampleApplication.SampleApplicationException;
import com.tier5.redeemar.SampleApplication.SampleApplicationSession;
import com.tier5.redeemar.SampleApplication.utils.LoadingDialogHandler;
import com.tier5.redeemar.SampleApplication.utils.SampleApplicationGLView;
import com.tier5.redeemar.SampleApplication.utils.Texture;
import com.vuforia.CameraDevice;
import com.vuforia.ObjectTracker;
import com.vuforia.State;
import com.vuforia.TargetFinder;
import com.vuforia.TargetSearchResult;
import com.vuforia.Trackable;
import com.vuforia.Tracker;
import com.vuforia.TrackerManager;
import com.vuforia.Vuforia;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Vector;

// The main activity for the CloudReco sample.
public class CloudReco extends Activity implements SampleApplicationControl,
        SampleAppMenuInterface
{
    private static final String LOGTAG = "CloudReco";

    SampleApplicationSession vuforiaAppSession;

    // These codes match the ones defined in TargetFinder in Vuforia.jar
    static final int INIT_SUCCESS = 2;
    static final int INIT_ERROR_NO_NETWORK_CONNECTION = -1;
    static final int INIT_ERROR_SERVICE_NOT_AVAILABLE = -2;
    static final int UPDATE_ERROR_AUTHORIZATION_FAILED = -1;
    static final int UPDATE_ERROR_PROJECT_SUSPENDED = -2;
    static final int UPDATE_ERROR_NO_NETWORK_CONNECTION = -3;
    static final int UPDATE_ERROR_SERVICE_NOT_AVAILABLE = -4;
    static final int UPDATE_ERROR_BAD_FRAME_QUALITY = -5;
    static final int UPDATE_ERROR_UPDATE_SDK = -6;
    static final int UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE = -7;
    static final int UPDATE_ERROR_REQUEST_TIMEOUT = -8;

    static final int HIDE_LOADING_DIALOG = 0;
    static final int SHOW_LOADING_DIALOG = 1;

    static final int SCANNING_TIMEOUT = 750;

    // Our OpenGL view:
    private SampleApplicationGLView mGlView;

    // Our renderer:
    private CloudRecoRenderer mRenderer;

    private SampleAppMenu mSampleAppMenu;

    private boolean mExtendedTracking = false;
    boolean mFinderStarted = false;
    boolean mStopFinderIfStarted = false;

    // The textures we will use for rendering:
    private Vector<Texture> mTextures;

    private static String vuforiaAccessKey = "";
    private static String vuforiaSecretKey = "";



    // View overlays to be displayed in the Augmented View
    private RelativeLayout mUILayout;

    // Error message handling:
    private int mlastErrorCode = 0;
    private int mInitErrorCode = 0;
    private boolean mFinishActivityOnError;

    // Alert Dialog used to display SDK errors
    private AlertDialog mErrorDialog;

    private GestureDetector mGestureDetector;

    private LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(
        this);

    private double mLastErrorTime;

    boolean mIsDroidDevice = false;

    int cnt = 0;

    private ImageView logoImage;

    private AlertDialog alertDialog;

    private Resources res;
    private SharedPreferences sharedpref;
    private SharedPreferences.Editor editor;



    // Called when the activity first starts or needs to be recreated after
    // resuming the application or a configuration change.
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(LOGTAG, "onCreate");
        super.onCreate(savedInstanceState);

        vuforiaAppSession = new SampleApplicationSession(this);

        startLoadingAnimation();

        vuforiaAppSession
            .initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Creates the GestureDetector listener for processing double tap
        mGestureDetector = new GestureDetector(this, new GestureListener());

        mTextures = new Vector<Texture>();
        loadTextures();

        mIsDroidDevice = android.os.Build.MODEL.toLowerCase().startsWith(
            "droid");


        res = getResources();
        sharedpref = getSharedPreferences(res.getString(R.string.spf_key), 0); // 0 - for private mode
        editor = sharedpref.edit();




    }

    // Process Single Tap event to trigger autofocus
    private class GestureListener extends
        GestureDetector.SimpleOnGestureListener
    {
        // Used to set autofocus one second after a manual focus is triggered
        private final Handler autofocusHandler = new Handler();


        @Override
        public boolean onDown(MotionEvent e)
        {
            return true;
        }


        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            // Generates a Handler to trigger autofocus
            // after 1 second
            autofocusHandler.postDelayed(new Runnable()
            {
                public void run()
                {
                    boolean result = CameraDevice.getInstance().setFocusMode(
                        CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO);

                    if (!result)
                        Log.e("SingleTapUp", "Unable to trigger focus");
                }
            }, 1000L);

            return true;
        }
    }


    // We want to load specific textures from the APK, which we will later use
    // for rendering.
    private void loadTextures()
    {
        Log.d(LOGTAG, "Loading textures.");
        mTextures.add(Texture.loadTextureFromApk("TextureTeapotRed.png",
            getAssets()));
    }


    // Called when the activity will start interacting with the user.
    @Override
    protected void onResume()
    {
        Log.d(LOGTAG, "onResume");
        cnt = 0;
        super.onResume();

        // This is needed for some Droid devices to force portrait
        if (mIsDroidDevice)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        try
        {
            vuforiaAppSession.resumeAR();
        } catch (SampleApplicationException e)
        {
            Log.e(LOGTAG, e.getString());
        }

        // Resume the GL view:
        if (mGlView != null)
        {
            mGlView.setVisibility(View.VISIBLE);
            mGlView.onResume();
        }

    }


    // Callback for configuration changes the activity handles itself
    @Override
    public void onConfigurationChanged(Configuration config)
    {
        Log.d(LOGTAG, "onConfigurationChanged");
        super.onConfigurationChanged(config);

        vuforiaAppSession.onConfigurationChanged();
    }


    // Called when the system is about to start resuming a previous activity.
    @Override
    protected void onPause()
    {
        Log.d(LOGTAG, "onPause");
        super.onPause();

        try
        {
            vuforiaAppSession.pauseAR();
        } catch (SampleApplicationException e)
        {
            Log.e(LOGTAG, e.getString());
        }

        // Pauses the OpenGLView
        if (mGlView != null)
        {
            mGlView.setVisibility(View.INVISIBLE);
            mGlView.onPause();
        }
    }


    // The final call you receive before your activity is destroyed.
    @Override
    protected void onDestroy()
    {
        Log.d(LOGTAG, "onDestroy");
        super.onDestroy();

        try
        {
            vuforiaAppSession.stopAR();
        } catch (SampleApplicationException e)
        {
            Log.e(LOGTAG, e.getString());
        }

        System.gc();
    }
    
    
    public void deinitCloudReco()
    {
        // Get the object tracker:
        TrackerManager trackerManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) trackerManager
            .getTracker(ObjectTracker.getClassType());
        if (objectTracker == null)
        {
            Log.e(LOGTAG,
                "Failed to destroy the tracking data set because the ObjectTracker has not"
                    + " been initialized.");
            return;
        }
        
        // Deinitialize target finder:
        TargetFinder finder = objectTracker.getTargetFinder();
        finder.deinit();
    }
    
    
    private void startLoadingAnimation()
    {
        // Inflates the Overlay Layout to be displayed above the Camera View
        LayoutInflater inflater = LayoutInflater.from(this);
        mUILayout = (RelativeLayout) inflater.inflate(R.layout.camera_overlay, null, false);
        
        mUILayout.setVisibility(View.VISIBLE);
        mUILayout.setBackgroundColor(Color.BLACK);
        
        // By default
        //loadingDialogHandler.mLoadingDialogContainer = mUILayout.findViewById(R.id.loading_indicator);
        //loadingDialogHandler.mLoadingDialogContainer.setVisibility(View.VISIBLE);
        
        addContentView(mUILayout, new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));

        logoImage = (ImageView) findViewById(R.id.logo_image);
        
    }
    
    
    // Initializes AR application components.
    private void initApplicationAR()
    {
        // Create OpenGL ES view:
        int depthSize = 16;
        int stencilSize = 0;
        boolean translucent = Vuforia.requiresAlpha();
        
        // Initialize the GLView with proper flags
        mGlView = new SampleApplicationGLView(this);
        mGlView.init(translucent, depthSize, stencilSize);
        
        // Setups the Renderer of the GLView
        mRenderer = new CloudRecoRenderer(vuforiaAppSession, this);
        mRenderer.setTextures(mTextures);
        mGlView.setRenderer(mRenderer);
        
    }
    
    
    // Returns the error message for each error code
    private String getStatusDescString(int code)
    {
        if (code == UPDATE_ERROR_AUTHORIZATION_FAILED)
            return getString(R.string.UPDATE_ERROR_AUTHORIZATION_FAILED_DESC);
        if (code == UPDATE_ERROR_PROJECT_SUSPENDED)
            return getString(R.string.UPDATE_ERROR_PROJECT_SUSPENDED_DESC);
        if (code == UPDATE_ERROR_NO_NETWORK_CONNECTION)
            return getString(R.string.UPDATE_ERROR_NO_NETWORK_CONNECTION_DESC);
        if (code == UPDATE_ERROR_SERVICE_NOT_AVAILABLE)
            return getString(R.string.UPDATE_ERROR_SERVICE_NOT_AVAILABLE_DESC);
        if (code == UPDATE_ERROR_UPDATE_SDK)
            return getString(R.string.UPDATE_ERROR_UPDATE_SDK_DESC);
        if (code == UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE)
            return getString(R.string.UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE_DESC);
        if (code == UPDATE_ERROR_REQUEST_TIMEOUT)
            return getString(R.string.UPDATE_ERROR_REQUEST_TIMEOUT_DESC);
        if (code == UPDATE_ERROR_BAD_FRAME_QUALITY)
            return getString(R.string.UPDATE_ERROR_BAD_FRAME_QUALITY_DESC);
        else
        {
            return getString(R.string.UPDATE_ERROR_UNKNOWN_DESC);
        }
    }
    
    
    // Returns the error message for each error code
    private String getStatusTitleString(int code)
    {

        if (code == UPDATE_ERROR_AUTHORIZATION_FAILED)
            return getString(R.string.UPDATE_ERROR_AUTHORIZATION_FAILED_TITLE);
        if (code == UPDATE_ERROR_PROJECT_SUSPENDED)
            return getString(R.string.UPDATE_ERROR_PROJECT_SUSPENDED_TITLE);
        if (code == UPDATE_ERROR_NO_NETWORK_CONNECTION)
            return getString(R.string.UPDATE_ERROR_NO_NETWORK_CONNECTION_TITLE);
        if (code == UPDATE_ERROR_SERVICE_NOT_AVAILABLE)
            return getString(R.string.UPDATE_ERROR_SERVICE_NOT_AVAILABLE_TITLE);
        if (code == UPDATE_ERROR_UPDATE_SDK)
            return getString(R.string.UPDATE_ERROR_UPDATE_SDK_TITLE);
        if (code == UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE)
            return getString(R.string.UPDATE_ERROR_TIMESTAMP_OUT_OF_RANGE_TITLE);
        if (code == UPDATE_ERROR_REQUEST_TIMEOUT)
            return getString(R.string.UPDATE_ERROR_REQUEST_TIMEOUT_TITLE);
        if (code == UPDATE_ERROR_BAD_FRAME_QUALITY)
            return getString(R.string.UPDATE_ERROR_BAD_FRAME_QUALITY_TITLE);
        else
        {
            return getString(R.string.UPDATE_ERROR_UNKNOWN_TITLE);
        }
    }
    
    
    // Shows error messages as System dialogs
    public void showErrorMessage(int errorCode, double errorTime, boolean finishActivityOnError)
    {
        Log.d(LOGTAG, "Last Error Code: "+errorCode);

        if (errorTime < (mLastErrorTime + 5.0) || errorCode == mlastErrorCode)
            return;
        
        mlastErrorCode = errorCode;
        mFinishActivityOnError = finishActivityOnError;
        
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (mErrorDialog != null)
                {
                    mErrorDialog.dismiss();
                }
                
                // Generates an Alert Dialog to show the error message
                AlertDialog.Builder builder = new AlertDialog.Builder(
                    CloudReco.this);
                builder
                    .setMessage(
                        getStatusDescString(CloudReco.this.mlastErrorCode))
                    .setTitle(
                        getStatusTitleString(CloudReco.this.mlastErrorCode))
                    .setCancelable(false)
                    .setIcon(0)
                    .setPositiveButton(getString(R.string.button_OK),
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                if(mFinishActivityOnError)
                                {
                                    finish();
                                }
                                else
                                {
                                    dialog.dismiss();
                                }
                            }
                        });
                
                mErrorDialog = builder.create();
                mErrorDialog.show();
            }
        });
    }
    
    
    // Shows initialization error messages as System dialogs
    public void showInitializationErrorMessage(String message)
    {
        final String errorMessage = message;
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                if (mErrorDialog != null)
                {
                    mErrorDialog.dismiss();
                }
                
                // Generates an Alert Dialog to show the error message
                AlertDialog.Builder builder = new AlertDialog.Builder(
                    CloudReco.this);
                builder
                    .setMessage(errorMessage)
                    .setTitle(getString(R.string.INIT_ERROR))
                    .setCancelable(false)
                    .setIcon(0)
                    .setPositiveButton(getString(R.string.button_OK),
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                finish();
                            }
                        });
                
                mErrorDialog = builder.create();
                mErrorDialog.show();
            }
        });
    }
    
    
    public void startFinderIfStopped()
    {
        if(!mFinderStarted)
        {
            mFinderStarted = true;
            
            // Get the object tracker:
            TrackerManager trackerManager = TrackerManager.getInstance();
            ObjectTracker objectTracker = (ObjectTracker) trackerManager
                .getTracker(ObjectTracker.getClassType());
            
            // Initialize target finder:
            TargetFinder targetFinder = objectTracker.getTargetFinder();
            
            targetFinder.clearTrackables();
            targetFinder.startRecognition();
        }
    }
    
    
    public void stopFinderIfStarted()
    {
        if(mFinderStarted)
        {
            mFinderStarted = false;
            
            // Get the object tracker:
            TrackerManager trackerManager = TrackerManager.getInstance();
            ObjectTracker objectTracker = (ObjectTracker) trackerManager
                .getTracker(ObjectTracker.getClassType());
            
            // Initialize target finder:
            TargetFinder targetFinder = objectTracker.getTargetFinder();
            
            targetFinder.stop();
        }
    }
    
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // Process the Gestures
        if (mSampleAppMenu != null && mSampleAppMenu.processEvent(event))
            return true;
        
        return mGestureDetector.onTouchEvent(event);
    }
    
    
    @Override
    public boolean doLoadTrackersData()
    {
        Log.d(LOGTAG, "initCloudReco");
        
        // Get the object tracker:
        TrackerManager trackerManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) trackerManager
            .getTracker(ObjectTracker.getClassType());
        
        // Initialize target finder:
        TargetFinder targetFinder = objectTracker.getTargetFinder();


        if(MyApplication.getAppEnvironment().equalsIgnoreCase("beta") || MyApplication.getAppEnvironment().equalsIgnoreCase("www")) {
            vuforiaAccessKey = Constants.kAccessKeyBeta;
            vuforiaSecretKey = Constants.kSecretKeyBeta;
        }
        else {
            vuforiaAccessKey = Constants.kAccessKeyDev;
            vuforiaSecretKey = Constants.kSecretKeyDev;
        }

        Log.d(LOGTAG, "App Environment: "+MyApplication.getAppEnvironment());
        Log.d(LOGTAG, "Access Key: "+vuforiaAccessKey);
        Log.d(LOGTAG, "Secret Key: "+vuforiaSecretKey);
        
        // Start initialization:
        if (targetFinder.startInit(vuforiaAccessKey, vuforiaSecretKey))
        {
            targetFinder.waitUntilInitFinished();
        }
        
        int resultCode = targetFinder.getInitState();
        if (resultCode != TargetFinder.INIT_SUCCESS)
        {
            if(resultCode == TargetFinder.INIT_ERROR_NO_NETWORK_CONNECTION)
            {
                mInitErrorCode = UPDATE_ERROR_NO_NETWORK_CONNECTION;
            }
            else
            {
                mInitErrorCode = UPDATE_ERROR_SERVICE_NOT_AVAILABLE;
            }
                
            Log.e(LOGTAG, "Failed to initialize target finder.");
            return false;
        }
        
        // Use the following calls if you would like to customize the color of
        // the UI
        // targetFinder->setUIScanlineColor(1.0, 0.0, 0.0);
        // targetFinder->setUIPointColor(0.0, 0.0, 1.0);
        
        return true;
    }
    
    
    @Override
    public boolean doUnloadTrackersData()
    {
        return true;
    }
    
    
    @Override
    public void onInitARDone(SampleApplicationException exception)
    {
        
        if (exception == null)
        {
            initApplicationAR();
            
            // Now add the GL surface view. It is important
            // that the OpenGL ES surface view gets added
            // BEFORE the camera is started and video
            // background is configured.
            addContentView(mGlView, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
            
            // Start the camera:
            try
            {
                vuforiaAppSession.startAR(CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_DEFAULT);
            } catch (SampleApplicationException e)
            {
                Log.e(LOGTAG, e.getString());
            }
            
            boolean result = CameraDevice.getInstance().setFocusMode(
                CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);
            
            if (!result)
                Log.e(LOGTAG, "Unable to enable continuous autofocus");
            
            mUILayout.bringToFront();
            
            // Hides the Loading Dialog
            //loadingDialogHandler.sendEmptyMessage(HIDE_LOADING_DIALOG);
            logoImage.setVisibility(View.GONE);
            
            mUILayout.setBackgroundColor(Color.TRANSPARENT);
            
            /*mSampleAppMenu = new SampleAppMenu(this, this, "Cloud Reco",
                mGlView, mUILayout, null);
            setSampleAppMenuSettings();*/
            
        } else
        {
            Log.e(LOGTAG, exception.getString());
            if(mInitErrorCode != 0)
            {
                showErrorMessage(mInitErrorCode,10, true);
            }
            else
            {
                showInitializationErrorMessage(exception.getString());
            }
        }
    }
    
    
    @Override
    public void onVuforiaUpdate(State state)
    {
        // Get the tracker manager:
        TrackerManager trackerManager = TrackerManager.getInstance();
        
        // Get the object tracker:
        ObjectTracker objectTracker = (ObjectTracker) trackerManager
            .getTracker(ObjectTracker.getClassType());
        
        // Get the target finder:
        TargetFinder finder = objectTracker.getTargetFinder();

        
        // Check if there are new results available:
        final int statusCode = finder.updateSearchResults();

        Log.d(LOGTAG, "Error Code: "+statusCode);


        
        // Show a message if we encountered an error:
        if (statusCode < 0)
        {
            
            boolean closeAppAfterError = (
                statusCode == UPDATE_ERROR_NO_NETWORK_CONNECTION ||
                statusCode == UPDATE_ERROR_SERVICE_NOT_AVAILABLE);
            
            showErrorMessage(statusCode, state.getFrame().getTimeStamp(), closeAppAfterError);
            
        }
        else if (statusCode == TargetFinder.UPDATE_RESULTS_AVAILABLE)  {



            // Process new search results
            Log.d(LOGTAG, "Tracking Status Code: "+statusCode);

            Log.d(LOGTAG, "Tracking Result Count: "+finder.getResultCount() );

            boolean targetFound = false;

            if (finder.getResultCount() > 0)
            {
                TargetSearchResult result = finder.getResult(0);

                String uniqueTargetId = result.getUniqueTargetId();
                Log.d(LOGTAG, "Cloud Reco Unique Target Id: " + uniqueTargetId);

                if(!uniqueTargetId.equalsIgnoreCase("")) {


                    String targetName = result.getTargetName();
                    Log.d(LOGTAG, "Unique Target Name: " + targetName);
                    Bundle extras = getIntent().getExtras();

                    // Check whether the usre is validating the
                    if (extras != null) {

                        String activityName = extras.getString(getString(R.string.ext_activity));
                        String user_id = extras.getString(getString(R.string.ext_user_id));
                        String offer_id = extras.getString(getString(R.string.ext_offer_id));

                        Log.d(LOGTAG, "After Recognition Activity Name: "+activityName);
                        Log.d(LOGTAG, "After Recognition User Id: "+user_id);
                        Log.d(LOGTAG, "After Recognition Offer Id: "+offer_id);


                        if(activityName.equals("Validation") && user_id != "" && offer_id != "" && targetFound == false) {
                            targetFound = true;
                            doStopTrackers();
                            doDeinitTrackers();
                            new ValidateOfferAsyncTask().execute(offer_id, user_id, uniqueTargetId);

                        }
                        else {
                            Log.d(LOGTAG, "Logo validation failed, user id or offer id missing");
                        }

                    }

                    else {
                        new ValidateLogoAsyncTask().execute(uniqueTargetId);
                    }


                } else {

                    Intent dispError = new Intent(getApplicationContext(), DisplayFailureActivity.class);
                    startActivity(dispError);

                    Log.d(LOGTAG, "Unable to get unique id");
                }
            }




        }
        else if(statusCode == 1) {
            cnt++;
            Log.d(LOGTAG, "Test Cnt: "+cnt);

            if(cnt >= SCANNING_TIMEOUT) {

                Intent dispError = new Intent(getApplicationContext(), DisplayFailureActivity.class);
                startActivity(dispError);
                finish();


            }
        }
    }
    
    
    @Override
    public boolean doInitTrackers()
    {
        TrackerManager tManager = TrackerManager.getInstance();
        Tracker tracker;
        
        // Indicate if the trackers were initialized correctly
        boolean result = true;
        
        tracker = tManager.initTracker(ObjectTracker.getClassType());
        if (tracker == null)
        {
            Log.e(
                LOGTAG,
                "Tracker not initialized. Tracker already initialized or the camera is already started");
            result = false;
        } else
        {
            Log.i(LOGTAG, "Tracker successfully initialized");
        }

        return result;
    }
    
    
    @Override
    public boolean doStartTrackers()
    {
        // Indicate if the trackers were started correctly
        boolean result = true;
        
        // Start the tracker:
        TrackerManager trackerManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) trackerManager
            .getTracker(ObjectTracker.getClassType());
        objectTracker.start();
        
        // Start cloud based recognition if we are in scanning mode:
        TargetFinder targetFinder = objectTracker.getTargetFinder();
        targetFinder.startRecognition();
        mFinderStarted = true;
        
        return result;
    }
    
    
    @Override
    public boolean doStopTrackers()
    {
        // Indicate if the trackers were stopped correctly
        boolean result = true;
        
        TrackerManager trackerManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) trackerManager
            .getTracker(ObjectTracker.getClassType());
        
        if(objectTracker != null)
        {
            objectTracker.stop();
            
            // Stop cloud based recognition:
            TargetFinder targetFinder = objectTracker.getTargetFinder();
            targetFinder.stop();
            mFinderStarted = false;
            
            // Clears the trackables
            targetFinder.clearTrackables();
        }
        else
        {
            result = false;
        }
        
        return result;
    }
    
    
    @Override
    public boolean doDeinitTrackers()
    {
        // Indicate if the trackers were deinitialized correctly
        boolean result = true;
        
        TrackerManager tManager = TrackerManager.getInstance();
        tManager.deinitTracker(ObjectTracker.getClassType());
        
        return result;
    }
    
    final public static int CMD_BACK = -1;
    final public static int CMD_EXTENDED_TRACKING = 1;
    
    // This method sets the menu's settings
    private void setSampleAppMenuSettings()
    {
        SampleAppMenuGroup group;
        
        group = mSampleAppMenu.addGroup("", false);
        group.addTextItem(getString(R.string.menu_back), -1);
        
        group = mSampleAppMenu.addGroup("", true);
        group.addSelectionItem(getString(R.string.menu_extended_tracking),
            CMD_EXTENDED_TRACKING, false);
        
        mSampleAppMenu.attachMenu();
    }
    
    
    @Override
    public boolean menuProcess(int command)
    {
        boolean result = true;
        
        switch (command)
        {
            case CMD_BACK:
                finish();
                break;
            
            case CMD_EXTENDED_TRACKING:
                TrackerManager trackerManager = TrackerManager.getInstance();
                ObjectTracker objectTracker = (ObjectTracker) trackerManager
                    .getTracker(ObjectTracker.getClassType());
                
                TargetFinder targetFinder = objectTracker.getTargetFinder();
                
                if (targetFinder.getNumImageTargets() == 0)
                {
                    result = true;
                }
                
                for (int tIdx = 0; tIdx < targetFinder.getNumImageTargets(); tIdx++)
                {
                    Trackable trackable = targetFinder.getImageTarget(tIdx);
                    
                    if (!mExtendedTracking)
                    {
                        if (!trackable.startExtendedTracking())
                        {
                            Log.e(LOGTAG,
                                "Failed to start extended tracking target");
                            result = false;
                        } else
                        {
                            Log.d(LOGTAG,
                                "Successfully started extended tracking target");
                        }
                    } else
                    {
                        if (!trackable.stopExtendedTracking())
                        {
                            Log.e(LOGTAG,
                                "Failed to stop extended tracking target");
                            result = false;
                        } else
                        {
                            Log.d(LOGTAG,
                                "Successfully started extended tracking target");
                        }
                    }
                }
                
                if (result)
                    mExtendedTracking = !mExtendedTracking;
                
                break;
            
        }
        
        return result;
    }





    private class ValidateLogoAsyncTask extends AsyncTask<String, Void, String> {


        String url = "", basePath = ""; String target_id = "";
        WebView displayYoutubeVideo;

        public ValidateLogoAsyncTask() {

            url = UrlEndpoints.validateLogoURL;
            basePath = UrlEndpoints.basePathURL;
        }

        @Override
        protected String doInBackground(String... params) {
            URL myUrl = null;
            HttpURLConnection conn = null;
            String response = "";
            target_id = params[0];

            try {
                myUrl = new URL(url);
                conn = (HttpURLConnection) myUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                JSONObject registerJson = new JSONObject();
                registerJson.put("webservice_name", "check_target");
                registerJson.put("target_id", target_id);

                Log.d("RMD", "Target Id: " + target_id);
                Log.d("RMD", "JSON: " + registerJson.toString());

                String postData = URLEncoder.encode("data", "UTF-8") + "=" + registerJson.toString();

                OutputStream os = conn.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bufferedWriter.write(postData);
                bufferedWriter.flush();
                bufferedWriter.close();

                InputStream inputStream = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }

                //Log.d(LOGTAG, "Do In background: " + response);
                bufferedReader.close();
                inputStream.close();
                conn.disconnect();
                os.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                //Utils.redirectToError(getApplicationContext());

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                //Utils.redirectToError(getApplicationContext());
            } catch (IOException e) {
                e.printStackTrace();
                //Utils.redirectToError(getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
                //Utils.redirectToError(getApplicationContext());
            }

            return response;
        }

        @Override
        protected void onPostExecute(String resp) {
            //do what ever you want with the response
            Log.d(LOGTAG, "On Post Execute: " + resp);

            if (resp != null) {


                try {
                    //JSONObject json= (JSONObject) new JSONTokener(result).nextValue();

                    JSONObject reader = new JSONObject(resp);
                    if (reader.getString("messageCode").equals("R01001")) {

                        Log.d(LOGTAG, "Message Code: " + reader.getString("messageCode"));
                        Log.d(LOGTAG, "Message Data: " + reader.getString("data"));


                        JSONObject json2 = new JSONObject(reader.getString("data"));

                        String action_id = "";
                        String reedemar_id = "";
                        ArrayList<Offer> offerList;

                        if(!json2.isNull("action_id") && !json2.isNull("reedemer_id")) {

                            action_id = String.valueOf(json2.get("action_id"));
                            reedemar_id = String.valueOf(json2.get("reedemer_id"));


                            Log.d(LOGTAG, "Action Id: "+action_id);
                            Log.d(LOGTAG, "Redeemar Id: "+reedemar_id);


                            // Redirect the user to appropriate activity based on the action id

                            // If 2 = Show list of offers for a particular campaign
                            if (action_id.equals("2")) {

                                if(!json2.isNull("campaign_id")) {

                                    String campaign_id = (String) json2.get("campaign_id");
                                    Log.d(LOGTAG, "Inside action id 2: "+campaign_id);

                                    editor.putString(getString(R.string.spf_redir_action), "CampaignOffers"); // Storing Last Activity
                                    editor.putString(getString(R.string.spf_redeemer_id), campaign_id); // Storing Redeemar Id
                                    editor.commit(); // commit changes

                                    Intent sIntent = new Intent(getApplicationContext(), BrowseOffersActivity.class);
                                    sIntent.putExtra(getString(R.string.ext_redir_to), "CampaignOffers");
                                    sIntent.putExtra(getString(R.string.ext_redeemar_id), reedemar_id);
                                    sIntent.putExtra(getString(R.string.ext_campaign_id), campaign_id);
                                    startActivity(sIntent);

                                }

                            }

                            // If 3 = Goto a particular offer
                            else if (action_id.equals("3")) {

                                if(!json2.isNull("offer_id")) {

                                    String offerId = json2.get("offer_id").toString();

                                    Log.d(LOGTAG, "Inside action id 3: "+offerId);

                                    Intent sIntent = new Intent(getApplicationContext(), OfferDetailsActivity.class);
                                    //sIntent.putExtra(getString(R.string.ext_redeemar_id), "R01002");
                                    sIntent.putExtra(getString(R.string.ext_offer_id), offerId);
                                    startActivity(sIntent);
                                    finish();

                                }

                            }


                            // If 4 = Validate a particular offer
                            else if (action_id.equals("4")) {

                                if(!json2.isNull("action_id") && !json2.isNull("reedemer_id")) {
                                    Log.d(LOGTAG, "Inside action id 4");
                                    //showInitializationErrorMessage(getString(R.string.error_validation_wrong_place));

                                    /*Intent intent = new Intent(getApplicationContext(), DisplaySuccessActivity.class);
                                    intent.putExtra(getString(R.string.ext_scan_err), "R02001");
                                    startActivity(intent);
                                    finish();*/

                                    Intent intent = new Intent(getApplicationContext(), DisplayFailureActivity.class);
                                    intent.putExtra(getString(R.string.ext_scan_err), "R02005");
                                    startActivity(intent);
                                    finish();

                                }
                                /*else {

                                    Intent intent = new Intent(getApplicationContext(), DisplayFailureActivity.class);
                                    intent.putExtra(getString(R.string.ext_scan_err), "R02005");
                                    startActivity(intent);
                                    finish();

                                }*/

                            }


                            // If 5 = Display a CGI Animation on top of the scanned image
                            else if (action_id.equals("5")) {

                                if(!json2.isNull("action_id") && !json2.isNull("reedemer_id")) {
                                    Log.d(LOGTAG, "Inside action id 5");



                                    //Toast.makeText(getApplicationContext(), "This will display a CGI animation.", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(getApplicationContext(), DisplayFailureActivity.class);
                                    intent.putExtra(getString(R.string.ext_scan_err), "R02010");
                                    startActivity(intent);
                                    finish();


                                }

                            }

                            // If 1 or default case = Go to brand dashboard
                            else {


                                String unique_target_id = (String) json2.get("reedemer_id");

                                Log.d(LOGTAG, "Inside action id 1 or default");

                                Intent sIntent = new Intent(getApplicationContext(), BrandMainActivity.class);
                                sIntent.putExtra("unique_target_id", target_id);
                                startActivity(sIntent);

                            }

                        }


                    } // End of if
                    else if (reader.getString("messageCode").equals("R01002")) {

                        Intent errIntent = new Intent(getApplicationContext(), DisplayFailureActivity.class);
                        errIntent.putExtra(getString(R.string.ext_scan_err), "R01002");
                        startActivity(errIntent);

                        //tvBrandName.setText(getString(R.string.brand_not_found));
                        //tvBrandName.setTextSize(12);

                    }

                    else if (reader.getString("messageCode").equals("R01003")) {

                        Intent errIntent = new Intent(getApplicationContext(), DisplayFailureActivity.class);
                        errIntent.putExtra(getString(R.string.ext_scan_err), "R01003");
                        startActivity(errIntent);

                        //tvBrandName.setText(getString(R.string.brand_not_found));
                        //tvBrandName.setTextSize(12);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //Utils.redirectToError(getApplicationContext());

                    Intent errIntent = new Intent(getApplicationContext(), DisplayFailureActivity.class);
                    startActivity(errIntent);
                }


            }
        }


    }


    private class ValidateOfferAsyncTask extends AsyncTask<String, Void, String> {


        String url = "";

        public ValidateOfferAsyncTask() {

            url = UrlEndpoints.redemptionURL;

        }

        @Override
        protected String doInBackground(String... params) {
            URL myUrl = null;
            HttpURLConnection conn = null;
            String response = "";

            String offer_id = params[0];
            String user_id = params[1];
            String target_id = params[2];


            try {
                myUrl = new URL(url);
                conn = (HttpURLConnection) myUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                JSONObject data = new JSONObject();
                data.put("offer_id", offer_id);
                data.put("user_id", user_id);
                data.put("target_id", target_id);

                OutputStream os = conn.getOutputStream();
                Log.d(LOGTAG, "Request: " + data.toString());
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bufferedWriter.write("data="+data.toString());
                bufferedWriter.flush();
                bufferedWriter.close();

                InputStream inputStream = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }

                bufferedReader.close();
                inputStream.close();
                conn.disconnect();
                os.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String resp) {
            //do what ever you want with the response

            if (resp != null) {


                try {


                    JSONObject reader = new JSONObject(resp);

                    // Offer validated successfully

                    if (reader.getString("messageCode").equals("R01001")) {

                        Intent intent = new Intent(getApplicationContext(), DisplaySuccessActivity.class);
                        intent.putExtra(getString(R.string.ext_scan_err), "R02001");
                        startActivity(intent);
                        finish();

                    }

                    else if (reader.getString("messageCode").equals("R01002")) {

                        //Toast.makeText(CloudReco.this, R.string.error_offer_expired, Toast.LENGTH_SHORT).show();
                        //showInitializationErrorMessage(getString(R.string.error_offer_expired));

                        Intent intent = new Intent(getApplicationContext(), DisplayFailureActivity.class);
                        intent.putExtra(getString(R.string.ext_scan_err), "R02002");
                        startActivity(intent);
                        finish();


                    }

                    else if (reader.getString("messageCode").equals("R01003")) {

                        //Toast.makeText(CloudReco.this, R.string.error_wrong_target, Toast.LENGTH_SHORT).show();
                        //showInitializationErrorMessage(getString(R.string.error_wrong_target));

                        Intent intent = new Intent(getApplicationContext(), DisplayFailureActivity.class);
                        intent.putExtra(getString(R.string.ext_scan_err), "R02003");
                        startActivity(intent);
                        finish();


                    }

                    else if (reader.getString("messageCode").equals("R01004")) {

                        //Toast.makeText(CloudReco.this, R.string.error_wrong_target, Toast.LENGTH_SHORT).show();
                        //showInitializationErrorMessage(getString(R.string.error_duplicate_validation));

                        Intent intent = new Intent(getApplicationContext(), DisplayFailureActivity.class);
                        intent.putExtra(getString(R.string.ext_scan_err), "R02004");
                        startActivity(intent);
                        finish();


                    }
                    else if (reader.getString("messageCode").equals("R01005")) {

                        //Toast.makeText(CloudReco.this, R.string.error_wrong_target, Toast.LENGTH_SHORT).show();
                        //showInitializationErrorMessage(getString(R.string.error_duplicate_validation));

                        Intent intent = new Intent(getApplicationContext(), DisplayFailureActivity.class);
                        intent.putExtra(getString(R.string.ext_scan_err), "R02006");
                        startActivity(intent);
                        finish();


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }


    }



    @Override
    public void onBackPressed()
    {
        //Intent intent = new Intent(CloudReco.this, BrowseOffersActivity.class);
        //startActivity(intent);
        finish();

    }





}
