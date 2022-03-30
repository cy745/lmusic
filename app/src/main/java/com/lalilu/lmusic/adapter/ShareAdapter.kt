package com.lalilu.lmusic.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.blankj.utilcode.util.GsonUtils
import com.lalilu.R
import com.lalilu.databinding.ItemShareItemBinding
import com.lalilu.lmusic.apis.bean.ShareDto
import io.ably.lib.types.Message
import javax.inject.Inject

class ShareAdapter @Inject constructor() :
    BaseAdapter<Message, ItemShareItemBinding>(R.layout.item_share_item) {
    override val itemCallback: DiffUtil.ItemCallback<Message>
        get() = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem.connectionId == newItem.connectionId
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem.name == newItem.name &&
                        oldItem.connectionId == newItem.connectionId &&
                        oldItem.id == newItem.id &&
                        oldItem.data.equals(newItem.data)
            }
        }

    override fun onBind(binding: ItemShareItemBinding, item: Message, position: Int) {
        binding.name = item.connectionId
        binding.state = item.name

        try {
            val shareDto = GsonUtils.fromJson(item.data.toString(), ShareDto::class.java)
            binding.musicTitle = shareDto.title
            binding.musicArtist = shareDto.artist
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}