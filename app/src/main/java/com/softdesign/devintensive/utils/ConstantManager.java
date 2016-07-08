package com.softdesign.devintensive.utils;

import android.content.IntentSender;

/**
 * Created by Ugluk on 24.06.2016.
 */
public interface ConstantManager {
    String TAG_PREFIX = "DEV ";
    String EDIT_MODE_KEY = "EDIT_MODE_KEY";

    String USER_PHONE_KEY = "USER_KEY_PHONE";
    String USER_MAIL_KEY = "USER_KEY_MAIL";
    String USER_VK_KEY = "USER_KEY_VK";
    String USER_GIT_KEY = "USER_KEY_GIT";
    String USER_BIO_KEY = "USER_KEY_BIO";
    String USER_PHOTO_KEY = "USER_PHOTO_KEY";


    int LOAD_PROFILE_PHOTO = 1;
    int REQUEST_CAMERA_PICTURE = 99;
    int REQUEST_GALLERY_PICTURE = 88;

    int PERMISSION_REQUEST_SETTINGS_CODE = 101;
    int CAMERA_REQUEST_PERMISSION_CODE = 102;
    int CALL_PHONE_REQUEST_PERMISSIONS_CODE = 103;
}