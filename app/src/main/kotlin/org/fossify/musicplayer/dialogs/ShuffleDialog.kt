package org.fossify.musicplayer.dialogs

import android.app.Activity
import org.fossify.commons.extensions.*
import org.fossify.musicplayer.R
import org.fossify.musicplayer.databinding.DialogShuffleBinding

class ShuffleDialog(val activity: Activity, val callback: () -> Unit) {
    private val binding by activity.viewBinding(DialogShuffleBinding::inflate)

    init {
        activity.getAlertDialogBuilder()
            .setPositiveButton(org.fossify.commons.R.string.yes) { _, _ -> dialogConfirmed() }
            .setNegativeButton(org.fossify.commons.R.string.no, null)
            .apply {
                activity.setupDialogStuff(binding.root, this, R.string.shuffle)
            }
    }

    private fun dialogConfirmed() {
        callback()
    }
}
