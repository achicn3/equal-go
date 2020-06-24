package com.local.local.screen.fragment.ui.profile

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


/**
 * Recyclerview 中的每個item間距設定
 * */
class SpaceItemDecoration(
        private val horizontalSpacing: Int,
        private val verticalSpacing: Int
) :
        RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.right = horizontalSpacing
        outRect.left = horizontalSpacing
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = verticalSpacing
        }
        outRect.bottom = verticalSpacing
    }
}