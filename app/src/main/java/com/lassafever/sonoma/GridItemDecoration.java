package com.lassafever.sonoma;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Custom item decoration. Adds a small amount of padding to the left of grid items, and a large amount of padding to the right.
 */
public class GridItemDecoration extends RecyclerView.ItemDecoration {
    private int largePadding;
    private int smallPadding;

    public GridItemDecoration(int largePadding, int smallPadding) {
        this.largePadding = largePadding;
        this.smallPadding = smallPadding;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = smallPadding;
        outRect.right = smallPadding;
        outRect.top = largePadding;
        outRect.bottom = largePadding;
    }
}
