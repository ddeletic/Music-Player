package org.fossify.musicplayer.activities

import android.content.Intent
import org.fossify.commons.activities.BaseSplashActivity

class SplashActivity : BaseSplashActivity() {
    override fun initActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtras(this.intent)

        startActivity(intent)
        finish()
    }
}
