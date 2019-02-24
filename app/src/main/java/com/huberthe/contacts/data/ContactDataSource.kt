package com.huberthe.contacts.data

import android.content.res.AssetManager
import java.io.InputStreamReader


interface ContactDataSource {

  /**
   * Get raw string data may from http call or read it from local assets file.
   */
  fun getRawData(): String


  /**
   * Default implementation of ContactDataSource to read data from assets file.
   */
  class ContactDataSourceImpl(private val assetManager: AssetManager) : ContactDataSource {

    override fun getRawData() = InputStreamReader(assetManager.open(FILE_PATH)).readText()

    companion object {
      private const val FILE_PATH = "contacts.json"
    }
  }

}