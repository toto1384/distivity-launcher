package com.distivity.productivitylauncher;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.distivity.productivitylauncher.Activities.GetPremiumActivity;
import com.distivity.productivitylauncher.Activities.HomeActivity;
import com.distivity.productivitylauncher.Adapters.AppsRecyclerViewAdapter;
import com.distivity.productivitylauncher.Adapters.MenuActionsAdapter;
import com.distivity.productivitylauncher.Database.DaoHelper;
import com.distivity.productivitylauncher.Pojos.MathProblem;
import com.distivity.productivitylauncher.Pojos.MenuActions;
import com.distivity.productivitylauncher.Pojos.References;
import com.distivity.productivitylauncher.Pojos.Todo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.view.Gravity.BOTTOM;

public class Utils {

    public  static DatabaseReference getUserrReference(){
        return FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public static MenuActions getEmptyWhitelistedApp(final HomeActivity homeActivity, final int position) {

        Drawable drawable = homeActivity.getResources().getDrawable(R.drawable.ic_add);

        if (ThemeManager.isNight(homeActivity)){
            drawable.setColorFilter(homeActivity.getResources().getColor(R.color.theWhitest), PorterDuff.Mode.SRC_IN);
        }else {
            drawable.setColorFilter(homeActivity.getResources().getColor(R.color.black161616), PorterDuff.Mode.SRC_IN);
        }

        return  new MenuActions(drawable, "Add app", new Runnable() {
            @Override
            public void run() {
                Utils.CustomPopups.openChoseAppPopup(homeActivity, position);
            }
        });
    }

    public static class Popups{
        public static Dialog showPopup(int RootView , BaseActivity context) {


            Dialog dialog = new Dialog(context,R.style.dialogStyle);



            if (ThemeManager.isNight(context)){
                dialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.popups_background_dark));
            }else {
                dialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.popups_background));
            }


            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            View contentView =LayoutInflater.from(context).inflate(RootView,null);

            Utils.Views.setPaddingForView(contentView,15,context);

            dialog.addContentView(contentView,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));



            dialog.show();


            return dialog;


        }

        public static void showOptionsPopupWindow(AppCompatActivity appCompatActivity,ImageView menuDotsImageView, ArrayList<MenuActions> menuActions) {


            View rootView =LayoutInflater.from
                    (appCompatActivity).inflate(R.layout.options_menu,null);

            final PopupWindow popupWindow = new PopupWindow(rootView, 500,
                    LinearLayout.LayoutParams.WRAP_CONTENT, true);

            ListView listView = rootView.findViewById(R.id.list_view_options_menu);

            listView.setDivider(null);
            listView.setDividerHeight(0);

            listView.setAdapter(new MenuActionsAdapter(appCompatActivity, menuActions,popupWindow));


            Views.setPaddingForView(popupWindow.getContentView(),10, appCompatActivity);

            popupWindow.showAsDropDown(menuDotsImageView, 0, 0);




        }

        public static void showHintPopup(BaseActivity context , String hint ){

            final Dialog dialog = showPopup(R.layout.hint_layout,context);
            View layout = dialog.getWindow().getDecorView();


            dialog.getWindow().setGravity(BOTTOM);


            ImageView closeButton = layout.findViewById( R.id.close_button_hint_popup);
            TextView hintTv= layout.findViewById(R.id.tv_hint_layout);
            hintTv.setText(hint);

            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.hide();
                }
            });

            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });



            dialog.show();

        }

        public static void displayYesNoQuestion (BaseActivity context , final Runnable runOnYesPressed,
                                                 String questionSmall, String questionFull, @Nullable String yesString,
                                                 @Nullable String noString, boolean hideNoButton){


            final Dialog dialog = Utils.Popups.showPopup(R.layout.popup_yes_no, context);
            final View mView = dialog.getWindow().getDecorView();


            TextView noButton = mView.findViewById(R.id.no_button_popup_yes_no);
            Button yesButton = mView.findViewById(R.id.yes_button_popup_yes_no);

            if (hideNoButton){
                noButton.setVisibility(View.GONE);
            }

            if (noString == null){
                noButton.setText("No");
            }else noButton.setText(noString);

            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            if (yesString == null){
                yesButton.setText("Yes");
            }else yesButton.setText(yesString);

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (runOnYesPressed!=null){
                        runOnYesPressed.run();
                    }

                    dialog.dismiss();
                }
            });

            TextView smallQuestionTextView = mView.findViewById(R.id.small_question_text_view_yes_no_popup);
            smallQuestionTextView.setText(questionSmall);

            TextView fullQuestionTextView = mView.findViewById(R.id.full_question_text_view_yes_no_popup);
            fullQuestionTextView.setText(questionFull);

        }
    }

    public static class Subscriptions{
        public static boolean checkIfPurchasedAndIfNotLaunchPurchaseFlow(final HomeActivity  activity,String message){

            final BillingProcessor billingProcessor = References.getInstance(activity).bp;


            if( BillingProcessor.isIabServiceAvailable(activity.getApplicationContext())) {

                if (billingProcessor.isSubscribed("distivity_premium")) {
                    return true;
                } else {
                    Intent intent = new Intent(activity, GetPremiumActivity.class);
                    intent.putExtra(CONSTANTS.MISSING_FEATURE_MESSAGE,message);
                    activity.startActivity(intent);
                    return false;
                }
            }else {
                //Toast.makeText(activity, "Error occurred :((. Your device might be too old for this", Toast.LENGTH_LONG).show();
                return false;
            }

        }

        public static void subscribeToPremium(BillingProcessor billingProcessor, AppCompatActivity activity) {

            if( billingProcessor.isSubscriptionUpdateSupported()) {

                //EVERYTHING's GOOD
                //Launch payment flow
                billingProcessor.subscribe(activity, "distivity_premium");

            }else {
                Toast.makeText(activity, "subscription update is not sported", Toast.LENGTH_SHORT).show();
                //Toast.makeText(activity, "Error occurred :((. Your device might be too old for this", Toast.LENGTH_LONG).show();
            }
        }
    }


    public static class CustomPopups{
        public static void showAddTodoPopup(final HomeActivity context,
                                            final Todo animal, boolean isForToday) {


            final Dialog dialog = Utils.Popups.showPopup(R.layout.add_todo_popup, context);
            final View mView = dialog.getWindow().getDecorView();


            final EditText editText = mView.findViewById(R.id.edit_text_todo_name_add_todo_popup);

            final RadioButton forToday = mView.findViewById(R.id.for_today_radio_group_add_todo_popup);
            final RadioButton forLater = mView.findViewById(R.id.for_later_radio_group_add_todo_popup);

            Button doneButton = mView.findViewById(R.id.done_button_add_todo_popup);

            final ProgressBar progressBar = mView.findViewById(R.id.progress_bar_add_todo_popup);

            EditTexts.requestFocusOnEditText(editText,context);

            if (animal!=null){
                editText.setText(animal.getName());
            }

            if (isForToday){
                forToday.setChecked(true);
                forLater.setChecked(false);
            }else {
                forLater.setChecked(true);
                forToday.setChecked(false);
            }

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
            });


            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    progressBar.setVisibility(View.VISIBLE);


                            if (animal!=null){


                                animal.setName(editText.getText().toString());
                                animal.setChecked(forToday.isChecked());

                                DaoHelper.getInstance(context).addTodosToUpdateForLater(animal,true);
                                InputMethodManager imm = (InputMethodManager) context.
                                        getSystemService(Activity.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                dialog.dismiss();
                                AppExecutors.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Todo Updated (ᵔᴥᵔ)", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                            else {

                                Todo todo=new Todo(DaoHelper.getInstance(context).
                                        getId(context),
                                        ""+editText.getText().toString(),false,-1, forToday.isChecked()
                                );

                                DaoHelper.getInstance(context).addTodoToAddForLater(todo,true);
                                InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                dialog.dismiss();
                                AppExecutors.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context, "Todo added (ᵔᴥᵔ)", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }





                }
            });
        }

        public static void openChoseAppPopup(final HomeActivity mContext, final int Position) {

            final Dialog dialog = Utils.Popups.showPopup(R.layout.chose_app_popup, mContext);
            final View mView = dialog.getWindow().getDecorView();


            RecyclerView recyclerView = mView.findViewById(R.id.recycler_view_chose_app_popup);

            AppsRecyclerViewAdapter appsRecyclerViewAdapter =new AppsRecyclerViewAdapter(
                    Utils.PackageManagerr.getAllInstalledApps(mContext),mContext,Position);

            appsRecyclerViewAdapter.setOnItemClickListener(new AppsRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(MenuActions item) {
                    dialog.dismiss();
                }
            });

            recyclerView.setLayoutManager(new GridLayoutManager(mContext,3));

            recyclerView.setAdapter(appsRecyclerViewAdapter);




        }

            public static void showFeedBackPopup(final BaseActivity context) {
        final Dialog dialog = Utils.Popups.showPopup(R.layout.send_feedback_popup, context);
        final View mView = dialog.getWindow().getDecorView();

        final EditText feedbackEditText =mView.findViewById(R.id.feed_back_edit_text_popup);



        InputMethodManager imm = (InputMethodManager)context.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(feedbackEditText, InputMethodManager.SHOW_IMPLICIT);

        Button sendFeedBackButton = mView.findViewById(R.id.send_feedback_button_popup);
        sendFeedBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("Feedback").push().
                        setValue(feedbackEditText.getText().toString()).
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context,
                                        "Thanks for the feedback ʕ•ᴥ•ʔ", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
            }
        });
    }

        public static void openMathProblemDialog(HomeActivity mContext, final MenuActions app) {
            Dialog dialog = Utils.Popups.showPopup(R.layout.popup_math_problem,mContext);
            View rootView = dialog.getWindow().getDecorView();

            TextView appToOpenTextView = rootView.findViewById(R.id.title_view_popup_math_problem);
            appToOpenTextView.setText(app.getTitle());
            final TextView remainingTextview = rootView.findViewById(R.id.problems_remaining_math_problem);
            final TextView mathProblemTextView = rootView.findViewById(R.id.current_problem_popup_math);
            final EditText resultEditText = rootView.findViewById(R.id.answer_edit_text_popup_math_problem);
            final int[] remainingProblems = {10};
            EditTexts.requestFocusOnEditText(resultEditText,mContext);

            remainingTextview.setText("Problems left : "+ remainingProblems[0]);
            mathProblemTextView.setText(MathProblem.getMathProblem().getProblem());
            resultEditText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (resultEditText.getText().toString().equals(MathProblem.getMathProblem().getCorrectAnswer())){
                        resultEditText.setText("");


                        remainingProblems[0]--;
                        if(remainingProblems[0] ==0){

                            if (app.getToRun()!=null){
                                app.getToRun().run();
                            }

                        }else{


                            remainingTextview.setText("Problems left : "+ remainingProblems[0]);
                            MathProblem.releaseMathProblem();
                            mathProblemTextView.setText(MathProblem.getMathProblem().getProblem());
                        }

                    }
                    return false;
                }
            });



        }
    }

    public class CONSTANTS {
        public static final String MISSING_FEATURE_MESSAGE = "mfm";

        private static final String SCHEME = "package";

        private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";

        private static final String APP_PKG_NAME_22 = "pkg";

        private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";

        private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";


        public class SharedPreferences{
            public static final String  FIRST_OPENED = "FIRST OPENED";
            public static final String SHARED_PREFERENCES_SP_STRING = "SHARED PRES";

            public static final String X_WHITELISTED_APP = "XWA";

            public static final String ISTHEMEDARK = "isthemedark";

            public static final String NUMBER_OF_APPS= "NUMBEROFAPPS";
            public static final String MATH_PROBLEMS_ON = "math_problems_om";


            public static final String ID = "ID";
            public static final String AUTOMATICALLY_DELETE_TODO_ON_CHECK = "DHFNDKFNDKDD ON CH";
            public static final String DELETE_CHECKED_TODOS_ON_CHECK_AGAIN = "DELDET TODOOS AN CHE";
        }
    }

    public static class PackageManagerr{

        public static IntentFilter getAppIntentFilterForReceiver(){
            IntentFilter
            forAppsReceiverIntentFilter = new IntentFilter();
            forAppsReceiverIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            forAppsReceiverIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            forAppsReceiverIntentFilter.addDataScheme("package");

            return forAppsReceiverIntentFilter;
        }

        public static List<MenuActions> getAllInstalledApps(final Context context) {
            List<MenuActions> list = new ArrayList<>();

            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> untreatedAppList = context.getApplicationContext().getPackageManager().queryIntentActivities(intent, 0);

            for(ResolveInfo untreatedApp : untreatedAppList){
                String appName = untreatedApp.activityInfo.loadLabel(context.getPackageManager()).toString();
                final String appPackageName = untreatedApp.activityInfo.packageName;
                Drawable appImage = untreatedApp.activityInfo.loadIcon(context.getPackageManager());

                MenuActions app = new MenuActions(appImage, appName, new Runnable() {
                    @Override
                    public void run() {
                        context.startActivity(context.getPackageManager().getLaunchIntentForPackage(appPackageName));
                    }
                });
                if (!list.contains(app))
                    list.add(app);
            }


            Collections.sort(list, new Comparator<MenuActions>() {
                @Override
                public int compare(MenuActions arg0, MenuActions arg1) {
                    CharSequence name0 = arg0.getTitle();
                    CharSequence name1 = arg1.getTitle();
                    if (name0 == null && name1 == null) {
                        return 0;
                    }
                    if (name0 == null) {
                        return -1;
                    }
                    if (name1 == null) {
                        return 1;
                    }
                    return name0.toString().compareTo(name1.toString());
                }
            });


            return list;
        }

        public static void showInstalledAppDetails(Context context, String packageName) {
            Intent intent = new Intent();
            final int apiLevel = Build.VERSION.SDK_INT;
            if (apiLevel >= 9) { // above 2.3
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts(CONSTANTS.SCHEME, packageName, null);
                intent.setData(uri);
            } else { // below 2.3
                final String appPkgName = (apiLevel == 8 ? CONSTANTS.APP_PKG_NAME_22
                        : CONSTANTS.APP_PKG_NAME_21);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setClassName(CONSTANTS.APP_DETAILS_PACKAGE_NAME,
                        CONSTANTS.APP_DETAILS_CLASS_NAME);
                intent.putExtra(appPkgName, packageName);
            }
            context.startActivity(intent);
        }


        public static String getPackNameByAppName(String name,Context context) {

            PackageManager pm = context.getPackageManager();
            List<ApplicationInfo> l = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            String packName = "";
            for (ApplicationInfo ai : l) {
                String n = (String)pm.getApplicationLabel(ai);
                if (n.equals(name)){
                    packName = ai.packageName;
                }
            }

            return packName;
        }

        public static MenuActions getWhiteListedApp(String prefPackageName, int position, final HomeActivity homeActivity)
                throws PackageManager.NameNotFoundException {
            String packageName= References.getInstance(homeActivity).prefs.getString(prefPackageName,"");

            if (!packageName.equals("")){

                    final ApplicationInfo AppInfo;

                    AppInfo = References.getInstance(homeActivity).packageManager.getApplicationInfo(packageName, 0);

                    return new MenuActions(AppInfo.loadIcon(References.getInstance(homeActivity).packageManager),
                            (String) References.getInstance(homeActivity)
                                    .packageManager.getApplicationLabel(AppInfo), new Runnable() {
                        @Override
                        public void run() {
                            homeActivity.startActivity(References.getInstance(homeActivity).packageManager.
                                    getLaunchIntentForPackage(AppInfo.packageName));
                        }
                    });
            }else {
                //IF APP IS NOT FOUND
                return Utils.getEmptyWhitelistedApp(homeActivity,position);
            }
        }
    }


    public static class EditTexts{
        public static void hideKeyboard(AppCompatActivity activity) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = activity.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(activity);
            }

            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        public static void requestFocusOnEditText(EditText editText, Context mContext) {
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

        public static void clearFocusOnEditText(EditText editText, AppCompatActivity mContext){
            if (editText!=null){
                editText.clearFocus();
                Utils.EditTexts.hideKeyboard(mContext);
            }
        }

        public static void setOnDeleteAndEnterKeyListener
                (EditText editText, final Runnable onDeleteRunnable, final Runnable onEnterRunnable1) {
            editText.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction()==KeyEvent.ACTION_DOWN&&(keyCode == KeyEvent.KEYCODE_ENTER)) {

                        if (onEnterRunnable1!=null){
                            onEnterRunnable1.run();
                        }
                        return true;
                    }
                    if (event.getAction()==KeyEvent.ACTION_DOWN&&(keyCode == KeyEvent.KEYCODE_DEL)) {
                        if (onDeleteRunnable!=null){
                            onDeleteRunnable.run();
                        }
                        return  false;
                    }

                    return false;
                }

            });
        }

    }

    public static class Views{

        public static int getTextColorPrimary(Context context) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = context.getTheme();
            theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);

            return context.obtainStyledAttributes(typedValue.data, new int[]{
                    android.R.attr.textColorPrimary}).getColor(0, -1);
        }

        private static void setPaddingForView(View view,int dp,Context context){

            view.setPadding(getPixelsFromDp(dp,context),getPixelsFromDp(dp,context),getPixelsFromDp(dp,context),getPixelsFromDp(dp,context));
        }

        public static int getPixelsFromDp(int dp,Context context) {
            Resources r = context.getResources();
            int px = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    dp,
                    r.getDisplayMetrics()
            );
            return px;
        }

        public static void hideFabOnRecyclerViewScroll(final RecyclerView todoRecyclerView, final FloatingActionButton addTodoFab) {
            todoRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0){addTodoFab.hide();}
                    else if (dy < 0){
                        addTodoFab.show();
                    }

                }
            });
        }

        public static void unregisterRecyclerView(RecyclerView todoRecyclerView) {
            if (todoRecyclerView!=null){
                todoRecyclerView.setAdapter(null);
            }
        }

        public static void preventSpamClicks(View expandImpandImageView, Runnable runnable,int intervalInMilliseconds) {
            //prevent multiclicks
            try {
                long lastClickTime = (long) expandImpandImageView.getTag();
                if (System.currentTimeMillis() - lastClickTime < intervalInMilliseconds)
                    return;
            } catch (Exception e) {
                expandImpandImageView.setTag(System.currentTimeMillis());
            }
            expandImpandImageView.setTag(System.currentTimeMillis());
            if (runnable!=null)runnable.run();
        }
    }


    public static boolean turnBooleanNegative(boolean monday) {
        if (monday==true)return false;
        else return true;
    }

    public static void rateAppLogic(Context context) {
        Uri uri = Uri.parse("market://details?id=" +context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" +
                            context.getPackageName())));
        }
    }





    }
