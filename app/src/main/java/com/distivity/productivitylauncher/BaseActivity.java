package com.distivity.productivitylauncher;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    public Runnable onStopRunnable;

    @Override
    protected void onDestroy() {
        if (onStopRunnable != null) {
            onStopRunnable.run();
        }
        super.onDestroy();
    }
}
