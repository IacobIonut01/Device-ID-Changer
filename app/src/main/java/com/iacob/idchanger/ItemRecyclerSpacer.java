package com.iacob.idchanger;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ItemRecyclerSpacer extends RecyclerView.ItemDecoration {
    private final int left;
    private final int right;
    private final int top;
    private final int bottom;
    private final int position;

    public ItemRecyclerSpacer(int left, int right, int top, int bottom, int position) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.position = position;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == this.position) {
            if (left != 0)
                outRect.left = left;
            if (right != 0)
                outRect.right = right;
            if (top != 0)
                outRect.top = top;
            if (bottom != 0)
                outRect.bottom = bottom;
        }
    }
}