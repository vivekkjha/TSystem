package com.vivek.tsystem.helper.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;

public class SquareLayout extends LinearLayout {
    // Desired width-to-height ratio - 1.0 for square
    private final double mScale = 1.0;

    public SquareLayout(Context context) {
        super(context);
    }

    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(widthMeasureSpec);
        int w1 = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int h1 = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        final ViewParent parent = getParent();
        if (parent != null) {
            if (parent instanceof ViewGroup) {
                final ViewGroup g = (ViewGroup) parent;
                if (w1 < g.getWidth() || h1 < g.getHeight()) {
                    w1 = MeasureSpec.makeMeasureSpec(g.getWidth(), MeasureSpec.EXACTLY);
                    h1 = MeasureSpec.makeMeasureSpec(g.getHeight(), MeasureSpec.EXACTLY);
                }
            }
        }
        super.onMeasure(
                w1,
                h1
        );
    }
}