package com.gazitf.etapp.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import java.util.Locale;

public class LocaleHelper {

    private static final String TAG = LocaleHelper.class.getSimpleName();
    private static final String SELECTED_LANGUAGE_PREFERENCE = "Locale.Helper.Selected.Language";

    /*
        Uygulama başlarken çağırılacak
        Son yapılan dil seçeneği istenecek
     */
    public static Context onAttach(Context context) {
        return setLocale(context, getLanguage(context));
    }

    public static String getLanguage(Context context) {
        return getPersistedData(context);
    }

    /*
        Belirtilen dili kayıt fonksiyonuna gönder
        Sürüme göre resource güncelleme işlemini gerçekleştir
     */
    public static Context setLocale(Context context, String language) {
        persist(context, language);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }

        return updateResourcesLegacy(context, language);
    }

    /*
        Belirtilen dili kaydet
     */
    private static void persist(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(SELECTED_LANGUAGE_PREFERENCE, language);
        editor.apply();
    }

    /*
        Daha önce kaydedilmiş bir dil var ise dön
        yoksa telefonun dilini dön
     */
    private static String getPersistedData(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SELECTED_LANGUAGE_PREFERENCE, Locale.getDefault().getLanguage());
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);

        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }
}
