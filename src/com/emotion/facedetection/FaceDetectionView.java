package com.emotion.facedetection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.samples.facedetect.DetectionBasedTracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.ContextMenu;
import android.view.SurfaceHolder;

class FaceDetectionView extends SampleCvViewBase {
	private static final String TAG = "Sample::FdView";
	private Mat mRgba;
	private Mat mGray;
	private File mCascadeFile, mEyeFile, mMouthFile, mSmileFile;
	private CascadeClassifier mJavaDetector, mEyeDetector, mSmileDetector,
			mMouthDetector;
	private DetectionBasedTracker mNativeDetector;

	private static final Scalar FACE_RECT_COLOR = new Scalar(255, 167, 192, 19);
	private static final Scalar EYE_RECT_COLOR = new Scalar(255, 0, 0, 255);
	private static final Scalar MOUTH_RECT_COLOR = new Scalar(181, 12, 115, 255);

	public static final int JAVA_DETECTOR = 0;
	public static final int NATIVE_DETECTOR = 1;

	private int mDetectorType = JAVA_DETECTOR;

	private float mRelativeFaceSize = 0;
	private int mAbsoluteFaceSize = 0;

	public void setMinFaceSize(float faceSize) {
		mRelativeFaceSize = faceSize;
		mAbsoluteFaceSize = 0;
	}

	public void setDetectorType(int type) {
		if (mDetectorType != type) {
			mDetectorType = type;

			if (type == NATIVE_DETECTOR) {
				Log.i(TAG, "Detection Based Tracker enabled");
				mNativeDetector.start();
			} else {
				Log.i(TAG, "Cascade detector enabled");
				mNativeDetector.stop();
			}
		}
	}

	public FaceDetectionView(Context context) {
		super(context);
		try {
			InputStream is = context.getResources().openRawResource(
					R.raw.haarcascade_frontalface_alt);
			File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
			mCascadeFile = new File(cascadeDir,
					"haarcascade_frontalface_alt.xml");
			FileOutputStream os = new FileOutputStream(mCascadeFile);

			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = is.read(buffer)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			is.close();
			os.close();

			mJavaDetector = new CascadeClassifier(
					mCascadeFile.getAbsolutePath());
			if (mJavaDetector.empty()) {
				Log.e(TAG, "Failed to load cascade classifier");
				mJavaDetector = null;
			} else {
				Log.i(TAG,
						"Loaded cascade classifier from "
								+ mCascadeFile.getAbsolutePath());
			}

			InputStream eye = context.getResources().openRawResource(
					R.raw.haarcascade_eye_tree_eyeglasses);
			File eyeDir = context.getDir("cascadeeye", Context.MODE_PRIVATE);
			mEyeFile = new File(eyeDir, "haarcascade_eye_tree_eyeglasses.xml");
			FileOutputStream os1 = new FileOutputStream(mEyeFile);

			byte[] buffer1 = new byte[4096];
			int bytesRead1;
			while ((bytesRead1 = eye.read(buffer1)) != -1) {
				os1.write(buffer1, 0, bytesRead1);
			}
			eye.close();
			os1.close();

			mEyeDetector = new CascadeClassifier(mEyeFile.getAbsolutePath());
			if (mEyeDetector.empty()) {
				Log.e(TAG, "Failed to load eye cascade classifier");
				mEyeDetector = null;
			} else {
				Log.i(TAG,
						"Loaded eye cascade classifier from "
								+ mEyeFile.getAbsolutePath());
			}

			// Changes for Face Made here
			// If want only Mouth then use haarcascade_mcs_mouth xml file
			InputStream mouth = context.getResources().openRawResource(
					R.raw.haarcascade_mcs_mouth);
			File mouthDir = context
					.getDir("cascademouth", Context.MODE_PRIVATE);
			mMouthFile = new File(mouthDir, "haarcascade_mcs_mouth.xml");
			FileOutputStream os2 = new FileOutputStream(mMouthFile);

			byte[] buffer2 = new byte[4096];
			int bytesRead2;
			while ((bytesRead2 = mouth.read(buffer2)) != -1) {
				os2.write(buffer2, 0, bytesRead2);
			}
			mouth.close();
			os2.close();

			mMouthDetector = new CascadeClassifier(mMouthFile.getAbsolutePath());
			if (mMouthDetector.empty()) {
				Log.e(TAG, "Failed to load Mouth cascade classifier");
				mMouthDetector = null;
			} else {
				Log.i(TAG,
						"Loaded mouth cascade classifier from "
								+ mMouthFile.getAbsolutePath());
			}

			// Changes for Face Made here
			// If want only Smile then use smile5 xml file
			InputStream smile = context.getResources().openRawResource(
					R.raw.haarcascade_smile);
			File smileDir = context
					.getDir("cascadesmile", Context.MODE_PRIVATE);
			mSmileFile = new File(smileDir, "haarcascade_smile.xml");
			FileOutputStream os3 = new FileOutputStream(mSmileFile);

			byte[] buffer3 = new byte[4096];
			int bytesRead3;
			while ((bytesRead3 = smile.read(buffer3)) != -1) {
				os3.write(buffer3, 0, bytesRead3);
			}
			smile.close();
			os3.close();

			mSmileDetector = new CascadeClassifier(mSmileFile.getAbsolutePath());
			if (mSmileDetector.empty()) {
				Log.e(TAG, "Failed to load Smile cascade classifier");
				mSmileDetector = null;
			} else {
				Log.i(TAG,
						"Loaded Smile cascade classifier from "
								+ mSmileFile.getAbsolutePath());
			}

			mNativeDetector = new DetectionBasedTracker(
					mCascadeFile.getAbsolutePath(), 0);
			if (mNativeDetector == null) {
				Log.e(TAG, "Failed to load native cascade classifier");
				// mJavaDetector = null;
			} else {
				Log.i(TAG, "Loaded native cascade classifier from "
						+ mCascadeFile.getAbsolutePath());
			}

			cascadeDir.delete();
			eyeDir.delete();
			mouthDir.delete();
			smileDir.delete();

		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
		}
	}


	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		synchronized (this) {
			// initialize Mats before usage
			mGray = new Mat();
			mRgba = new Mat();
		}

		super.surfaceCreated(holder);
	}

	@Override
	protected void onCreateContextMenu(ContextMenu menu) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu);
	}

	Rect[] mouthArray;
	Rect[] smileArray;

	@Override
	protected Bitmap processFrame(VideoCapture capture) {
		capture.retrieve(mRgba, Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA);
		capture.retrieve(mGray, Highgui.CV_CAP_ANDROID_GREY_FRAME);

		if (mAbsoluteFaceSize == 0) {
			int height = mGray.rows();
			if (Math.round(height * mRelativeFaceSize) > 0)
				;
			{
				mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
			}
			mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
		}

		MatOfRect faces = new MatOfRect();
	//	MatOfRect eyes = new MatOfRect();
		MatOfRect mouth = new MatOfRect();
		MatOfRect smile = new MatOfRect();

		if (mDetectorType == JAVA_DETECTOR) {
			if (mJavaDetector != null)
				mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2,
						2 // TODO: objdetect.CV_HAAR_SCALE_IMAGE
						, new Size(mAbsoluteFaceSize, mAbsoluteFaceSize),
						new Size());
		} else if (mDetectorType == NATIVE_DETECTOR) {
			if (mNativeDetector != null)
				mNativeDetector.detect(mGray, faces);
		} else {
			Log.e(TAG, "Detection method is not selected!");
		}

		Rect[] facesArray = faces.toArray();
		for (int i = 0; i < facesArray.length; i++)
			Core.rectangle(mRgba, facesArray[0].tl(), facesArray[0].br(),
					FACE_RECT_COLOR, 6);

		if (facesArray.length > 0) {

			// Rect roi = new
			// Rect((int)facesArray[0].tl().x,(int)facesArray[0].tl().y,facesArray[0].width,facesArray[0].height);
			// Rect roi = new
			// Rect((int)facesArray[0].tl().x,(int)(facesArray[0].tl().y+facesArray[0].height/5),facesArray[0].width,(int)(facesArray[0].height/3));//
			Rect roi = new Rect((int) facesArray[0].tl().x,
					(int) (facesArray[0].tl().y), facesArray[0].width,
					(int) (facesArray[0].height));//
			// taking inputs from nustrat opencv example
			// check above, using tl of x and tl of y.other wise it will give
			// runtime errors
			//Mat cropped = new Mat();
			// cropped = mGray.submat(facesArray[0]);// yuppie!, this did the
			// trick!...everything else was failing
			// refer to opencv 2.4 tut pdf
			if (roi.x >= 0 && roi.width >= 0 && roi.y >= 0 && roi.height >= 0) {
//				cropped = mGray.submat(roi);
//
//				// cropped.copyTo(mGray.submat(roi));
//				if (mEyeDetector != null && iEyeType == 0)
//					mEyeDetector.detectMultiScale(cropped, eyes, 1.1, 2, 2,
//							new Size(mAbsoluteFaceSize, mAbsoluteFaceSize),
//							new Size());
//				else
//					Log.i("Fdvuew", "mEyeDetector is NULL");
//
//				// release the memory
//				cropped.release();
//				cropped = null;
//
//				Rect[] eyesArray;
//				eyesArray = eyes.toArray();
			//	Point x1 = new Point();
				// using opencv tutorials for circle, its working fine now.
//				for (int i = 0; i < eyesArray.length; i++) {
//
//					x1.x = facesArray[0].x + eyesArray[i].x
//							+ eyesArray[i].width * 0.5;
//					x1.y = facesArray[0].y + eyesArray[i].y
//							+ eyesArray[i].height * 0.5;
//					int Radius = (int) ((eyesArray[i].width + eyesArray[i].height) * 0.25);
//					Core.circle(mRgba, x1, Radius, EYE_RECT_COLOR, 3);
//
//					// x1.y=faces[i].y + eyes[j].y + eyes[j].height*0.5;
//
//					// Core.rectangle(mRgba,eyesArray[i].tl(),
//					// eyesArray[i].br(), EYE_RECT_COLOR, 3);
//					// x1.x=eyesArray[i].tl().x + facesArray[0].width;
//					// x1.y=eyesArray[i].tl().y + facesArray[0].width;
//					// Core.rectangle(mRgba,x1, eyesArray[i].br(),
//					// EYE_RECT_COLOR, 3);
//				}

				// is Menu is Selected
				if (isTurnedOn) {
					Rect roi_Mouth = new Rect((int) facesArray[0].tl().x,
							(int) (facesArray[0].tl().y)/*
														 * +facesArray[0].height*
														 * 2/3
														 */,
							facesArray[0].width, (int) (facesArray[0].height)/**
					 * 
					 * 2/5
					 */
					);//
					Mat cropped_Mouth = new Mat();
					Mat cropped_Smile = new Mat();
					cropped_Mouth = mGray.submat(roi_Mouth);
					cropped_Smile = mGray.submat(roi_Mouth);

					CascadeClassifier classifier = mMouthDetector;
					CascadeClassifier classifier_Smile = null;
					if (strTypeSelected.equals("Mouth")) {
						classifier = mMouthDetector;
						classifier_Smile = mSmileDetector;
					} else if (strTypeSelected.equals("Smile")) {
						classifier = mSmileDetector;
					}

					if (classifier != null)
						classifier.detectMultiScale(cropped_Mouth, mouth, 1.1,
								0, 1, new Size(mAbsoluteFaceSize,
										mAbsoluteFaceSize), new Size());
					else
						Log.i("Fdvuew", "classifier is NULL");

					// Smile Detector Classsifier only in-case of Mouth
					// detection
					if (classifier_Smile != null)
						classifier_Smile.detectMultiScale(cropped_Smile, smile,
								1.1, 0, 1, new Size(mAbsoluteFaceSize,
										mAbsoluteFaceSize), new Size());
					else
						Log.i("Fdvuew", "classifier is NULL");

					cropped_Smile.release();
					cropped_Mouth.release();
					cropped_Mouth = null;

					mouthArray = mouth.toArray();
					smileArray = smile.toArray();
					 Log.i("smileArraysmileArray","smileArray Count"+smileArray.length+" mouthArray "+mouthArray.length);
					Point x2 = new Point();
					for (int i = 0; i < mouthArray.length; i++) {
						// int Radius=(int)((mouthArray[i].width +
						// mouthArray[i].height)*0.25 );
						// x2.x=facesArray[0].x + mouthArray[i].tl().x +Radius;
						// x2.y=mouthArray[i].br().y + facesArray[0].height;
						// Core.rectangle(mRgba, x2, mouthArray[i].br(),
						// MOUTH_RECT_COLOR, 3);
						x2.x = facesArray[0].x + mouthArray[i].x
								+ mouthArray[i].width * 0.5;
						x2.y = facesArray[0].y + mouthArray[i].y
								+ mouthArray[i].height * 0.5;
						if (x2.y > (facesArray[0].y * 5)) {
							int Radius = (int) ((mouthArray[i].width + mouthArray[i].height) * 0.20);
							Core.circle(mRgba, x2, Radius, MOUTH_RECT_COLOR, 4);
							if (iCircleType == 1) {
								break;
							}
						}

					}

				}
				smile.release();
				faces.release();
				mouth.release();
				//eyes.release();
				faces = null;
				mouth = null;
			//	eyes = null;
			}

		}

		// Bitmap bmp = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(),
		// Bitmap.Config.ARGB_8888);
		Bitmap bmp = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(),
				Bitmap.Config.ARGB_8888);

		try {
			Utils.matToBitmap(mRgba, bmp);
		} catch (Exception e) {
			Log.e(TAG,
					"Utils.matToBitmap() throws an exception: "
							+ e.getMessage());
			bmp.recycle();
			bmp = null;
		}

		return bmp;

	}

	protected int updateMouth() {
		if (smileArray != null)
			return smileArray.length;
		else
			return 0;
	}

	@Override
	public void run() {
		super.run();

		synchronized (this) {
			// Explicitly deallocate Mats
			if (mRgba != null)
				mRgba.release();
			if (mGray != null)
				mGray.release();
			if (mCascadeFile != null)
				mCascadeFile.delete();
			if (mNativeDetector != null)
				mNativeDetector.release();

			mRgba = null;
			mGray = null;
			mCascadeFile = null;
		}
	}

	/**
	 * sets the Smile or Mouth Setting of the user setting
	 * 
	 * @param string
	 * @param string2
	 */

	String strTypeSelected;
	boolean isTurnedOn;

	public void setMouthEvent(String strTypeSelected, boolean isTurnedOn) {
		this.strTypeSelected = strTypeSelected;
		this.isTurnedOn = isTurnedOn;
	}

	int iCircleType;

	public void setCircleType(int iCircleType) {
		this.iCircleType = iCircleType;
	}

	int iEyeType;

	public void setEyeState(int iEyeType) {
		this.iEyeType = iEyeType;
	}
}