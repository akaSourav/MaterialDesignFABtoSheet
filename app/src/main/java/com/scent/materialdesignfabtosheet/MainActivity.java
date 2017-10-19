package com.scent.materialdesignfabtosheet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;

import com.scent.fabtosheet.FABtoSheet;

public class MainActivity extends AppCompatActivity {
    private FABtoSheet fabToSheet;
    boolean isCardVisible= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = findViewById(R.id.fab);
        final CardView cardView = findViewById(R.id.cardView);

        ViewTreeObserver viewTreeObserver = findViewById(R.id.rootView).getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                fabToSheet = new FABtoSheet(fab, cardView);

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isCardVisible) {
                    Animator animator = fabToSheet.forwardAnimation();
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            isCardVisible = true;
                        }
                    });
                    animator.start();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(isCardVisible){
            Animator animator = fabToSheet.reverseAnimation();
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    isCardVisible = false;
                }
            });
            animator.start();
        } else {
            super.onBackPressed();
        }
    }
}
