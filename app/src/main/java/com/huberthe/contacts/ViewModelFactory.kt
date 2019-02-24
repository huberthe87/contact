package com.huberthe.contacts

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.huberthe.contacts.data.ContactDataSource
import com.huberthe.contacts.data.ContactRepository

class ViewModelFactory(private val repository: ContactRepository) : ViewModelProvider.Factory {

  companion object {

    @Volatile
    private var INSTANCE: ViewModelFactory? = null

    @JvmStatic
    fun getInstance(context: Context): ViewModelFactory = INSTANCE ?: synchronized(this) {
      INSTANCE = ViewModelFactory(ContactRepository(ContactDataSource.ContactDataSourceImpl(context.assets)))
      return INSTANCE as ViewModelFactory
    }

    // For test only
    @JvmStatic
    internal fun setInstance(viewModelFactory: ViewModelFactory) {
      INSTANCE = viewModelFactory
    }
  }

  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return modelClass.getDeclaredConstructor(ContactRepository::class.java).newInstance(repository)
  }
}