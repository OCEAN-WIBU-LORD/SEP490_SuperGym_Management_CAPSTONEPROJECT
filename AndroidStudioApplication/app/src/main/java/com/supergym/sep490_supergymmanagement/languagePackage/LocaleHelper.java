package com.supergym.sep490_supergymmanagement.languagePackage;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LocaleHelper {

    // Function to apply system language
    public static void applySystemLanguage(Context context) {
        String systemLanguage = Locale.getDefault().getLanguage();

        // Check the system language and set app language
        if (systemLanguage.equals("vi")) {
            setLocale(context, "vi"); // Vietnamese
        } else {
            setLocale(context, "en"); // English (default)
        }
    }

    // Function to set locale
    public static void setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);

        context.getResources().updateConfiguration(config, resources.getDisplayMetrics());
    }
}
