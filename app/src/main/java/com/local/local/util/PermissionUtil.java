package com.local.local.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtil {
    public static boolean hasGranted(Context context, String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        int check = ContextCompat.checkSelfPermission(context, permission);
        return check == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasGrantedReadWriteExternalStorage(Context context) {
        boolean read = hasGranted(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        boolean write = hasGranted(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return read & write;
    }

    public static boolean hasGrantedCamera(Context context) {
        return hasGranted(context, Manifest.permission.CAMERA);
    }

    public static boolean hasGrantedReadContacts(Context context) {
        return hasGranted(context, Manifest.permission.READ_CONTACTS);
    }

    public static boolean hasGrantedReadPhoneState(Context context) {
        return hasGranted(context, Manifest.permission.READ_PHONE_STATE);
    }

    public static boolean shouldShowRationale(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    public static boolean shouldShowCameraRationale(Activity activity) {
        return shouldShowRationale(activity, Manifest.permission.CAMERA);
    }

    public static boolean shouldShowReadContactsRationale(Activity activity) {
        return shouldShowRationale(activity, Manifest.permission.READ_CONTACTS);
    }

    public static boolean shouldShowReadPhoneStateRationale(Activity activity) {
        return shouldShowRationale(activity, Manifest.permission.READ_PHONE_STATE);
    }

    public static boolean shouldShowReadWriteExternalStorageRationale(Activity activity) {
        boolean read = shouldShowRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        boolean write = shouldShowRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return read & write;
    }

    public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    public static void requestReadWriteExternalStorage(Activity activity, int requestCode) {
        requestPermissions(activity, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
    }

    public static void requestCameraPermission(Activity activity, int requestCode) {
        requestPermissions(activity, new String[] {Manifest.permission.CAMERA}, requestCode);
    }

    public static void requestReadContactsPermission(Activity activity, int requestCode) {
        requestPermissions(activity, new String[] {Manifest.permission.READ_CONTACTS}, requestCode);
    }

    public static void requestReadPhoneStatePermission(Activity activity, int requestCode) {
        requestPermissions(activity, new String[] {Manifest.permission.READ_PHONE_STATE}, requestCode);
    }
}
