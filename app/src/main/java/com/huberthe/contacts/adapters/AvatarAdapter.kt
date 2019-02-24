package com.huberthe.contacts.adapters

import android.content.res.AssetManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics.*
import android.util.Log
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
import java.io.IOException
import java.io.InputStream

class AvatarAdapter(private val densityDpi: Int, private val assetManager: AssetManager) : RecyclerView.Adapter<AvatarAdapter.AvatarItemViewHolder>() {

  val contacts: MutableList<Contact> = ArrayList()

  private val channel: Channel<Int> = Channel()

  var currentPosition: Int = 0

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AvatarItemViewHolder(parent, channel)

  override fun getItemCount() = contacts.size

  override fun onBindViewHolder(holder: AvatarItemViewHolder, position: Int) {
    var fileName = contacts[position].avatar
    // Create an input stream to read from the asset folder
    var inputStream: InputStream? = null
    var drawable: Drawable? = null
    try {
      // Adjust fileName by density
      if(densityDpi >= DENSITY_XHIGH) {
        fileName = fileName.replace(".", "@3x.")
      } else if(densityDpi >= DENSITY_LOW) {
        fileName = fileName.replace(".", "@2x.")
      }
      Log.d("AVATAR", "densityDpi $densityDpi $fileName")
      inputStream = assetManager.open("avatars/$fileName")
      drawable = Drawable.createFromStream(inputStream, null)
    } catch(e: Exception) {
      Log.e("AVATAR", "Unexpected error occurred.", e)
    } finally {
      //Always clear and close
      try {
        inputStream?.close()
      } catch(e: IOException) {
      }
    }

    holder.bind(position, currentPosition, drawable ?: ColorDrawable(Color.BLACK))
  }

  class AvatarItemViewHolder(parent: ViewGroup, channel: SendChannel<Int>) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_avatar_item, parent, false)) {

    var index = -1

    init {
      itemView.setOnClickListener {
        if(!avatarIv.isSelected) {
          GlobalScope.launch {
            channel.send(index)
          }
        }
      }
    }

    private val avatarIv: ImageView = itemView.findViewById(R.id.rv_avatar_item_iv)

    fun bind(position: Int, currentPosition: Int, drawable: Drawable) {
      avatarIv.setImageDrawable(drawable)
      avatarIv.contentDescription
      avatarIv.isSelected = currentPosition == position
      index = position
    }
  }

  fun selectedChangeChannel(): ReceiveChannel<Int> = channel
}