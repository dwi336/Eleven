/*
 * Copyright (C) 2012 Andrew Neal
 * Copyright (C) 2014 The CyanogenMod Project
 * Copyright (C) 2018-2021 The LineageOS Project
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
package org.lineageos.eleven.cache;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentCallbacks2;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import org.lineageos.eleven.cache.disklrucache.DiskLruCache;
import org.lineageos.eleven.utils.ElevenUtils;
import org.lineageos.eleven.utils.IoUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;

/**
 * This class holds the memory and disk bitmap caches.
 */
public final class ImageCache {
    private static final String TAG = ImageCache.class.getSimpleName();

    /**
     * The {@link Uri} used to retrieve album art
     */
    private static final Uri mArtworkUri;

    /**
     * Default memory cache size as a percent of device memory class
     */
    private static final float MEM_CACHE_DIVIDER = 0.50f;

    /**
     * Default disk cache size 50MB
     */
    private static final int DISK_CACHE_SIZE = 50 * 1024 * 1024;

    /**
     * Compression settings when writing images to disk cache
     */
    private static final CompressFormat COMPRESS_FORMAT = CompressFormat.JPEG;

    /**
     * Disk cache index to read from
     */
    private static final int DISK_CACHE_INDEX = 0;

    /**
     * Image compression quality
     */
    private static final int COMPRESS_QUALITY = 98;

    /**
     * LRU cache
     */
    private MemoryCache mLruCache;

    /**
     * Disk LRU cache
     */
    private DiskLruCache mDiskCache;

    private static ImageCache sInstance;

    static {
        mArtworkUri = Uri.parse("content://media/external/audio/albumart");
    }

    /**
     * Constructor of <code>ImageCache</code>
     *
     * @param context The {@link Context} to use
     */
    public ImageCache(final Context context) {
        init(context);
    }

    /**
     * Used to create a singleton of {@link ImageCache}
     *
     * @param context The {@link Context} to use
     * @return A new instance of this class.
     */
    public static ImageCache getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new ImageCache(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Initialize the cache, providing all parameters.
     *
     * @param context     The {@link Context} to use
     */
    private void init(final Context context) {
        ElevenUtils.execute(new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(final Void... unused) {
                // Initialize the disk cache in a background thread
                initDiskCache(context);
                return null;
            }
        }, (Void[]) null);
        // Set up the memory cache
        initLruCache(context);
    }

    /**
     * Initializes the disk cache. Note that this includes disk access so this
     * should not be executed on the main/UI thread. By default an ImageCache
     * does not initialize the disk cache when it is created, instead you should
     * call initDiskCache() to initialize it on a background thread.
     *
     * @param context The {@link Context} to use
     */
    private synchronized void initDiskCache(final Context context) {
        // Set up disk cache
        if (mDiskCache == null || mDiskCache.isClosed()) {
            File diskCacheDir = getDiskCacheDir(context, TAG);
            if (!diskCacheDir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                diskCacheDir.mkdirs();
            }
            if (getUsableSpace(diskCacheDir) > DISK_CACHE_SIZE) {
                try {
                    mDiskCache = DiskLruCache.open(diskCacheDir, 1, 1, DISK_CACHE_SIZE);
                } catch (final IOException ignored) {
                }
            }
        }
    }

    /**
     * Sets up the Lru cache
     *
     * @param context The {@link Context} to use
     */
    public void initLruCache(final Context context) {
        final ActivityManager activityManager = ContextCompat.getSystemService(context, ActivityManager.class);
        final int lruCacheSize = Math.round(MEM_CACHE_DIVIDER * activityManager.getMemoryClass()
                * 1024 * 1024);
        mLruCache = new MemoryCache(lruCacheSize);

        // Release some memory as needed
        context.registerComponentCallbacks(new ComponentCallbacks2() {

            @Override
            public void onTrimMemory(final int level) {
                if (level >= TRIM_MEMORY_MODERATE) {
                    evictAll();
                } else if (level >= TRIM_MEMORY_BACKGROUND) {
                    mLruCache.trimToSize(mLruCache.size() / 2);
                }
            }

            @Override
            public void onLowMemory() {
                // Nothing to do
            }

            @Override
            public void onConfigurationChanged(@NonNull final Configuration newConfig) {
                // Nothing to do
            }
        });
    }

    /**
     * Find and return an existing ImageCache stored in a {@link RetainFragment}
     * , if not found a new one is created using the supplied params and saved
     * to a {@link RetainFragment}
     *
     * @param activity The calling {@link Activity}
     * @return An existing retained ImageCache object or a new one if one did
     * not exist
     */
    public static ImageCache findOrCreateCache(final FragmentActivity activity) {

        // Search for, or create an instance of the non-UI RetainFragment
        final RetainFragment retainFragment = findOrCreateRetainFragment(
                activity.getSupportFragmentManager());

        // See if we already have an ImageCache stored in RetainFragment
        ImageCache cache = (ImageCache) retainFragment.getObject();

        // No existing ImageCache, create one and store it in RetainFragment
        if (cache == null) {
            cache = getInstance(activity);
            retainFragment.setObject(cache);
        }
        return cache;
    }

    /**
     * Locate an existing instance of this {@link Fragment} or if not found,
     * create and add it using {@link FragmentManager}
     *
     * @param fm The {@link FragmentManager} to use
     * @return The existing instance of the {@link Fragment} or the new instance
     * if just created
     */
    public static RetainFragment findOrCreateRetainFragment(final FragmentManager fm) {
        // Check to see if we have retained the worker fragment
        RetainFragment retainFragment = (RetainFragment) fm.findFragmentByTag(TAG);

        // If not retained, we need to create and add it
        if (retainFragment == null) {
            retainFragment = new RetainFragment();
            fm.beginTransaction().add(retainFragment, TAG).commit();
        }
        return retainFragment;
    }

    /**
     * Adds a new image to the memory and disk caches
     *
     * @param data   The key used to store the image
     * @param bitmap The {@link Bitmap} to cache
     */
    public void addBitmapToCache(final String data, final Bitmap bitmap) {
        addBitmapToCache(data, bitmap, false);
    }

    /**
     * Adds a new image to the memory and disk caches
     *
     * @param data    The key used to store the image
     * @param bitmap  The {@link Bitmap} to cache
     * @param replace force a replace even if the bitmap exists in the cache
     */
    public void addBitmapToCache(final String data, final Bitmap bitmap, final boolean replace) {
        if (data == null || bitmap == null) {
            return;
        }

        // Add to memory cache
        addBitmapToMemCache(data, bitmap, replace);

        // Add to disk cache
        if (mDiskCache != null && !mDiskCache.isClosed()) {
            final String key = hashKeyForDisk(data);
            OutputStream out = null;
            try {
                final DiskLruCache.Snapshot snapshot = mDiskCache.get(key);
                if (snapshot != null) {
                    snapshot.getInputStream(DISK_CACHE_INDEX).close();
                }

                if (snapshot == null || replace) {
                    final DiskLruCache.Editor editor = mDiskCache.edit(key);
                    if (editor != null) {
                        out = editor.newOutputStream(DISK_CACHE_INDEX);
                        bitmap.compress(COMPRESS_FORMAT, COMPRESS_QUALITY, out);
                        editor.commit();
                        out.close();
                        flush();
                    }
                }
            } catch (final IOException e) {
                // if the user clears the cache while we have an async task going we could try
                // writing to the disk cache while it isn't ready. Catching here will silently
                // fail instead
                Log.e(TAG, "addBitmapToCache", e);
            } finally {
                IoUtils.closeQuietly(out);
            }
        }
    }

    /**
     * Called to add a new image to the memory cache
     *
     * @param data   The key identifier
     * @param bitmap The {@link Bitmap} to cache
     */
    public void addBitmapToMemCache(final String data, final Bitmap bitmap) {
        addBitmapToMemCache(data, bitmap, false);
    }

    /**
     * Called to add a new image to the memory cache
     *
     * @param data    The key identifier
     * @param bitmap  The {@link Bitmap} to cache
     * @param replace whether to force a replace if it already exists
     */
    public void addBitmapToMemCache(final String data, final Bitmap bitmap, final boolean replace) {
        if (data == null || bitmap == null) {
            return;
        }
        // Add to memory cache
        if (replace || getBitmapFromMemCache(data) == null) {
            mLruCache.put(data, bitmap);
        }
    }

    /**
     * Fetches a cached image from the memory cache
     *
     * @param data Unique identifier for which item to get
     * @return The {@link Bitmap} if found in cache, null otherwise
     */
    public final Bitmap getBitmapFromMemCache(final String data) {
        return (data == null || mLruCache == null) ? null : mLruCache.get(data);
    }

    /**
     * Fetches a cached image from the disk cache
     *
     * @param data Unique identifier for which item to get
     * @return The {@link Bitmap} if found in cache, null otherwise
     */
    public final Bitmap getBitmapFromDiskCache(final String data) {
        if (data == null) {
            return null;
        }

        // Check in the memory cache here to avoid going to the disk cache less
        // often
        if (getBitmapFromMemCache(data) != null) {
            return getBitmapFromMemCache(data);
        }

        final String key = hashKeyForDisk(data);
        if (mDiskCache != null) {
            InputStream inputStream = null;
            try {
                final DiskLruCache.Snapshot snapshot = mDiskCache.get(key);
                if (snapshot != null) {
                    inputStream = snapshot.getInputStream(DISK_CACHE_INDEX);
                    if (inputStream != null) {
                        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        if (bitmap != null) {
                            return bitmap;
                        }
                    }
                }
            } catch (final IOException e) {
                Log.e(TAG, "getBitmapFromDiskCache", e);
            } finally {
                IoUtils.closeQuietly(inputStream);
            }
        }
        return null;
    }

    /**
     * Tries to return a cached image from memory cache before fetching from the
     * disk cache
     *
     * @param data Unique identifier for which item to get
     * @return The {@link Bitmap} if found in cache, null otherwise
     */
    public final Bitmap getCachedBitmap(final String data) {
        if (data == null) {
            return null;
        }
        Bitmap cachedImage = getBitmapFromMemCache(data);
        if (cachedImage == null) {
            cachedImage = getBitmapFromDiskCache(data);
        }
        if (cachedImage != null) {
            addBitmapToMemCache(data, cachedImage);
            return cachedImage;
        }
        return null;
    }

    /**
     * Tries to return the album art from memory cache and disk cache, before
     * calling {@code #getArtworkFromFile(Context, String)} again
     *
     * @param context The {@link Context} to use
     * @param data    The name of the album art
     * @param id      The ID of the album to find artwork for
     * @return The artwork for an album
     */
    public final Bitmap getCachedArtwork(final Context context, final String data, final long id) {
        if (context == null || data == null) {
            return null;
        }
        Bitmap cachedImage = getCachedBitmap(data);
        if (cachedImage == null && id >= 0) {
            cachedImage = getArtworkFromFile(context, id);
        }
        if (cachedImage != null) {
            addBitmapToMemCache(data, cachedImage);
            return cachedImage;
        }
        return null;
    }

    /**
     * Used to fetch the artwork for an album locally from the user's device
     *
     * @param context The {@link Context} to use
     * @param albumId The ID of the album to find artwork for
     * @return The artwork for an album
     */
    public final Bitmap getArtworkFromFile(final Context context, final long albumId) {
        if (albumId < 0) {
            return null;
        }
        Bitmap artwork = null;

        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            final Uri uri = ContentUris.withAppendedId(mArtworkUri, albumId);
            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            if (parcelFileDescriptor != null) {
                final FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                artwork = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            }
        } catch (final IllegalStateException e) {
            // Log.e(TAG, "IllegalStateException - getArtworkFromFile - ", e);
        } catch (final FileNotFoundException e) {
            // Log.e(TAG, "FileNotFoundException - getArtworkFromFile - ", e);
        } catch (final OutOfMemoryError evict) {
            // Log.e(TAG, "OutOfMemoryError - getArtworkFromFile - ", evict);
            evictAll();
        } finally {
            IoUtils.closeQuietly(parcelFileDescriptor);
        }
        return artwork;
    }

    /**
     * flush() is called to synchronize up other methods that are accessing the
     * cache first
     */
    public void flush() {
        ElevenUtils.execute(new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(final Void... unused) {
                if (mDiskCache != null) {
                    try {
                        if (!mDiskCache.isClosed()) {
                            mDiskCache.flush();
                        }
                    } catch (final IOException e) {
                        Log.e(TAG, "flush", e);
                    }
                }
                return null;
            }
        });
    }

    /**
     * Clears the disk and memory caches
     */
    public void clearCaches() {
        ElevenUtils.execute(new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(final Void... unused) {
                // Clear the disk cache
                try {
                    if (mDiskCache != null) {
                        mDiskCache.delete();
                        mDiskCache = null;
                    }
                } catch (final IOException e) {
                    Log.e(TAG, "clearCaches", e);
                }
                // Clear the memory cache
                evictAll();
                return null;
            }
        });
    }

    /**
     * Closes the disk cache associated with this ImageCache object. Note that
     * this includes disk access so this should not be executed on the main/UI
     * thread.
     */
    public void close() {
        ElevenUtils.execute(new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(final Void... unused) {
                if (mDiskCache != null) {
                    try {
                        if (!mDiskCache.isClosed()) {
                            mDiskCache.close();
                            mDiskCache = null;
                        }
                    } catch (final IOException e) {
                        Log.e(TAG, "close", e);
                    }
                }
                return null;
            }
        });
    }

    /**
     * Evicts all of the items from the memory cache and lets the system know
     * now would be a good time to garbage collect
     */
    public void evictAll() {
        if (mLruCache != null) {
            mLruCache.evictAll();
        }
        System.gc();
    }

    /**
     * @param key The key used to identify which cache entries to delete.
     */
    public void removeFromCache(final String key) {
        if (key == null) {
            return;
        }
        // Remove the Lru entry
        if (mLruCache != null) {
            mLruCache.remove(key);
        }

        try {
            // Remove the disk entry
            if (mDiskCache != null) {
                mDiskCache.remove(hashKeyForDisk(key));
            }
        } catch (final IOException e) {
            Log.e(TAG, "removeFromCache(" + key + ")", e);
        }
        flush();
    }

    /**
     * Get a usable cache directory (external if available, internal otherwise)
     *
     * @param context    The {@link Context} to use
     * @param uniqueName A unique directory name to append to the cache
     *                   directory
     * @return The cache directory
     */
    public static File getDiskCacheDir(final Context context, final String uniqueName) {
        // getExternalCacheDir(context) returns null if external storage is not ready
        final String cachePath = getExternalCacheDir(context) != null
                ? getExternalCacheDir(context).getPath()
                : context.getCacheDir().getPath();
        return new File(cachePath, uniqueName);
    }

    /**
     * Get the external app cache directory
     *
     * @param context The {@link Context} to use
     * @return The external cache directory
     */
    public static File getExternalCacheDir(final Context context) {
        return context.getExternalCacheDir();
    }

    /**
     * Check how much usable space is available at a given path.
     *
     * @param path The path to check
     * @return The space available in bytes
     */
    public static long getUsableSpace(final File path) {
        return path.getUsableSpace();
    }

    /**
     * A hashing method that changes a string (like a URL) into a hash suitable
     * for using as a disk filename.
     *
     * @param key The key used to store the file
     */
    public static String hashKeyForDisk(final String key) {
        String cacheKey;
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(key.getBytes());
            cacheKey = bytesToHexString(digest.digest());
        } catch (final NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    /**
     * http://stackoverflow.com/questions/332079
     *
     * @param bytes The bytes to convert.
     * @return A {@link String} converted from the bytes of a hashable key used
     * to store a filename on the disk, to hex digits.
     */
    private static String bytesToHexString(final byte[] bytes) {
        final StringBuilder builder = new StringBuilder();
        for (final byte b : bytes) {
            final String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                builder.append('0');
            }
            builder.append(hex);
        }
        return builder.toString();
    }

    /**
     * A simple non-UI Fragment that stores a single Object and is retained over
     * configuration changes. In this sample it will be used to retain an
     * {@link ImageCache} object.
     */
    public static final class RetainFragment extends Fragment {

        /**
         * The object to be stored
         */
        private Object mObject;

        /**
         * Empty constructor as per the {@link Fragment} documentation
         */
        public RetainFragment() {
        }

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Make sure this Fragment is retained over a configuration change
            setRetainInstance(true);
        }

        /**
         * Store a single object in this {@link Fragment}
         *
         * @param object The object to store
         */
        public void setObject(final Object object) {
            mObject = object;
        }

        /**
         * Get the stored object
         *
         * @return The stored object
         */
        public Object getObject() {
            return mObject;
        }
    }

    /**
     * Used to cache images via {@link LruCache}.
     */
    public static final class MemoryCache extends LruCache<String, Bitmap> {

        /**
         * Constructor of <code>MemoryCache</code>
         *
         * @param maxSize The allowed size of the {@link LruCache}
         */
        public MemoryCache(final int maxSize) {
            super(maxSize);
        }

        /**
         * Get the size in bytes of a bitmap.
         */
        public static int getBitmapSize(final Bitmap bitmap) {
            return bitmap.getByteCount();
        }

        @Override
        protected int sizeOf(final String paramString, final Bitmap paramBitmap) {
            return getBitmapSize(paramBitmap);
        }
    }
}
