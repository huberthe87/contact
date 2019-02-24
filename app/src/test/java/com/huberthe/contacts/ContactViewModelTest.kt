package com.huberthe.contacts

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.huberthe.contacts.data.ContactRepository
import com.huberthe.contacts.data.entities.Contact
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner.Silent::class)
class ContactViewModelTest {

  // Apply this rule to allow the lifecycle component running in work thread
  @get:Rule
  var instantTaskExecutorRule = InstantTaskExecutorRule()

  private val repository = mock<ContactRepository>()

  private val contactViewModel = ContactViewModel(repository)

  @Test
  fun loadData() {
    val contacts = listOf<Contact>(mock(), mock())
    whenever(repository.getContacts()).thenReturn(contacts)
    contactViewModel.loadData()
    verify(repository).getContacts()
  }

  @Test
  fun getContacts() {
    val mock1 = mock<Contact>()
    val mock2 = mock<Contact>()
    val contacts = listOf(mock1, mock2)
    whenever(repository.getContacts()).thenReturn(contacts)
    val countDownLatch = CountDownLatch(1)
    val observer = Observer<List<Contact>> {
      Assert.assertArrayEquals(arrayOf(mock1, mock2), it.toTypedArray())
      countDownLatch.countDown()
    }

    val liveData = contactViewModel.getContacts()
    liveData.observeForever(observer)
    contactViewModel.loadData()
    verify(repository).getContacts()

    Assert.assertTrue(countDownLatch.await(2, TimeUnit.SECONDS))
    liveData.removeObserver(observer)
  }

  @Test
  fun getContacts_observeAfterChange() {
    val mock1 = mock<Contact>()
    val mock2 = mock<Contact>()
    val contacts = listOf(mock1, mock2)
    whenever(repository.getContacts()).thenReturn(contacts)
    contactViewModel.loadData()
    val countDownLatch = CountDownLatch(1)
    val observer = Observer<List<Contact>> {
      Assert.assertArrayEquals(arrayOf(mock1, mock2), it.toTypedArray())
      countDownLatch.countDown()
    }

    val liveData = contactViewModel.getContacts()
    liveData.observeForever(observer)
    verify(repository).getContacts()

    Assert.assertTrue(countDownLatch.await(2, TimeUnit.SECONDS))
    liveData.removeObserver(observer)
  }

  @Test
  fun getContacts_multipleUpdate() {
    val mock1 = mock<Contact>()
    val mock2 = mock<Contact>()
    val contacts = listOf(mock1, mock2)
    whenever(repository.getContacts()).thenReturn(contacts)
    contactViewModel.loadData()
    val countDownLatch = CountDownLatch(2)
    val observer = Observer<List<Contact>> {
      Assert.assertArrayEquals(arrayOf(mock1, mock2), it.toTypedArray())
      countDownLatch.countDown()
    }

    val liveData = contactViewModel.getContacts()
    liveData.observeForever(observer)

    contactViewModel.loadData()

    verify(repository, times(2)).getContacts()

    Assert.assertTrue(countDownLatch.await(2, TimeUnit.SECONDS))
    liveData.removeObserver(observer)
  }
}