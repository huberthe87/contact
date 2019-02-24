package com.huberthe.contacts

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.huberthe.contacts.adapters.AvatarAdapter
import com.huberthe.contacts.adapters.ProfileAdapter
import com.huberthe.contacts.data.entities.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

  private lateinit var avatarRv: RecyclerView

  private lateinit var profileRv: RecyclerView

  private lateinit var avatarAdapter: AvatarAdapter

  private lateinit var profileAdapter: ProfileAdapter

  private lateinit var viewModel: ContactViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    if(savedInstanceState == null) {
      viewModel = ViewModelProviders.of(this, ViewModelFactory.getInstance(this))
          .get(ContactViewModel::class.java)

      avatarRv = findViewById(R.id.activity_main_avatar_rv)

      profileRv = findViewById(R.id.activity_main_profile_rv)

      val scrollHelper = SyncSnapHelper(this)
      scrollHelper.attachToRecyclerViews(avatarRv, profileRv)
      val metric = DisplayMetrics()
      windowManager.defaultDisplay.getMetrics(metric)
      avatarAdapter = AvatarAdapter(metric.densityDpi, applicationContext.assets)
      profileAdapter = ProfileAdapter()

      avatarRv.adapter = avatarAdapter
      profileRv.adapter = profileAdapter

      // Add position changed listener via Kotlin Coroutine
      GlobalScope.launch(Dispatchers.Main) {
        launch {
          scrollHelper.scrollChangeChannel().consumeEach { position ->
            notifySelectChanged(avatarAdapter.currentPosition, position)
          }
        }
        launch {
          avatarAdapter.selectedChangeChannel().consumeEach { position ->
            notifySelectChanged(avatarAdapter.currentPosition, position)

            // Scroll to target position
            avatarRv.smoothScrollToPosition(position)
            profileRv.smoothScrollToPosition(position)
          }
        }
      }
    }

    viewModel.getContacts().observe(this, Observer<List<Contact>> {
      avatarAdapter.contacts.clear()
      avatarAdapter.contacts.addAll(it)

      profileAdapter.contacts.clear()
      profileAdapter.contacts.addAll(it)

      avatarAdapter.notifyDataSetChanged()
      profileAdapter.notifyDataSetChanged()
    })

    viewModel.loadData()
  }

  private fun notifySelectChanged(oldPosition: Int, newPosition: Int) {
    if(avatarAdapter.currentPosition == newPosition) {
      return
    }
    Log.d("POSITION", "oldPosition: $oldPosition newPosition: $newPosition")
    avatarAdapter.currentPosition = newPosition
    avatarRv.adapter?.notifyItemChanged(oldPosition)
    avatarRv.adapter?.notifyItemChanged(newPosition)
  }

}