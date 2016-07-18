package com.softdesign.devintensive.data.managers;

import android.content.Context;

import com.softdesign.devintensive.data.network.RestService;
import com.softdesign.devintensive.data.network.ServiceGenerator;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.utils.DevintensiveApplication;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by Ugluk on 27.06.2016.
 */
public class DataManager {

    private Context mContext;
    private static DataManager INSTANCE = null;
    private PreferencesManager mPreferencesManager;
    private RestService mRestService;

    public DataManager() {
        this.mContext = DevintensiveApplication.getContext();
        this.mPreferencesManager = new PreferencesManager();
        this.mRestService = ServiceGenerator.createService(RestService.class);
    }

    public static DataManager getInstance() {
        if (INSTANCE==null) {
            INSTANCE = new DataManager();
        }

        return INSTANCE;
    }

    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }

    public Context getContext() {
        return mContext;
    }

    //region =============== Network ================

    public Call<UserModelRes> loginUser(UserLoginReq userLoginReq) {
        return mRestService.loginUser(userLoginReq);
    }

    public Call<ResponseBody> savePhoto(RequestBody body, MultipartBody.Part file){
        return mRestService.savePhoto(body, file);
    }

    public Call<UserListRes> getUserList() {
        return mRestService.getUserList();
    }
    //endregion

    //region =============== Database ================

    //endregion
}
