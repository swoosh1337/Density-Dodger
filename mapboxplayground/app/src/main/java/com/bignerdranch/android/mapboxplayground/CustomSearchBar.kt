package com.bignerdranch.android.mapboxplayground

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet

class CustomAutoCompleteTextView(context: Context, attributeSet: AttributeSet): androidx.appcompat.widget.AppCompatAutoCompleteTextView(
    context,
    attributeSet
) {
    override fun enoughToFilter(): Boolean {
        super.enoughToFilter()
        return true
    }

    override fun onFocusChanged(
        focused: Boolean, direction: Int,
        previouslyFocusedRect: Rect?
    ) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused && filter != null) {
            performFiltering(text, 0)
        }
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }
}