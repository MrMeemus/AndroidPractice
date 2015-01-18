package com.practice.rquan24.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by rquan24 on 1/8/15.
 * This fragment is hosted by the CrimeCameraActivity. It will be started
 * from crime fragment when the camera icon button is clicked. You have to
 * implement the interface of surface holder to synchronize the call back methods
 * with camera methods.
 *
 * Since camera is a single resource it is very important to release it as soon as you are done
 * and this must be done in all life cycle methods.
 */
public class CrimeCameraFragment extends Fragment
{
    private static final String TAG = "CrimeCameraFragment";
    public static final String EXTRA_PHOTO_FILENAME = "com.practice.rquan24.criminalintent.photo_filename";

    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private View mProgressContainer;

    /*
     Implementing call backs for takePicture(...)
     Each interface has one method to implement
     we are only setting the progress bar when the picture is taken.
     We don't have to set it invisible again because the activity will
     end after processing the picture.
    */


    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback()
    {
        @Override
        public void onShutter()
        {
            // Display the progress indicator
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };

    /*
        This is the method where the picture has been taken and the data as a JPEG is available
        We create a random file name to save the JPEG to disk

     */
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera)
        {
            //Create a filename
            String filename = UUID.randomUUID().toString() + ".jpg";
            //Save the Jpeg data to disk
            FileOutputStream os = null;
            boolean success = true;

            try
            {
                os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                os.write(bytes);
            }
            catch(Exception e)
            {
                Log.e(TAG, "Error saving the photo", e);
            }
            finally
            {
                try
                {
                    if(os != null)
                    {
                        os.close();
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    Log.e(TAG, "Error closing output stream", e);
                    success = false;
                }
            }

            if(success)
            {
                Intent i = new Intent();
                i.putExtra(EXTRA_PHOTO_FILENAME, filename);
                getActivity().setResult(Activity.RESULT_OK, i);
            }
            else
            {
                getActivity().setResult(Activity.RESULT_CANCELED);
            }

            getActivity().finish();
        }
    };

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_crime_camera, parent, false);

        mProgressContainer = v.findViewById(R.id.crime_camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);

        Button takePictureButton = (Button) v.findViewById(R.id.crime_camera_takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(mCamera != null)
                {
                    mCamera.takePicture(mShutterCallback, null, mPictureCallback);
                }
            }
        });

        mSurfaceView = (SurfaceView) v.findViewById(R.id.crime_camera_surfaceView);
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // Surface Holder has call back methods that we can use to wire up Camera
        holder.addCallback(new SurfaceHolder.Callback()
        {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder)
            {
                try
                {
                    if(mCamera != null)
                    {
                        mCamera.setPreviewDisplay(surfaceHolder);
                    }
                } catch (IOException e)
                {
                    e.printStackTrace();
                    Log.e(TAG,"Error setting up display", e);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3)
            {
                if(mCamera == null) return;

                Camera.Parameters parameters = mCamera.getParameters();
                Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), i2, i3);
                parameters.setPreviewSize(s.width, s.height);
                s = getBestSupportedSize(parameters.getSupportedPictureSizes(), i2, i3);
                parameters.setPictureSize(s.width, s.height);
                mCamera.setParameters(parameters);

                try
                {
                    mCamera.startPreview();
                }
                catch(Exception e)
                {
                    Log.e(TAG, "could not start preview", e);
                    mCamera.release();
                    mCamera = null;
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder)
            {
                if(mCamera != null)
                {
                    mCamera.stopPreview();
                }
            }
        });


        return v;
    }

    @TargetApi(19)
    @Override
    public void onResume()
    {
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
        {
            mCamera = Camera.open(0);
        }
        else
        {
            mCamera = Camera.open();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(mCamera != null)
        {
            mCamera.release();
            mCamera = null;
        }
    }

    private Size getBestSupportedSize(List<Size> sizes, int width, int height)
    {
        Size bestSize = sizes.get(0);
        int largestSize = bestSize.width * bestSize.height;
        for(Size s: sizes)
        {
            int currentSize = s.width * s.height;
            if(currentSize > largestSize)
            {
                largestSize = currentSize;
                bestSize = s;
            }
        }

        return bestSize;
    }
}
