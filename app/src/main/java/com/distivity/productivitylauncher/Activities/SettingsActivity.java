package com.distivity.productivitylauncher.Activities;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.distivity.productivitylauncher.BaseActivity;
import com.distivity.productivitylauncher.CustomSwitch;
import com.distivity.productivitylauncher.Pojos.References;
import com.distivity.productivitylauncher.R;
import com.distivity.productivitylauncher.ThemeManager;
import com.distivity.productivitylauncher.Utils;


public class SettingsActivity extends BaseActivity {

    SeekBar numberOfProblemsSeekBar;
    CustomSwitch mathProbemsSwitch;
    CustomSwitch automaticallyDeleteOnCheck;
    CustomSwitch deleteCheckedTodoOnCheckAgain;

    TextView setNumberOfWhitelistedAppsTv;

    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.setupTheme(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_settings);

        sharedPreferences= References.getPrefs(this);

        numberOfProblemsSeekBar= findViewById(R.id.seek_bar_number_of_whitelisted_apps_activity_settings);
        mathProbemsSwitch = findViewById(R.id.math_problems_activity_settings);

        automaticallyDeleteOnCheck = findViewById(R.id.delete_todo_when_checked_switch_settings);
        deleteCheckedTodoOnCheckAgain = findViewById(R.id.delete_checked_todo_on_one_more_check);

        setNumberOfWhitelistedAppsTv = findViewById(R.id.tv_set_number_of_whitelisted_apps_activity_settingds);

        numberOfProblemsSeekBar.setProgress(sharedPreferences.getInt(Utils.CONSTANTS.SharedPreferences.NUMBER_OF_APPS,5));
        mathProbemsSwitch.setChecked(sharedPreferences.getBoolean(Utils.CONSTANTS.SharedPreferences.MATH_PROBLEMS_ON,false));

        setNumberOfWhitelistedAppsTv.setText
                (getResources().getString(R.string.set_number_or_whitelisted_apps)+"("+numberOfProblemsSeekBar.getProgress()+")");

        numberOfProblemsSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               sharedPreferences.edit().putInt(Utils.CONSTANTS.SharedPreferences.NUMBER_OF_APPS,progress).apply();
                setNumberOfWhitelistedAppsTv.setText(getResources().getString(R.string.set_number_or_whitelisted_apps)+"("+progress+")");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mathProbemsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean(Utils.CONSTANTS.SharedPreferences.MATH_PROBLEMS_ON,isChecked).apply();
            }
        });





        automaticallyDeleteOnCheck.setChecked(sharedPreferences.getBoolean
                (Utils.CONSTANTS.SharedPreferences.AUTOMATICALLY_DELETE_TODO_ON_CHECK,false));
        automaticallyDeleteOnCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean(Utils.CONSTANTS.SharedPreferences
                        .AUTOMATICALLY_DELETE_TODO_ON_CHECK,isChecked).apply();
            }
        });


        deleteCheckedTodoOnCheckAgain.setChecked(sharedPreferences.getBoolean(
                Utils.CONSTANTS.SharedPreferences.DELETE_CHECKED_TODOS_ON_CHECK_AGAIN,false));
        deleteCheckedTodoOnCheckAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean(Utils.CONSTANTS.SharedPreferences.
                        DELETE_CHECKED_TODOS_ON_CHECK_AGAIN,isChecked).apply();
            }
        });




    }
}
