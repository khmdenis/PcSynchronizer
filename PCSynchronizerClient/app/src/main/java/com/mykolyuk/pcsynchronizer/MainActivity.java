package com.mykolyuk.pcsynchronizer;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;

import android.widget.Button;
import android.widget.Toast;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "STATUS";
    private static final File mainDirectory = new File(Environment.getExternalStorageDirectory() + "/PCSynchronized");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button synchroButton = (Button) findViewById(R.id.synchro_btn);
        synchroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           new SynchronizeTask(MainActivity.this, mainDirectory).execute();
            }
        });
    }
}