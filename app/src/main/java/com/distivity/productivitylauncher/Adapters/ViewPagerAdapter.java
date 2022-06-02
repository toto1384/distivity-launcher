package com.distivity.productivitylauncher.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.distivity.productivitylauncher.R;
import com.distivity.productivitylauncher.Utils;

public class ViewPagerAdapter extends PagerAdapter {

    public Object instantiateItem(ViewGroup collection, int position) {


        View rootView = LayoutInflater.from(collection.getContext()).inflate(R.layout.welcome_pages,null);

        switch (position) {
            case 0:
                ((ImageView)rootView.findViewById(R.id.image_view_welcome_pages))
                        .setBackgroundColor(Utils.Views.getTextColorPrimary(collection.getContext()));
                ((TextView)rootView.findViewById(R.id.text_view_welcome_pages)).setText("Sampletext 1");
                break;
            case 1:
                ((ImageView)rootView.findViewById(R.id.image_view_welcome_pages))
                        .setBackgroundColor(Utils.Views.getTextColorPrimary(collection.getContext()));
                ((TextView)rootView.findViewById(R.id.text_view_welcome_pages)).setText("Sampletext 2");
                break;
            case 2:
                ((ImageView)rootView.findViewById(R.id.image_view_welcome_pages))
                        .setBackgroundColor(Utils.Views.getTextColorPrimary(collection.getContext()));
                ((TextView)rootView.findViewById(R.id.text_view_welcome_pages)).setText("Sampletext 3");
                break;
        }
        return rootView;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
        // No super
    }
}
