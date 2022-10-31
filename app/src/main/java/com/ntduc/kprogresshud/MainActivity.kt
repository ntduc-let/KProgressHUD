/*
 * Copyright (c) 2015 Kaopiz Software Co., Ltd
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ntduc.kprogresshud

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val indeterminate = findViewById<View>(R.id.indeterminate) as Button
        indeterminate.setOnClickListener(this)
        val labelIndeterminate = findViewById<View>(R.id.label_indeterminate) as Button
        labelIndeterminate.setOnClickListener(this)
        val detailIndeterminate = findViewById<View>(R.id.detail_indeterminate) as Button
        detailIndeterminate.setOnClickListener(this)
        val graceIndeterminate = findViewById<View>(R.id.grace_indeterminate) as Button
        graceIndeterminate.setOnClickListener(this)
        val determinate = findViewById<View>(R.id.determinate) as Button
        determinate.setOnClickListener(this)
        val annularDeterminate = findViewById<View>(R.id.annular_determinate) as Button
        annularDeterminate.setOnClickListener(this)
        val barDeterminate = findViewById<View>(R.id.bar_determinate) as Button
        barDeterminate.setOnClickListener(this)
        val customView = findViewById<View>(R.id.custom_view) as Button
        customView.setOnClickListener(this)
        val dimBackground = findViewById<View>(R.id.dim_background) as Button
        dimBackground.setOnClickListener(this)
        val customColor = findViewById<View>(R.id.custom_color_animate) as Button
        customColor.setOnClickListener(this)
    }

    private var hud: KProgressHUD? = null
    override fun onClick(v: View) {
        when (v.id) {
            R.id.indeterminate -> {
                hud = KProgressHUD.create(this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                scheduleDismiss()
            }
            R.id.label_indeterminate -> {
                hud = KProgressHUD.create(this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .setCancellable {
                        Toast.makeText(
                            this@MainActivity, "You " +
                                    "cancelled manually!", Toast.LENGTH_SHORT
                        ).show()
                    }
                scheduleDismiss()
            }
            R.id.detail_indeterminate -> {
                hud = KProgressHUD.create(this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Please wait")
                    .setDetailsLabel("Downloading data")
                scheduleDismiss()
            }
            R.id.grace_indeterminate -> {
                hud = KProgressHUD.create(this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setGraceTime(1000)
                scheduleDismiss()
            }
            R.id.determinate -> {
                hud = KProgressHUD.create(this@MainActivity)
                    .setStyle(KProgressHUD.Style.PIE_DETERMINATE)
                    .setLabel("Please wait")
                simulateProgressUpdate()
            }
            R.id.annular_determinate -> {
                hud = KProgressHUD.create(this@MainActivity)
                    .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                    .setLabel("Please wait")
                    .setDetailsLabel("Downloading data")
                simulateProgressUpdate()
            }
            R.id.bar_determinate -> {
                hud = KProgressHUD.create(this@MainActivity)
                    .setStyle(KProgressHUD.Style.BAR_DETERMINATE)
                    .setLabel("Please wait")
                simulateProgressUpdate()
            }
            R.id.custom_view -> {
                val imageView = ImageView(this)
                imageView.setBackgroundResource(R.drawable.spin_animation)
                val drawable = imageView.background as AnimationDrawable
                drawable.start()
                hud = KProgressHUD.create(this)
                    .setCustomView(imageView)
                    .setLabel("This is a custom view")
                scheduleDismiss()
            }
            R.id.dim_background -> {
                hud = KProgressHUD.create(this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setDimAmount(0.5f)
                scheduleDismiss()
            }
            R.id.custom_color_animate -> {
                hud = KProgressHUD.create(this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setWindowColor(resources.getColor(R.color.colorPrimary))
                    .setAnimationSpeed(2)
                scheduleDismiss()
            }
        }
        hud?.show()
    }

    private fun simulateProgressUpdate() {
        hud?.setMaxProgress(100)
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            var currentProgress = 0
            override fun run() {
                currentProgress += 1
                hud?.setProgress(currentProgress)
                if (currentProgress == 80) {
                    hud?.setLabel("Almost finish...")
                }
                if (currentProgress < 100) {
                    handler.postDelayed(this, 50)
                }
            }
        }, 100)
    }

    private fun scheduleDismiss() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ hud?.dismiss() }, 2000)
    }
}