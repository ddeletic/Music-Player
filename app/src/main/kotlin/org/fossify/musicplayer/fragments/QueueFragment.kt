package org.fossify.musicplayer.fragments

import android.content.Context
import android.util.AttributeSet
import org.fossify.commons.activities.BaseSimpleActivity
import org.fossify.commons.extensions.beGoneIf
import org.fossify.commons.extensions.beVisibleIf
import org.fossify.commons.extensions.normalizeString
import org.fossify.musicplayer.activities.SimpleActivity
import org.fossify.musicplayer.activities.SimpleControllerActivity
import org.fossify.musicplayer.adapters.QueueAdapter
import org.fossify.musicplayer.databinding.FragmentQueueBinding
import org.fossify.musicplayer.extensions.*
import org.fossify.musicplayer.models.Track

class QueueFragment(context: Context, attributeSet: AttributeSet) : MyViewPagerFragment(context, attributeSet) {
    private var tracks = ArrayList<Track>()
    private val binding by viewBinding(FragmentQueueBinding::bind)

    override fun setupFragment(activity: BaseSimpleActivity) {
        (activity as? SimpleControllerActivity)?.withPlayer {
            tracks = currentMediaItemsShuffled.toTracks().toMutableList() as ArrayList<Track>

            activity.runOnUiThread {
                binding.queuePlaceholder.beVisibleIf(tracks.isEmpty())
                val adapter = binding.queueList.adapter
                if (adapter == null) {
                    QueueAdapter(
                        activity = activity as SimpleActivity,
                        items = tracks,
                        currentTrack = currentMediaItem?.toTrack(),
                        recyclerView = binding.queueList
                    ) {
                        activity.withPlayer {
                            val startIndex = currentMediaItems.indexOfTrack(it as Track)
                            seekTo(startIndex, 0)
                            if (!isReallyPlaying) {
                                play()
                            }
                        }
                    }.apply {
                        binding.queueList.adapter = this
                    }
                } else {
                    (adapter as QueueAdapter).updateItems(tracks)
                }
            }
        }
    }

    override fun finishActMode() {
        getAdapter()?.finishActMode()
    }

    override fun onSearchQueryChanged(text: String) {
        val normalizedText = text.normalizeString()
        val filtered = ArrayList(
            tracks.filter { track ->
                val title = track.title.normalizeString()
                val artistAlbum = "${track.artist} - ${track.album}".normalizeString()
                title.contains(normalizedText, ignoreCase = true) ||
                    artistAlbum.contains(normalizedText, ignoreCase = true)
            }
        )
        getAdapter()?.updateItems(filtered, text)
        binding.queuePlaceholder.beVisibleIf(filtered.isEmpty())
    }

    override fun onSearchClosed() {
        getAdapter()?.updateItems(tracks)
        binding.queuePlaceholder.beGoneIf(tracks.isNotEmpty())
    }

    override fun onSortOpen(activity: SimpleActivity) {}

    override fun onShuffle(activity: SimpleActivity) {}

    override fun setupColors(textColor: Int, adjustedPrimaryColor: Int) {
        binding.queuePlaceholder.setTextColor(textColor)
        binding.queueFastscroller.updateColors(adjustedPrimaryColor)
        getAdapter()?.updateColors(textColor)
    }

    override fun updateCurrentTrack() {
        getAdapter()?.updateCurrentTrack()
    }

    override fun onTimelineChanged() {
        (context as? SimpleControllerActivity)?.withPlayer {
            tracks = currentMediaItemsShuffled.toTracks().toMutableList() as ArrayList<Track>
            (context as? SimpleControllerActivity)?.runOnUiThread {
                getAdapter()?.updateItems(tracks)
                binding.queuePlaceholder.beVisibleIf(tracks.isEmpty())
            }
        }
    }

    private fun getAdapter() = binding.queueList.adapter as? QueueAdapter
}
