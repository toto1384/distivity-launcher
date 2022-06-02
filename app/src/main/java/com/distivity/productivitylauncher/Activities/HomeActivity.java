package com.distivity.productivitylauncher.Activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.distivity.productivitylauncher.Adapters.AppsRecyclerViewAdapter;
import com.distivity.productivitylauncher.Adapters.OnSwipeTouchListener;
import com.distivity.productivitylauncher.Adapters.TodoRecyclerViewAdapter;
import com.distivity.productivitylauncher.AppExecutors;
import com.distivity.productivitylauncher.BaseActivity;
import com.distivity.productivitylauncher.Database.DaoHelper;
import com.distivity.productivitylauncher.Pojos.CustomRunnable;
import com.distivity.productivitylauncher.Pojos.MenuActions;
import com.distivity.productivitylauncher.Pojos.References;
import com.distivity.productivitylauncher.Pojos.TreeNode;
import com.distivity.productivitylauncher.R;
import com.distivity.productivitylauncher.ThemeManager;
import com.distivity.productivitylauncher.Utils;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class HomeActivity extends BaseActivity {

    TextView dateAndTime;
    RecyclerView todoRecyclerView;

    FloatingActionButton addTodoFab;
    TextView forNowTextView;
    TextView forLaterTextView;
    BottomAppBar bottomAppBar;
    ImageView hintImageView;

    View emptyView;
    ImageView emptyImage;
    TextView emptyText;

    EditText toUnFocusEditText;
    CoordinatorLayout rootView;

    boolean forNowOn = true;


    TodoRecyclerViewAdapter adapter;


    BottomSheetDialog bottomSheetDialog;
    View bottomSheetView;

    RecyclerView appsRecyclerView;
    AppsRecyclerViewAdapter appsRecyclerViewAdapter;
    ImageView infoImageViewBottomSheet;

    FirebaseAuth firebaseAuth;


    final SimpleDateFormat _sdfWatchTime = new SimpleDateFormat("HH:mm , dd MMM yyyy");

    BroadcastReceiver appsReceiver;
    BroadcastReceiver timeUpdatedReceiver;

    private SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;

    public EditText getToUnFocusEditText() {
        return toUnFocusEditText;
    }


    @Override
    protected void onDestroy() {

        adapter.releaseListener();

        if (timeUpdatedReceiver != null) unregisterReceiver(timeUpdatedReceiver);

        if(appsReceiver!=null) unregisterReceiver(appsReceiver);


        References.getPrefs(this).
                unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);

        DaoHelper.getInstance(this).update(null,forNowOn);
        DaoHelper.releaseDaoHelper();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.EditTexts.clearFocusOnEditText(toUnFocusEditText,this);
    }

    public void setToUnFocusEditText(EditText toUnFocusEditText) {
        this.toUnFocusEditText = toUnFocusEditText;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Utils.EditTexts.clearFocusOnEditText(toUnFocusEditText,this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ThemeManager.setupTheme(this);
        setContentView(R.layout.activity_home);



//        if (prefs.getBoolean(Utils.CONSTANTS.SharedPreferences.FIRST_OPENED, true)) {
//
//            startActivity(new Intent(HomeActivity.this, WelcomeActivity.class));
//            prefs.edit().putBoolean(Utils.CONSTANTS.SharedPreferences.FIRST_OPENED, false).apply();
//        }else {

            init();

            //}

    }





    @SuppressLint("ClickableViewAccessibility")
    private void initListeners() {

        onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                initApps();
            }
        };


        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                setVisibilityForBottomAppBarAndFab(!isOpen);
                if (!isOpen&&toUnFocusEditText!=null){
                    toUnFocusEditText.clearFocus();
                }
            }
        });

        bottomAppBar.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
            }

            @Override
            public void onSwipeDown() {
                super.onSwipeDown();
            }

            @Override
            public void onSwipeUp() {
                super.onSwipeUp();
                bottomSheetDialog.show();
            }
        });


        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });

        hintImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.Popups.showHintPopup(HomeActivity.this,getResources().getString(R.string.hint_for_now));
            }
        });


        addTodoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.CustomPopups.showAddTodoPopup(HomeActivity.this,null,forNowOn);
            }
        });

        setBackgroundOnForTvTabLayout(forNowTextView);

        forNowTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forNowOn=true;

                setBackgroundOnForTvTabLayout(forNowTextView);
                emptyText.setText(getResources().getString(R.string.empty_view_string_for_now));
                Utils.EditTexts.hideKeyboard(HomeActivity.this);

                //GET NEW DATA
                AppExecutors.runOnBackgroundThread(new Runnable() {
                    @Override
                    public void run() {
                        DaoHelper.getInstance(HomeActivity.this).getSortedTodos(true, new CustomRunnable<List<TreeNode>>() {
                            @Override
                            public void onCustomRunnableRun(List<TreeNode> data) {
                                adapter.setData(true,data);
                            }
                        });
                    }
                });


            }
        });

        forLaterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forNowOn=false;
                setBackgroundOnForTvTabLayout((forLaterTextView));
                emptyText.setText(getResources().getString(R.string.empty_view_string_for_later));
                Utils.EditTexts.hideKeyboard(HomeActivity.this);

                //GET NEW DATA
                AppExecutors.runOnBackgroundThread(new Runnable() {
                    @Override
                    public void run() {
                        DaoHelper.getInstance(HomeActivity.this).getSortedTodos(false, new CustomRunnable<List<TreeNode>>() {
                            @Override
                            public void onCustomRunnableRun(List<TreeNode> data) {
                                adapter.setData(false,data);
                            }
                        });
                    }
                });

            }
        });

        infoImageViewBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.Popups.showHintPopup(HomeActivity.this,getResources().getString(R.string.bottom_sheet_hint));
            }
        });


         //THEME MANAGER

        Drawable drawable = bottomAppBar.getNavigationIcon();

        Drawable overFlowIcon =  bottomAppBar.getOverflowIcon();

        if (ThemeManager.isNight(this)){

            bottomAppBar.setBackgroundTint(ColorStateList.valueOf(getResources().getColor(R.color.blackLighter)));
            drawable.setColorFilter(getResources().getColor(R.color.theWhitest), PorterDuff.Mode.SRC_IN);

            overFlowIcon.setColorFilter(getResources().getColor(R.color.theWhitest), PorterDuff.Mode.SRC_IN);

            emptyImage.setImageResource(R.drawable.ic_empty_illustration);

        }else {
            bottomAppBar.setBackgroundTint(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
            drawable.setColorFilter(getResources().getColor(R.color.black161616), PorterDuff.Mode.SRC_IN);

            overFlowIcon.setColorFilter(getResources().getColor(R.color.black161616), PorterDuff.Mode.SRC_IN);

            emptyImage.setImageResource(R.drawable.ic_empty_illustration_dark);
        }


    }


    private void initReceivers(){
        //RECEIVERS
        appsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initApps();
            }
        };

        timeUpdatedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0)
                    dateAndTime.setText(_sdfWatchTime.format(new Date()));
            }
        };
        registerReceiver(timeUpdatedReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        registerReceiver(appsReceiver,Utils.PackageManagerr.getAppIntentFilterForReceiver());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!References.getInstance(this).bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    public void initApps() {

        List<MenuActions> whiteListedApps = new ArrayList<>();
        List<MenuActions> nonWhiteListedApps = new ArrayList<>();


        for (int i = 0; i<References.getInstance(this).prefs.getInt(Utils.CONSTANTS.SharedPreferences.NUMBER_OF_APPS,5);i++){
            try {
                whiteListedApps.add(Utils.PackageManagerr.getWhiteListedApp
                        (Utils.CONSTANTS.SharedPreferences.X_WHITELISTED_APP+i,i,this));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        for (MenuActions menuActions:Utils.PackageManagerr.getAllInstalledApps(this)){
            if (!whiteListedApps.contains(menuActions)){
                nonWhiteListedApps.add(menuActions);
            }
        }

        appsRecyclerViewAdapter.setData(whiteListedApps,nonWhiteListedApps);
    }



    private void init() {

        initReceivers();


        firebaseAuth = FirebaseAuth.getInstance();

        rootView = findViewById(R.id.root_view_activity_home);

        emptyImage=findViewById(R.id.image_view_empty_view_activity_home);
        emptyView=findViewById(R.id.emptyViewLinearLayout);
        emptyText=findViewById(R.id.empty_text_home);

        hintImageView=findViewById(R.id.for_now_later_hint_activity_home);

        dateAndTime = findViewById(R.id.date_time_activity_home);
        todoRecyclerView = findViewById(R.id.recycler_view_activity_home);

        addTodoFab = findViewById(R.id.fab_add_todo_activity_home);

        forNowTextView = findViewById(R.id.for_now_text_view_activity_home);
        forLaterTextView = findViewById(R.id.for_later_text_view_activity_home);

        bottomAppBar = findViewById(R.id.bottom_app_bar_activity_home);
        setSupportActionBar(bottomAppBar);

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet,null);
        bottomSheetDialog.setContentView(bottomSheetView);

        appsRecyclerView = bottomSheetView.findViewById(R.id.apps_recycler_view_bottom_sheet);
        infoImageViewBottomSheet = bottomSheetView.findViewById(R.id.info_button_bottom_sheet);

        appsRecyclerViewAdapter = new AppsRecyclerViewAdapter
                (HomeActivity.this);

        appsRecyclerView.setLayoutManager(appsRecyclerViewAdapter.getAppsLayoutManager());
        appsRecyclerView.setAdapter(appsRecyclerViewAdapter);
        appsRecyclerViewAdapter.setOnItemClickListener(new AppsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MenuActions item) {
                bottomSheetDialog.hide();
            }
        });


        initTodoRecyclerView();

        initListeners();

        initApps();

        References.getPrefs(this).registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    private void initTodoRecyclerView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        todoRecyclerView.setLayoutManager(layoutManager);


        adapter = new TodoRecyclerViewAdapter(HomeActivity.this,
                layoutManager,forNowOn){
            @Override
            public void onDataUpdated(int dataSize) {
                if (forNowOn){
                    appsRecyclerViewAdapter.setWorkMode(dataSize!=0);
                }
                //todo todos updated but item size no

                if (dataSize==0){
                    emptyView.setVisibility(View.VISIBLE);
                    todoRecyclerView.setVisibility(View.GONE);
                }else {
                    emptyView.setVisibility(View.GONE);
                    todoRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        };

        DaoHelper.getInstance(this).setOnDataBaseUpdate(new CustomRunnable<List<TreeNode>>() {
            @Override
            public void onCustomRunnableRun(List<TreeNode> data) {
                adapter.setData(true,data);
            }
        });



        todoRecyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_app_bar_menu,menu);

        MenuItem item=menu.getItem(4); // here itemIndex is int
        if (firebaseAuth==null){
            item.setTitle("Log in");
        }else {
            item.setTitle("Log off");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {


        switch (item.getItemId()){

            case R.id.app_bar_settings:startActivity(new Intent(this, SettingsActivity.class));break;

            case R.id.app_bar_dark_mode:if (ThemeManager.isNight(this))
            {ThemeManager.turnThemeLight(this);}
            else {ThemeManager.turnThemeDark(this);}break;

            case R.id.app_bar_rate_app:Utils.rateAppLogic(this);break;
            case R.id.app_bar_send_feedback:Utils.CustomPopups.showFeedBackPopup(this);break;

            case R.id.log_in_off_app_bar:Utils.Popups.displayYesNoQuestion(this, new Runnable() {
                        @Override
                        public void run() {
                            if (firebaseAuth.getCurrentUser()!=null){
                                firebaseAuth.signOut();
                                item.setTitle("Log in");
                            }else {
                                item.setTitle("Log off");
                                startActivity(new Intent(HomeActivity.this, WelcomeActivity.class));
                            }
                        }
                    },"Log out?",
                    "Are you sure you want to log out? You will" +
                            " lose the ability to sync across devices and to cloud save !",
                    "Log out!",null,false);break;
        }

        return true;
    }

    public void setVisibilityForBottomAppBarAndFab(boolean show){
        if (show){
            bottomAppBar.setVisibility(View.VISIBLE);
            addTodoFab.show();
        }else {
            addTodoFab.hide();
            bottomAppBar.setVisibility(View.GONE);
        }
    }


    private void setBackgroundOnForTvTabLayout(TextView toTurnOn){

        forNowTextView.setBackground(null);

        forLaterTextView.setBackground(null);

        forLaterTextView.setTextColor(Utils.Views.getTextColorPrimary(this));
        forNowTextView.setTextColor(Utils.Views.getTextColorPrimary(this));

        toTurnOn.setBackground(getResources().getDrawable(R.drawable.buttons_background));
        toTurnOn.setTextColor(getResources().getColor(R.color.colorWhite));
    }



}
