package com.distivity.productivitylauncher.Pojos;

import android.graphics.drawable.Drawable;

public class MenuActions {

    private Drawable icon;
    private String title;
    private Runnable toRun;


    public MenuActions(Drawable icon, String title, Runnable toRun) {
        this.icon = icon;
        this.title = title;
        this.toRun = toRun;
    }


    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String  getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Runnable getToRun() {
        return toRun;
    }

    public void setToRun(Runnable toRun) {
        this.toRun = toRun;
    }
}
