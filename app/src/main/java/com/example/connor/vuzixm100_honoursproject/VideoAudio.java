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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Connor on 24/01/2017.
 * Handles video/audio data.
 * https://developer.android.com/guide/topics/media/camera.html#capture-video
 * ^ Used throughout class. Accessed: 25/01/2017 @ 19:10
 * http://stackoverflow.com/questions/1817742/how-can-i-capture-a-video-recording-on-android
 * ^ Also used to aid with video capturing due to docs code being a bit "poor". Accessed: 04/02/2017 @ 20:27
 */
public class VideoAudio implements SurfaceHolder.Callback
{
    private Context context;
    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder mHolder;
    private MediaRecorder mr;

    public VideoAudio(Context context, SurfaceView surfaceView) {
        this.context = context;
        this.surfaceView = surfaceView;
        camera = Camera.open();
        mr = new MediaRecorder();
        mHolder = surfaceView.getHolder();
        mHolder.addCallback(this);
        init();
    }

    public void init()
    {
        camera.unlock();
        mr.setCamera(camera);

        if (camera == null)
        {
            System.out.print("Camera = null");
        }

        mr.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mr.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ACELP");

        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                System.out.println("Failed to create directory...");
            }
        }

        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "NewVideo.mp4");

        mr.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
        mr.setOutputFile(mediaFile.toString());

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    mr.setPreviewDisplay(mHolder.getSurface());
                } catch (Exception e) {
                    System.out.println("Error:" + String.valueOf(e));
                }
                try {
                    mr.prepare();
                } catch (IllegalStateException e) {
                    System.out.println("Error: " + String.valueOf(e));
                } catch (IOException e) {
                    System.out.println("Error: " + String.valueOf(e));
                }
                // this code will be executed after 2 seconds
                mr.start();
            }
        }, 1000);
    }

    public void surfaceCreated(SurfaceHolder holder)
    {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        try {
            camera.setPreviewDisplay(mHolder);
            camera.startPreview();

        } catch (Exception e) {
            System.out.println("Error: " + String.valueOf(e));
        }
    }
}


