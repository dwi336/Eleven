/*
 * Copyright (C) 2008 The Android Open Source Project
 * Copyright (C) 2021 The LineageOS Project
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
package org.lineageos.eleven.widgets;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import org.lineageos.eleven.R;
import org.lineageos.eleven.utils.ElevenUtils;
import org.lineageos.eleven.utils.MusicUtils;

/**
 * A {@link AppCompatImageButton} that will repeatedly call a 'listener' method as long
 * as the button is pressed, otherwise functions like a typical
 * {@link AppCompatImageButton}
 */
public class RepeatingImageButton extends AppCompatImageButton implements OnClickListener {

    private static final long sInterval = 400;

    private long mStartTime;

    private int mRepeatCount;

    private RepeatListener mListener;

    /**
     * @param context The {@link Context} to use
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    public RepeatingImageButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setPadding(0, 0, 0, 0);
        ViewCompat.setBackground(this, ContextCompat.getDrawable(context, R.drawable.selectable_background));
        setFocusable(true);
        setLongClickable(true);
        setOnClickListener(this);
        updateState();
    }

    @Override
    public void onClick(final View view) {
        final int id = view.getId();
        if (id == R.id.action_button_previous) {
            MusicUtils.previous(getContext(), false);
        } else if (id == R.id.action_button_next) {
            MusicUtils.next();
        }
    }

    /**
     * Sets the listener to be called while the button is pressed and the
     * interval in milliseconds with which it will be called.
     *
     * @param l The listener that will be called
     */
    public void setRepeatListener(final RepeatListener l) {
        mListener = l;
    }

    @Override
    public boolean performLongClick() {
        if (mListener == null) {
            ElevenUtils.showCheatSheet(this);
        }
        mStartTime = SystemClock.elapsedRealtime();
        mRepeatCount = 0;
        post(mRepeater);
        return true;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            /* Remove the repeater, but call the hook one more time */
            removeCallbacks(mRepeater);
            if (mStartTime != 0) {
                doRepeat(true);
                mStartTime = 0;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                /*
                 * Need to call super to make long press work, but return true
                 * so that the application doesn't get the down event
                 */
                super.onKeyDown(keyCode, event);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(final int keyCode, final KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                // Remove the repeater, but call the hook one more time
                removeCallbacks(mRepeater);
                if (mStartTime != 0) {
                    doRepeat(true);
                    mStartTime = 0;
                }
        }
        return super.onKeyUp(keyCode, event);
    }

    private final Runnable mRepeater = new Runnable() {
        @Override
        public void run() {
            doRepeat(false);
            if (isPressed()) {
                postDelayed(this, sInterval);
            }
        }
    };

    /**
     * @param shouldRepeat If True the repeat count stops at -1, false if to add
     *                     incrementally add the repeat count
     */
    private void doRepeat(final boolean shouldRepeat) {
        final long now = SystemClock.elapsedRealtime();
        if (mListener != null) {
            mListener.onRepeat(this, now - mStartTime, shouldRepeat ? -1 : mRepeatCount++);
        }
    }

    /**
     * Sets the correct drawable for playback.
     */
    public void updateState() {
        final int id = getId();
        if (id == R.id.action_button_next) {
            setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.btn_playback_next));
        } else if (id == R.id.action_button_previous) {
            setImageDrawable(ContextCompat.getDrawable(getContext(),
                    R.drawable.btn_playback_previous));
        }
    }

    public interface RepeatListener {

        /**
         * @param v           View to be set
         * @param duration    Duration of the long press
         * @param repeatCount The number of repeat counts
         */
        void onRepeat(View v, long duration, int repeatCount);
    }
}
