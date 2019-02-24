package com.huberthe.contacts.data

import com.google.gson.JsonParser
import com.huberthe.contacts.data.entities.Contact

class ContactRepository(private val contactDataSource: ContactDataSource) {

  fun getContacts(): List<Contact> {
    val content = contactDataSource.getRawData()
    val rawContactArray = JsonParser().parse(content).asJsonArray

    val contacts = ArrayList<Contact>()

    rawContactArray.forEach {
      val rawContactObj = it.asJsonObject
      val firstName = rawContactObj.getAsJsonPrimitive("first_name")?.asString
      val lastName = rawContactObj.getAsJsonPrimitive("last_name")?.asString
      val avatar = rawContactObj.getAsJsonPrimitive("avatar_filename")?.asString
      val introduction = rawContactObj.getAsJsonPrimitive("introduction")?.asString
      val title = rawContactObj.getAsJsonPrimitive("title")?.asString

      contacts.add(Contact(firstName ?: "", lastName ?: "", avatar ?: "", title ?: "", introduction
          ?: ""))
    }

    return contacts
  }
}