package com.huberthe.contacts.adapters

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.huberthe.contacts.R
import com.huberthe.contacts.data.entities.Contact

class ProfileAdapter : RecyclerView.Adapter<ProfileAdapter.ProfileItemViewHolder>() {

  val contacts: MutableList<Contact> = ArrayList()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProfileItemViewHolder(parent)

  override fun getItemCount() = contacts.size

  override fun onBindViewHolder(holder: ProfileItemViewHolder, position: Int) {
    holder.bind(contacts[position])
  }

  class ProfileItemViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_profile_item, parent, false)) {

    private val fullNameTv: TextView = itemView.findViewById(R.id.rv_profile_item_full_name_tv)
    private val titleTv: TextView = itemView.findViewById(R.id.rv_profile_item_title_tv)
    private val introductionTv: TextView = itemView.findViewById(R.id.rv_profile_item_introduction_tv)

    fun bind(contact: Contact) {
      val ssb = SpannableStringBuilder()
      ssb.append(contact.firstName, ForegroundColorSpan(Color.BLACK), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
      ssb.setSpan(StyleSpan(Typeface.BOLD), 0, ssb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
      ssb.append(' ').append(contact.lastName)
      fullNameTv.text = ssb
      titleTv.text = contact.title
      introductionTv.text = contact.introduction
    }
  }
}