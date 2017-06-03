package com.laocuo.jumpjump.setting;

import android.content.Context;
import android.preference.Preference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.laocuo.jumpjump.R;
import com.laocuo.jumpjump.login.JumpUser;
import com.laocuo.jumpjump.utils.FactoryInterface;
import com.laocuo.jumpjump.utils.L;

import cn.bmob.v3.BmobUser;

/**
 * Created by hoperun on 2/13/17.
 */

public class UserInfoPreference extends Preference {
    private ImageView mHead;
    private TextView mUserName, mName;
    private Context mContext;
    public UserInfoPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserInfoPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setLayoutResource(R.layout.pref_userinfo);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        mHead = (ImageView) view.findViewById(R.id.userinfo_head);
        mName = (TextView) view.findViewById(R.id.userinfo_name);
        mUserName = (TextView) view.findViewById(R.id.userinfo_username);
        updateUserInfo();
    }

    private void updateUserInfo() {
        L.d("updateUserInfo");
        final JumpUser user = BmobUser.getCurrentUser(JumpUser.class);
        if (user != null) {
            L.d("updateUserInfo user != null");
//            mHead.setImageDrawable(FactoryInterface.getAvatar(mContext));
            FactoryInterface.setAvatar(mContext, user, mHead);
            mUserName.setText("ID:" + user.getUsername());
            if (TextUtils.isEmpty(user.getAlias())) {
                mName.setText("empty");
            } else {
                mName.setText(user.getAlias());
            }
            notifyChanged();
        }
    }
}
