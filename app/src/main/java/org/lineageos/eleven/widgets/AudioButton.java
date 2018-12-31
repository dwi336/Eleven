package org.lineageos.eleven.widgets;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;

import org.lineageos.eleven.R;
import org.lineageos.eleven.utils.ElevenUtils;

public abstract class AudioButton extends AppCompatImageButton implements OnClickListener, OnLongClickListener {
    public static float ACTIVE_ALPHA = 1.0f;
    public static float INACTIVE_ALPHA = 0.4f;

    @SuppressWarnings("deprecation")
    public AudioButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setPadding(0, 0, 0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        	setBackground(ContextCompat.getDrawable(context, R.drawable.selectable_background));
        } else {
            setBackgroundResource(R.drawable.selectable_background);
        }
        // Control playback (cycle shuffle)
        setOnClickListener(this);
        // Show the cheat sheet
        setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(final View view) {
        if (TextUtils.isEmpty(view.getContentDescription())) {
            return false;
        } else {
            ElevenUtils.showCheatSheet(view);
            return true;
        }
    }
}
