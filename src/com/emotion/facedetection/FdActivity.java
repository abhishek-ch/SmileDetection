package com.emotion.facedetection;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class FdActivity extends Activity {
    private static final String TAG         = "Sample::Activity";

    private MenuItem            mItemType;
    
    private MenuItem mSmileState;
    private MenuItem mMouthState;
    private MenuItem mCameraView;
    private MenuItem mCircleType;
    private MenuItem mEyeView;
    
    
    private FaceDetectionView		mView;
    FdActivity activity;
    
    private BaseLoaderCallback  mOpenCVCallBack = new BaseLoaderCallback(this) {
    	@SuppressWarnings("deprecation")
		@Override
    	public void onManagerConnected(int status) {
    		switch (status) {
				case LoaderCallbackInterface.SUCCESS:
				{
					Log.i(TAG, "OpenCV loaded successfully");
					//define new API

					// Load native libs after OpenCV initialization
					System.loadLibrary("detection_based_tracker");

					// Create and set View
					mView = new FaceDetectionView(mAppContext);
					mView.setDetectorType(mDetectorType);
					mView.setMouthEvent("Mouth",true);
				if (mSmileState != null) {
					mSmileState.setEnabled(true);
				}
					mView.setCircleType(iCircleType);
					mView.setEyeState(iEyeType);
					mView.setMinFaceSize(0.2f);
					setContentView(mView);
					// Check native OpenCV camera
					if( !mView.openCamera(iCameraType) ) {
						AlertDialog ad = new AlertDialog.Builder(mAppContext).create();
						ad.setCancelable(false); // This blocks the 'BACK' button
						ad.setMessage("Fatal error: can't open camera!");
						ad.setButton("OK", new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							finish();
						    }
						});
						ad.show();
					}
				} break;
				default:
				{
					super.onManagerConnected(status);
				} break;
			}
    	}
    };


    private int                 mDetectorType = 1;
    private String[]            mDetectorName; 
    private String[] mSmileNames;
    //Default Smile Value
    private int iSmileType = 0;
    private int iCircleType = 1;
    private int iMouthType = 1;
    private int iCameraType = 0;
    private int iEyeType = 0;
    private String[] mMouthNames;
    private String[] mCameraViewValues;
    private String[] mCircleValues;
    private String[] mEyeValues;

    public FdActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
        activity = this;
        mDetectorName = new String[2];
        mSmileNames =  new String[]{"Smile ON","Smile OFF"};
        mMouthNames =  new String[]{"Mouth ON","Mouth OFF"};
        mCircleValues = new String[]{"Single","Multiple"};
        mEyeValues = new String[]{"Eye OFF","Eye ON"};
        mCameraViewValues = new String[]{"Back Camera","Front Camera"};
        mDetectorName[FaceDetectionView.JAVA_DETECTOR] = "Java";
        mDetectorName[FaceDetectionView.NATIVE_DETECTOR] = "Native (tracking)";
    }

	@Override
	protected void onPause() {
        Log.i(TAG, "onPause");
		super.onPause();
		if (mView != null)
			mView.releaseCamera();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
        Log.i(TAG, "onResume");
		super.onResume();
		if( mView != null && !mView.openCamera(iCameraType) ) {
			AlertDialog ad = new AlertDialog.Builder(this).create();  
			ad.setCancelable(false); // This blocks the 'BACK' button  
			ad.setMessage("Fatal error: can't open camera!");  
			ad.setButton("OK", new DialogInterface.OnClickListener() {  
			    public void onClick(DialogInterface dialog, int which) {  
			        dialog.dismiss();                      
					finish();
			    }  
			});  
			ad.show();
		}
	}

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        try{
        	Bundle extras = getIntent().getExtras();
        	//Define which camera type , Native or Java
          iCameraType = (Integer) extras.get("mCameraType");
        }catch(Exception e){
        }
        //Enable extended window features. This is a convenience for calling getWindow().requestFeature().
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Log.i(TAG, "Trying to load OpenCV library");
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this, mOpenCVCallBack))
        {
        	Log.e(TAG, "Cannot connect to OpenCV Manager");
       }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu--------------------------->");
        mSmileState = menu.add(mSmileNames[iSmileType]);
        mMouthState = menu.add(mMouthNames[iMouthType]);
        mCircleType = menu.add(mCircleValues[iCircleType]);
        mEyeView 	= menu.add(mEyeValues[iEyeType]);
        mCameraView = menu.add(mCameraViewValues[iCameraType]);
        mItemType   = menu.add(mDetectorName[mDetectorType]);
        mSmileState.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "Menu Item selected " + item);
         if (item == mEyeView){
        	iEyeType = (iEyeType == 0)?1:0;
        	item.setTitle(mEyeValues[iEyeType]);
        	mView.setEyeState(iEyeType);
        }
        else if (item == mCircleType){
        	iCircleType = (iCircleType == 0)?1:0;
        	item.setTitle(mCircleValues[iCircleType]);
        	mView.setCircleType(iCircleType);
        }
        else if (item == mSmileState){
        	iSmileType = (iSmileType==0)?1:0;
        	item.setTitle(mSmileNames[iSmileType]);
        	if(iSmileType == 1){
        		mMouthState.setEnabled(false);
        		mView.setMouthEvent("Smile",true);
        	}else if(iSmileType == 0){
        		mMouthState.setEnabled(true);
        		mView.setMouthEvent("Smile",false);
        	}
        } else if (item == mMouthState){
        	iMouthType = (iMouthType==0)?1:0;
        	item.setTitle(mMouthNames[iMouthType]);
        	if(iMouthType == 1){
        		mSmileState.setEnabled(false);
        		mView.setMouthEvent("Mouth",true);
        	}else if(iMouthType == 0){
        		mSmileState.setEnabled(true);
        		mView.setMouthEvent("Mouth",false);
        	}
        }
        else if (item == mItemType)
        {
        	mDetectorType = (mDetectorType + 1) % mDetectorName.length;
        	item.setTitle(mDetectorName[mDetectorType]);
        	mView.setDetectorType(mDetectorType);
        }else if(item == mCameraView){
        	finish();
        	Intent intent = new Intent(this,FdActivity.class);
        	Bundle b = new Bundle();
        	iCameraType = (iCameraType==0)?1:0;
        	b.putInt("mCameraType", iCameraType);
        	item.setTitle(mCameraViewValues[iCameraType]);
        	intent.putExtras(b);
        	startActivity(intent);
        }
        return true;
    }
    
  
}
