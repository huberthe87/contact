package com.huberthe.contacts.data

import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(JUnit4::class)
class ContactRepositoryTest {

  private val mockDataSource = mock(ContactDataSource::class.java)

  private val contactRepository = ContactRepository(mockDataSource)

  @Test
  fun getContacts() {
    whenever(mockDataSource.getRawData()).thenReturn(TEST_RAW_CONTACTS)

    val contacts = contactRepository.getContacts()
    verify(mockDataSource).getRawData()
    Assert.assertEquals(3, contacts.size)

    Assert.assertEquals("Allan", contacts[0].firstName)
    Assert.assertEquals("Munger", contacts[0].lastName)
    Assert.assertEquals("Allan Munger.png", contacts[0].avatar)
    Assert.assertEquals("Writer", contacts[0].title)
    Assert.assertEquals("Ut malesuada sollicitudin tincidunt. Maecenas volutpat suscipit efficitur. Curabitur ut tortor sit amet lacus pellentesque convallis in laoreet lectus. Curabitur lorem velit, bibendum et vulputate vulputate, commodo in tortor. Curabitur a dapibus mauris. Vestibulum hendrerit euismod felis at hendrerit. Pellentesque imperdiet volutpat molestie. Nam vehicula dui eu consequat finibus. Phasellus sed placerat lorem. Nulla pretium a magna sit amet iaculis. Aenean eget eleifend elit. Ut eleifend aliquet interdum. Cras pulvinar elit a dapibus iaculis. Nullam fermentum porttitor ultrices.", contacts[0].introduction)


    Assert.assertEquals("Amanda", contacts[1].firstName)
    Assert.assertEquals("Brady", contacts[1].lastName)
    Assert.assertEquals("Amanda Brady.png", contacts[1].avatar)
    Assert.assertEquals("Sales Representative", contacts[1].title)
    Assert.assertEquals("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam pulvinar neque in ullamcorper finibus. Aliquam ante orci, elementum non efficitur id, commodo ac velit. Proin non ornare neque, ac ornare odio. Nullam imperdiet tellus lacinia, semper justo vel, elementum metus. Aenean eget diam at quam dignissim varius. Nunc sed urna vehicula ipsum efficitur volutpat. Mauris vel augue ut magna tincidunt imperdiet. Integer sit amet vestibulum justo. Aenean placerat, nibh ac accumsan tincidunt, lorem arcu maximus justo, sed elementum tellus nisi id purus. Sed ac porttitor orci. Etiam et augue ullamcorper nibh mattis pharetra. Suspendisse ac mauris nec velit euismod rhoncus. Vestibulum tempor magna purus, id lacinia erat tempus eget.", contacts[1].introduction)

    Assert.assertEquals("Ashley", contacts[2].firstName)
    Assert.assertEquals("Mc Carthy", contacts[2].lastName)
    Assert.assertEquals("Ashley Mc Carthy.png", contacts[2].avatar)
    Assert.assertEquals("Sales Representative", contacts[2].title)
    Assert.assertEquals("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam pulvinar neque in ullamcorper finibus. Aliquam ante orci, elementum non efficitur id, commodo ac velit. Proin non ornare neque, ac ornare odio. Nullam imperdiet tellus lacinia, semper justo vel, elementum metus. Aenean eget diam at quam dignissim varius. Nunc sed urna vehicula ipsum efficitur volutpat. Mauris vel augue ut magna tincidunt imperdiet. Integer sit amet vestibulum justo. Aenean placerat, nibh ac accumsan tincidunt, lorem arcu maximus justo, sed elementum tellus nisi id purus. Sed ac porttitor orci. Etiam et augue ullamcorper nibh mattis pharetra. Suspendisse ac mauris nec velit euismod rhoncus. Vestibulum tempor magna purus, id lacinia erat tempus eget.", contacts[2].introduction)
  }

  @Test
  fun getContacts_wrongFormat() {
    whenever(mockDataSource.getRawData()).thenReturn(WRONG_FORMAT)

    val contacts = contactRepository.getContacts()
    verify(mockDataSource).getRawData()
    Assert.assertEquals(2, contacts.size)

    Assert.assertEquals("", contacts[0].firstName)
    Assert.assertEquals("", contacts[0].lastName)
    Assert.assertEquals("", contacts[0].avatar)
    Assert.assertEquals("", contacts[0].title)
    Assert.assertEquals("", contacts[0].introduction)


    Assert.assertEquals("", contacts[1].firstName)
    Assert.assertEquals("", contacts[1].lastName)
    Assert.assertEquals("", contacts[1].avatar)
    Assert.assertEquals("", contacts[1].title)
    Assert.assertEquals("", contacts[1].introduction)
  }

  @Test
  fun getContacts_mixture() {
    whenever(mockDataSource.getRawData()).thenReturn(MIXTURE)

    val contacts = contactRepository.getContacts()
    verify(mockDataSource).getRawData()
    Assert.assertEquals(3, contacts.size)

    Assert.assertEquals("Allan", contacts[0].firstName)
    Assert.assertEquals("Munger", contacts[0].lastName)
    Assert.assertEquals("Allan Munger.png", contacts[0].avatar)
    Assert.assertEquals("Writer", contacts[0].title)
    Assert.assertEquals("Ut malesuada sollicitudin tincidunt. Maecenas volutpat suscipit efficitur. Curabitur ut tortor sit amet lacus pellentesque convallis in laoreet lectus. Curabitur lorem velit, bibendum et vulputate vulputate, commodo in tortor. Curabitur a dapibus mauris. Vestibulum hendrerit euismod felis at hendrerit. Pellentesque imperdiet volutpat molestie. Nam vehicula dui eu consequat finibus. Phasellus sed placerat lorem. Nulla pretium a magna sit amet iaculis. Aenean eget eleifend elit. Ut eleifend aliquet interdum. Cras pulvinar elit a dapibus iaculis. Nullam fermentum porttitor ultrices.", contacts[0].introduction)


    Assert.assertEquals("", contacts[1].firstName)
    Assert.assertEquals("", contacts[1].lastName)
    Assert.assertEquals("", contacts[1].avatar)
    Assert.assertEquals("", contacts[1].title)
    Assert.assertEquals("", contacts[1].introduction)

    Assert.assertEquals("", contacts[2].firstName)
    Assert.assertEquals("", contacts[2].lastName)
    Assert.assertEquals("Ashley Mc Carthy.png", contacts[2].avatar)
    Assert.assertEquals("Sales Representative", contacts[2].title)
    Assert.assertEquals("", contacts[2].introduction)

  }

  companion object {
    private const val TEST_RAW_CONTACTS = "[\n" +
        "  {\n" +
        "    \"first_name\": \"Allan\",\n" +
        "    \"last_name\": \"Munger\",\n" +
        "    \"avatar_filename\": \"Allan Munger.png\",\n" +
        "    \"title\": \"Writer\",\n" +
        "    \"introduction\": \"Ut malesuada sollicitudin tincidunt. Maecenas volutpat suscipit efficitur. Curabitur ut tortor sit amet lacus pellentesque convallis in laoreet lectus. Curabitur lorem velit, bibendum et vulputate vulputate, commodo in tortor. Curabitur a dapibus mauris. Vestibulum hendrerit euismod felis at hendrerit. Pellentesque imperdiet volutpat molestie. Nam vehicula dui eu consequat finibus. Phasellus sed placerat lorem. Nulla pretium a magna sit amet iaculis. Aenean eget eleifend elit. Ut eleifend aliquet interdum. Cras pulvinar elit a dapibus iaculis. Nullam fermentum porttitor ultrices.\"\n" +
        "  },\n" +
        "  {\n" +
        "    \"first_name\": \"Amanda\",\n" +
        "    \"last_name\": \"Brady\",\n" +
        "    \"avatar_filename\": \"Amanda Brady.png\",\n" +
        "    \"title\": \"Sales Representative\",\n" +
        "    \"introduction\": \"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam pulvinar neque in ullamcorper finibus. Aliquam ante orci, elementum non efficitur id, commodo ac velit. Proin non ornare neque, ac ornare odio. Nullam imperdiet tellus lacinia, semper justo vel, elementum metus. Aenean eget diam at quam dignissim varius. Nunc sed urna vehicula ipsum efficitur volutpat. Mauris vel augue ut magna tincidunt imperdiet. Integer sit amet vestibulum justo. Aenean placerat, nibh ac accumsan tincidunt, lorem arcu maximus justo, sed elementum tellus nisi id purus. Sed ac porttitor orci. Etiam et augue ullamcorper nibh mattis pharetra. Suspendisse ac mauris nec velit euismod rhoncus. Vestibulum tempor magna purus, id lacinia erat tempus eget.\"\n" +
        "  },\n" +
        "  {\n" +
        "    \"first_name\": \"Ashley\",\n" +
        "    \"last_name\": \"Mc Carthy\",\n" +
        "    \"avatar_filename\": \"Ashley Mc Carthy.png\",\n" +
        "    \"title\": \"Sales Representative\",\n" +
        "    \"introduction\": \"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam pulvinar neque in ullamcorper finibus. Aliquam ante orci, elementum non efficitur id, commodo ac velit. Proin non ornare neque, ac ornare odio. Nullam imperdiet tellus lacinia, semper justo vel, elementum metus. Aenean eget diam at quam dignissim varius. Nunc sed urna vehicula ipsum efficitur volutpat. Mauris vel augue ut magna tincidunt imperdiet. Integer sit amet vestibulum justo. Aenean placerat, nibh ac accumsan tincidunt, lorem arcu maximus justo, sed elementum tellus nisi id purus. Sed ac porttitor orci. Etiam et augue ullamcorper nibh mattis pharetra. Suspendisse ac mauris nec velit euismod rhoncus. Vestibulum tempor magna purus, id lacinia erat tempus eget.\"\n" +
        "  }" +
        "]"

    private const val WRONG_FORMAT = "[{},{}]"

    private const val MIXTURE = "[\n" +
        "  {\n" +
        "    \"first_name\": \"Allan\",\n" +
        "    \"last_name\": \"Munger\",\n" +
        "    \"avatar_filename\": \"Allan Munger.png\",\n" +
        "    \"title\": \"Writer\",\n" +
        "    \"introduction\": \"Ut malesuada sollicitudin tincidunt. Maecenas volutpat suscipit efficitur. Curabitur ut tortor sit amet lacus pellentesque convallis in laoreet lectus. Curabitur lorem velit, bibendum et vulputate vulputate, commodo in tortor. Curabitur a dapibus mauris. Vestibulum hendrerit euismod felis at hendrerit. Pellentesque imperdiet volutpat molestie. Nam vehicula dui eu consequat finibus. Phasellus sed placerat lorem. Nulla pretium a magna sit amet iaculis. Aenean eget eleifend elit. Ut eleifend aliquet interdum. Cras pulvinar elit a dapibus iaculis. Nullam fermentum porttitor ultrices.\"\n" +
        "  },{},  {\n" +
        "    \"first_name\": \"\",\n" +
        "    \"last_name\": \"\",\n" +
        "    \"avatar_filename\": \"Ashley Mc Carthy.png\",\n" +
        "    \"title\": \"Sales Representative\"\n" +
        "  }]"
  }
}