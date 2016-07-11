package com.softdesign.devintensive.ui.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.data.managers.DataManager;
import com.softdesign.devintensive.utils.CircleAvatarDrawable;
import com.softdesign.devintensive.utils.ConstantManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = ConstantManager.TAG_PREFIX + "Main Activity";

    private DataManager mDataManager;
    private boolean[] status = {true, false};
    private int mCurrentEditMode=0;

    private ImageView avatarImg;
    @BindView(R.id.main_coordinator_container)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.navigation_drawer)
    DrawerLayout mNavigationDrawer;
    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.profile_placeholder)
    RelativeLayout mProfilePlaceholder;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.appbar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.user_photo_img)
    ImageView mProfileImage;

    @BindView(R.id.img_call)
    ImageView mCallPhone;
    @BindView(R.id.img_mail)
    ImageView mSendMail;
    @BindView(R.id.img_vk)
    ImageView mViewVk;
    @BindView(R.id.img_github)
    ImageView mViewGithub;

    @BindViews({R.id.input_layout_phone, R.id.input_layout_mail, R.id.input_layout_vk, R.id.input_layout_github})
    List<TextInputLayout> mUserInputFieldViews;

    @BindViews({R.id.et_phone, R.id.et_email, R.id.et_vk, R.id.et_github, R.id.et_account})
    List<EditText> mUserFieldViews;

    @BindView (R.id.et_email)
    EditText mUserMail;

    private TextView mUserEmail;

    private AppBarLayout.LayoutParams mAppBarParams = null;

    private File mPhotoFile = null;
    private Uri mSelectedImage = null;

    private Integer[] mArrayErrors = {R.string.error_format_phone, R.string.error_format_email,
            R.string.error_format_path_vk, R.string.error_format_path_github};

    /**
     * @param savedInstanceState - объект со значениями сохраненными в Bundle - состояние UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ButterKnife.apply(mUserFieldViews, ADD_LISTENER);

        Log.d(TAG, "onCreate");

        mDataManager = DataManager.getInstance();
        View v = mNavigationView.getHeaderView(0);

        avatarImg = (ImageView) v.findViewById(R.id.iv_avatar);
        mUserEmail = (TextView) v.findViewById(R.id.user_email_txt);

        mFab.setOnClickListener(this);
        mProfilePlaceholder.setOnClickListener(this);

        mCallPhone.setOnClickListener(this);
        mSendMail.setOnClickListener(this);
        mViewVk.setOnClickListener(this);
        mViewGithub.setOnClickListener(this);

        setupToolbar();
        setupDrawer();
        loadUserInfoValue();
        Picasso.with(this)
                .load(mDataManager.getPreferencesManager().loadUserPhoto())
                .placeholder(R.drawable.userphoto) //// TODO: 07.07.2016 сделать плейсхолдер
                .into(mProfileImage);

        createCircleAvatar();


        if (savedInstanceState == null) {
            //приложение запущено впервые
        } else {
            //приложение запущено повторно
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
            case R.id.profile_placeholder:
                showDialog(ConstantManager.LOAD_PROFILE_PHOTO);
                break;
            case R.id.img_call:
                callPhone();
                break;
            case R.id.img_mail:
                sendMail();
                break;
            case R.id.img_vk:
                openVk();
                break;
            case R.id.img_github:
                openGithub();
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ConstantManager.EDIT_MODE_KEY, mCurrentEditMode);
    }

    /**
     * отображение Snackbar
     * @param message - текст сообщения
     * */
    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * установка Toolbar
     */
    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();


        mAppBarParams = (AppBarLayout.LayoutParams)mCollapsingToolbar.getLayoutParams();
        if (actionBar!=null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * настройка NavigationView
     */
    private void setupDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                showSnackbar(item.getTitle().toString());
                item.setChecked(true);

                if (item.getItemId() == R.id.exit_menu) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }

                mNavigationDrawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    /**
     * Получение результата из других активити
     * @param requestCode - код запроса
     * @param resultCode - код ответа
     * @param data - содержание
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case ConstantManager.REQUEST_GALLERY_PICTURE:
                if (resultCode==RESULT_OK&&data!=null) {
                    mSelectedImage = data.getData();
                    insertProfileImage(mSelectedImage);
                }
                break;
            case ConstantManager.REQUEST_CAMERA_PICTURE:
                if (resultCode==RESULT_OK&&mPhotoFile!=null) {
                    mSelectedImage = Uri.fromFile(mPhotoFile);

                    insertProfileImage(mSelectedImage);
                }
        }

    }

    /**
     * перехват нажатия кнопки "назад"
     */
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
        ButterKnife.apply(mUserFieldViews, CHANGE_EDIT_ALL);
        mFab.setImageResource(R.drawable.ic_done_24dp);
        if (mode==1) {

            for (TextInputLayout textInputLayout : mUserInputFieldViews) {
                if(textInputLayout.getId()==R.id.input_layout_phone) {
                    textInputLayout.requestFocus();
                }
            }

            showProfilePlaceholder();
            lockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
        } else {

            mFab.setImageResource(R.drawable.ic_create_24dp);
            boolean save = true;

            for (TextInputLayout textInputLayout : mUserInputFieldViews) {
                if (textInputLayout.isErrorEnabled()) {
                    save = false;
                    loadUserInfoValue();
                    showSnackbar(getString(R.string.error_input_user_info));
                    break;
                }
            }
            if (save) {
                saveUserInfoValue();
                setUserEmail();
            }

            hideProfilePlaceholder();
            unlockToolbar();
            mCollapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));
            ButterKnife.apply(mUserInputFieldViews, CHANGE_ERROR_ALL);
        }

    }

    /**
     * переписывает email профиля
     */
    private void setUserEmail() {
        mUserEmail.setText(mUserMail.getText().toString());
    }

    /**
     * загрузка данных пользователя из памяти
     */
    private void loadUserInfoValue() {
        List<String> userData = mDataManager.getPreferencesManager().loadUserProfileData();

        for (int i = 0; i < userData.size(); i++) {
            mUserFieldViews.get(i).setText(userData.get(i));
        }
    }

    /**
     * сохранение данных пользователя в память
     */
    private void saveUserInfoValue() {
        List<String> userData = new ArrayList<>();
        for (EditText userFieldsView: mUserFieldViews) {
            userData.add(userFieldsView.getText().toString());
        }

        mDataManager.getPreferencesManager().saveUserProfileData(userData);
    }

    /**
     * создание круглой картинки аватара
     */
    private void createCircleAvatar() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = false;
        Bitmap avatar = BitmapFactory.decodeResource(getResources(), R.drawable.avatar, options);
        CircleAvatarDrawable circleAvatarDrawable = new CircleAvatarDrawable(avatar);
        avatarImg.setImageDrawable(circleAvatarDrawable);
    }

    /**
     * запрос фото из галлереи
     */
    private void loadPhotoFromGallery() {

        Intent takeGalleryIntent = new Intent(Intent.ACTION_PICK
                , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        takeGalleryIntent.setType("image/*");

        startActivityForResult(Intent.createChooser(takeGalleryIntent
                , getString(R.string.user_profile_choise_message))
                , ConstantManager.REQUEST_GALLERY_PICTURE);
    }

    /**
     * запрос фото из камеры
     */
    private void loadPhotoFromCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==PackageManager.PERMISSION_GRANTED) {

            Intent takeCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                mPhotoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (mPhotoFile !=null) {
                //TODO: передать фото в интент
                takeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                startActivityForResult(takeCaptureIntent, ConstantManager.REQUEST_CAMERA_PICTURE);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, ConstantManager.CAMERA_REQUEST_PERMISSION_CODE);

            Snackbar.make(mCoordinatorLayout, R.string.label_info_permission, Snackbar.LENGTH_LONG)
                    .setAction(R.string.label_title_permission, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openApplicationSettings();
                        }
                    }).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == ConstantManager.CAMERA_REQUEST_PERMISSION_CODE && grantResults.length==3) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //// TODO: 07.07.2016 Обработать разрешение (разрешение получено)
            }
            if (grantResults[1]==PackageManager.PERMISSION_GRANTED) {
                //// TODO: 07.07.2016 Обработать разрешение (разрешение получено)
            }
            if (grantResults[2]==PackageManager.PERMISSION_GRANTED) {
                //// TODO: 07.07.2016 Обработать разрешение (разрешение получено)
            }
        }
    }

    /**
     * скрытие placeholder при выхде из режима редактирвания
     */
    private void hideProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.GONE);
    }

    /**
     * отбражение placeholder в режиме редактирвания
     */
    private void showProfilePlaceholder() {
        mProfilePlaceholder.setVisibility(View.VISIBLE);
    }

    /**
     * блокировка mCollapsingToolbar
     */
    private void lockToolbar() {
        mAppBarLayout.setExpanded(true, true);
        mAppBarParams.setScrollFlags(0);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    /**
     * Снятие блокировки mCollapsingToolbar
     */
    private void unlockToolbar() {
        mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        mCollapsingToolbar.setLayoutParams(mAppBarParams);
    }

    /**
     * создание диалга
     * @param id
     * @return - диалог
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ConstantManager.LOAD_PROFILE_PHOTO:
                String[] selectItems = {getString(R.string.user_profile_dialog_gallery),
                        getString(R.string.user_profile_dialog_camera),
                        getString(R.string.user_profile_dialog_cancel)};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.user_profile_dialog_title));
                builder.setItems(selectItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choiceItem) {
                        switch (choiceItem) {
                            case 0:
                                loadPhotoFromGallery();
                                break;
                            case 1:
                                loadPhotoFromCamera();
                                break;
                            case 2:
                                dialog.cancel();
                                break;
                        }
                    }
                });
                return builder.create();

            default:
                return null;
        }
    }

    /**
     * создание файла изображения
     * @return - имя файла изображения
     * @throws IOException - ошибка ввода/вывода
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, image.getAbsolutePath());

        this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        return image;
    }

    /**
     * добавление изображения пользвателя
     * @param selectedImage путь изображения
     */
    private void insertProfileImage(Uri selectedImage) {
        Picasso.with(this)
                .load(selectedImage)
                .into(mProfileImage);
        //// TODO: 07.07.2016 сделать плейсхолдер
        mDataManager.getPreferencesManager().saveUserPhoto(selectedImage);
    }

    /**
     * открытие настроек приложения
     */
    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                ,Uri.parse("package:" + getPackageName()));

        startActivityForResult(appSettingsIntent, ConstantManager.PERMISSION_REQUEST_SETTINGS_CODE);
    }

    /**
     * отображение ошибки
     * @param result результат проверки
     * @param index номер элемента
     */
    private void showError(boolean result, int index) {
        if (!result) {
            mUserInputFieldViews.get(index).setError(getString(mArrayErrors[index]));
        }
        else {
            mUserInputFieldViews.get(index).setErrorEnabled(false);
        }
    }
    /**
     * вызов номера телефона
     */
    private void callPhone() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +
                    mDataManager.getPreferencesManager().loadUserProfileData().get(0)));
            startActivity(callIntent);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CALL_PHONE}, ConstantManager.CALL_PHONE_REQUEST_PERMISSIONS_CODE);
                Snackbar.make(mCollapsingToolbar, R.string.label_info_permission, Snackbar.LENGTH_LONG)
                        .setAction(R.string.label_title_permission, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openApplicationSettings();
                            }
                        }).show();
        }
    }
    /**
     * отправка письма
     */
    private void sendMail() {
        Intent mailIntent = new Intent(Intent.ACTION_SEND);
        mailIntent.setType("text/plain");
        mailIntent.putExtra(Intent.EXTRA_EMAIL, mDataManager.getPreferencesManager().loadUserProfileData().get(1));
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        mailIntent.putExtra(Intent.EXTRA_TEXT, "Text");
        startActivity(Intent.createChooser(mailIntent, "Send Email"));
    }

    /**
     * открвтие профиля VK
     */
    private void openVk() {
        Uri addressVk = Uri.parse("https://" + mDataManager.getPreferencesManager().loadUserProfileData().get(2));
        Intent vkIntent = new Intent(Intent.ACTION_VIEW, addressVk);
        startActivity(vkIntent);
    }
    /**
     * открвтие профиля Github
     */
    private void openGithub() {
        Uri addressGithub = Uri.parse("https://" + mDataManager.getPreferencesManager().loadUserProfileData().get(3));
        Intent githubIntent = new Intent(Intent.ACTION_VIEW, addressGithub);
        startActivity(githubIntent);
    }

    /**
     * смена сстояния EditText
     */
    final ButterKnife.Action<EditText> CHANGE_EDIT_ALL = new ButterKnife.Action<EditText>() {
        @Override
        public void apply(@NonNull EditText view, int index) {
            view.setEnabled(status[mCurrentEditMode]);
            view.setFocusable(status[mCurrentEditMode]);
            view.setFocusableInTouchMode(status[mCurrentEditMode]);
        }
    };

    /**
     * отключение флагов ошибок у TextInputLayout
     */
    final ButterKnife.Action<TextInputLayout> CHANGE_ERROR_ALL = new ButterKnife.Action<TextInputLayout>() {
        @Override
        public void apply(@NonNull TextInputLayout view, int index) {
            view.setErrorEnabled(false);
        }
    };

    /**
     * установка обработчика на EditText
     */
    final ButterKnife.Action<EditText> ADD_LISTENER = new ButterKnife.Action<EditText>() {
        @Override
        public void apply(@NonNull EditText view, int index) {
            view.addTextChangedListener(new ValidTextWatcher(view, index));
        }
    };

    /**
     * обработчик событий редактирования текста EditText
     */
    private class ValidTextWatcher implements TextWatcher {

        private int index;
        private EditText view;

        public ValidTextWatcher(EditText view, int index) {
            this.index = index;
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (view.getId()) {
                case R.id.et_phone:
                    showError(validatePhone(mUserFieldViews.get(index).getText().toString()), index);
                    break;
                case R.id.et_email:
                    showError(validateEmail(mUserFieldViews.get(index).getText().toString()), index);
                    break;
                case R.id.et_vk:
                    showError(validatePathVk(mUserFieldViews.get(index).getText().toString()), index);
                    break;
                case R.id.et_github:
                    showError(validatePathGit(mUserFieldViews.get(index).getText().toString()), index);
                    break;
            }
        }

        /**
         * проверка страницы git
         * @param s - страница git
         * @return true - корректная
         */
        private boolean validatePathGit(String s) {
            String patternGit = "^github.com/[1-9A-Za-z/]{3,}$";
            return Pattern.compile(patternGit).matcher(s).find();
        }
        /**
         * проверка страницы vk
         * @param s - страница vk
         * @return true - корректная
         */
        private boolean validatePathVk(String s) {
            String patternVk = "^vk.com/[1-9A-Za-z]{3,}$";
            return Pattern.compile(patternVk).matcher(s).find();
        }

        /**
         * проверка почты
         * @param s - почта
         * @return true - корректная
         */
        private boolean validateEmail(String s) {
            String patternEmail = "^[1-9A-Za-z]{3,}@[a-z]{2,}\\.[a-z]{2,}$";
            return Pattern.compile(patternEmail).matcher(s).find();
        }
        /**
        * проверка телефона
        * @param s - телефон
        * @return true - корректный
        */
        private boolean validatePhone(String s) {
            String patternPhone = "(^[+]?[1-9][ ]?[0-9]{3}[ ]?[0-9]{3}[-]?[0-9]{2}[-]?[0-9]{2}$)|(^[1-9][0-9]{10,19}$)";
            return Pattern.compile(patternPhone).matcher(s).find();
        }
    }
}