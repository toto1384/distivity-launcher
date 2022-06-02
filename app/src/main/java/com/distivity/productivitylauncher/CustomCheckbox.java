package com.distivity.productivitylauncher;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;

public class CustomCheckbox extends FrameLayout {

    boolean checked =false;

    LottieAnimationView lottieAnimationView;
    ImageView imageView;

    Runnable onAnimationEndRunnable;


    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked ,Runnable onAnimationEndRunnable) {

        this.onAnimationEndRunnable=onAnimationEndRunnable;

        if (this.checked!=checked){
            if (checked){
                lottieAnimationView.setSpeed(4f);
                lottieAnimationView.setVisibility(View.VISIBLE);
                imageView.setVisibility(GONE);
                lottieAnimationView.playAnimation();
            }else {
               lottieAnimationView.setSpeed(-4);
                lottieAnimationView.playAnimation();
                imageView.setVisibility(VISIBLE);
                lottieAnimationView.setVisibility(GONE);
            }
        }



        this.checked = checked;

    }

    public CustomCheckbox(Context context, @Nullable AttributeSet attrs){
       super(context, attrs);
       LayoutInflater.from(context).inflate(R.layout.check_box_pojo, this, true);

        lottieAnimationView = findViewById(R.id.lottie_animation_view_checkbox_pojo);
        imageView = findViewById(R.id.image_view_checkbox_pojo);

        lottieAnimationView.setAnimation("433-checked-done.json");
        lottieAnimationView.setRepeatCount(0);

        lottieAnimationView.setVisibility(GONE);
        imageView.setVisibility(VISIBLE);

        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {


                if (onAnimationEndRunnable!=null){
                    onAnimationEndRunnable.run();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


   }

}
