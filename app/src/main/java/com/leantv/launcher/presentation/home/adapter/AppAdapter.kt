package com.leantv.launcher.presentation.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.leantv.launcher.data.model.AppModel
import com.leantv.launcher.databinding.ItemTvAppBinding
import com.leantv.launcher.presentation.home.viewholder.AppViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppAdapter(
    private val iconLoader: suspend (AppModel) -> android.graphics.drawable.Drawable?,
    private val scope: CoroutineScope,
    private val onItemClick: (AppModel) -> Unit,
    private val onItemLongClick: (AppModel, View) -> Unit
) : ListAdapter<AppModel, AppViewHolder>(AppDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val binding = ItemTvAppBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AppViewHolder(binding, onItemClick, onItemLongClick)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = getItem(position)
        holder.bind(app)

        // Load icon asynchronously off main thread
        scope.launch {
            val drawable = iconLoader(app)
            withContext(Dispatchers.Main) {
                // Verify holder is still displaying the same item
                if (holder.bindingAdapterPosition == position) {
                    holder.setIcon(drawable)
                }
            }
        }
    }

    companion object {
        private val AppDiffCallback = object : DiffUtil.ItemCallback<AppModel>() {
            override fun areItemsTheSame(oldItem: AppModel, newItem: AppModel): Boolean {
                return oldItem.componentKey == newItem.componentKey
            }

            override fun areContentsTheSame(oldItem: AppModel, newItem: AppModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}

