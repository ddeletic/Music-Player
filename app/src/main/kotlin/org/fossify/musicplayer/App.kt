package org.fossify.musicplayer

import android.app.Application
import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import org.fossify.commons.extensions.checkUseEnglish
import org.fossify.musicplayer.helpers.SimpleMediaController

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        checkUseEnglish()
        syncServiceAlias()
        initController()
    }

    private fun syncServiceAlias() {
        val colors = listOf(
            "Red", "Pink", "Purple", "Deep_purple", "Indigo", "Blue", "Light_blue", "Cyan", "Teal",
            "Green", "Light_green", "Lime", "Yellow", "Amber", "Orange", "Deep_orange", "Brown",
            "Blue_grey", "Grey_black"
        )

        var activeColor = "Green"
        for (color in colors) {
            val aliasName = "$packageName.activities.SplashActivity.$color"
            if (packageManager.getComponentEnabledSetting(ComponentName(this, aliasName)) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                activeColor = color
                break
            }
        }

        for (color in colors) {
            val serviceName = "$packageName.playback.PlaybackService${color.split("_").joinToString("") { it.replaceFirstChar { char -> char.uppercase() } }}"
            val state = if (color == activeColor) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            val componentName = ComponentName(this, serviceName)
            if (packageManager.getComponentEnabledSetting(componentName) != state) {
                packageManager.setComponentEnabledSetting(componentName, state, PackageManager.DONT_KILL_APP)
            }
        }
    }

    private fun initController() {
        SimpleMediaController.getInstance(applicationContext).createControllerAsync()
        ProcessLifecycleOwner.get().lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onStop(owner: LifecycleOwner) {
                    SimpleMediaController.destroyInstance()
                }
            }
        )
    }
}
