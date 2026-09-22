package com.example.contacts.logic;

import com.example.contacts.objects.Contact;
import com.example.contacts.persistence.ContactPersistence;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Logic tier: business rules (validation, normalization) and orchestration.
 * Pure Java - it receives its persistence through the constructor
 * (dependency injection), so it doesn't know whether the database is fake or real.
 */
public class ContactManager {

    public static final int MAX_NAME_LENGTH = 100;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_CHARS_PATTERN =
            Pattern.compile("^\\+?[0-9 ().-]+$");
    private static final int MIN_PHONE_DIGITS = 7;
    private static final int MAX_PHONE_DIGITS = 15;

    private final ContactPersistence persistence;

    public ContactManager(ContactPersistence persistence) {
        if (persistence == null) {
            throw new IllegalArgumentException("persistence must not be null");
        }
        this.persistence = persistence;
    }

    public List<Contact> getAllContacts() {
        return persistence.getAllContacts();
    }

    public Contact getContact(long id) {
        return persistence.getContactById(id);
    }

    /** Blank query returns every contact. */
    public List<Contact> searchContacts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return persistence.getAllContacts();
        }
        return persistence.searchContacts(query.trim());
    }

    public Contact addContact(String name, String email, String phone)
            throws ContactValidationException {
        Contact contact = validate(0, name, email, phone);
        long id = persistence.insertContact(contact);
        contact.setId(id);
        return contact;
    }

    /** @return false if no contact with that id exists. */
    public boolean updateContact(long id, String name, String email, String phone)
            throws ContactValidationException {
        Contact contact = validate(id, name, email, phone);
        return persistence.updateContact(contact);
    }

    /** @return false if no contact with that id exists. */
    public boolean deleteContact(long id) {
        return persistence.deleteContact(id);
    }

    /** Trims, normalizes and validates input, then builds a Contact. */
    private Contact validate(long id, String name, String email, String phone)
            throws ContactValidationException {
        String n = name == null ? "" : name.trim().replaceAll("\\s+", " ");
        String e = email == null ? "" : email.trim().toLowerCase();
        String p = phone == null ? "" : phone.trim();

        if (n.isEmpty()) {
            throw new ContactValidationException(
                    ContactValidationException.Field.NAME, "Name is required");
        }
        if (n.length() > MAX_NAME_LENGTH) {
            throw new ContactValidationException(ContactValidationException.Field.NAME,
                    "Name must be at most " + MAX_NAME_LENGTH + " characters");
        }
        if (e.isEmpty()) {
            throw new ContactValidationException(
                    ContactValidationException.Field.EMAIL, "Email is required");
        }
        if (!EMAIL_PATTERN.matcher(e).matches()) {
            throw new ContactValidationException(
                    ContactValidationException.Field.EMAIL, "Email address is not valid");
        }
        if (p.isEmpty()) {
            throw new ContactValidationException(
                    ContactValidationException.Field.PHONE, "Phone is required");
        }
        int digits = p.replaceAll("[^0-9]", "").length();
        if (!PHONE_CHARS_PATTERN.matcher(p).matches()
                || digits < MIN_PHONE_DIGITS || digits > MAX_PHONE_DIGITS) {
            throw new ContactValidationException(ContactValidationException.Field.PHONE,
                    "Phone must contain " + MIN_PHONE_DIGITS + "-" + MAX_PHONE_DIGITS + " digits");
        }
        return new Contact(id, n, e, p);
    }
}
