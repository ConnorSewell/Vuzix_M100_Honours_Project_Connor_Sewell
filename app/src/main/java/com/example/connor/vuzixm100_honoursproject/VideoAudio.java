package com.example.connor.vuzixm100_honoursproject;

import android.app.Activity;
import android.content.Context;
import android.media.CamcorderProfile;
import android.media.CameraProfile;
import android.media.MediaRecorder;
import android.hardware.Camera;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
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

    private String TAG = "Video Audio Class: ";

    public VideoAudio(SurfaceView surfaceView)
    {
        //this.context = context;
        this.surfaceView = surfaceView;
        camera = Camera.open();
        mr = new MediaRecorder();
        mHolder = surfaceView.getHolder();
        mHolder.addCallback(this);
    }

    public void init(Socket client)
    {
        camera.unlock();
        mr.setCamera(camera);

        Log.i(TAG,"Video init");

        if (camera == null)
        {
            System.out.print("Camera = null");
        }

        mr.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mr.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ACELP");

        if (!mediaStorageDir.exists())
        {
            if (!mediaStorageDir.mkdirs())
            {
                System.out.println("Failed to create directory...");
            }
        }

        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "NewVideo.mp4");

        CamcorderProfile cp = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
        cp.videoFrameRate = 24;
        mr.setProfile(cp);

        mr.setOutputFile(mediaFile.toString());
        //ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(socket);
        //mr.setOutputFile(pfd.getFileDescriptor());

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try
                {
                    mr.setPreviewDisplay(mHolder.getSurface());
                } catch (Exception e) {
                    System.out.println("Error on set preview display:" + String.valueOf(e));
                }
                try
                {
                    mr.prepare();
                } catch (Exception e)
                {
                    System.out.println("Error on prepare: " + String.valueOf(e));
                }
                try
                {
                    mr.start();
                }
                catch(Exception e)
                {
                    System.out.println("Error on start: " + String.valueOf(e));
                }
            }
        }, 5000);
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

        try
        {
            //camera.setPreviewDisplay(mHolder);
            //camera.startPreview();

        } catch (Exception e) {
            System.out.println("Error on camera preview: " + String.valueOf(e));
        }
    }
}


