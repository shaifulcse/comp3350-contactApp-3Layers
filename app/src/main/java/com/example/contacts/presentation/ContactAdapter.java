package com.example.contacts.presentation;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.contacts.objects.Contact;

import java.util.ArrayList;
import java.util.List;

/** Two-line list rows: name on top, email and phone below. */
public class ContactAdapter extends ArrayAdapter<Contact> {

    public ContactAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_2, android.R.id.text1,
                new ArrayList<Contact>());
    }

    public void setContacts(List<Contact> contacts) {
        setNotifyOnChange(false);
        clear();
        addAll(contacts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        Contact contact = getItem(position);
        if (contact != null) {
            TextView line1 = view.findViewById(android.R.id.text1);
            TextView line2 = view.findViewById(android.R.id.text2);
            line1.setText(contact.getName());
            line2.setText(contact.getEmail() + "  •  " + contact.getPhone());
        }
        return view;
    }
}
