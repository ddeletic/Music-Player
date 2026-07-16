package org.fossify.musicplayer.views

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.media3.common.MediaItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import org.fossify.commons.extensions.*
import org.fossify.musicplayer.R
import org.fossify.musicplayer.databinding.ViewCurrentTrackBarBinding
import org.fossify.musicplayer.extensions.*
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.ColorUtils

class CurrentTrackBar(context: Context, attributeSet: AttributeSet) : RelativeLayout(context, attributeSet) {
    private val binding by viewBinding(ViewCurrentTrackBarBinding::bind)

    fun initialize(togglePlayback: () -> Unit) {
        binding.currentTrackPlayPause.setOnClickListener {
            togglePlayback()
        }
    }

    fun updateColors() {
        val backgroundColor = context.getProperBackgroundColor()
        val bottomNavColor = context.getBottomNavigationBackgroundColor()
        val mixedColor = ColorUtils.blendARGB(backgroundColor, bottomNavColor, 0.5f)
        background = mixedColor.toDrawable()

        val textColor = context.getProperTextColor()
        binding.currentTrackTitle.setTextColor(textColor)
        binding.currentTrackAlbum.setTextColor(textColor)
        binding.currentTrackArtist.setTextColor(textColor)
    }

    fun updateCurrentTrack(mediaItem: MediaItem?) {
        val track = mediaItem?.toTrack()
        if (track == null) {
            fadeOut()
            return
        } else {
            fadeIn()
        }

        binding.currentTrackTitle.text = track.title
        binding.currentTrackAlbum.text = track.album
        binding.currentTrackArtist.text = track.artist

        val cornerRadius = resources.getDimension(org.fossify.commons.R.dimen.rounded_corner_radius_small).toInt()
        val currentTrackPlaceholder = resources.getColoredDrawableWithColor(R.drawable.ic_headset, context.getProperTextColor())
        val options = RequestOptions()
            .error(currentTrackPlaceholder)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))

        context.getTrackCoverArt(track) { coverArt ->
            (context as? Activity)?.ensureActivityNotDestroyed {
                Glide.with(this)
                    .load(coverArt)
                    .apply(options)
                    .into(binding.currentTrackImage)
            }
        }
    }

    fun updateTrackState(isPlaying: Boolean) {
        binding.currentTrackPlayPause.updatePlayPauseIcon(isPlaying, context.getProperTextColor())
    }
}
