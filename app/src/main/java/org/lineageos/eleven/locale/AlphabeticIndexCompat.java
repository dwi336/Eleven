package org.lineageos.eleven.locale;

import android.os.Build;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Locale;

public class AlphabeticIndexCompat {
    private static final String TAG = "AlphabeticIndexCompat";

    private BaseAlphabeticIndex index;    
    
    public AlphabeticIndexCompat(LocaleSet locales) {
        index = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            index = new AlphabeticIndexVN(locales);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            index = new AlphabeticIndexV18(locales);
        } else {
            index = new BaseAlphabeticIndex ();
        }
    }

    public int getBucketCount() {
        return index.getBucketCount();
    }
    
    public int getBucketIndex(String s) {
        return index.getBucketIndex(s);
    }

    public String getBucketLabel(int i) {
        return index.getBucketLabel(i);
    }
    
    private static class BaseAlphabeticIndex {
        private static final String BUCKETS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-";
        private static final int UNKNOWN_BUCKET_INDEX = BUCKETS.length() - 1;

        protected int getBucketCount() {
            return BUCKETS.length();
        }

        protected int getBucketIndex(String s) {
            if (s == null) {
                return -1;
            }
            if (s.isEmpty()) {
                return UNKNOWN_BUCKET_INDEX;
            }
            int index = BUCKETS.indexOf(s.substring(0, 1).toUpperCase(Locale.US));
            if (index != -1) {
                return index;
            }
            return UNKNOWN_BUCKET_INDEX;
        }

        protected String getBucketLabel(int index) {
            return BUCKETS.substring(index, index + 1);
        }
    }

    private static final Locale LOCALE_ARABIC = new Locale("ar");
    private static final Locale LOCALE_GREEK = new Locale("el");
    private static final Locale LOCALE_HEBREW = new Locale("he");
    // Serbian and Ukrainian labels are complementary supersets of Russian
    private static final Locale LOCALE_SERBIAN = new Locale("sr");
    private static final Locale LOCALE_UKRAINIAN = new Locale("uk");
    private static final Locale LOCALE_THAI = new Locale("th");

    /**
     * Reflected libcore.icu.AlphabeticIndex implementation, falls back to the base
     * alphabetic index.
     */
    private class AlphabeticIndexV18 extends BaseAlphabeticIndex {

        private Object mAlphabeticIndex;
        private Method mGetBucketIndex;
        private Method mGetBucketCount;
        private Method mGetBucketLabel;

        public AlphabeticIndexV18(LocaleSet locales) {
            super();
            try {
                final Locale secondaryLocale = locales.getSecondaryLocale();
                Class<?> clazz = Class.forName("libcore.icu.AlphabeticIndex");
                Constructor<?> constructor = clazz.getConstructor(new Class[] { Locale.class }); 
                mAlphabeticIndex = constructor.newInstance(new Object[] { locales.getPrimaryLocale() });
                Method m = clazz.getMethod("setMaxLabelCount", int.class);
                mAlphabeticIndex = m.invoke(mAlphabeticIndex,300);
                m = clazz.getMethod("addLabels", Locale.class);
                if (secondaryLocale != null) {
                    m.invoke(mAlphabeticIndex, secondaryLocale);
                }
                m.invoke(mAlphabeticIndex, Locale.ENGLISH);
                m.invoke(mAlphabeticIndex, Locale.JAPANESE);
                m.invoke(mAlphabeticIndex, Locale.KOREAN);
                m.invoke(mAlphabeticIndex, LOCALE_THAI);
                m.invoke(mAlphabeticIndex, LOCALE_ARABIC);
                m.invoke(mAlphabeticIndex, LOCALE_HEBREW);
                m.invoke(mAlphabeticIndex, LOCALE_GREEK);
                m.invoke(mAlphabeticIndex, LOCALE_UKRAINIAN);
                m.invoke(mAlphabeticIndex, LOCALE_SERBIAN);
                m = clazz.getMethod("getImmutableIndex", new Class[0]);
                mAlphabeticIndex = m.invoke(mAlphabeticIndex, new Object[0]);
                clazz = mAlphabeticIndex.getClass();
                mGetBucketCount = clazz.getMethod("getBucketCount", new Class[0]);
                mGetBucketIndex = clazz.getMethod("getBucketIndex", new Class[] { String.class });
                mGetBucketLabel = clazz.getMethod("getBucketLabel", new Class[] { int.class });
            } catch (Exception e) {
                Log.d(TAG, "Unable to load the system index", e);
            }
        }

        protected int getBucketCount() {
            try {
                return ((Integer)(mGetBucketCount.invoke(mAlphabeticIndex, new Object[0]))).intValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return super.getBucketCount();
        }

        protected int getBucketIndex(String s) {
            if (s == null) {
                return -1;
            }
            try {
                return (Integer) mGetBucketIndex.invoke(mAlphabeticIndex, new Object[] { s });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return super.getBucketIndex(s);
        }

        protected String getBucketLabel(int index) {
            try {
                return (String) mGetBucketLabel.invoke(mAlphabeticIndex, index);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return super.getBucketLabel(index);
        }
    }

    /**
     * Implementation based on {@link AlphabeticIndex}.
     */
    private static class AlphabeticIndexVN extends BaseAlphabeticIndex {

        private Object mAlphabeticIndex;
        private Method mGetBucketIndex;
        private Method mGetBucketCount;
        private Method mGetBucket;

        public AlphabeticIndexVN(LocaleSet locales){
            super();
            try {
                final Locale secondaryLocale = locales.getSecondaryLocale();
                final Locale[] LOCALES = new Locale[] {
                        Locale.ENGLISH,
                        Locale.JAPANESE,
                        Locale.KOREAN,
                        LOCALE_THAI,
                        LOCALE_ARABIC,
                        LOCALE_HEBREW,
                        LOCALE_GREEK,
                        LOCALE_UKRAINIAN,
                        LOCALE_SERBIAN
                };
                Class<?> clazz = Class.forName("android.icu.text.AlphabeticIndex");
                Constructor<?> constructor = clazz.getConstructor(new Class[] { Locale.class }); 
                mAlphabeticIndex = constructor.newInstance(new Object[] { locales.getPrimaryLocale() });
                Method m = clazz.getMethod("setMaxLabelCount", int.class);
                mAlphabeticIndex = m.invoke(mAlphabeticIndex,300);
                m = clazz.getMethod("addLabels", Locale[].class);
                if (secondaryLocale != null) {
                    m.invoke(mAlphabeticIndex, new Object[]{ new Locale[] {secondaryLocale}});
                }
                m.invoke(mAlphabeticIndex, new Object[]{ LOCALES});
                m = clazz.getMethod("buildImmutableIndex", new Class[0]);
                mAlphabeticIndex = m.invoke(mAlphabeticIndex, new Object[0]);
                clazz = mAlphabeticIndex.getClass();
                mGetBucket = clazz.getMethod("getBucket", new Class[] { int.class });
                mGetBucketCount = clazz.getMethod("getBucketCount", new Class[0]);
                mGetBucketIndex = clazz.getMethod("getBucketIndex", new Class[] { CharSequence.class } );
            } catch (Exception e) {
                Log.d(TAG, "Unable to load the system index", e);
            } 
        }

        protected int getBucketCount() {
            try {
                return ((Integer)(mGetBucketCount.invoke(mAlphabeticIndex, new Object[0]))).intValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return super.getBucketCount();
        }

        protected int getBucketIndex(String s) {
            if (s == null) {
                return -1;
            }
            try {
                return ((Integer)(mGetBucketIndex.invoke(mAlphabeticIndex, new Object[] { s }))).intValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return super.getBucketIndex(s);
        }

        protected String getBucketLabel(int index) {
            try {
                Object bucket = mGetBucket.invoke(mAlphabeticIndex,index);
                Class<?> clazz = bucket.getClass();
                Method m = clazz.getMethod("getLabel", new Class[0]);
                return (String)(m.invoke(bucket, new Object[0]));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return super.getBucketLabel(index);
        }
    }
}
