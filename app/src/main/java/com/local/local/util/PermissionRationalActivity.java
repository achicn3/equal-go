/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.local.local.util;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.local.local.R;
import com.local.local.screen.FirstActivity;

/**
 * Displays rationale for allowing the activity recognition permission and allows user to accept
 * the permission. After permission is accepted, finishes the activity so main activity can
 * show transitions.
 */
public class PermissionRationalActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "PermissionRational";

    /* Id to identify Activity Recognition permission request. */
    private static final int PERMISSION_REQUEST_LOCATION = 45;
    private static final int PERMISSION_REQUEST_READ_WRITE = 5566;
    private static final int PERMISSION_REQUEST_CAMERA = 5577;
    private static final int PERMISSION_REQUEST_AUDIO_RECORD = 4488;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If permissions granted, we start the main activity (shut this activity down).
        if (PermissionUtil.hasGrantedReadWriteExternalStorage(this) &&
                PermissionUtil.hasGrantedActivity(this) &&
                PermissionUtil.hasGrantedCamera(this) &&
                PermissionUtil.hasGrantedRecordAudio(this)
        ) {
            finish();
        }

        setContentView(R.layout.activity_permission_rational);

        findViewById(R.id.btn_permission_ok).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                    onClickApprovePermissionRequest(v);
            }
        });

        findViewById(R.id.btn_permission_exit).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onClickDenyPermissionRequest(v);
            }
        });
    }
    public void onClickApprovePermissionRequest(View view) {
        if(!PermissionUtil.hasGrantedReadWriteExternalStorage(this))
            PermissionUtil.requestReadWriteExternalStorage(this,PERMISSION_REQUEST_READ_WRITE);

        if(!PermissionUtil.hasGrantedCamera(this))
            PermissionUtil.requestCameraPermission(this,PERMISSION_REQUEST_CAMERA);

        if(!PermissionUtil.hasGrantedActivity(this))
            PermissionUtil.requestLocation(this, PERMISSION_REQUEST_LOCATION);
    }

    public void onClickDenyPermissionRequest(View view) {
        Log.d(TAG, "onClickDenyPermissionRequest()");
        System.exit(0);
    }

    /*
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            boolean camera = PermissionUtil.hasGrantedCamera(this);
            boolean location = PermissionUtil.hasGrantedActivity(this);
            boolean readWrite = PermissionUtil.hasGrantedReadWriteExternalStorage(this);
            boolean recordVideo = PermissionUtil.hasGrantedRecordAudio(this);
            switch (requestCode){
                case PERMISSION_REQUEST_CAMERA:
                    if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        camera = true;
                    }
                    break;
                case PERMISSION_REQUEST_LOCATION:
                    if(grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                        location = true;
                    }
                    break;
                case PERMISSION_REQUEST_READ_WRITE:
                    if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        readWrite = true;
                    }
            }
        if (camera && location && readWrite) {
                startActivity(new Intent(this, FirstActivity.class));
                finish();
            }
    }
}
