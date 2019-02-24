package com.huberthe.contacts.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.huberthe.contacts.R
import com.huberthe.contacts.data.entities.Contact

class AvatarAdapter : RecyclerView.Adapter<AvatarAdapter.AvatarItemViewHolder>() {

  val contacts: MutableList<Contact> = ArrayList()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AvatarItemViewHolder(parent)

  override fun getItemCount() = contacts.size

  override fun onBindViewHolder(holder: AvatarItemViewHolder, position: Int) {
//    holder.bind(Drawable.createFromPath(contacts[position].avatar))
  }

  class AvatarItemViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_avatar_item, parent, false)) {

    private val avatarIv: ImageView = itemView.findViewById(R.id.rv_avatar_item_iv)

    fun bind(drawable: Drawable) {
      avatarIv.setImageDrawable(drawable)
    }
  }
}