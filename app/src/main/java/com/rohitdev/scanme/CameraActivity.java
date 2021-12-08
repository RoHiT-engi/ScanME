package com.rohitdev.scanme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.TorchState;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity {
    private static final long ANIMATION_FAST_MILLIS = 50L;
    private static final long ANIMATION_SLOW_MILLIS = 100L;

    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageButton imageButtonCapture;
    private CameraSelector cameraSelector;
    private ImageCapture imageCapture ;
    private ExecutorService cameraExecutor;
    private ImageButton imageButtonGallery;
    private ImageButton imageButtonReverseCam;
    private ImageButton mTorchStateHandle;
    private ImageButton mBackButton;
    private Camera camera;
    private Animation animation;
    private int lensFacing = CameraSelector.LENS_FACING_BACK;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_ui_container);

        if(getSupportActionBar()!=null)
        {this.getSupportActionBar().hide();}
        previewView = findViewById(R.id.view_finder);
        imageButtonCapture = findViewById(R.id.camera_capture_button);
        imageButtonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCapture();
            }
        });
        mTorchStateHandle = findViewById(R.id.camera_Flash_button);
        mTorchStateHandle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.getCameraControl().enableTorch(camera.getCameraInfo().getTorchState().getValue() == TorchState.OFF);
            }
        });
        imageButtonGallery = findViewById(R.id.photo_view_button);
        imageButtonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CameraActivity.this,GallaryPreview.class);
                startActivity(intent);
            }
        });
        imageButtonReverseCam = findViewById(R.id.camera_switch_button);
        imageButtonReverseCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageButtonReverseCam.setEnabled(false);
                if(CameraSelector.LENS_FACING_FRONT == lensFacing){
                    lensFacing = CameraSelector.LENS_FACING_BACK;
                }else{
                    lensFacing = CameraSelector.LENS_FACING_FRONT;
                }
                try {
                    bindPreview(cameraProviderFuture.get());
                    imageButtonReverseCam.setEnabled(true);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        mBackButton= (ImageButton) findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CameraActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        cameraExecutor = Executors.newSingleThreadExecutor();
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }



    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();
        Preview preview = new Preview.Builder()
                .build();

        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        imageCapture =  new ImageCapture.Builder()
                .setTargetRotation(previewView.getDisplay().getRotation())
                .build();
        camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector,imageCapture, preview);
    }


    public void onCapture(){
        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        File photofile = new File(getBatchDirectoryName(), mDateFormat.format(new Date())+ ".jpg");
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(photofile)
                        .build();
        imageCapture.takePicture(outputFileOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Uri savedUri = outputFileResults.getSavedUri();
                Log.d("in omImageSaved", "Photo capture succeeded: " + savedUri);
                previewView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        previewView.setForeground(new ColorDrawable(Color.WHITE));
                        previewView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                previewView.setForeground(null);
                            }
                        },ANIMATION_FAST_MILLIS);
                    }
                },ANIMATION_SLOW_MILLIS);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e("Capture Error : \n ","Cause :"+ exception.getCause().toString() + " Message : " + exception.getMessage() );
            }
        });
    }



    public String getBatchDirectoryName() {
        String app_folder_path = "";
        app_folder_path = getApplicationContext().getExternalMediaDirs()[0] + "/images";
        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {
            Toast.makeText(this, "Trip", Toast.LENGTH_SHORT).show();
        }
        return app_folder_path;
    }


}
