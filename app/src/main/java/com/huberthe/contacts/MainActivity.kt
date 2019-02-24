package com.huberthe.contacts

import android.app.Activity
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.huberthe.contacts.adapters.AvatarAdapter
import com.huberthe.contacts.adapters.ProfileAdapter
import com.huberthe.contacts.data.entities.Contact


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

      val scrollHelper = ScrollHelper(this)
      scrollHelper.attachToRecyclerViews(avatarRv, profileRv)

      avatarAdapter = AvatarAdapter()
      profileAdapter = ProfileAdapter()

      avatarRv.adapter = avatarAdapter
      profileRv.adapter = profileAdapter
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

  class ScrollHelper(activity: Activity) {

    private val screenWidth: Int

    private val screenHeight: Int

    private val horizontalPadding: Int

    private val avatarItemSize: Int

    private val appMarginNormal: Int

    private val profileSnapHelper = LinearSnapHelper()

    private val avatarSnapHelper = LinearSnapHelper()

    init {
      val display = activity.windowManager.defaultDisplay
      val size = Point()
      display.getSize(size)
      screenWidth = size.x
      screenHeight = size.x
      avatarItemSize = activity.resources.getDimensionPixelOffset(R.dimen.rv_avatar_item_iv_size)
      appMarginNormal = activity.resources.getDimensionPixelOffset(R.dimen.app_margin_normal)

      horizontalPadding = (screenWidth - avatarItemSize) / 2
    }

    fun attachToRecyclerViews(avatarRv: RecyclerView, profileRv: RecyclerView) {
      avatarRv.setPadding(horizontalPadding, 0, horizontalPadding, 0)

      // Use LinearSnapHelper rather than PagerSnapHelper, cause user may fling to a long distance target.
      profileSnapHelper.attachToRecyclerView(profileRv)

      avatarSnapHelper.attachToRecyclerView(avatarRv)

      profileRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {

        private var increaseY = 0

        private var originalScrollX = 0

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
          super.onScrollStateChanged(recyclerView, newState)
          if(newState == SCROLL_STATE_IDLE) {
            increaseY = 0
            originalScrollX = avatarRv.scrollX
          }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
          super.onScrolled(recyclerView, dx, dy)
          val rate: Float = dy.toFloat() / profileRv.height
          val scrollX = rate * (avatarItemSize + 2 * appMarginNormal)
          Log.d("SCROLL_HELPER", "$dy ScrollX:$scrollX")
          avatarRv.scrollBy(scrollX.toInt(), 0)
        }
      })
    }
  }
}