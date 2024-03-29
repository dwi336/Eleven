/*
 * Copyright (C) 2012 Andrew Neal
 * Copyright (C) 2014 The CyanogenMod Project
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
package org.lineageos.eleven.appwidgets;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.widget.RemoteViews;

import org.lineageos.eleven.MusicPlaybackService;
import org.lineageos.eleven.R;
import org.lineageos.eleven.ui.activities.HomeActivity;
import org.lineageos.eleven.widgets.RepeatButton;
import org.lineageos.eleven.widgets.ShuffleButton;

/**
 * 4x2 App-Widget
 *
 * @author Andrew Neal (andrewdneal@gmail.com)
 */
@SuppressLint("NewApi")
public class AppWidgetLargeAlternate extends AppWidgetBase {

    public static final String APP_WIDGET_UPDATE = "app_widget_large_alternate_update";

    private static AppWidgetLargeAlternate mInstance;

    public static synchronized AppWidgetLargeAlternate getInstance() {
        if (mInstance == null) {
            mInstance = new AppWidgetLargeAlternate();
        }
        return mInstance;
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager,
            final int[] appWidgetIds) {
        defaultAppWidget(context, appWidgetIds);
        final Intent updateIntent = new Intent(MusicPlaybackService.SERVICECMD);
        updateIntent.putExtra(MusicPlaybackService.CMDNAME,
                AppWidgetLargeAlternate.APP_WIDGET_UPDATE);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        updateIntent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        context.sendBroadcast(updateIntent);
    }

    /**
     * Initialize given widgets to default state, where we launch Music on
     * default click and hide actions if service not running.
     */
    private void defaultAppWidget(final Context context, final int[] appWidgetIds) {
        final RemoteViews appWidgetViews = new RemoteViews(context.getPackageName(),
                R.layout.app_widget_large_alternate);
        // RemoteViews doesn't support AppCompatImageView. Drawable must be set programmatically.
        int shuffleAllButtonResId = 0;
        int previousButtonResId = 0;
        int pauseButtonResId = 0;
        int nextButtonResId = 0;
        int repeatAllButtonResId = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shuffleAllButtonResId = R.drawable.btn_playback_shuffle_all_widget;
            previousButtonResId = R.drawable.btn_playback_previous_widget;
            pauseButtonResId = R.drawable.btn_playback_pause_widget;
            nextButtonResId = R.drawable.btn_playback_next_widget;
            repeatAllButtonResId = R.drawable.btn_playback_repeat_all_widget;
        } else {
            shuffleAllButtonResId = R.drawable.btn_playback_shuffle_all_widget_compat;
            previousButtonResId = R.drawable.btn_playback_previous_widget_compat;
            pauseButtonResId = R.drawable.btn_playback_pause_widget_compat;
            nextButtonResId = R.drawable.btn_playback_next_widget_compat;
            repeatAllButtonResId = R.drawable.btn_playback_repeat_all_widget_compat;
        }
        appWidgetViews.setImageViewResource(R.id.app_widget_large_alternate_shuffle,
                shuffleAllButtonResId);
        appWidgetViews.setImageViewResource(R.id.app_widget_large_alternate_previous,
                previousButtonResId);
        appWidgetViews.setImageViewResource(R.id.app_widget_large_alternate_play,
                    pauseButtonResId);
        appWidgetViews.setImageViewResource(R.id.app_widget_large_alternate_next,
                nextButtonResId);
        appWidgetViews.setImageViewResource(R.id.app_widget_large_alternate_repeat,
                repeatAllButtonResId);
        linkButtons(context, appWidgetViews);
        pushUpdate(context, appWidgetIds, appWidgetViews);
    }

    private void pushUpdate(final Context context, final int[] appWidgetIds,
                            final RemoteViews views) {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        if (appWidgetIds != null) {
            appWidgetManager.updateAppWidget(appWidgetIds, views);
        } else {
            appWidgetManager.updateAppWidget(new ComponentName(context, getClass()), views);
        }
    }

    /**
     * Check against {@link AppWidgetManager} if there are any instances of this
     * widget.
     */
    private boolean hasInstances(final Context context) {
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final int[] mAppWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context,
                getClass()));
        return mAppWidgetIds.length > 0;
    }

    /**
     * Handle a change notification coming over from
     * {@link MusicPlaybackService}
     */
    public void notifyChange(final MusicPlaybackService service, final String what) {
        if (hasInstances(service)) {
            if (MusicPlaybackService.META_CHANGED.equals(what)
                    || MusicPlaybackService.PLAYSTATE_CHANGED.equals(what)
                    || MusicPlaybackService.REPEATMODE_CHANGED.equals(what)
                    || MusicPlaybackService.SHUFFLEMODE_CHANGED.equals(what)) {
                performUpdate(service, null);
            }
        }
    }

    /**
     * Update all active widget instances by pushing changes
     */
    public void performUpdate(final MusicPlaybackService service, final int[] appWidgetIds) {
        int playButtonResId = 0;
        int pauseButtonResId = 0;
        int repeatAllButtonResId = 0;
        int repeatOneButtonResId = 0;
        int shuffleAllButtonResId = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            playButtonResId = R.drawable.btn_playback_play_widget;
            pauseButtonResId = R.drawable.btn_playback_pause_widget;
            repeatAllButtonResId = R.drawable.btn_playback_repeat_all_widget;
            repeatOneButtonResId = R.drawable.btn_playback_repeat_one_widget;
            shuffleAllButtonResId = R.drawable.btn_playback_shuffle_all_widget;
        } else {
            playButtonResId = R.drawable.btn_playback_play_widget_compat;
            pauseButtonResId = R.drawable.btn_playback_pause_widget_compat;
            repeatAllButtonResId = R.drawable.btn_playback_repeat_all_widget_compat;
            repeatOneButtonResId = R.drawable.btn_playback_repeat_one_widget_compat;
            shuffleAllButtonResId = R.drawable.btn_playback_shuffle_all_widget_compat;
        }
        final RemoteViews appWidgetView = new RemoteViews(service.getPackageName(),
                R.layout.app_widget_large_alternate);

        final CharSequence trackName = service.getTrackName();
        final CharSequence artistName = service.getArtistName();
        final CharSequence albumName = service.getAlbumName();
        final Bitmap bitmap = service.getAlbumArt(true).getBitmap();

        // Set the titles and artwork
        appWidgetView.setTextViewText(R.id.app_widget_large_alternate_line_one, trackName);
        appWidgetView.setTextViewText(R.id.app_widget_large_alternate_line_two, artistName);
        appWidgetView.setTextViewText(R.id.app_widget_large_alternate_line_three, albumName);
        appWidgetView.setImageViewBitmap(R.id.app_widget_large_alternate_image, bitmap);

        // Set correct drawable for pause state
        final boolean isPlaying = service.isPlaying();
        if (isPlaying) {
            appWidgetView.setImageViewResource(R.id.app_widget_large_alternate_play,
                    pauseButtonResId);
            appWidgetView.setContentDescription(R.id.app_widget_large_alternate_play,
                    service.getString(R.string.accessibility_pause));
        } else {
            appWidgetView.setImageViewResource(R.id.app_widget_large_alternate_play,
                    playButtonResId);
            appWidgetView.setContentDescription(R.id.app_widget_large_alternate_play,
                    service.getString(R.string.accessibility_play));
        }

        // Set the correct drawable for the repeat state
        switch (service.getRepeatMode()) {
            case MusicPlaybackService.REPEAT_ALL:
                appWidgetView.setImageViewResource(R.id.app_widget_large_alternate_repeat,
                        repeatAllButtonResId);
                appWidgetView.setInt(R.id.app_widget_large_alternate_repeat, "setAlpha",
                        (int)(RepeatButton.ACTIVE_ALPHA * 255));
                break;
            case MusicPlaybackService.REPEAT_CURRENT:
                appWidgetView.setImageViewResource(R.id.app_widget_large_alternate_repeat,
                        repeatOneButtonResId);
                appWidgetView.setInt(R.id.app_widget_large_alternate_repeat, "setAlpha",
                        (int)(RepeatButton.ACTIVE_ALPHA * 255));
                break;
            default:
                appWidgetView.setImageViewResource(R.id.app_widget_large_alternate_repeat,
                        repeatAllButtonResId);
                appWidgetView.setInt(R.id.app_widget_large_alternate_repeat, "setAlpha",
                        (int)(RepeatButton.INACTIVE_ALPHA * 255));
                break;
        }

        // Set the correct drawable for the shuffle state
        switch (service.getShuffleMode()) {
            case MusicPlaybackService.SHUFFLE_NONE:
                appWidgetView.setImageViewResource(R.id.app_widget_large_alternate_shuffle,
                        shuffleAllButtonResId);
                appWidgetView.setInt(R.id.app_widget_large_alternate_shuffle, "setAlpha",
                        (int)(ShuffleButton.INACTIVE_ALPHA * 255));
                break;
            case MusicPlaybackService.SHUFFLE_AUTO:
            case MusicPlaybackService.SHUFFLE_NORMAL:
            default:
                appWidgetView.setImageViewResource(R.id.app_widget_large_alternate_shuffle,
                        shuffleAllButtonResId);
                appWidgetView.setInt(R.id.app_widget_large_alternate_shuffle, "setAlpha",
                        (int)(ShuffleButton.ACTIVE_ALPHA * 255));
                break;
        }

        // Link actions buttons to intents
        linkButtons(service, appWidgetView);

        // Update the app-widget
        pushUpdate(service, appWidgetIds, appWidgetView);
    }

    /**
     * Link up various button actions using {@link PendingIntent}s.
     *
     */
    private void linkButtons(final Context context, final RemoteViews views) {
        Intent action;
        PendingIntent pendingIntent;

        final ComponentName serviceName = new ComponentName(context, MusicPlaybackService.class);

        // Home
        action = new Intent(context, HomeActivity.class);
        action.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pendingIntent = PendingIntent.getActivity(context, 0, action, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.app_widget_large_alternate_info_container,
                pendingIntent);
        views.setOnClickPendingIntent(R.id.app_widget_large_alternate_image, pendingIntent);

        // Shuffle modes
        pendingIntent = buildPendingIntent(context, MusicPlaybackService.SHUFFLE_ACTION,
                serviceName);
        views.setOnClickPendingIntent(R.id.app_widget_large_alternate_shuffle, pendingIntent);

        // Previous track
        pendingIntent = buildPendingIntent(context, MusicPlaybackService.PREVIOUS_ACTION,
                serviceName);
        views.setOnClickPendingIntent(R.id.app_widget_large_alternate_previous, pendingIntent);

        // Play and pause
        pendingIntent = buildPendingIntent(context, MusicPlaybackService.TOGGLEPAUSE_ACTION,
                serviceName);
        views.setOnClickPendingIntent(R.id.app_widget_large_alternate_play, pendingIntent);

        // Next track
        pendingIntent = buildPendingIntent(context, MusicPlaybackService.NEXT_ACTION, serviceName);
        views.setOnClickPendingIntent(R.id.app_widget_large_alternate_next, pendingIntent);

        // Repeat modes
        pendingIntent = buildPendingIntent(context, MusicPlaybackService.REPEAT_ACTION,
                serviceName);
        views.setOnClickPendingIntent(R.id.app_widget_large_alternate_repeat, pendingIntent);
    }

}
