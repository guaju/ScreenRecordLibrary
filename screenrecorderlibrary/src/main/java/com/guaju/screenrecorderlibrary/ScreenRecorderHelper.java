package com.guaju.screenrecorderlibrary;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

/**
 * Created by guaju on 2017/12/28.
 */

public class ScreenRecorderHelper {


    private static final int RECORD_REQUEST_CODE = 101;
    private static final int STORAGE_REQUEST_CODE = 102;
    private static final int AUDIO_REQUEST_CODE = 103;


    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private RecordService recordService;




    private static volatile ScreenRecorderHelper mRecorder;
    Context mcontext;

    private ScreenRecorderHelper(Context context) {
        mcontext = context;
    }

    //stop record
    public void stopRecord(OnRecordStatusChangeListener listener) {
        if (recordService.stopRecord()) {
            //stop record success
            listener.onChangeSuccess();
        } else {
            //stop record failed
            listener.onChangeFailed();
        }
    }


    //application context is better
    public static ScreenRecorderHelper getInstance(Context context) {
        if (mRecorder == null)
            synchronized (ScreenRecorderHelper.class) {
                if (mRecorder == null) {
                    mRecorder = new ScreenRecorderHelper(context);
                }
            }
        context.startService(new Intent(context, RecordService.class));
        return mRecorder;
    }


    //default record setting
    public void startRecord(final Activity activity) {


        MediaProjectionManager projectionManager = (MediaProjectionManager) mcontext.getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        if (recordService.isRunning()) {
            recordService.stopRecord();
        } else {
            Intent captureIntent = projectionManager.createScreenCaptureIntent();
            activity.startActivityForResult(captureIntent, RECORD_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_REQUEST_CODE);
        }
    }

    //custom record setting
    public void startCustomRecord(final Activity activity, final int width, final int height,String dirpath,String fileName ) {
        MediaProjectionManager projectionManager = (MediaProjectionManager) mcontext.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        recordService.setRecorderConfig(width, height,dirpath,fileName);
        if (recordService.isRunning()) {
            recordService.stopRecord();
        } else {
            Intent captureIntent = projectionManager.createScreenCaptureIntent();
            activity.startActivityForResult(captureIntent, RECORD_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_REQUEST_CODE);
        }

    }

    public interface OnRecordStatusChangeListener {
        //operate success
        void onChangeSuccess();

        //operate error
        void onChangeFailed();
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data,OnRecordStatusChangeListener listener) {
        if (requestCode == RECORD_REQUEST_CODE && resultCode == activity.RESULT_OK) {
            mediaProjection = projectionManager.getMediaProjection(resultCode, data);
            recordService.setMediaProject(mediaProjection);
            boolean b = recordService.startRecord();
            if (b){
                listener.onChangeSuccess();
            }else{
                listener.onChangeFailed();
            }
        }
    }

    public void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == STORAGE_REQUEST_CODE || requestCode == AUDIO_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                activity.finish();
            }
        }
    }

    public void initRecordService(final Activity activity) {
        projectionManager = (MediaProjectionManager) activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                DisplayMetrics metrics = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                RecordService.RecordBinder binder = (RecordService.RecordBinder) service;
                recordService = binder.getRecordService();
                recordService.setConfig(metrics.widthPixels, metrics.heightPixels, metrics.densityDpi);
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
            }
        };

        Intent intent = new Intent(activity, RecordService.class);
        activity.bindService(intent, connection, Activity.BIND_AUTO_CREATE);
    }


    public String  getRecordFilePath(){
        return recordService.getRecordFilePath();
    }
}
