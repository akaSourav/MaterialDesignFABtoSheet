package com.scent.fabtosheet;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.view.View;

public class FABtoSheet {
    private FloatingActionButton floatingActionButton;
    private CardView cardView;
    private int radius;
    private int startWidth;
    private int startHeight;
    private int startLeft;
    private int startTop;

    private int endWidth;
    private int endHeight;
    private int endLeft;
    private int endTop;
    private Rect startRect;
    private Rect endRect;



    public FABtoSheet(FloatingActionButton fab, CardView card) {
        this.floatingActionButton = fab;
        this.cardView = card;


        startWidth = floatingActionButton.getWidth();
        startHeight = floatingActionButton.getHeight();
        startLeft = floatingActionButton.getLeft();
        startTop = floatingActionButton.getTop();
        int startRight = floatingActionButton.getRight();
        int startBottom = floatingActionButton.getBottom();
        startRect = new Rect(startLeft, startTop, startRight, startBottom);

        endWidth = cardView.getWidth();
        endHeight = cardView.getHeight();
        endLeft = cardView.getLeft();
        endTop = cardView.getTop();
        int endRight = cardView.getRight();
        int endBottom = cardView.getBottom();
        endRect = new Rect(endLeft, endTop, endRight, endBottom);
        radius = Math.min(endWidth, endHeight)/2;
    }


    public Animator forwardAnimation() {
        cardView.setRadius(radius);
        cardView.setAlpha(0f);
        setRect(cardView, startRect);
        cardView.bringToFront();
        cardView.setVisibility(View.VISIBLE);

        final PointF startPoint = new PointF(startLeft, startTop);
        final PointF controlPoint = getPath(startLeft, startTop, endLeft, endTop);
        final PointF endPoint = new PointF(endLeft, endTop);

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(400);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fraction = valueAnimator.getAnimatedFraction();
                cardView.setAlpha(fraction);
                floatingActionButton.setAlpha(1f-fraction);
                PointF pointF = getPointOnQuad(fraction, startPoint, controlPoint, endPoint);
                Rect currentRect = evaluate(fraction, startWidth, startHeight, endWidth, endHeight, pointF );
                setRect(floatingActionButton, currentRect);
                setRect(cardView, currentRect);
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                floatingActionButton.setVisibility(View.INVISIBLE);
                cardView.setAlpha(1f);
                setRect(cardView, endRect);
                setRect(floatingActionButton, startRect);
            }
        });

        ValueAnimator radiusAnimator = ValueAnimator.ofInt(radius, 4);
        radiusAnimator.setDuration(50);
        radiusAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                cardView.setRadius((int) valueAnimator.getAnimatedValue());
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(valueAnimator, radiusAnimator);

        return animatorSet;
    }

    public Animator reverseAnimation(){
        floatingActionButton.setAlpha(0f);
        setRect(floatingActionButton, endRect);
        floatingActionButton.bringToFront();
        floatingActionButton.setVisibility(View.VISIBLE);

        final PointF startPoint = new PointF(endLeft, endTop);
        final PointF controlPoint = getPath(endLeft, endTop, startLeft, startTop);
        final PointF endPoint = new PointF(startLeft, startTop);

        ValueAnimator radiusAnimator = ValueAnimator.ofInt(4, radius);
        radiusAnimator.setDuration(50);
        radiusAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                cardView.setRadius((int) valueAnimator.getAnimatedValue());
            }
        });

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(400);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fraction = valueAnimator.getAnimatedFraction();
                floatingActionButton.setAlpha(fraction);
                cardView.setAlpha(1f-fraction);
                PointF pointF = getPointOnQuad(fraction, startPoint, controlPoint, endPoint);
                Rect currentRect = evaluate(fraction, endWidth, endHeight, startWidth, startHeight, pointF );
                setRect(floatingActionButton, currentRect);
                setRect(cardView, currentRect);
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cardView.setVisibility(View.INVISIBLE);
                floatingActionButton.setAlpha(1f);
                setRect(floatingActionButton, startRect);
                setRect(cardView, endRect);
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(radiusAnimator, valueAnimator);

        return animatorSet;
    }

    private void setRect(View view, Rect rect){
        view.layout(rect.left, rect.top, rect.right, rect.bottom);
    }

    private Rect evaluate(float fraction, int startWidth, int startHeight, int endWidth, int endHeight, PointF position) {
        int width =startWidth + (int)((endWidth-startWidth)*fraction);
        int height = startHeight + (int)((endHeight-startHeight)*fraction);
        int x = (int) position.x;
        int y = (int) position.y;
        return new Rect(x ,y , x+width, y+height);
    }

    private PointF getPath(float startX, float startY, float endX, float endY) {
        if (startY > endY) {
            return new PointF( endX, startY );
        } else {
            return new PointF( startX, endY );
        }
    }

    private PointF getPointOnQuad(float fraction, PointF startPoint, PointF controlPoint, PointF endPoint){
        float x = (startPoint.x-2*controlPoint.x+endPoint.x)*fraction*fraction + 2*(controlPoint.x-startPoint.x)*fraction + startPoint.x;
        float y = (startPoint.y-2*controlPoint.y+endPoint.y)*fraction*fraction + 2*(controlPoint.y-startPoint.y)*fraction + startPoint.y;
        return new PointF(x,y);
    }
}
