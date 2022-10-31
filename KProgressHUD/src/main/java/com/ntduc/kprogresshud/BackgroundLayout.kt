/*
 *    Copyright 2015 Kaopiz Software Co., Ltd.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.ntduc.kprogresshud

import android.widget.LinearLayout
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet

internal class BackgroundLayout : LinearLayout {
    private var mCornerRadius = 0f
    private var mBackgroundColor = 0

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        val color = context.resources.getColor(R.color.kprogresshud_default_color)
        initBackground(color, mCornerRadius)
    }

    private fun initBackground(color: Int, cornerRadius: Float) {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.RECTANGLE
        drawable.setColor(color)
        drawable.cornerRadius = cornerRadius
        background = drawable
    }

    fun setCornerRadius(radius: Float) {
        mCornerRadius = Helper.dpToPixel(radius, context).toFloat()
        initBackground(mBackgroundColor, mCornerRadius)
    }

    fun setBaseColor(color: Int) {
        mBackgroundColor = color
        initBackground(mBackgroundColor, mCornerRadius)
    }
}