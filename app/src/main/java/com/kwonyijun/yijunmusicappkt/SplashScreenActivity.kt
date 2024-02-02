package com.kwonyijun.yijunmusicappkt

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val splashImageView: ImageView = findViewById(R.id.splashImageView)
        // fade in animation
        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.duration= 2000
        fadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {}
            override fun onAnimationEnd(p0: Animation?) {
                val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            override fun onAnimationRepeat(p0: Animation?) {}
        })

        splashImageView.startAnimation(fadeIn)
        splashImageView.visibility = ImageView.VISIBLE
    }
}