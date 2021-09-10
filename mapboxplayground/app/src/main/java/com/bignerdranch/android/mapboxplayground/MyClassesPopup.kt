package com.bignerdranch.android.mapboxplayground

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.graphics.ColorUtils
import kotlinx.android.synthetic.main.activity_my_classes.*

class MyClassesPopup : AppCompatActivity() {

    private lateinit var okBtn: Button
    private lateinit var addBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_classes)

        initializeUIElements()

        // Fade animation for the background of Popup Window
        val alpha = 100 //between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, alphaColor)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            popup_window_background.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()

        // Fade animation for the Popup Window
        popup_window_view_with_border.alpha = 0f
        popup_window_view_with_border.animate().alpha(1f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()

    }

    override fun onBackPressed() {
        // Fade animation for the background of Popup Window when you press the back button
        val alpha = 100 // between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), alphaColor, Color.TRANSPARENT)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            popup_window_background.setBackgroundColor(
                animator.animatedValue as Int
            )
        }

        // Fade animation for the Popup Window when you press the back button
        popup_window_view_with_border.animate().alpha(0f).setDuration(500).setInterpolator(
            DecelerateInterpolator()
        ).start()

        // After animation finish, close the Activity
        colorAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                finish()
                overridePendingTransition(0, 0)
            }
        })
        colorAnimation.start()
    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_BUILDING_INFO) {
            val id = data?.getStringExtra(DIRECTIONS_REQUESTED_FOR_ID).toString()
            setDirectionsRequested(id)
        }
    }


    @SuppressLint("ResourceType")
    fun initializeUIElements(){
        okBtn = findViewById(R.id.popup_window_button)
        okBtn.setOnClickListener {
            onBackPressed()
        }

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "My Classes"

        var firstClassroom = ""
        var secondClassroom = ""
        var thirdClassroom = ""

        addBtn = findViewById(R.id.add_card_view)
        addBtn.setOnClickListener {
            val newCardView = CardView(this) // creating CardView
            newCardView.layoutParams = class1.layoutParams //setting params like 1st CardView
            root.addView(newCardView, root.childCount - 1) // adding cardView to LinearLayout at the second to last position
        }
        class1Btn.setOnClickListener{
            firstClassroom = room1Text.text.toString()
            room1Text.setText(firstClassroom)
            firstClassroom = firstTwo(firstClassroom)
            setDirectionsRequested(firstClassroom)
        }

        class2Btn.setOnClickListener{
            secondClassroom = room1Text.text.toString()
            room1Text.setText(secondClassroom)
            secondClassroom = firstTwo(secondClassroom)
            setDirectionsRequested(secondClassroom)
        }

        class3Btn.setOnClickListener{
            thirdClassroom = room1Text.text.toString()
            room1Text.setText(thirdClassroom)
            thirdClassroom = firstTwo(thirdClassroom)
            setDirectionsRequested(thirdClassroom)
        }
    }

    private fun setDirectionsRequested(buildingId: String) {
        val data = Intent().apply {
            putExtra(DIRECTIONS_REQUESTED_FOR_ID, buildingId)
        }
        setResult(Activity.RESULT_OK, data)
        this.finish()
    }

    private fun firstTwo(str: String): String {
        return if (str.length < 2) str else str.substring(0, 2)
    }

    companion object {
        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, MyClassesPopup::class.java)
        }
    }
}