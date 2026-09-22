package com.example.contacts.logic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.example.contacts.objects.Contact;
import com.example.contacts.persistence.fake.FakeContactPersistence;

import org.junit.Before;
import org.junit.Test;

/** Logic tier tested against the fake database - no emulator needed. */
public class ContactManagerTest {

    private ContactManager manager;

    @Before
    public void setUp() {
        manager = new ContactManager(new FakeContactPersistence(false));
    }

    @Test
    public void addContact_trimsAndStores() throws Exception {
        Contact c = manager.addContact("  Jane   Doe ", " JANE@Example.com ", "204-555-0199");
        assertEquals("Jane Doe", c.getName());
        assertEquals("jane@example.com", c.getEmail());
        assertEquals(1, manager.getAllContacts().size());
    }

    @Test
    public void addContact_rejectsBadEmail() {
        try {
            manager.addContact("Jane", "not-an-email", "204-555-0199");
            fail("Expected validation error");
        } catch (ContactValidationException e) {
            assertEquals(ContactValidationException.Field.EMAIL, e.getField());
        }
    }

    @Test
    public void addContact_rejectsShortPhone() {
        try {
            manager.addContact("Jane", "jane@example.com", "123");
            fail("Expected validation error");
        } catch (ContactValidationException e) {
            assertEquals(ContactValidationException.Field.PHONE, e.getField());
        }
    }

    @Test
    public void updateContact_changesFields() throws Exception {
        Contact c = manager.addContact("Jane", "jane@example.com", "204-555-0199");
        assertTrue(manager.updateContact(c.getId(), "Janet", "janet@example.com", "204-555-0100"));
        assertEquals("Janet", manager.getContact(c.getId()).getName());
    }

    @Test
    public void updateContact_unknownIdReturnsFalse() throws Exception {
        assertFalse(manager.updateContact(999, "X Y", "x@example.com", "204-555-0100"));
    }

    @Test
    public void deleteContact_removesIt() throws Exception {
        Contact c = manager.addContact("Jane", "jane@example.com", "204-555-0199");
        assertTrue(manager.deleteContact(c.getId()));
        assertNull(manager.getContact(c.getId()));
    }

    @Test
    public void search_matchesNameEmailOrPhone() throws Exception {
        manager.addContact("Jane Doe", "jane@example.com", "204-555-0199");
        manager.addContact("Sam Lee", "sam@work.org", "431-555-0123");
        assertEquals(1, manager.searchContacts("jane").size());
        assertEquals(1, manager.searchContacts("work.org").size());
        assertEquals(1, manager.searchContacts("431").size());
        assertEquals(2, manager.searchContacts("  ").size());
    }
}
