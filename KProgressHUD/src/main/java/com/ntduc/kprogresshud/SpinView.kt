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

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

internal class SpinView : AppCompatImageView, Indeterminate {
    private var mRotateDegrees = 0f
    private var mFrameTime = 0
    private var mNeedToUpdateView = false
    private var mUpdateViewRunnable: Runnable? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        setImageResource(R.drawable.kprogresshud_spinner)
        mFrameTime = 1000 / 12
        mUpdateViewRunnable = object : Runnable {
            override fun run() {
                mRotateDegrees += 30f
                mRotateDegrees = if (mRotateDegrees < 360) mRotateDegrees else mRotateDegrees - 360
                invalidate()
                if (mNeedToUpdateView) {
                    postDelayed(this, mFrameTime.toLong())
                }
            }
        }
    }

    override fun setAnimationSpeed(scale: Float) {
        mFrameTime = (1000 / 12 / scale).toInt()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.rotate(mRotateDegrees, (width / 2).toFloat(), (height / 2).toFloat())
        super.onDraw(canvas)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mNeedToUpdateView = true
        post(mUpdateViewRunnable)
    }

    override fun onDetachedFromWindow() {
        mNeedToUpdateView = false
        super.onDetachedFromWindow()
    }
}