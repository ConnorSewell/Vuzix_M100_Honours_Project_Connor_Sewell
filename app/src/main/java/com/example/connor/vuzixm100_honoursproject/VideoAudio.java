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
    boolean mr1;

    private String TAG = "Video Audio Class: ";

    private ImageView imgView;

    public VideoAudio(SurfaceView surfaceView, boolean mr1, ImageView imgView)
    {
        //this.context = context;
        this.surfaceView = surfaceView;
        camera = Camera.open();
        //mr = new MediaRecorder();
        mHolder = surfaceView.getHolder();
        mHolder.addCallback(this);
        this.imgView = imgView;
        //this.mr1 = mr1;
    }

    public void init()
    {
        //camera.unlock();
        //mr.setCamera(camera);

        //camera.stopPreview();
        //camera.unlock();

        //Log.i(TAG, "Video init");

        //mr.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        //mr.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        //mr.setOutputFormat(8);
        //mr.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //mr.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

        //File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "ACELP");

        //    if (!mediaStorageDir.exists())
        //    {
        //        if (!mediaStorageDir.mkdirs())
        //        {
        //            System.out.println("Failed to create directory...");
        //        }
        //    }

        //File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "NewVideo.mp4");

        //CamcorderProfile cp = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
        //cp.fileFormat = MediaRecorder.OutputFormat.MPEG_4;

        //cp.videoFrameRate = 24;
        //mr.setProfile(cp);

        //mr.setOutputFile(mediaFile.toString());
        //ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(client);
        //mr.setOutputFile(pfd.getFileDescriptor());

       // camera.setPreviewDisplay(mHolder);

        //new Timer().schedule(new TimerTask() {
        //    @Override
        //    public void run() {
        //        try {
        //            mr.setPreviewDisplay(mHolder.getSurface());
        //        } catch (Exception e) {
        //            System.out.println("Error on set preview display:" + String.valueOf(e));
        //        }
        //        try {
        //            mr.prepare();
        //        } catch (Exception e) {
        //            System.out.println("Error on prepare: " + String.valueOf(e));
        //        }
        //        try {
        //            mr.start();
        //        } catch (Exception e) {
        //            System.out.println("Error on start: " + String.valueOf(e));
        //        }
        //    }
        //}, 500);


        //new Timer().schedule(new TimerTask() {
        //    @Override
        //    public void run()
        //    {
        //        mr.stop();
        //    }
        //}, 10000);

    }

    public void surfaceCreated(SurfaceHolder holder)
    {
        try
        {
            camera.setPreviewDisplay(mHolder);
            camera.startPreview();
        }
        catch(Exception e)
        {
            Log.e("Preview Error: ", e.toString());
        }

        camera.setPreviewCallback(new Camera.PreviewCallback() {

            @Override
            public void onPreviewFrame ( final byte[] data, Camera camera)
            {
                Log.e(TAG, "Preview Frame Found...");
                //http://stackoverflow.com/questions/20298699/onpreviewframe-data-image-to-imageview
                //^ Accessed: 08/03/2017 @ 14:00
                Camera.Parameters parameters = camera.getParameters();
                int width = parameters.getPreviewSize().width;
                int height = parameters.getPreviewSize().height;
                YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);
                byte[] byteArray = out.toByteArray();
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                imgView.setImageBitmap(Bitmap.createScaledBitmap(bmp, imgView.getWidth(), imgView.getHeight(), false));
            }
        });
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
            //camera.stopPreview();
            //camera.release();
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


