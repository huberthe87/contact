package com.huberthe.contacts

import android.app.Activity
import android.graphics.Point
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

class SyncSnapHelper(activity: Activity) {

  private val screenWidth: Int

  private val screenHeight: Int

  private val horizontalPadding: Int

  private val avatarItemSize: Int

  private val appMarginNormal: Int

  private var controlView: View? = null

  private val avatarScroller: Scroller

  private val profileScroller: Scroller

  private lateinit var avatarRv: RecyclerView

  private lateinit var profileRv: RecyclerView

  private lateinit var profileOnFlingListener: SnapFlingListener

  private lateinit var avatarOnFlingListener: SnapFlingListener

  private lateinit var profileLayoutManager: LinearLayoutManager

  private lateinit var avatarLayoutManager: LinearLayoutManager

  private lateinit var profileOrientationHelper: OrientationHelper

  private lateinit var avatarOrientationHelper: OrientationHelper

  init {
    val display = activity.windowManager.defaultDisplay
    val size = Point()
    display.getSize(size)
    screenWidth = size.x
    screenHeight = size.x
    avatarItemSize = activity.resources.getDimensionPixelOffset(R.dimen.rv_avatar_item_iv_size)
    appMarginNormal = activity.resources.getDimensionPixelOffset(R.dimen.app_margin_normal)
    avatarScroller = Scroller(activity,
        DecelerateInterpolator())
    profileScroller = Scroller(activity,
        DecelerateInterpolator())
    horizontalPadding = (screenWidth - avatarItemSize) / 2 - appMarginNormal
  }

  fun attachToRecyclerViews(avatarRv: RecyclerView, profileRv: RecyclerView) {
    this.avatarRv = avatarRv
    this.profileRv = profileRv

    // Programmatically set the padding, should consider use xml definition.
    avatarRv.setPadding(horizontalPadding, 0, horizontalPadding, 0)

    initializeControls(profileRv, avatarRv)

    profileRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {

      var scrolling = false

      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if(newState == RecyclerView.SCROLL_STATE_IDLE) {
          val profileSnapView = findCenterView(profileLayoutManager, profileOrientationHelper)
              ?: return

          // Find profile target position by snap view.
          val profileTargetPosition = profileLayoutManager.getPosition(profileSnapView)

          // Find avatar snap view regarding to profile position.
          val avatarSnapView = avatarLayoutManager.findViewByPosition(profileTargetPosition)
              ?: return

          // Smooth scroll to target
          avatarRv.smoothScrollBy(distanceToCenter(avatarLayoutManager, avatarSnapView, avatarOrientationHelper), 0)
          profileRv.smoothScrollBy(0, distanceToCenter(profileLayoutManager, profileSnapView, profileOrientationHelper))

          controlView = null

          GlobalScope.launch { channel.send(profileTargetPosition) }

        } else if(newState == RecyclerView.SCROLL_STATE_DRAGGING) {
          controlView = recyclerView
        }
        scrolling = newState != RecyclerView.SCROLL_STATE_IDLE
      }

      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if(controlView != recyclerView) {
          return
        }
        // Use float to avoid precision loss
        val rate = (avatarItemSize + appMarginNormal * 2).toFloat() / recyclerView.height.toFloat()
        val scrollX = Math.round(dy.toFloat() * rate)
        if(scrolling) {
          avatarRv.scrollBy(scrollX, 0)
        }
      }
    })

    avatarRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {

      var dragging = false

      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if(newState == RecyclerView.SCROLL_STATE_IDLE) {
          val avatarSnapView = findCenterView(avatarLayoutManager, avatarOrientationHelper)
              ?: return

          // Find avatar target position by snap view.
          val avatarTargetPosition = avatarLayoutManager.getPosition(avatarSnapView)

          // Find profile snap view regarding to profile position.
          val profileSnapView = profileLayoutManager.findViewByPosition(avatarTargetPosition)
              ?: return

          // Smooth scroll to target
          avatarRv.smoothScrollBy(distanceToCenter(avatarLayoutManager, avatarSnapView, avatarOrientationHelper), 0)
          profileRv.smoothScrollBy(0, distanceToCenter(profileLayoutManager, profileSnapView, profileOrientationHelper))
          controlView = null

          GlobalScope.launch { channel.send(avatarTargetPosition) }
        } else if(newState == RecyclerView.SCROLL_STATE_DRAGGING) {
          controlView = recyclerView
        }
        dragging = newState != RecyclerView.SCROLL_STATE_IDLE
      }

      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if(controlView != recyclerView) {
          return
        }
        // Use float to avoid precision loss
        val rate = profileRv.height.toFloat() / (avatarItemSize + appMarginNormal * 2).toFloat()
        val scrollY = Math.round(dx.toFloat() * rate)
        if(dragging) {
          profileRv.scrollBy(0, scrollY)
        }
      }
    })
  }

  private fun initializeControls(profileRv: RecyclerView, avatarRv: RecyclerView) {
    profileLayoutManager = profileRv.layoutManager as LinearLayoutManager
    avatarLayoutManager = avatarRv.layoutManager as LinearLayoutManager
    profileOrientationHelper = OrientationHelper.createVerticalHelper(profileLayoutManager)
    avatarOrientationHelper = OrientationHelper.createHorizontalHelper(avatarLayoutManager)
    profileOnFlingListener = SnapFlingListener(profileRv, profileScroller, profileOrientationHelper)
    avatarOnFlingListener = SnapFlingListener(avatarRv, avatarScroller, avatarOrientationHelper)

    profileRv.onFlingListener = profileOnFlingListener
    avatarRv.onFlingListener = avatarOnFlingListener
  }

  /**
   * Copied from LinearSnapHelper that find the centered view item position
   */
  internal fun findCenterView(layoutManager: RecyclerView.LayoutManager,
                              helper: OrientationHelper): View? {

    val childCount = layoutManager.childCount
    if(childCount == 0) {
      return null
    }

    var closestChild: View? = null
    val centerPosition: Int = if(layoutManager.clipToPadding) {
      helper.startAfterPadding + helper.totalSpace / 2
    } else {
      helper.end / 2
    }
    var absClosest = Integer.MAX_VALUE

    for(i in 0 until childCount) {
      val child = layoutManager.getChildAt(i)
      val childCenter = helper.getDecoratedStart(child) + helper.getDecoratedMeasurement(child) / 2
      val absDistance = Math.abs(childCenter - centerPosition)

      /** if child center is closer than previous closest, set it as closest   */
      if(absDistance < absClosest) {
        absClosest = absDistance
        closestChild = child
      }
    }
    return closestChild
  }

  /**
   * Calculate the offset of target view to center
   */
  private fun distanceToCenter(layoutManager: RecyclerView.LayoutManager,
                               targetView: View, helper: OrientationHelper): Int {
    val childCenter = helper.getDecoratedStart(targetView) + helper.getDecoratedMeasurement(targetView) / 2
    val containerCenter: Int = if(layoutManager.clipToPadding) {
      helper.startAfterPadding + helper.totalSpace / 2
    } else {
      helper.end / 2
    }
    return childCenter - containerCenter
  }

  private class SnapFlingListener(private val recyclerView: RecyclerView,
                                  private val gravityScroller: Scroller,
                                  private val orientationHelper: OrientationHelper)
    : RecyclerView.OnFlingListener() {

    val layoutManager = recyclerView.layoutManager as LinearLayoutManager

    override fun onFling(velocityX: Int, velocityY: Int): Boolean {
      if(Math.abs(velocityX) < recyclerView.minFlingVelocity && Math.abs(velocityY) < recyclerView.minFlingVelocity) {
        return false
      }
      val originalPosition = layoutManager.findFirstVisibleItemPosition()
      val positionDiff = estimateNextPositionDiffForFling(layoutManager, orientationHelper, velocityX, velocityY)
      val targetPosition = Math.min(Math.max(0, originalPosition + positionDiff), layoutManager.itemCount)
      recyclerView.smoothScrollToPosition(targetPosition)
      Log.d("FLING", "velocityX: $velocityX velocityY: $velocityY targetPosition $targetPosition")
      GlobalScope.launch { channel.send(targetPosition) }
      return true
    }

    /**
     * Calculated the estimated scroll distance in each direction given velocities on both axes.
     *
     * @param velocityX     Fling velocity on the horizontal axis.
     * @param velocityY     Fling velocity on the vertical axis.
     *
     * @return array holding the calculated distances in x and y directions
     * respectively.
     */
    fun calculateScrollDistance(velocityX: Int, velocityY: Int): IntArray {
      val outDist = IntArray(2)
      gravityScroller.fling(0, 0, velocityX, velocityY,
          Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE)
      outDist[0] = gravityScroller.finalX
      outDist[1] = gravityScroller.finalY
      return outDist
    }

    private fun computeDistancePerChild(layoutManager: RecyclerView.LayoutManager,
                                        helper: OrientationHelper): Float {
      var minPosView: View? = null
      var maxPosView: View? = null
      var minPos = Integer.MAX_VALUE
      var maxPos = Integer.MIN_VALUE
      val childCount = layoutManager.childCount
      if(childCount == 0) {
        return INVALID_DISTANCE
      }

      for(i in 0 until childCount) {
        val child = layoutManager.getChildAt(i)
        val pos = layoutManager.getPosition(child!!)
        if(pos == RecyclerView.NO_POSITION) {
          continue
        }
        if(pos < minPos) {
          minPos = pos
          minPosView = child
        }
        if(pos > maxPos) {
          maxPos = pos
          maxPosView = child
        }
      }
      if(minPosView == null || maxPosView == null) {
        return INVALID_DISTANCE
      }
      val start = Math.min(helper.getDecoratedStart(minPosView),
          helper.getDecoratedStart(maxPosView))
      val end = Math.max(helper.getDecoratedEnd(minPosView),
          helper.getDecoratedEnd(maxPosView))
      val distance = end - start
      return if(distance == 0) {
        INVALID_DISTANCE
      } else 1f * distance / (maxPos - minPos + 1)
    }

    private fun estimateNextPositionDiffForFling(layoutManager: RecyclerView.LayoutManager,
                                                 helper: OrientationHelper, velocityX: Int, velocityY: Int): Int {
      val distances = calculateScrollDistance(velocityX, velocityY)
      val distancePerChild = computeDistancePerChild(layoutManager, helper)
      if(distancePerChild <= 0) {
        return 0
      }
      val distance = if(Math.abs(distances[0]) > Math.abs(distances[1])) distances[0] else distances[1]
      return Math.round(distance / distancePerChild)
    }

  }

  companion object {
    private const val INVALID_DISTANCE = 1f

    private val channel: Channel<Int> = Channel()
  }

  fun scrollChangeChannel(): ReceiveChannel<Int> = channel

}