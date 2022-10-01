/*
 * Copyright (C) 2014 The Android Open Source Project
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
package org.lineageos.eleven.locale;

import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Objects;

public class LocaleSet {
    private static final String CHINESE_LANGUAGE = Locale.CHINESE.getLanguage().toLowerCase(Locale.US);
    private static final String JAPANESE_LANGUAGE = Locale.JAPANESE.getLanguage().toLowerCase(Locale.US);
    private static final String KOREAN_LANGUAGE = Locale.KOREAN.getLanguage().toLowerCase(Locale.US);

    private static class LocaleWrapper {
        private final Locale mLocale;
        private final String mLanguage;
        private final boolean mLocaleIsCJK;

        private static boolean isLanguageCJK(String language) {
            return CHINESE_LANGUAGE.equals(language) ||
                    JAPANESE_LANGUAGE.equals(language) ||
                    KOREAN_LANGUAGE.equals(language);
        }

        public LocaleWrapper(Locale locale) {
            mLocale = locale;
            if (mLocale != null) {
                mLanguage = mLocale.getLanguage().toLowerCase(Locale.US);
                mLocaleIsCJK = isLanguageCJK(mLanguage);
            } else {
                mLanguage = null;
                mLocaleIsCJK = false;
            }
        }

        public boolean hasLocale() {
            return mLocale != null;
        }

        public Locale getLocale() {
            return mLocale;
        }

        public boolean isLocale(Locale locale) {
            return Objects.equals(mLocale, locale);
        }

        public boolean isLocaleCJK() {
            return mLocaleIsCJK;
        }

        public boolean isLanguage(String language) {
            return mLanguage == null ? (language == null)
                    : mLanguage.equalsIgnoreCase(language);
        }

        @NonNull
        public String toString() {
            return mLocale != null ? LocaleSet.toBcp47Language(mLocale) : "(null)";
        }
    }

    public static LocaleSet getDefault() {
        return new LocaleSet(Locale.getDefault());
    }

    public LocaleSet(Locale locale) {
        this(locale, null);
    }

    /**
     * Returns locale set for a given set of IETF BCP-47 tags separated by ';'.
     * BCP-47 tags are what is used by ICU 52's toLanguageTag/forLanguageTag
     * methods to represent individual Locales: "en-US" for Locale.US,
     * "zh-CN" for Locale.CHINA, etc. So eg "en-US;zh-CN" specifies the locale
     * set LocaleSet(Locale.US, Locale.CHINA).
     *
     * @param localeString One or more BCP-47 tags separated by ';'.
     * @return LocaleSet for specified locale string, or default set if null
     * or unable to parse.
     */
    public static LocaleSet getLocaleSet(String localeString) {
        // Locale.toString() generates strings like "en_US" and "zh_CN_#Hans".
        // Locale.toLanguageTag() generates strings like "en-US" and "zh-Hans-CN".
        // We can only parse language tags.
        if (localeString != null && localeString.indexOf('_') == -1) {
            final String[] locales = localeString.split(";");
            final Locale primaryLocale = LocaleSet.forLanguageTag(locales[0]);
            // ICU tags undefined/unparseable locales "und"
            if (primaryLocale != null &&
                    !TextUtils.equals(LocaleSet.toBcp47Language(primaryLocale), "und")) {
                if (locales.length > 1 && locales[1] != null) {
                    final Locale secondaryLocale = LocaleSet.forLanguageTag(locales[1]);
                    if (secondaryLocale != null &&
                            !TextUtils.equals(LocaleSet.toBcp47Language(secondaryLocale), "und")) {
                        return new LocaleSet(primaryLocale, secondaryLocale);
                    }
                }
                return new LocaleSet(primaryLocale);
            }
        }
        return getDefault();
    }

    /**
     * Modified from:
     * https://github.com/apache/cordova-plugin-globalization/blob/master/src/android/Globalization.java
     * 
     * Returns a well-formed ITEF BCP 47 language tag representing this locale string
     * identifier for the client's current locale
     *
     * @return String: The BCP 47 language tag for the current locale
     */
    public static String toBcp47Language(Locale loc) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                // return loc.toLanguageTag();
                Class<?> clazz = loc.getClass();
                Method m = clazz.getMethod("toLanguageTag", new Class[0]);
                return ((String)(m.invoke(loc, new Object[0])));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        // we will use a dash as per BCP 47
        final char SEP = '-';
        String language = loc.getLanguage();
        String region = loc.getCountry();
        String variant = loc.getVariant();

        // special case for Norwegian Nynorsk since "NY" cannot be a variant as per BCP 47
        // this goes before the string matching since "NY" wont pass the variant checks
        if (language.equals("no") && region.equals("NO") && variant.equals("NY")) {
            language = "nn";
            region = "NO";
            variant = "";
        }

        if (language.isEmpty() || !language.matches("\\p{Alpha}{2,8}")) {
            language = "und";       // Follow the Locale#toLanguageTag() implementation
            // which says to return "und" for Undetermined
        } else if (language.equals("iw")) {
            language = "he";        // correct deprecated "Hebrew"
        } else if (language.equals("in")) {
            language = "id";        // correct deprecated "Indonesian"
        } else if (language.equals("ji")) {
            language = "yi";        // correct deprecated "Yiddish"
        } else if (language.equals("tl")) {
            language = "fil";        // correct deprecated "Filipino"
        }
        
        // ensure valid country code, if not well formed, it's omitted
        if (!region.matches("\\p{Alpha}{2}|\\p{Digit}{3}")) {
            region = "";
        }

        // variant subtags that begin with a letter must be at least 5 characters long
        if (!variant.matches("\\p{Alnum}{5,8}|\\p{Digit}\\p{Alnum}{3}")) {
            variant = "";
        }

        StringBuilder bcp47Tag = new StringBuilder(language);
        if (!region.isEmpty()) {
            bcp47Tag.append(SEP).append(region);
        }
        if (!variant.isEmpty()) {
            bcp47Tag.append(SEP).append(variant);
        }

        return bcp47Tag.toString();
    }
    
    /* From
     * https://chromium.googlesource.com/chromium/src/base/+/master/android/java/src/org/chromium/base/LocaleUtils.java
     */
    public static Locale forLanguageTag(String languageTag) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return Locale.forLanguageTag(languageTag);
        }

        String[] tag = languageTag.split("-");
        if (tag.length == 0) {
            return new Locale("");
        }
        String language = tag[0];
        if ((language.length() != 2 && language.length() != 3) || language.equals("und")) {
            return new Locale("");
        }
        if ( language.equals("fil") ){
            language = "tl";
        }
        if (tag.length == 1) {
            return new Locale(language);
        }
        String country = tag[1];
        if (country.length() != 2 && country.length() != 3) {
            return new Locale(language);
        }
        return new Locale(language, country);
    }

    private final LocaleWrapper mPrimaryLocale;
    private final LocaleWrapper mSecondaryLocale;

    public LocaleSet(Locale primaryLocale, Locale secondaryLocale) {
        mPrimaryLocale = new LocaleWrapper(primaryLocale);
        mSecondaryLocale = new LocaleWrapper(
                Objects.equals(mPrimaryLocale, new LocaleWrapper(secondaryLocale)) ?
                        null : secondaryLocale);
    }

    public LocaleSet normalize() {
        final Locale primaryLocale = getPrimaryLocale();
        if (primaryLocale == null) {
            return getDefault();
        }
        Locale secondaryLocale = getSecondaryLocale();
        // disallow both locales with same language (redundant and/or conflicting)
        // disallow both locales CJK (conflicting rules)
        if (secondaryLocale == null ||
                isPrimaryLanguage(secondaryLocale.getLanguage()) ||
                (isPrimaryLocaleCJK() && isSecondaryLocaleCJK())) {
            return new LocaleSet(primaryLocale);
        }
        // unnecessary to specify English as secondary locale (redundant)
        if (isSecondaryLanguage(Locale.ENGLISH.getLanguage())) {
            return new LocaleSet(primaryLocale);
        }
        return this;
    }

    public boolean hasSecondaryLocale() {
        return mSecondaryLocale.hasLocale();
    }

    public Locale getPrimaryLocale() {
        return mPrimaryLocale.getLocale();
    }

    public Locale getSecondaryLocale() {
        return mSecondaryLocale.getLocale();
    }

    public boolean isPrimaryLocale(Locale locale) {
        return mPrimaryLocale.isLocale(locale);
    }

    public boolean isSecondaryLocale(Locale locale) {
        return mSecondaryLocale.isLocale(locale);
    }

    public boolean isPrimaryLocaleCJK() {
        return mPrimaryLocale.isLocaleCJK();
    }

    public boolean isSecondaryLocaleCJK() {
        return mSecondaryLocale.isLocaleCJK();
    }

    public boolean isPrimaryLanguage(String language) {
        return mPrimaryLocale.isLanguage(language);
    }

    public boolean isSecondaryLanguage(String language) {
        return mSecondaryLocale.isLanguage(language);
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof LocaleSet) {
            final LocaleSet other = (LocaleSet) object;
            return other.isPrimaryLocale(mPrimaryLocale.getLocale())
                    && other.isSecondaryLocale(mSecondaryLocale.getLocale());
        }
        return false;
    }

    @Override
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(mPrimaryLocale.toString());
        if (hasSecondaryLocale()) {
            builder.append(";");
            builder.append(mSecondaryLocale.toString());
        }
        return builder.toString();
    }
}
