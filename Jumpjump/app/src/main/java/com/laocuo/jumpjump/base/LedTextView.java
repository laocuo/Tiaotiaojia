package com.laocuo.jumpjump.base;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.laocuo.jumpjump.utils.L;

import java.io.File;
import java.util.HashSet;

/**
 * Created by hoperun on 3/1/17.
 */

public class LedTextView extends TextView {
    private static final String FONTS_FOLDER = "fonts";
    private static final String FONT_DIGITAL_7 = FONTS_FOLDER
            + File.separator + "digital-7.ttf";

    private final int STEP_TIME = 30;
    private final int ONE_SECOND = 1000;
    private int timeleft = STEP_TIME;
    private boolean run;
    private TimeRunnable mTimeRunnable;
    private final static HashSet<UpdateListener> mListeners = new HashSet<UpdateListener>();

    public interface UpdateListener {
        void timeout();
    }

    public LedTextView(Context context) {
        this(context, null);
    }

    public LedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        AssetManager assets = context.getAssets();
        final Typeface font = Typeface.createFromAsset(assets, FONT_DIGITAL_7);
        setTypeface(font);
        mTimeRunnable = new TimeRunnable();
    }

    public void start() {
        L.d("Time start run="+run);
        if (run) {
            removeCallbacks(mTimeRunnable);
        }
        run = true;
        timeleft = STEP_TIME;
        setText(String.format("%02d", timeleft));
        setVisibility(View.VISIBLE);
        postDelayed(mTimeRunnable, ONE_SECOND);
    }

    public void stop() {
        L.d("Time stop run="+run);
        if (run) {
            removeCallbacks(mTimeRunnable);
        }
        run = false;
        timeleft = STEP_TIME;
        setText(String.format("%02d", timeleft));
        setVisibility(View.INVISIBLE);
    }

    public void release() {
        L.d("Time release");
        mListeners.clear();
    }

    public boolean isRun() {
        return run;
    }

    public void addListener(UpdateListener l) {
        synchronized (mListeners) {
            mListeners.add(l);
        }
    }

    public static void removeListener(UpdateListener l) {
        synchronized (mListeners) {
            mListeners.remove(l);
        }
    }

    private class TimeRunnable implements Runnable {

        @Override
        public void run() {
            if (run) {
                timeleft--;
                setText(String.format("%02d", timeleft));
                if (timeleft == 0) {
                    run = false;
                    for(UpdateListener l : mListeners) {
                        l.timeout();
                    }
                } else {
                    postDelayed(mTimeRunnable, ONE_SECOND);
                }
            }
        }
    }
}
