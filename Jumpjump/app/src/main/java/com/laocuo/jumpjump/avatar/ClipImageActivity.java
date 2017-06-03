package com.laocuo.jumpjump.avatar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.laocuo.jumpjump.R;

import java.io.ByteArrayOutputStream;

/**
 * Created by hoperun on 2/9/17.
 */

public class ClipImageActivity extends AppCompatActivity {
    private ClipImageLayout mClipImageLayout;
    private Intent mIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip);
        getSupportActionBar().setTitle("");
        mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);
        mIntent = getIntent();
        if (mIntent != null) {
            String s = mIntent.getStringExtra("CROP_URI");
            Uri mUri = Uri.parse(s);
            mClipImageLayout.setImageSrc(this, mUri);
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_clip_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clip:
                Bitmap bitmap = mClipImageLayout.clip();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] datas = baos.toByteArray();
                mIntent.putExtra("CROP_BITMAP", datas);
                setResult(1, mIntent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
