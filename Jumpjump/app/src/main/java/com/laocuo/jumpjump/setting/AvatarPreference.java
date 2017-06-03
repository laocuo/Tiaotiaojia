package com.laocuo.jumpjump.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.laocuo.jumpjump.R;
import com.laocuo.jumpjump.login.JumpUser;
import com.laocuo.jumpjump.utils.FactoryInterface;
import com.laocuo.jumpjump.utils.L;

import cn.bmob.v3.BmobUser;


/**
 * Created by hoperun on 2/13/17.
 */

public class AvatarPreference extends Preference {
    private ImageView mAvatar;
    private Context mContext;
    private SharedPreferences sp;

    public AvatarPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AvatarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setLayoutResource(R.layout.pref_avatar);
        sp = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        mAvatar = (ImageView) view.findViewById(R.id.userinfo_avatar);
        L.d("onBindView");
        updateAvatar();
    }

//    private void setAvatar() {
//        Drawable d = FactoryInterface.getAvatar(mContext);
//        if (d != null) {
//            mAvatar.setImageDrawable(d);
//            notifyChanged();
//        }
//    }

    public void updateAvatar() {
        L.d("updateAvatar");
        JumpUser user = BmobUser.getCurrentUser(JumpUser.class);
        if (user != null) {
            FactoryInterface.setAvatar(mContext, user, mAvatar);
        }
    }

//    public void updateAvatarOld() {
//        L.d("updateAvatar");
//        final JumpUser user = BmobUser.getCurrentUser(JumpUser.class);
//        if (user != null) {
//            String avatar_url = sp.getString(SettingActivity.AVATAR_URL, "");
//            BmobFile avatar = user.getAvatar();
//            final String current_url = avatar == null ? "" : avatar.getFileUrl();
//            if (!TextUtils.isEmpty(current_url)) {
//                if (current_url.equals(avatar_url)) {
//                    setAvatar();
//                } else {
//                    //download
//                    L.d("download Avatar");
//                    File file =  new File(FactoryInterface.getAvatarPath(mContext));
//                    avatar.download(file, new DownloadFileListener() {
//                        @Override
//                        public void done(String s, BmobException e) {
//                            if (e == null) {
//                                L.d("下载成功,保存路径:"+s);
//                                SharedPreferences.Editor editor = sp.edit();
//                                editor.putString(SettingActivity.AVATAR_URL, current_url);
//                                editor.commit();
//                                setAvatar();
//                            } else {
//                                L.i("bmob", "下载失败："+e.getErrorCode()+","+e.getMessage());
//                            }
//                        }
//
//                        @Override
//                        public void onProgress(Integer value, long newworkSpeed) {
//                            L.i("bmob", "下载进度："+value+","+newworkSpeed);
//                        }
//                    });
//                }
//            }
//        }
//    }
}
