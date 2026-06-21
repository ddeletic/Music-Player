package org.fossify.musicplayer.activities

import android.os.Bundle
import org.fossify.commons.dialogs.FilePickerDialog
import org.fossify.commons.extensions.beVisibleIf
import org.fossify.commons.extensions.getProperTextColor
import org.fossify.commons.extensions.viewBinding
import org.fossify.commons.helpers.NavigationIcon
import org.fossify.commons.interfaces.RefreshRecyclerViewListener
import org.fossify.musicplayer.adapters.IncludedFoldersAdapter
import org.fossify.musicplayer.databinding.ActivityIncludedFoldersBinding
import org.fossify.musicplayer.extensions.config

class IncludedFoldersActivity : SimpleActivity(), RefreshRecyclerViewListener {

    private val binding by viewBinding(ActivityIncludedFoldersBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupEdgeToEdge(padBottomSystem = listOf(binding.includedFoldersList))
        setupMaterialScrollListener(binding.includedFoldersList, binding.includedFoldersAppbar)
        updateFolders()

        binding.includedFoldersFab.setOnClickListener {
            FilePickerDialog(this, pickFile = false, enforceStorageRestrictions = false) { path ->
                config.addIncludedFolder(path)
                updateFolders()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupTopAppBar(binding.includedFoldersAppbar, NavigationIcon.Arrow)
    }

    private fun updateFolders() {
        val folders = config.includedFolders.toMutableList() as ArrayList<String>
        binding.includedFoldersPlaceholder.apply {
            beVisibleIf(folders.isEmpty())
            setTextColor(getProperTextColor())
        }

        val adapter = IncludedFoldersAdapter(this, folders, this, binding.includedFoldersList) {}
        binding.includedFoldersList.adapter = adapter
    }

    override fun refreshItems() {
        updateFolders()
    }
}
