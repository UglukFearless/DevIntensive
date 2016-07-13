package com.softdesign.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.data.network.req.UserLoginReq;
import com.softdesign.devintensive.data.network.res.UserModelRes;
import com.softdesign.devintensive.utils.NetworkStatusChecker;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ugluk on 08.07.2016.
 */
public class AuthActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_login)
    Button mSignIn;
    @BindView(R.id.remember_txt)
    TextView mRememberPassword;
    @BindView(R.id.et_login_email)
    EditText mLogin;
    @BindView(R.id.et_login_password)
    EditText mPassword;
    @BindView(R.id.main_coordinator_container)
    CoordinatorLayout mCoordinatorLayout;

    private DataManager mDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mDataManager = DataManager.getInstance();

        ButterKnife.bind(this);

        mRememberPassword.setOnClickListener(this);
        mSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.remember_txt:
                rememberPassword();
                break;
            case R.id.btn_login:
                signIn();
                break;
        }
    }

    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void rememberPassword() {
        Intent rememberIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://devintensive.softdesign-apps.ru/forgotpass"));
        startActivity(rememberIntent);
    }

    private void loginSuccess(UserModelRes userModel) {

        mDataManager.getPreferencesManager().saveAuthToken(userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveUserId(userModel.getData().getUser().getId());
        saveUserValues(userModel);
        saveFieldValues(userModel);
        saveMainValues(userModel);
        savePhoto(userModel);
        Intent loginIntent = new Intent(this, MainActivity.class);
        startActivity(loginIntent);
    }

    private void signIn() {
        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            Call<UserModelRes> call = mDataManager.loginUser(new UserLoginReq(mLogin.getText().toString(), mPassword.getText().toString()));
            call.enqueue(new Callback<UserModelRes>() {
                @Override
                public void onResponse(Call<UserModelRes> call, Response<UserModelRes> response) {
                    if (response.code()==200) {
                        loginSuccess(response.body());
                    } else if (response.code()==404) {
                        showSnackbar(getString(R.string.error_auth_login));
                    } else {
                        showSnackbar(getString(R.string.error_auth_other));
                    }
                }

                @Override
                public void onFailure(Call<UserModelRes> call, Throwable t) {
                    //// TODO: 13.07.2016 Обработать ошибки ретрофита
                }
            });
        } else {
            showSnackbar(getString(R.string.error_auth_internet));
        }
    }

    private void saveUserValues(UserModelRes userModel) {

        int [] userValues = {
                userModel.getData().getUser().getProfileValues().getRating(),
                userModel.getData().getUser().getProfileValues().getLinesCode(),
                userModel.getData().getUser().getProfileValues().getProjects()
        };

        mDataManager.getPreferencesManager().saveUserProfileValues(userValues);
    }

    private void saveFieldValues(UserModelRes userModel){
        String[] fieldValues = {
                userModel.getData().getUser().getContacts().getPhone(),
                userModel.getData().getUser().getContacts().getEmail(),
                userModel.getData().getUser().getRepositories().getRepo().get(0).getGit(),
                userModel.getData().getUser().getContacts().getVk(),
                userModel.getData().getUser().getPublicInfo().getBio()

        };
        mDataManager.getPreferencesManager().saveFieldValue(fieldValues);
    }

    private void saveMainValues(UserModelRes userModel){
        String[] mainValues = {
                userModel.getData().getUser().getFirstName(),
                userModel.getData().getUser().getSecondName(),
                userModel.getData().getUser().getPublicInfo().getAvatar()
        };
        mDataManager.getPreferencesManager().saveMainValue(mainValues);
    }
    private void savePhoto(UserModelRes userModel){
        mDataManager.getPreferencesManager().saveUserPhoto(Uri.parse(userModel.getData().getUser().getPublicInfo().getPhoto()));
    }
}
