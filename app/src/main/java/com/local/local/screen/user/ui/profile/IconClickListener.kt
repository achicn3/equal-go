package com.local.local.screen.user.ui.profile

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView


open class IconClickListener(
        context: Context,
        recyclerView: RecyclerView,
        listener: OnItemClickListener
) : RecyclerView.OnItemTouchListener {
    private var mListener: OnItemClickListener? = listener
    private var gestureDetector: GestureDetector

    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    init {
        val simpleOnGestureListener = object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                e ?: return false
                val child = recyclerView.findChildViewUnder(e.x, e.y) ?: return false
                val mListener = mListener ?: return false
                mListener.onItemClick(child, recyclerView.getChildAdapterPosition(child))
                return true
            }
        }
        gestureDetector = GestureDetector(context, simpleOnGestureListener)
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val childView: View? = rv.findChildViewUnder(e.x, e.y)
        if (childView != null && mListener != null && gestureDetector.onTouchEvent(e)) {
            mListener?.onItemClick(childView, rv.getChildAdapterPosition(childView))
            return true
        }
        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }
}