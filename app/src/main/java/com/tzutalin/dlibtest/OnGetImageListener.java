/*
 * Copyright 2016-present Tzutalin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tzutalin.dlibtest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.media.Image.Plane;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Handler;
import android.os.Trace;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.tzutalin.dlib.Constants;
import com.tzutalin.dlib.FaceDet;
import com.tzutalin.dlib.VisionDetRet;

import junit.framework.Assert;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that takes in preview frames and converts the image to Bitmaps to process with dlib lib.
 */
@SuppressLint("NewApi")
public class OnGetImageListener implements OnImageAvailableListener {

    private static final int INPUT_SIZE = 320;
    private static final String TAG = "OnGetImageListener";
    private static final String JNITAG = "JNIArucoTest";

    static {
        System.loadLibrary("native-lib");
    }

    private int mScreenRotation = 90;

    private int mPreviewWdith = 0;
    private int mPreviewHeight = 0;
    private byte[][] mYUVBytes;
    private int[] mRGBBytes = null;
    private Bitmap mRGBframeBitmap = null;
    private Bitmap mCroppedBitmap = null;

    private boolean mIsComputing = false;
    private Handler mInferenceHandler;

    private Context mContext;
    private FaceDet mFaceDet;
    private TrasparentTitleView mTransparentTitleView;
    private FloatingCameraWindow mWindow;

    private static float tVecX;

    public void initialize(
            final Context context,
            final AssetManager assetManager,
            final TrasparentTitleView scoreView,
            final Handler handler) {
        this.mContext = context;
        this.mTransparentTitleView = scoreView;
        this.mInferenceHandler = handler;
        mFaceDet = new FaceDet(Constants.getFaceShapeModelPath());
        mWindow = new FloatingCameraWindow(mContext);

    }

    public void deInitialize() {
        synchronized (OnGetImageListener.this) {

            if (mWindow != null) {
                mWindow.release();
            }
        }
    }

    private void drawResizedBitmap(final Bitmap src, final Bitmap dst) {

        Display getOrient = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int orientation = Configuration.ORIENTATION_UNDEFINED;
        Point point = new Point();
        getOrient.getSize(point);
        int screen_width = point.x;
        int screen_height = point.y;
        Log.d(TAG, String.format("screen size (%d,%d)", screen_width, screen_height));
        if (screen_width < screen_height) {
            orientation = Configuration.ORIENTATION_PORTRAIT;
            mScreenRotation = 90;
        } else {
            orientation = Configuration.ORIENTATION_LANDSCAPE;
            mScreenRotation = 0;
        }

        Assert.assertEquals(dst.getWidth(), dst.getHeight());
        final float minDim = Math.min(src.getWidth(), src.getHeight());

        final Matrix matrix = new Matrix();

        // We only want the center square out of the original rectangle.
        final float translateX = -Math.max(0, (src.getWidth() - minDim) / 2);
        final float translateY = -Math.max(0, (src.getHeight() - minDim) / 2);
        matrix.preTranslate(translateX, translateY);

        final float scaleFactor = dst.getHeight() / minDim;
        matrix.postScale(scaleFactor, scaleFactor);

        // Rotate around the center if necessary.
        if (mScreenRotation != 0) {
            matrix.postTranslate(-dst.getWidth() / 2.0f, -dst.getHeight() / 2.0f);
            matrix.postRotate(mScreenRotation);
            matrix.postTranslate(dst.getWidth() / 2.0f, dst.getHeight() / 2.0f);
        }

        final Canvas canvas = new Canvas(dst);
        canvas.drawBitmap(src, matrix, null);
    }

    private Mat convertBitmapToMat(Bitmap inRgbaImage){

        // create a Mat to Output
        Mat rgbaMatOut = new Mat(inRgbaImage.getWidth(), inRgbaImage.getHeight(), CvType.CV_8UC4, Scalar.all(255));
        //Bitmap bm32 = inRgbaImage.copy(Config.ARGB_8888,true);

        // convert Bitmap to Mat using OpenCV-Java
        Utils.bitmapToMat(inRgbaImage,rgbaMatOut);

        return rgbaMatOut;
    }

    private Bitmap converMatToBitmap(Mat rgbaMat){

        // create a Bitmap to Output
        Bitmap bmOutput = Bitmap.createBitmap(rgbaMat.width(),rgbaMat.height(), Config.ARGB_8888);

        // convert Mat to Bitmap using OpenCV-Java
        Utils.matToBitmap(rgbaMat,bmOutput);

        return bmOutput;
    }

    @Override
    public void onImageAvailable(final ImageReader reader) {
        Image image = null;
        try {
            image = reader.acquireLatestImage();

            if (image == null) {
                return;
            }

            // No mutex needed as this method is not reentrant.
            if (mIsComputing) {
                image.close();
                return;
            }
            mIsComputing = true;

            Trace.beginSection("imageAvailable");

            final Plane[] planes = image.getPlanes();

            // Initialize the storage bitmaps once when the resolution is known.
            if (mPreviewWdith != image.getWidth() || mPreviewHeight != image.getHeight()) {
                mPreviewWdith = image.getWidth();
                mPreviewHeight = image.getHeight();

                Log.d(TAG, String.format("mPreview at size %dx%d", mPreviewWdith, mPreviewHeight));

                mRGBBytes = new int[mPreviewWdith * mPreviewHeight];
                mRGBframeBitmap = Bitmap.createBitmap(mPreviewWdith, mPreviewHeight, Config.ARGB_8888);
                mCroppedBitmap = Bitmap.createBitmap(mPreviewWdith, mPreviewHeight, Config.ARGB_8888);

                Log.d(TAG, String.format("mCroppedBitmap at size %dx%d", mCroppedBitmap.getWidth(), mCroppedBitmap.getHeight()));

                mYUVBytes = new byte[planes.length][];
                for (int i = 0; i < planes.length; ++i) {
                    mYUVBytes[i] = new byte[planes[i].getBuffer().capacity()];
                }
            }

            for (int i = 0; i < planes.length; ++i) {
                planes[i].getBuffer().get(mYUVBytes[i]);
            }

            final int yRowStride = planes[0].getRowStride();
            final int uvRowStride = planes[1].getRowStride();
            final int uvPixelStride = planes[1].getPixelStride();
            ImageUtils.convertYUV420ToARGB8888(
                    mYUVBytes[0],
                    mYUVBytes[1],
                    mYUVBytes[2],
                    mRGBBytes,
                    mPreviewWdith,
                    mPreviewHeight,
                    yRowStride,
                    uvRowStride,
                    uvPixelStride,
                    false);

            image.close();
        } catch (final Exception e) {
            if (image != null) {
                image.close();
            }
            Log.e(TAG, "Exception!", e);
            Trace.endSection();
            return;
        }

        // covert RGB to Bitmap
        mRGBframeBitmap.setPixels(mRGBBytes, 0, mPreviewWdith, 0, 0, mPreviewWdith, mPreviewHeight);
        //Log.d(TAG, String.format("Initializing[mRGBframeBitmap] at size %dx%d", mRGBframeBitmap.getWidth(), mRGBframeBitmap.getHeight()));

        //drawResizedBitmap(mRGBframeBitmap, mCroppedBitmap);
        mCroppedBitmap = mRGBframeBitmap;

        mInferenceHandler.post(
                new Runnable() {
                    @Override
                    public void run() {
                        long startTime = System.currentTimeMillis();

                        synchronized (OnGetImageListener.this) {

                            Mat matRgbaInput = convertBitmapToMat(mCroppedBitmap);
                            Mat matGray = new Mat(mCroppedBitmap.getWidth(), mCroppedBitmap.getHeight(), CvType.CV_8UC1, Scalar.all(255));

                            //convertGray(matRgbaInput.getNativeObjAddr(), matGray.getNativeObjAddr());


                            tVecX = arucoSimple(matGray.getNativeObjAddr(), matRgbaInput.getNativeObjAddr());
                            //Log.d(TAG, String.format("Tx: %.3f", tVecX));
                            mTransparentTitleView.setText("Altura(Tz): " + String.valueOf(tVecX));

                            mCroppedBitmap = converMatToBitmap(matRgbaInput);
                            Log.d(JNITAG,jniGetLog());

                        }
                        long endTime = System.currentTimeMillis();
                        //mTransparentTitleView.setText("Time cost: " + String.valueOf((endTime - startTime) / 1000f ) + " sec");
                        //mTransparentTitleView.setText("Frames p/second: " + String.valueOf(1/ ((endTime - startTime) / 1000f) ) + " fps");
                        mWindow.setRGBBitmap(mCroppedBitmap);
                        mIsComputing = false;
                    }
                });

        Trace.endSection();
    }

    public native float arucoSimple(long imgGray, long imgColor);
    //public native void convertGray(long imgRgba, long imgGray);
    public native String jniGetLog();

}
