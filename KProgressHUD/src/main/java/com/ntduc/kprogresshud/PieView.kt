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
import android.graphics.Color
import android.graphics.Paint
import com.ntduc.kprogresshud.Helper.dpToPixel
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

internal class PieView : View, Determinate {
    private var mWhitePaint: Paint? = null
    private var mGreyPaint: Paint? = null
    private var mBound: RectF? = null
    private var mMax = 100
    private var mProgress = 0

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
        mWhitePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mWhitePaint!!.style = Paint.Style.FILL_AND_STROKE
        mWhitePaint!!.strokeWidth = dpToPixel(0.1f, context).toFloat()
        mWhitePaint!!.color = Color.WHITE
        mGreyPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mGreyPaint!!.style = Paint.Style.STROKE
        mGreyPaint!!.strokeWidth = dpToPixel(2f, context).toFloat()
        mGreyPaint!!.color = Color.WHITE
        mBound = RectF()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val padding = dpToPixel(4f, context)
        mBound!![padding.toFloat(), padding.toFloat(), (w - padding).toFloat()] =
            (h - padding).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val mAngle = mProgress * 360f / mMax
        canvas.drawArc(mBound!!, 270f, mAngle, true, mWhitePaint!!)
        val padding = dpToPixel(4f, context)
        canvas.drawCircle(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            (width / 2 - padding).toFloat(),
            mGreyPaint!!
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val dimension = dpToPixel(40f, context)
        setMeasuredDimension(dimension, dimension)
    }

    override fun setMax(max: Int) {
        mMax = max
    }

    override fun setProgress(progress: Int) {
        mProgress = progress
        invalidate()
    }
}