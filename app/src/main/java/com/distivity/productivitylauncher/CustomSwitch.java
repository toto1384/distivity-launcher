package com.distivity.productivitylauncher;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Switch;

import androidx.core.content.res.ResourcesCompat;

/**
 * 28/12/13
 * Created by Eugenio Marletti
 * https://gist.github.com/Takhion/8197815
 */

/**
 * A Switch that accepts the attribute "android:fontFamily" inside a style defined by the
 * attribute "android:switchTextAppearance".
 *
 * @attr ref android.R.styleable#TextView_fontFamily
 * @attr ref android.R.styleable#Switch_switchTextAppearance
 */
@TargetApi(16)
public final class CustomSwitch extends Switch
{
    /**
     * Construct a new Switch with default styling.
     *
     * @param context The Context that will determine this widget's theming.
     */
    public CustomSwitch(Context context)
    {
        super(context);
    }

    /**
     * Construct a new Switch with default styling, overriding specific style
     * attributes as requested.
     *
     * @param context The Context that will determine this widget's theming.
     * @param attrs   Specification of attributes that should deviate from default styling.
     */
    public CustomSwitch(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    /**
     * Construct a new Switch with a default style determined by the given theme attribute,
     * overriding specific style attributes as requested.
     *
     * @param context  The Context that will determine this widget's theming.
     * @param attrs    Specification of attributes that should deviate from the default styling.
     * @param defStyle An attribute ID within the active theme containing a reference to the
     *                 default style for this widget. e.g. android.R.attr.switchStyle.
     */
    public CustomSwitch(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    /**
     * Sets the switch text color, size, style, font family, hint color, and
     * highlight color from the specified TextAppearance resource.
     *
     * @attr ref android.R.styleable#Switch_switchTextAppearance
     */
    @Override
    public void setSwitchTextAppearance(Context context, int resid)
    {
        super.setSwitchTextAppearance(context, resid);



        setTypeface(ResourcesCompat.getFont(context, R.font.montserrat));


        TypedArray appearance = null;
        try
        {

        }
        finally
        {
            if (appearance != null) appearance.recycle();
        }

        if (ThemeManager.isNight(getContext())){
            setTrackResource(R.drawable.track_switch_dark);
        }else {
            setTrackResource(R.drawable.track_switch);
        }
    }
}
