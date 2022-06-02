package com.distivity.productivitylauncher;

import android.content.Context;

import com.distivity.productivitylauncher.Pojos.References;


public class ThemeManager {


    public static final int THEME_LIGHT = 1;
    public static final int THEM_DARK = 2;




    public static   boolean isNight(Context context) {
        if (References.getPrefs(context).getBoolean(Utils.CONSTANTS.SharedPreferences.ISTHEMEDARK, false)) {
            return true;
        }else return false;

    }

    public static   void turnThemeDark(BaseActivity context) {
        References.getInstance(context).prefs.edit().putBoolean(Utils.CONSTANTS.SharedPreferences.ISTHEMEDARK, true).apply();


        context.setTheme(R.style.AppThemeDark);

        context.recreate();
    }

    public static   void turnThemeLight(BaseActivity context) {
        References.getInstance(context).prefs.edit().putBoolean(Utils.CONSTANTS.SharedPreferences.ISTHEMEDARK, false).apply();

        context.setTheme(R.style.AppTheme);

        context.recreate();
    }


    public static void setupTheme (final BaseActivity context){

        if (isNight(context)){
            context.setTheme(R.style.AppThemeDark);
        }else {
            context.setTheme(R.style.AppTheme);
        }


    }
}
