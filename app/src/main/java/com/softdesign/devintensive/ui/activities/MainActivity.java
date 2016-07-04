package com.softdesign.devintensive.ui.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.CircleAvatarDrawable;
import com.softdesign.devintensive.utils.ConstantManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = ConstantManager.TAG_PREFIX + "Main Activity";

    private DataManager mDataManager;
    private boolean[] status = {false, true};
    private int mCurrentEditMode=0;

    private ImageView mCallImg;
    private ImageView avatarImg;
    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private DrawerLayout mNavigationDrawer;
    private NavigationView mNavigationView;
    private FloatingActionButton mFab;
    private EditText mUserPhone, mUserMail, mUserVk, mUserGit, mUserBio;
    private TextView mUserEmail;

    private List<EditText> mUserInfoViews;


    /**
     * @param savedInstanceState - объект со значениями сохраненными в Bundle - состояние UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        mDataManager = DataManager.getInstance();
        mCallImg = (ImageView) findViewById(R.id.img_call);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_container);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNavigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        View v = mNavigationView.getHeaderView(0);
        avatarImg = (ImageView) v.findViewById(R.id.iv_avatar);
        mUserEmail = (TextView) v.findViewById(R.id.user_email_txt);

        mUserPhone = (EditText) findViewById(R.id.et_phone);
        mUserMail = (EditText) findViewById(R.id.et_email);
        mUserVk = (EditText) findViewById(R.id.et_vk);
        mUserGit = (EditText) findViewById(R.id.et_github);
        mUserBio = (EditText) findViewById(R.id.et_account);

        mUserInfoViews = new ArrayList<EditText>();
        mUserInfoViews.add(mUserPhone);
        mUserInfoViews.add(mUserMail);
        mUserInfoViews.add(mUserVk);
        mUserInfoViews.add(mUserGit);
        mUserInfoViews.add(mUserBio);

        mFab.setOnClickListener(this);
        setupToolBar();
        setupDrawer();
        loadUserInfoValue();

        createCircleAvatar();


        if (savedInstanceState == null) {
            //активити запускается впервые
        } else {
            //активити запускается повторно
            mCurrentEditMode = savedInstanceState.getInt(ConstantManager.EDIT_MODE_KEY, 0);
            changeEditMode(mCurrentEditMode);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mNavigationDrawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "onPause");

        saveUserInfoValue();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d(TAG, "onRestart");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fab:
                if (mCurrentEditMode==0) {
                    changeEditMode(1);
                    mCurrentEditMode=1;
                } else {
                    changeEditMode(0);
                    mCurrentEditMode=0;
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void setupToolBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar!=null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                showSnackbar(item.getTitle().toString());
                item.setChecked(true);
                mNavigationDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawer.isDrawerOpen(GravityCompat.START))
            mNavigationDrawer.closeDrawer(GravityCompat.START);
        else {
            onBackPressed();
        }
    }

    /**
     * переключает режим редактирования
     * @param mode - если 1 режим редактирования, если 0 режим просмотра
     */
    private void changeEditMode(int mode) {
        mFab.setImageResource(R.drawable.ic_done_24dp);
        if (mode==1) {
            for(EditText userValue : mUserInfoViews) {
                userValue.setEnabled(true);
                userValue.setFocusable(true);
                userValue.setFocusableInTouchMode(true);
            }
        } else {
            mFab.setImageResource(R.drawable.ic_create_24dp);
            for(EditText userValue : mUserInfoViews) {
                userValue.setEnabled(false);
                userValue.setFocusable(false);
                userValue.setFocusableInTouchMode(false);
            }
            saveUserInfoValue();
            setUserEmail();
        }

    }

    private void setUserEmail() {
        mUserEmail.setText(mUserMail.getText().toString());
    }

    private void loadUserInfoValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();

        for (int i = 0; i < userData.size(); i++) {
            mUserInfoViews.get(i).setText(userData.get(i));
        }
    }

    private void saveUserInfoValue() {
        List<String> userData = new ArrayList<>();
        for (EditText userFieldsView: mUserInfoViews) {
            userData.add(userFieldsView.getText().toString());
        }

        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    private void createCircleAvatar() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = false;
        Bitmap avatar = BitmapFactory.decodeResource(getResources(), R.drawable.avatar, options);
        CircleAvatarDrawable circleAvatarDrawable = new CircleAvatarDrawable(avatar);
        avatarImg.setImageDrawable(circleAvatarDrawable);
    }
}
