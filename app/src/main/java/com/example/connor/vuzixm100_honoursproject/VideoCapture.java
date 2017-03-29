package com.example.connor.vuzixm100_honoursproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
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
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
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
public class VideoCapture implements SurfaceHolder.Callback
{
    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder mHolder;
    private MediaRecorder mr = new MediaRecorder();
    private DataOutputStream outputPoint;
    private String outputDirectory;
    private boolean streamMode;
    private String TAG = "Video Audio Class: ";
    private ImageView imgView;
    private Main activity;

    public VideoCapture(SurfaceView surfaceView, boolean mr1, String outputDirectory, boolean streamMode, Main activity)
    {
        this.surfaceView = surfaceView;
        this.outputDirectory = outputDirectory;
        this.streamMode = streamMode;
        this.activity = activity;
        this.mr = mr;

        camera = Camera.open();
        mHolder = surfaceView.getHolder();
        mHolder.addCallback(this);

        if(!streamMode)
        {
            init();
        }
        else
        {
            setCameraProperties();
        }
    }

    private void setCameraProperties()
    {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewFpsRange(24000, 24000);
        parameters.setPreviewSize(320, 240);
        camera.setParameters(parameters);

        mHolder = surfaceView.getHolder();
        mHolder.addCallback(this);
    }

    private void init()
    {

        camera.unlock();

        activity.getMediaRecorder().setAudioSamplingRate(8000);
        activity.getMediaRecorder().setCamera(camera);
        activity.getMediaRecorder().setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        activity.getMediaRecorder().setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        CamcorderProfile cp = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        cp.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
        cp.videoFrameRate = 24;

        activity.getMediaRecorder().setProfile(cp);

        File mediaFile = new File(outputDirectory + File.separator + "Video.mp4");
        activity.getMediaRecorder().setOutputFile(mediaFile.toString());

        activity.setSensorReadyStoreMode();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                try {
                    activity.getMediaRecorder().setPreviewDisplay(mHolder.getSurface());
                } catch (Exception e) {
                    System.out.println("Error on set preview display:" + String.valueOf(e));
                }
                try {
                    activity.getMediaRecorder().prepare();
                } catch (Exception e) {
                    System.out.println("Error on prepare: " + String.valueOf(e));
                }
                try {

                    activity.getMediaRecorder().start();


                } catch (Exception e) {
                    System.out.println("Error on start: " + String.valueOf(e));
                }
            }
        }, 500);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                activity.getMediaRecorder().stop();
            }
        }, 30000);


    }

    public void setOutputPoint(DataOutputStream outputPoint)
    {
        this.outputPoint = outputPoint;
    }

    public void changePreviewStreamingState()
    {
        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(final byte[] data, Camera camera) {
                //http://stackoverflow.com/questions/20298699/onpreviewframe-data-image-to-imageview
                //^ Accessed: 08/03/2017 @ 14:00
                Camera.Parameters parameters = camera.getParameters();
                int width = parameters.getPreviewSize().width;
                int height = parameters.getPreviewSize().height;

                YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);
                byte[] byteArray = out.toByteArray();

                try {
                    //http://stackoverflow.com/questions/2878867/how-to-send-an-array-of-bytes-over-a-tcp-connection-java-programming
                    //^ This method of sending byte array taken from above array (starts in VideoStreamer class)
                    //created in VideoStreamer class. Accessed: 08/03/2017 @ 21:00
                    int arrayLength = byteArray.length;
                    outputPoint.writeInt(arrayLength);
                    if (arrayLength > 0)
                    {
                        outputPoint.write(byteArray, 0, arrayLength);
                        outputPoint.flush();
                    }
                } catch (IOException io) {
                    Log.e(TAG, io.toString());
                }
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        try
        {
            camera.setPreviewDisplay(mHolder);
            camera.startPreview();

        }
        catch(Exception e)
        {
            Log.e(TAG, e.toString());
        }
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
    }
}


