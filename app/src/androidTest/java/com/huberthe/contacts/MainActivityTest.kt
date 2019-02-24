package com.huberthe.contacts

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.huberthe.contacts.adapters.ProfileAdapter
import com.huberthe.contacts.data.ContactDataSource
import com.huberthe.contacts.data.ContactRepository
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.StringBuilder

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

  @get:Rule
  val activityTestRule = ActivityTestRule(MainActivity::class.java, false, false)

  private lateinit var contactDataSource: ContactDataSource

  @Test
  fun testLoad1Item() {
    contactDataSource = object : ContactDataSource {
      override fun getRawData(): String = ONE_ITEM
    }
    ViewModelFactory.setInstance(ViewModelFactory(ContactRepository(contactDataSource)))
    activityTestRule.launchActivity(null)
    onView(withId(R.id.rv_profile_item_full_name_tv)).check(matches(Matchers.allOf(isDisplayed(), withText("Allan Munger"))))
    onView(withId(R.id.rv_profile_item_about_me_tv)).check(matches(Matchers.allOf(isDisplayed(), withText(R.string.rv_profile_item_about_me))))
    onView(withId(R.id.rv_profile_item_title_tv)).check(matches(Matchers.allOf(isDisplayed(), withText("Writer"))))
    onView(withId(R.id.rv_profile_item_introduction_tv)).check(matches(Matchers.allOf(isDisplayed(), withText("Ut malesuada sollicitudin tincidunt. Maecenas volutpat suscipit efficitur. Curabitur ut tortor sit amet lacus pellentesque convallis in laoreet lectus. Curabitur lorem velit, bibendum et vulputate vulputate, commodo in tortor. Curabitur a dapibus mauris. Vestibulum hendrerit euismod felis at hendrerit. Pellentesque imperdiet volutpat molestie. Nam vehicula dui eu consequat finibus. Phasellus sed placerat lorem. Nulla pretium a magna sit amet iaculis. Aenean eget eleifend elit. Ut eleifend aliquet interdum. Cras pulvinar elit a dapibus iaculis. Nullam fermentum porttitor ultrices."))))

    Thread.sleep(2000)
  }

  @Test
  fun testLoad2Item() {
    contactDataSource = object : ContactDataSource {
      override fun getRawData(): String = TWO_ITEM
    }
    ViewModelFactory.setInstance(ViewModelFactory(ContactRepository(contactDataSource)))
    activityTestRule.launchActivity(null)

    for(i in 0..1) {
      Thread.sleep(100)
      onView(withId(R.id.activity_main_profile_rv))
          .perform(RecyclerViewActions.scrollToPosition<ProfileAdapter.ProfileItemViewHolder>(i))
          .check(matches(atPosition(i,
              withChild(allOf(withId(R.id.rv_profile_item_full_name_tv), withText("FIRST$i LAST$i"))),
              withChild(allOf(withId(R.id.rv_profile_item_title_tv), withText("TITLE$i"))),
              withChild(allOf(withId(R.id.rv_profile_item_introduction_tv), withText("INTRODUCTION$i"))),
              withChild(allOf(withId(R.id.rv_profile_item_about_me_tv), withText(R.string.rv_profile_item_about_me))))))
    }


    Thread.sleep(2000)
  }

  @Test
  fun testLoad100Item() {
    contactDataSource = object : ContactDataSource {
      override fun getRawData(): String {
        val sb = StringBuilder()
        sb.append('[')
        for(i in 0 until 100) {
          sb.append('{')
              .append("\"first_name\": \"FIRST$i\",\n")
              .append("\"last_name\": \"LAST$i\",\n")
              .append("\"avatar_filename\": \"Allan Munger.png\",\n")
              .append("\"title\": \"TITLE$i\",\n")
              .append("\"introduction\": \"INTRODUCTION$i\"")
              .append('}')
          if(i < 99) {
            sb.append(',')
          }
        }
        sb.append(']')
        println(sb.toString())
        return sb.toString()
      }
    }
    ViewModelFactory.setInstance(ViewModelFactory(ContactRepository(contactDataSource)))
    activityTestRule.launchActivity(null)

    for(i in 0 until 100) {
      Thread.sleep(100)
      onView(withId(R.id.activity_main_profile_rv))
          .perform(RecyclerViewActions.scrollToPosition<ProfileAdapter.ProfileItemViewHolder>(i))
          .check(matches(atPosition(i,
              withChild(allOf(withId(R.id.rv_profile_item_full_name_tv), withText("FIRST$i LAST$i"))),
              withChild(allOf(withId(R.id.rv_profile_item_title_tv), withText("TITLE$i"))),
              withChild(allOf(withId(R.id.rv_profile_item_introduction_tv), withText("INTRODUCTION$i"))),
              withChild(allOf(withId(R.id.rv_profile_item_about_me_tv), withText(R.string.rv_profile_item_about_me))))))
    }

    Thread.sleep(2000)
  }

  fun nthChildOf(parentMatcher: Matcher<View>, childPosition: Int): Matcher<View> {
    return object : TypeSafeMatcher<View>() {
      override fun describeTo(description: Description) {
        description.appendText("with $childPosition child view of type parentMatcher")
      }

      override fun matchesSafely(view: View): Boolean {
        if(view.parent !is ViewGroup) {
          return parentMatcher.matches(view.parent)
        }

        val group = view.parent as ViewGroup
        return parentMatcher.matches(view.parent) && group.getChildAt(childPosition) == view
      }
    }
  }

  fun atPosition(position: Int, @NonNull vararg itemMatcher: Matcher<View>): Matcher<View> {
    checkNotNull(itemMatcher)
    return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
      override fun describeTo(description: Description) {
        for(item in itemMatcher) {
          description.appendText("has item at position $position\n")
          item.describeTo(description)
        }
      }

      override fun matchesSafely(view: RecyclerView): Boolean {
        val viewHolder = view.findViewHolderForAdapterPosition(position)
            ?: // has no item on such position
            return false
        return itemMatcher.all {
          it.matches(viewHolder.itemView)
        }
      }
    }
  }

  companion object {
    private const val ONE_ITEM = "[\n" +
        "  {\n" +
        "    \"first_name\": \"Allan\",\n" +
        "    \"last_name\": \"Munger\",\n" +
        "    \"avatar_filename\": \"Allan Munger.png\",\n" +
        "    \"title\": \"Writer\",\n" +
        "    \"introduction\": \"Ut malesuada sollicitudin tincidunt. Maecenas volutpat suscipit efficitur. Curabitur ut tortor sit amet lacus pellentesque convallis in laoreet lectus. Curabitur lorem velit, bibendum et vulputate vulputate, commodo in tortor. Curabitur a dapibus mauris. Vestibulum hendrerit euismod felis at hendrerit. Pellentesque imperdiet volutpat molestie. Nam vehicula dui eu consequat finibus. Phasellus sed placerat lorem. Nulla pretium a magna sit amet iaculis. Aenean eget eleifend elit. Ut eleifend aliquet interdum. Cras pulvinar elit a dapibus iaculis. Nullam fermentum porttitor ultrices.\"\n" +
        "  }]"

    private const val TWO_ITEM = "[\n" +
        "  {\n" +
        "    \"first_name\": \"FIRST0\",\n" +
        "    \"last_name\": \"LAST0\",\n" +
        "    \"avatar_filename\": \"Allan Munger.png\",\n" +
        "    \"title\": \"TITLE0\",\n" +
        "    \"introduction\": \"INTRODUCTION0\"\n" +
        "  },\n" +
        "  {\n" +
        "    \"first_name\": \"FIRST1\",\n" +
        "    \"last_name\": \"LAST1\",\n" +
        "    \"avatar_filename\": \"Amanda Brady.png\",\n" +
        "    \"title\": \"TITLE1\",\n" +
        "    \"introduction\": \"INTRODUCTION1\"\n" +
        "  }]"
  }
}