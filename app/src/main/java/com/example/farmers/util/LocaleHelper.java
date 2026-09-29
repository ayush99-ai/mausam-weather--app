package com.example.farmers.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import java.util.Locale;

public class LocaleHelper {

    public static final String LANG_ENGLISH = "en";
    public static final String LANG_HINDI = "hi";
    public static final String LANG_MARATHI = "mr";

    public static Context onAttach(Context context) {
        String lang = getPersistedLanguage(context);
        return setLocale(context, lang);
    }

    public static String getPersistedLanguage(Context context) {
        return PrefsManager.getInstance(context).getLanguage();
    }

    public static Context setLocale(Context context, String language) {
        if (language == null || language.isEmpty()) {
            language = LANG_ENGLISH;
        }

        PrefsManager.getInstance(context).setLanguage(language);

        try {
            LocaleListCompat appLocale = LocaleListCompat.forLanguageTags(language);
            AppCompatDelegate.setApplicationLocales(appLocale);
        } catch (Exception ignored) {}

        return updateResources(context, language);
    }

    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(new LocaleList(locale));
            return context.createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
            return context;
        }
    }

    public static String getLanguageName(String code) {
        if (LANG_HINDI.equalsIgnoreCase(code)) {
            return "हिन्दी (Hindi)";
        } else if (LANG_MARATHI.equalsIgnoreCase(code)) {
            return "मराठी (Marathi)";
        }
        return "English";
    }
}
