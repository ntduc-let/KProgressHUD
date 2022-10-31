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

import android.app.Dialog
import android.content.Context
import com.ntduc.kprogresshud.Helper.dpToPixel
import android.content.DialogInterface
import android.graphics.Color
import android.widget.TextView
import android.widget.FrameLayout
import android.os.Bundle
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.*
import java.lang.RuntimeException

class KProgressHUD(context: Context) {
    enum class Style {
        SPIN_INDETERMINATE, PIE_DETERMINATE, ANNULAR_DETERMINATE, BAR_DETERMINATE
    }

    // To avoid redundant APIs, make the HUD as a wrapper class around a Dialog
    private val mProgressDialog: ProgressDialog?
    private var mDimAmount: Float
    private var mWindowColor: Int
    private var mCornerRadius: Float
    private val mContext: Context?
    private var mAnimateSpeed: Int
    private var mMaxProgress = 0
    private var mIsAutoDismiss: Boolean
    private var mGraceTimeMs: Int
    private var mGraceTimer: Handler? = null
    private var mFinished: Boolean

    /**
     * Specify the HUD style (not needed if you use a custom view)
     * @param style One of the KProgressHUD.Style values
     * @return Current HUD
     */
    fun setStyle(style: Style): KProgressHUD {
        val view: View = when (style) {
            Style.SPIN_INDETERMINATE -> SpinView(
                mContext!!
            )
            Style.PIE_DETERMINATE -> PieView(mContext)
            Style.ANNULAR_DETERMINATE -> AnnularView(
                mContext!!
            )
            Style.BAR_DETERMINATE -> BarView(mContext)
        }
        mProgressDialog?.setView(view)
        return this
    }

    /**
     * Specify the dim area around the HUD, like in Dialog
     * @param dimAmount May take value from 0 to 1. Default to 0 (no dimming)
     * @return Current HUD
     */
    fun setDimAmount(dimAmount: Float): KProgressHUD {
        if (dimAmount in 0.0..1.0) {
            mDimAmount = dimAmount
        }
        return this
    }

    /**
     * Set HUD size. If not the HUD view will use WRAP_CONTENT instead
     * @param width in dp
     * @param height in dp
     * @return Current HUD
     */
    fun setSize(width: Int, height: Int): KProgressHUD {
        mProgressDialog?.setSize(width, height)
        return this
    }

    /**
     * @param color ARGB color
     * @return Current HUD
     */
    @Deprecated(
        """As of release 1.1.0, replaced by {@link #setBackgroundColor(int)}
      """
    )
    fun setWindowColor(color: Int): KProgressHUD {
        mWindowColor = color
        return this
    }

    /**
     * Specify the HUD background color
     * @param color ARGB color
     * @return Current HUD
     */
    fun setBackgroundColor(color: Int): KProgressHUD {
        mWindowColor = color
        return this
    }

    /**
     * Specify corner radius of the HUD (default is 10)
     * @param radius Corner radius in dp
     * @return Current HUD
     */
    fun setCornerRadius(radius: Float): KProgressHUD {
        mCornerRadius = radius
        return this
    }

    /**
     * Change animation speed relative to default. Used with indeterminate style
     * @param scale Default is 1. If you want double the speed, set the param at 2.
     * @return Current HUD
     */
    fun setAnimationSpeed(scale: Int): KProgressHUD {
        mAnimateSpeed = scale
        return this
    }

    /**
     * Optional label to be displayed.
     * @return Current HUD
     */
    fun setLabel(label: String?): KProgressHUD {
        mProgressDialog?.setLabel(label)
        return this
    }

    /**
     * Optional label to be displayed
     * @return Current HUD
     */
    fun setLabel(label: String?, color: Int): KProgressHUD {
        mProgressDialog?.setLabel(label, color)
        return this
    }

    /**
     * Optional detail description to be displayed on the HUD
     * @return Current HUD
     */
    fun setDetailsLabel(detailsLabel: String?): KProgressHUD {
        mProgressDialog?.setDetailsLabel(detailsLabel)
        return this
    }

    /**
     * Optional detail description to be displayed
     * @return Current HUD
     */
    fun setDetailsLabel(detailsLabel: String?, color: Int): KProgressHUD {
        mProgressDialog?.setDetailsLabel(detailsLabel, color)
        return this
    }

    /**
     * Max value for use in one of the determinate styles
     * @return Current HUD
     */
    fun setMaxProgress(maxProgress: Int): KProgressHUD {
        mMaxProgress = maxProgress
        return this
    }

    /**
     * Set current progress. Only have effect when use with a determinate style, or a custom
     * view which implements Determinate interface.
     */
    fun setProgress(progress: Int) {
        mProgressDialog?.setProgress(progress)
    }

    /**
     * Provide a custom view to be displayed.
     * @param view Must not be null
     * @return Current HUD
     */
    fun setCustomView(view: View?): KProgressHUD {
        if (view != null) {
            mProgressDialog?.setView(view)
        } else {
            throw RuntimeException("Custom view must not be null!")
        }
        return this
    }

    /**
     * Specify whether this HUD can be cancelled by using back button (default is false)
     *
     * Setting a cancelable to true with this method will set a null callback,
     * clearing any callback previously set with
     * [.setCancellable]
     *
     * @return Current HUD
     */
    fun setCancellable(isCancellable: Boolean): KProgressHUD {
        mProgressDialog?.setCancelable(isCancellable)
        mProgressDialog?.setOnCancelListener(null)
        return this
    }

    /**
     * Specify a callback to run when using the back button (default is null)
     *
     * @param listener The code that will run if the user presses the back
     * button. If you pass null, the dialog won't be cancellable, just like
     * if you had called [.setCancellable] passing false.
     *
     * @return Current HUD
     */
    fun setCancellable(listener: DialogInterface.OnCancelListener?): KProgressHUD {
        mProgressDialog?.setCancelable(null != listener)
        mProgressDialog?.setOnCancelListener(listener)
        return this
    }

    /**
     * Specify whether this HUD closes itself if progress reaches max. Default is true.
     * @return Current HUD
     */
    fun setAutoDismiss(isAutoDismiss: Boolean): KProgressHUD {
        mIsAutoDismiss = isAutoDismiss
        return this
    }

    /**
     * Grace period is the time (in milliseconds) that the invoked method may be run without
     * showing the HUD. If the task finishes before the grace time runs out, the HUD will
     * not be shown at all.
     * This may be used to prevent HUD display for very short tasks.
     * Defaults to 0 (no grace time).
     * @param graceTimeMs Grace time in milliseconds
     * @return Current HUD
     */
    fun setGraceTime(graceTimeMs: Int): KProgressHUD {
        mGraceTimeMs = graceTimeMs
        return this
    }

    fun show(): KProgressHUD {
        if (!isShowing) {
            mFinished = false
            if (mGraceTimeMs == 0) {
                mProgressDialog?.show()
            } else {
                mGraceTimer = Handler(Looper.getMainLooper())
                mGraceTimer!!.postDelayed({
                    if (mProgressDialog != null && !mFinished) {
                        mProgressDialog.show()
                    }
                }, mGraceTimeMs.toLong())
            }
        }
        return this
    }

    val isShowing: Boolean
        get() = mProgressDialog != null && mProgressDialog.isShowing

    fun dismiss() {
        mFinished = true
        if (mContext != null && mProgressDialog != null && mProgressDialog.isShowing) {
            mProgressDialog.dismiss()
        }
        if (mGraceTimer != null) {
            mGraceTimer!!.removeCallbacksAndMessages(null)
            mGraceTimer = null
        }
    }

    private inner class ProgressDialog(context: Context?) : Dialog(
        context!!
    ) {
        private var mDeterminateView: Determinate? = null
        private var mIndeterminateView: Indeterminate? = null
        private var mView: View? = null
        private var mLabelText: TextView? = null
        private var mDetailsText: TextView? = null
        private var mLabel: String? = null
        private var mDetailsLabel: String? = null
        private var mCustomViewContainer: FrameLayout? = null
        private var mBackgroundLayout: BackgroundLayout? = null
        private var mWidth = 0
        private var mHeight = 0
        private var mLabelColor = Color.WHITE
        private var mDetailColor = Color.WHITE
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.kprogresshud_hud)
            val window = window
            window!!.setBackgroundDrawable(ColorDrawable(0))
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            val layoutParams = window.attributes
            layoutParams.dimAmount = mDimAmount
            layoutParams.gravity = Gravity.CENTER
            window.attributes = layoutParams
            setCanceledOnTouchOutside(false)
            initViews()
        }

        private fun initViews() {
            mBackgroundLayout = findViewById<View>(R.id.background) as BackgroundLayout
            mBackgroundLayout!!.setBaseColor(mWindowColor)
            mBackgroundLayout!!.setCornerRadius(mCornerRadius)
            if (mWidth != 0) {
                updateBackgroundSize()
            }
            mCustomViewContainer = findViewById<View>(R.id.container) as FrameLayout
            addViewToFrame(mView)
            if (mDeterminateView != null) {
                mDeterminateView!!.setMax(mMaxProgress)
            }
            if (mIndeterminateView != null) {
                mIndeterminateView!!.setAnimationSpeed(mAnimateSpeed.toFloat())
            }
            mLabelText = findViewById<View>(R.id.label) as TextView
            setLabel(mLabel, mLabelColor)
            mDetailsText = findViewById<View>(R.id.details_label) as TextView
            setDetailsLabel(mDetailsLabel, mDetailColor)
        }

        private fun addViewToFrame(view: View?) {
            if (view == null) return
            val wrapParam = ViewGroup.LayoutParams.WRAP_CONTENT
            val params = ViewGroup.LayoutParams(wrapParam, wrapParam)
            mCustomViewContainer!!.addView(view, params)
        }

        private fun updateBackgroundSize() {
            val params = mBackgroundLayout!!.layoutParams
            params.width = dpToPixel(mWidth.toFloat(), context)
            params.height = dpToPixel(mHeight.toFloat(), context)
            mBackgroundLayout!!.layoutParams = params
        }

        fun setProgress(progress: Int) {
            if (mDeterminateView != null) {
                mDeterminateView!!.setProgress(progress)
                if (mIsAutoDismiss && progress >= mMaxProgress) {
                    dismiss()
                }
            }
        }

        fun setView(view: View?) {
            if (view != null) {
                if (view is Determinate) {
                    mDeterminateView = view
                }
                if (view is Indeterminate) {
                    mIndeterminateView = view
                }
                mView = view
                if (isShowing) {
                    mCustomViewContainer!!.removeAllViews()
                    addViewToFrame(view)
                }
            }
        }

        fun setLabel(label: String?) {
            mLabel = label
            if (mLabelText != null) {
                if (label != null) {
                    mLabelText!!.text = label
                    mLabelText!!.visibility = View.VISIBLE
                } else {
                    mLabelText!!.visibility = View.GONE
                }
            }
        }

        fun setDetailsLabel(detailsLabel: String?) {
            mDetailsLabel = detailsLabel
            if (mDetailsText != null) {
                if (detailsLabel != null) {
                    mDetailsText!!.text = detailsLabel
                    mDetailsText!!.visibility = View.VISIBLE
                } else {
                    mDetailsText!!.visibility = View.GONE
                }
            }
        }

        fun setLabel(label: String?, color: Int) {
            mLabel = label
            mLabelColor = color
            if (mLabelText != null) {
                if (label != null) {
                    mLabelText!!.text = label
                    mLabelText!!.setTextColor(color)
                    mLabelText!!.visibility = View.VISIBLE
                } else {
                    mLabelText!!.visibility = View.GONE
                }
            }
        }

        fun setDetailsLabel(detailsLabel: String?, color: Int) {
            mDetailsLabel = detailsLabel
            mDetailColor = color
            if (mDetailsText != null) {
                if (detailsLabel != null) {
                    mDetailsText!!.text = detailsLabel
                    mDetailsText!!.setTextColor(color)
                    mDetailsText!!.visibility = View.VISIBLE
                } else {
                    mDetailsText!!.visibility = View.GONE
                }
            }
        }

        fun setSize(width: Int, height: Int) {
            mWidth = width
            mHeight = height
            if (mBackgroundLayout != null) {
                updateBackgroundSize()
            }
        }
    }

    companion object {
        /**
         * Create a new HUD. Have the same effect as the constructor.
         * For convenient only.
         * @param context Activity context that the HUD bound to
         * @return An unique HUD instance
         */
        fun create(context: Context): KProgressHUD {
            return KProgressHUD(context)
        }

        /**
         * Create a new HUD. specify the HUD style (if you use a custom view, you need `KProgressHUD.create(Context context)`).
         *
         * @param context Activity context that the HUD bound to
         * @param style One of the KProgressHUD.Style values
         * @return An unique HUD instance
         */
        fun create(context: Context, style: Style): KProgressHUD {
            return KProgressHUD(context).setStyle(style)
        }
    }

    init {
        mContext = context
        mProgressDialog = ProgressDialog(context)
        mDimAmount = 0f
        mWindowColor = context.resources.getColor(R.color.kprogresshud_default_color)
        mAnimateSpeed = 1
        mCornerRadius = 10f
        mIsAutoDismiss = true
        mGraceTimeMs = 0
        mFinished = false
        setStyle(Style.SPIN_INDETERMINATE)
    }
}