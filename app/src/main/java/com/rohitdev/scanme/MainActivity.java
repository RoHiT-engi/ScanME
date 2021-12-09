package com.rohitdev.scanme;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rohitdev.scanme.Adapters.PDFviewerAdapter;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private final int requestCode = 1001;
    private FloatingActionButton mFloatingActionButton;
    private RecyclerView recyclerView;
    private PDFviewerAdapter pdFviewerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DividerItemDecoration vItemDecoration = new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL);
        recyclerView =(RecyclerView) findViewById(R.id.pdf_list);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(vItemDecoration);
        pdFviewerAdapter = new PDFviewerAdapter(this);
        String app_folder_path = getApplicationContext().getExternalMediaDirs()[0] + "/pdf";
        File rootDir = new File(app_folder_path);
        File[] imagesFiles = rootDir.listFiles();
        pdFviewerAdapter.setImages(imagesFiles);
        recyclerView.setAdapter(pdFviewerAdapter);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkallpermission()) {
                    enableCamera();
                }else{
                    try {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                        Toast.makeText(MainActivity.this,"Please Go to Settings and Provide Necessary permissions to Proceed Further",Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        finish();
                    }
                }
            }
        });


    }

    public void enableCamera(){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    public boolean checkallpermission(){
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

}
