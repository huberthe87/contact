package com.huberthe.contacts.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.huberthe.contacts.R
import com.huberthe.contacts.data.entities.Contact
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch

class AvatarAdapter : RecyclerView.Adapter<AvatarAdapter.AvatarItemViewHolder>() {

  val contacts: MutableList<Contact> = ArrayList()

  private val channel: Channel<Int> = Channel()

  var currentPosition: Int = 0

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AvatarItemViewHolder(parent, channel)

  override fun getItemCount() = contacts.size

  override fun onBindViewHolder(holder: AvatarItemViewHolder, position: Int) {
    holder.bind(position, currentPosition, null)
  }

  class AvatarItemViewHolder(parent: ViewGroup, channel: SendChannel<Int>) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_avatar_item, parent, false)) {

    var index = -1

    init {
      itemView.setOnClickListener {
        if(!itemView.isSelected) {
          GlobalScope.launch {
            channel.send(index)
          }
        }
      }
    }

    private val avatarIv: ImageView = itemView.findViewById(R.id.rv_avatar_item_iv)

    fun bind(position: Int, currentPosition: Int, drawable: Drawable?) {
//      avatarIv.setImageDrawable(drawable)
      index = position
      itemView.isSelected = currentPosition == position
    }
  }

  fun selectedChangeChannel(): ReceiveChannel<Int> = channel
}