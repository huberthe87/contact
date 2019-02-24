package com.huberthe.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.huberthe.contacts.data.ContactRepository
import com.huberthe.contacts.data.entities.Contact

class ContactViewModel(private val repository: ContactRepository) : ViewModel() {

  private val contacts: MutableLiveData<List<Contact>> = MutableLiveData()

  fun loadData() {
    contacts.value = repository.getContacts()
  }

  fun getContacts(): LiveData<List<Contact>> = contacts
}