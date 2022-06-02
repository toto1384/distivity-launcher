package com.distivity.productivitylauncher.Activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.distivity.productivitylauncher.Adapters.ViewPagerAdapter;
import com.distivity.productivitylauncher.BaseActivity;
import com.distivity.productivitylauncher.R;
import com.distivity.productivitylauncher.ThemeManager;


public class WelcomeActivity extends BaseActivity {


//    private void registerUserToDatabase() {
//        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        final DatabaseReference databaseReference = Utils.getUserrReference();
//
//
//        databaseReference.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (!dataSnapshot.exists()){
//                    User userr =
//                            new User(firebaseUser.getUid(),new ArrayList<ReplacementHabitPOJO>());
//
//
//                    databaseReference.child(firebaseUser.getUid()).setValue(userr);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    ViewPager viewPager;
    View dot1,dot2,dot3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.setupTheme(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_welcome);

        init();

        initAnimation();

        pagerLogic();
    }

    private void pagerLogic() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter();

        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i){
                    case 1:setDotsToUnselected();
                    dot1.setBackground(getResources().getDrawable(R.drawable.dot_selected_background));break;
                    case 2:setDotsToUnselected();
                    dot2.setBackground(getResources().getDrawable(R.drawable.dot_selected_background));break;
                    case 3:setDotsToUnselected();
                    dot3.setBackground(getResources().getDrawable(R.drawable.dot_selected_background));break;
                    default:
                        Toast.makeText(WelcomeActivity.this,
                                "View pager pages are outside the boundary(3)",
                                Toast.LENGTH_SHORT).show();
                         break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void setDotsToUnselected() {
        dot1.setBackground(getResources().getDrawable(R.drawable.dot_background));
        dot2.setBackground(getResources().getDrawable(R.drawable.dot_background));
        dot3.setBackground(getResources().getDrawable(R.drawable.dot_background));
    }


    private void initAnimation() {
        dot1.setVisibility(View.GONE);
        TranslateAnimation animation = new TranslateAnimation(dot1.getX()-500, dot1.getY(),
                dot1.getX(), dot1.getY());
        animation.setDuration(1000); // duartion in ms
        animation.setFillAfter(false);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                dot1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        dot1.startAnimation(animation);

        dot3.setVisibility(View.GONE);
        TranslateAnimation animation1 = new TranslateAnimation(dot3.getX()+500, dot3.getY(),
                dot3.getX(), dot3.getY());
        animation1.setDuration(1000); // duartion in ms
        animation1.setFillAfter(false);
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                dot3.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        dot1.startAnimation(animation);
    }



    private void init() {

         viewPager = findViewById(R.id.view_pager_activity_welcome);

         dot1 = findViewById(R.id.dot1);

         dot2 =findViewById(R.id.dot2);

         dot3 = findViewById(R.id.dot3);


    }


}
