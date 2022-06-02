package com.distivity.productivitylauncher.Activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.distivity.productivitylauncher.BaseActivity;
import com.distivity.productivitylauncher.Pojos.References;
import com.distivity.productivitylauncher.R;
import com.distivity.productivitylauncher.ThemeManager;
import com.distivity.productivitylauncher.Utils;

public class GetPremiumActivity extends BaseActivity {

    Button getPremiumButton;
    TextView message;



    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.setupTheme(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_get_premium);



        getPremiumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.Subscriptions.subscribeToPremium(References.getInstance(GetPremiumActivity.this).
                        bp,GetPremiumActivity.this);
            }
        });

        message.setText(getIntent().getExtras().getString(Utils.CONSTANTS.MISSING_FEATURE_MESSAGE));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
