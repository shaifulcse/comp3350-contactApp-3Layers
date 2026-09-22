package com.example.contacts;

import android.app.Application;

import com.example.contacts.logic.ContactManager;
import com.example.contacts.persistence.ContactPersistence;
import com.example.contacts.persistence.fake.FakeContactPersistence;
import com.example.contacts.persistence.sqlite.SQLiteContactPersistence;

/**
 * Composition root: the only place that knows which concrete persistence
 * implementation is used. It builds the persistence tier, injects it into
 * the logic tier, and exposes the logic tier to the presentation tier.
 */
public class ContactsApp extends Application {

    private ContactManager contactManager;
    private String databaseLabel;

    @Override
    public void onCreate() {
        super.onCreate();

        ContactPersistence persistence;
        if (BuildConfig.USE_FAKE_DB) {
            persistence = new FakeContactPersistence();
            databaseLabel = "Fake (in-memory)";
        } else {
            persistence = new SQLiteContactPersistence(this);
            databaseLabel = "Real (SQLite)";
        }

        contactManager = new ContactManager(persistence);
    }

    public ContactManager getContactManager() {
        return contactManager;
    }

    public String getDatabaseLabel() {
        return databaseLabel;
    }
}
