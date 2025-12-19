package org.fossify.musicplayer.dialogs

import android.app.Activity
import org.fossify.commons.extensions.*
import org.fossify.musicplayer.R
import org.fossify.musicplayer.databinding.DialogYesNoBinding

class YesNoDialog(val activity: Activity, val title_string_id: Int, val text: String, val callback: () -> Unit) {
    private val binding by activity.viewBinding(DialogYesNoBinding::inflate)

    init {
        binding.yesNoDialogLabel.text = text

        activity.getAlertDialogBuilder()
            .setPositiveButton(org.fossify.commons.R.string.yes) { _, _ -> dialogConfirmed() }
            .setNegativeButton(org.fossify.commons.R.string.no, null)
            .apply {
                activity.setupDialogStuff(binding.root, this, title_string_id)
            }
    }

    private fun dialogConfirmed() {
        callback()
    }
}
