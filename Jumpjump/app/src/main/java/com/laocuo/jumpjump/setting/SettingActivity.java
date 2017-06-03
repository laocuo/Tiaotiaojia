/*
* Copyright (C) 2013-2016 laocuo@163.com .
* Modification based on code covered by the mentioned copyright
* and/or permission notice(s).
*/
/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.laocuo.jumpjump.setting;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;

import com.laocuo.jumpjump.R;
import com.laocuo.jumpjump.help.HelpActivity;
import com.laocuo.jumpjump.login.JumpUser;
import com.laocuo.jumpjump.login.LoginActivity;
import com.laocuo.jumpjump.utils.FactoryInterface;
import com.laocuo.jumpjump.utils.L;
import com.laocuo.jumpjump.utils.SnackbarUtil;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class SettingActivity extends AppCompatActivity {
    public static final String USERINFO = "pref_key_user";
    public static final String AVATAR = "pref_key_user_avatar";
    public static final String ALIAS = "pref_key_user_alias";
    public static final String COMSWITCH = "pref_key_vs_com";
    public static final String COMDIFFCULTY = "pref_key_com_difficulty";
    public static final String AUDIOSWITCH = "pref_key_audio_open";
    public static final String LOGINSTATUS = "pref_key_login_status";
    public static final String ABOUT = "pref_key_about";

//    public static final String AVATAR_URL = "avatar_url";

    private ActionBar mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setTitle(R.string.setting);
        if (savedInstanceState == null) {
            SettingFragment settingFragment = new SettingFragment();
            getFragmentManager().beginTransaction().add(android.R.id.content, settingFragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void updateActionBarIndicator() {
//        if (JumpUser.getCurrentUser() != null) {
//            Drawable d = FactoryInterface.getAvatar(this);
//            if (d != null) {
//                mActionBar.setHomeAsUpIndicator(d);
//                return;
//            }
//        }
//        mActionBar.setHomeAsUpIndicator(R.mipmap.ic_launcher);
//    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    public static class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        private static final int REQUEST_LOGIN = 1;
        private static final int SELECT_PORTRAIT = 2;
        private static final int CROP_PORTRAIT = 3;

        private PreferenceCategory mUserInfo;
        private SharedPreferences sp;
        private CheckBoxPreference mComSwitch;
        private ListPreference mComDiffcult;
        private CheckBoxPreference mAudioSwitch;
        private SwitchPreference mLoginSwitch;
        private AvatarPreference mAvatar;
        private EditTextPreference mAlias;
        private Context mContext;
        private String mAvatarUrl;
        private boolean isNetworkBattle;
        private ProgressDialog mWaittingDialog;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_preferences);
            mContext = getActivity();
            isNetworkBattle = getActivity().getIntent().getBooleanExtra("NETWORK_BATTLE", false);
            mUserInfo = (PreferenceCategory) findPreference(USERINFO);
            mAvatar = (AvatarPreference) findPreference(AVATAR);
            mAlias = (EditTextPreference) findPreference(ALIAS);
            mAlias.setOnPreferenceChangeListener(this);
            mComSwitch = (CheckBoxPreference) findPreference(COMSWITCH);
            mComSwitch.setOnPreferenceChangeListener(this);
            mComDiffcult = (ListPreference) findPreference(COMDIFFCULTY);
            mComDiffcult.setOnPreferenceChangeListener(this);
            mAudioSwitch = (CheckBoxPreference) findPreference(AUDIOSWITCH);
            mAudioSwitch.setOnPreferenceChangeListener(this);
            mLoginSwitch = (SwitchPreference) findPreference(LOGINSTATUS);
            mLoginSwitch.setOnPreferenceChangeListener(this);
            init();
        }

        private void selectPortrait() {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_PORTRAIT);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            final String key = preference.getKey();
            if (AVATAR.equals(key)) {
                selectPortrait();
                return true;
            } else if (ABOUT.equals(key)) {
                showAbout();
                return true;
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        private void showAbout() {
            Intent i = new Intent(mContext, HelpActivity.class);
            startActivity(i);
        }

        @Override
        public boolean onPreferenceChange(Preference arg0, Object arg1) {
            // TODO Auto-generated method stub
            final String key = arg0.getKey();
            if (COMDIFFCULTY.equals(key)) {
                mComDiffcult.setSummary(getVisualTextName(mContext, (String) arg1,
                        R.array.pref_key_com_difficulty_choices,
                        R.array.pref_key_com_difficulty_values));
            } else if (LOGINSTATUS.equals(key)) {
                if ((Boolean) arg1 == true) {
                    startActivityForResult(new Intent(mContext, LoginActivity.class), REQUEST_LOGIN);
                } else {
                    JumpUser.logOut();
                    mAlias.setSummary(mContext.getResources().getString(R.string.unset));
                }
            } else if (ALIAS.equals(key)) {
                saveAndUpdateAlias((String) arg1);
            }
            return true;
        }

        private void saveAndUpdateAlias(final String alias) {
            L.d("saveAndUpdateAlias:"+alias);
            if (alias.contains("admin")) {
                SnackbarUtil.showShortSnackbar(getView(), "can not include \"admin\"");
                return;
            }
            final JumpUser currentUser = BmobUser.getCurrentUser(JumpUser.class);
            String current_alias = currentUser.getAlias();
            if (current_alias == null || !current_alias.equals(alias)) {
                showProgress(true);
                JumpUser user = new JumpUser();
                user.setAlias(alias);
                user.update(currentUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e != null) {
                            L.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                        }
                        mAlias.setSummary(alias);
                        showProgress(false);
                    }
                });
            }
        }

        private void uploadAvatar() {
            String path = FactoryInterface.getAvatarPath(mContext);
            final BmobFile bmobFile = new BmobFile(new File(path));
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        L.i("文件上传成功，返回的名称--" + bmobFile.getFileUrl());
//                        mAvatarUrl = bmobFile.getFileUrl();
//                        SharedPreferences.Editor editor = sp.edit();
//                        editor.putString(AVATAR_URL, mAvatarUrl);
//                        editor.commit();
                        insertObject(bmobFile);
                    } else {
                        L.i("bmob", "上传失败：" + e.getMessage() + "," + e.getErrorCode());
                        showProgress(false);
                    }
                }
            });
        }

        private void saveAndUpdateAvatar(byte[] b) {
            L.d("saveAndUpdateAvatar");
            showProgress(true);
            if (FactoryInterface.saveAvatar(mContext, b)) {
                final JumpUser currentUser = BmobUser.getCurrentUser(JumpUser.class);
                BmobFile avatar = currentUser.getAvatar();
                final String current_url = avatar == null ? "" : avatar.getFileUrl();
                if (!TextUtils.isEmpty(current_url)) {
                    //delete old avatar first
                    BmobFile bmobFile = new BmobFile();
                    bmobFile.setUrl(current_url);
                    bmobFile.delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e != null) {
                                L.i("bmob", "文件删除失败：" + e.getMessage() + "," + e.getErrorCode());
                            } else {
                                L.d("delete old avatar success");
                            }
                            uploadAvatar();
                        }
                    });
                } else {
                    uploadAvatar();
                }
            } else {
                showProgress(false);
            }
        }

        private void insertObject(final BmobFile obj){
            final JumpUser currentUser = BmobUser.getCurrentUser(JumpUser.class);
            JumpUser user = new JumpUser();
            user.setAvatar(obj);
            user.update(currentUser.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e != null) {
                        L.i("bmob", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                    } else {
                        mAvatar.updateAvatar();
                    }
                    showProgress(false);
                }
            });
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                case REQUEST_LOGIN:
                    if (resultCode != 1) {
                        //login fail
                        mLoginSwitch.setChecked(false);
                    } else {
                        final JumpUser user = BmobUser.getCurrentUser(JumpUser.class);
                        mAlias.setSummary(user.getAlias());
                        mAlias.setText(user.getAlias());
                        mAvatar.updateAvatar();
                    }
                    break;

                case SELECT_PORTRAIT:
                    L.d("SELECT_PORTRAIT: data = " + data);
                    if (data != null) {
                        Intent i = new Intent("android.intent.action.jumpjump.Crop");
                        i.setType("image/*");
                        i.putExtra("CROP_URI", data.getData().toString());
                        startActivityForResult(i,CROP_PORTRAIT);
                    }
                    break;

                case CROP_PORTRAIT:
                    L.d("CROP_PORTRAIT: data = " + data);
                    if (resultCode > 0 && data != null) {
                        byte[] b = data.getByteArrayExtra("CROP_BITMAP");
                        saveAndUpdateAvatar(b);
                    }
                    break;
                default:
                    break;
            }
        }

        private void init() {
            sp = PreferenceManager.getDefaultSharedPreferences(mContext);
            String value = sp.getString(COMDIFFCULTY, "0");
//            mAvatarUrl = sp.getString(AVATAR_URL, "");
            mComDiffcult.setSummary(getVisualTextName(mContext, value,
                    R.array.pref_key_com_difficulty_choices,
                    R.array.pref_key_com_difficulty_values));

            final JumpUser user = BmobUser.getCurrentUser(JumpUser.class);
            if (user == null) {
                mLoginSwitch.setChecked(false);
                mAlias.setSummary(mContext.getResources().getString(R.string.unset));
            } else {
                mLoginSwitch.setChecked(true);
                if (TextUtils.isEmpty(user.getAlias())) {
                    mAlias.setSummary(mContext.getResources().getString(R.string.unset));
                } else {
                    mAlias.setSummary(user.getAlias());
                    mAlias.setText(user.getAlias());
                }
            }
            if (isNetworkBattle) {
                mUserInfo.setEnabled(false);
            }
            mWaittingDialog = new ProgressDialog(mContext);
            mWaittingDialog.setCancelable(false);
        }

        private void showProgress(boolean show) {
            if (show == true) {
                if (mWaittingDialog.isShowing() == false) {
                    mWaittingDialog.show();
                }
            } else {
                if (mWaittingDialog.isShowing() == true) {
                    mWaittingDialog.dismiss();
                }
            }
        }

        private CharSequence getVisualTextName(Context context,
                                               String enumName,
                                               int choiceNameResId,
                                               int choiceValueResId) {
            CharSequence[] visualNames = null;
            visualNames = context.getResources().getTextArray(choiceNameResId);
            CharSequence[] enumNames = context.getResources().getTextArray(choiceValueResId);
            if (visualNames.length != enumNames.length) {
                return "";
            }
            for (int i = 0; i < enumNames.length; i++) {
                if (enumNames[i].equals(enumName)) {
                    return visualNames[i];
                }
            }
            return "";
        }
    }
}
