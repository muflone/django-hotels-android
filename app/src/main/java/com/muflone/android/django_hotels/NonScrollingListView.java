package com.muflone.android.django_hotels;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class NonScrollingListView extends ListView {
    private static final int MAX_HEIGHT = (1 << 30) - 1;

    public NonScrollingListView(Context context) {
        super(context);
    }

    public NonScrollingListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NonScrollingListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,
                View.MeasureSpec.makeMeasureSpec(NonScrollingListView.MAX_HEIGHT, View.MeasureSpec.AT_MOST));
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}