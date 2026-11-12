package com.leantv.launcher.presentation.home.viewholder

import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.leantv.launcher.data.model.AppModel
import com.leantv.launcher.databinding.ItemTvAppBinding
import com.leantv.launcher.util.FocusAnimationHelper

class AppViewHolder(
    private val binding: ItemTvAppBinding,
    private val onItemClick: (AppModel) -> Unit,
    private val onItemLongClick: (AppModel, View) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var currentApp: AppModel? = null

    init {
        binding.root.setOnFocusChangeListener { view, hasFocus ->
            FocusAnimationHelper.applyFocusScale(view, hasFocus)
            binding.tvAppLabel.isSelected = hasFocus // enables marquee if text is long
            binding.tvAppLabel.setTextColor(
                if (hasFocus) 0xFFFFFFFF.toInt() else 0xFFE0E0E0.toInt()
            )
        }

        binding.root.setOnClickListener {
            currentApp?.let { app -> onItemClick(app) }
        }

        binding.root.setOnLongClickListener { view ->
            currentApp?.let { app ->
                onItemLongClick(app, view)
                true
            } ?: false
        }
    }

    fun bind(app: AppModel) {
        currentApp = app
        binding.tvAppLabel.text = app.label
        binding.ivFavoriteBadge.visibility = if (app.isFavorite) View.VISIBLE else View.GONE
        binding.ivAppIcon.setImageDrawable(null)
    }

    fun setIcon(drawable: Drawable?) {
        binding.ivAppIcon.setImageDrawable(drawable)
    }
}

