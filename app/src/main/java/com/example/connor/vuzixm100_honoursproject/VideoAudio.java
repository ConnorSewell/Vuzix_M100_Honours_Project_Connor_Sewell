package com.example.connor.vuzixm100_honoursproject;

import android.app.Activity;
import android.content.Context;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.hardware.Camera;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.IOException;

/**
 * Created by Connor on 24/01/2017.
 * Handles video/audio data.
 * https://developer.android.com/guide/topics/media/camera.html#capture-video
 * ^ Used throughout class. Accessed: 25/01/2017 @ 19:10
 */
public class VideoAudio
{
    private Context context;
    private CameraPreview cp;
    private Camera camera;

    public VideoAudio(Context context) {
        this.context = context;
        cp = new CameraPreview(this.context, camera);

        camera = Camera.open();
        init();
    }

    private void init() {
        MediaRecorder mr = new MediaRecorder();

        camera.unlock();
        mr.setCamera(camera);

        mr.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mr.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ACELP");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                System.out.println("Failed to create directory...");
            }
        }

        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "VID_.mp4");

        mr.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        mr.setOutputFile(mediaFile.toString());

        mr.setPreviewDisplay(cp.getHolder().getSurface());

        try
        {
            mr.prepare();
        } catch (IllegalStateException e) {
            System.out.println("Error: " + String.valueOf(e));
        } catch (IOException e) {
            System.out.println("Error: " + String.valueOf(e));
        }

        //mr.start();
    }

    /**
     * https://developer.android.com/guide/topics/media/camera.html#capture-video
     * ^Used throughout class. Accessed: 25/01/2017 @ 19:10
     */
    class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;
            mHolder = getHolder();
            mHolder.addCallback(this);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                System.out.println("Error with previw: " + String.valueOf(e));
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. Take care of releasing the Camera preview in your activity.
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            if (mHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e) {
                System.out.println("Error: " + String.valueOf(e));
            }
        }
    }
}

