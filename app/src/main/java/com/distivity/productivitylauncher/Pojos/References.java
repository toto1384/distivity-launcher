package com.distivity.productivitylauncher.Pojos;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.distivity.productivitylauncher.BaseActivity;
import com.distivity.productivitylauncher.Database.DaoHelper;
import com.distivity.productivitylauncher.Utils;

import static android.content.Context.MODE_PRIVATE;

public class References implements BillingProcessor.IBillingHandler {


    public BillingProcessor bp;

    public static SharedPreferences prefs;

    public PackageManager packageManager;


    private String licenseKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAthR2hfam8qinWduIYCyI915hBPJKMjoW5u5FEqIB6WtDO99BU4O25TpngRuKfHOe8QSKMJ6pmkajhxhqcqOlAMV0hjWNdHrztsq41M4tq7GoWTJEG3lqyPG3V6D9L2qh/a2AH3piJZ/5tG6mYgKPJ4Jd/s1wwqp1qN9Nz7tvlFWWS5WXZ7be/B1WmEkxiVqmFvxKAKKFY4McL+UihjLF0d7j2gD+qJwVd66uQLcRVjbqPV7XwsLogJrZzGbH7GLPkfDo6+dvVy8xVA8Ok3nkM5jjOTM+O84mjzjyPXEVD4xb2hD4Giv0dtpwxXdSCNBUfLUb8Ffs6DMloqEkgMrELQIDAQAB";


    private static References references;

    private References(Context context) {

        initBillingProcessor(context);

        prefs = context.getSharedPreferences(Utils.CONSTANTS.SharedPreferences.
                SHARED_PREFERENCES_SP_STRING,MODE_PRIVATE);
        packageManager = context.getPackageManager();

    }

    public static SharedPreferences getPrefs(Context context) {
        if (prefs==null){
            prefs = context.getSharedPreferences(Utils.CONSTANTS.SharedPreferences.
                    SHARED_PREFERENCES_SP_STRING,MODE_PRIVATE);
        }

        return prefs;
    }

    public static References getInstance(BaseActivity context) {

        if (references == null) {
            references=new References(context.getApplicationContext());
        }

        context.onStopRunnable = new Runnable() {
            @Override
            public void run() {
                releaseReference();
                DaoHelper.releaseDaoHelper();
            }
        };

        return references ;
    }

    public static void  releaseReference(){
        references=null;
    }


    private void releaseBillingProcessor (){
        if (bp != null) {
            bp.release();
        }
    }

    private void initBillingProcessor (Context context){
        bp = new BillingProcessor(context, licenseKey
                ,"00051052908754618258", this);
        bp.initialize();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }
}
