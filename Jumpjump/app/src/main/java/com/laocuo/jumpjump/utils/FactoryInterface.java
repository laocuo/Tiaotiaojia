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
package com.laocuo.jumpjump.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.laocuo.jumpjump.R;
import com.laocuo.jumpjump.login.JumpUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.bmob.v3.datatype.BmobFile;

public class FactoryInterface {
    private static final String AVATAR = "avatar.jpg";
//    private static final String ANOTHER_AVATAR = "another_avatar.jpg";

    public static Bitmap getBitmap(Context context, int id) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
        return bitmap;
    }

    public static Bitmap getBitmap(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static boolean saveAvatar(Context context, byte[] b) {
        boolean ret = false;
        try {
            context.deleteFile(AVATAR);
            FileOutputStream outputStream =  context.openFileOutput(AVATAR, Context.MODE_PRIVATE);
            outputStream.write(b);
            outputStream.close();
            ret = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

//    public static Drawable getAvatar(Context context) {
//        BitmapDrawable d = null;
//        String path = getAvatarPath(context);
//        File file = new File(path);
//        if (file.exists()) {
//            d = new BitmapDrawable(context.getResources(), BitmapFactory.decodeFile(path));
//        } else {
//            d = new BitmapDrawable(context.getResources(), BitmapFactory.decodeResource(context.getResources(), R.drawable.perm_group_personal_info));
//        }
//        return d;
//    }
//
//    public static Drawable getAnotherAvatar(Context context) {
//        BitmapDrawable d = null;
//        String path = getAnotherAvatarPath(context);
//        File file = new File(path);
//        if (file.exists()) {
//            d = new BitmapDrawable(context.getResources(), BitmapFactory.decodeFile(path));
//        } else {
//            d = new BitmapDrawable(context.getResources(), BitmapFactory.decodeResource(context.getResources(), R.drawable.perm_group_personal_info));
//        }
//        return d;
//    }

    public static String getAvatarPath(Context context) {
        String path = context.getFilesDir()+"/"+AVATAR;
        L.d("getAvatarPath:"+path);
        return path;
    }
//
//    public static String getAnotherAvatarPath(Context context) {
//        String path = context.getFilesDir()+"/"+ANOTHER_AVATAR;
//        L.d("getAnotherAvatarPath:"+path);
//        return path;
//    }

    public static Dialog createProgressDialog(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.progress, null);
        ImageView progress = (ImageView) v.findViewById(R.id.progress_img);
        progress.setAnimation(AnimationUtils.loadAnimation(context,R.anim.progress_animation));
        Dialog dialog = new Dialog(context, R.style.ProgressTheme);
        dialog.setCancelable(false);
        dialog.setContentView(v);
        return dialog;
    }

    public static void setAvatar(Context context, JumpUser user, ImageView imageview) {
        BmobFile avatar = user.getAvatar();
        String url = avatar == null ? "" : avatar.getFileUrl();
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.user)
                    .crossFade()
                    .into(imageview);
        }
    }

    public static void setAvatar(Context context, String url, ImageView imageview) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.user)
                    .crossFade()
                    .into(imageview);
        }
    }
}
