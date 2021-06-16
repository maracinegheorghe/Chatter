package com.chatter.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.chatter.R;
import com.chatter.activities.ContactListActivity;
import com.chatter.activities.ConversationActivity;
import com.chatter.classes.Contact;
import com.chatter.classes.User;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactHolder> {

    public static ArrayList<Contact> selectedContacts = new ArrayList<>();
    public ArrayList<Contact> contacts = new ArrayList<>();
    public ContactsAdapter(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_view, parent, false);

        return new ContactHolder(view);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    @Override
    public void onBindViewHolder(ContactHolder viewHolder, int position) {
        viewHolder.getTextViewContactEmail().setText(contacts.get(position).getEmail());

        Class contextClass = viewHolder.itemView.getContext().getClass();
        if (contextClass.equals(ContactListActivity.class)) {
            viewHolder.getTextViewContactEmail().setOnClickListener(v -> {
                contacts.get(position).select();
                if (contacts.get(position).isSelected()) {
                    selectedContacts.add(contacts.get(position));
                    Toast.makeText(v.getContext(), "Selectat", Toast.LENGTH_SHORT).show();
                    v.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.black));
                } else {
                    selectedContacts.remove(contacts.get(position));
                    Toast.makeText(v.getContext(), "Deselectat", Toast.LENGTH_SHORT).show();
                    v.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.white));
                }
            });
        }
        if (contextClass.equals(ConversationActivity.class)) {
            //TODO:DESCHIDE DIALOG PENTRU ADAUGARE LA CONTACTE
        }

    }

    public static class ContactHolder extends RecyclerView.ViewHolder {
        private final TextView textViewContactEmail;

        public ContactHolder(View view) {
            super(view);
            textViewContactEmail = view.findViewById(R.id.contactEmail);
        }

        public TextView getTextViewContactEmail() {
            return textViewContactEmail;
        }
    }
    
}
