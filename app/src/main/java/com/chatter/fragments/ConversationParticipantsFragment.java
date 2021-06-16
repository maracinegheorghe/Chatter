package com.chatter.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chatter.R;
import com.chatter.adapters.ContactsAdapter;
import com.chatter.classes.Contact;
import com.chatter.classes.Conversation;
import com.chatter.classes.User;
import com.chatter.viewModels.ContactsViewModel;

import java.util.ArrayList;

public class ConversationParticipantsFragment extends Fragment {

    Conversation conversation;
    ContactsAdapter contactsAdapter;
    ContactsViewModel contactsViewModel;
    RecyclerView recyclerView;
    Observer<ArrayList<Contact>> contactsListUpdateObserver = new Observer<ArrayList<Contact>>() {
        @Override
        public void onChanged(ArrayList<Contact> contactsArrayList) {
            contactsAdapter.notifyDataSetChanged();
        }
    };

    public ConversationParticipantsFragment() {
        super(R.layout.fragment_conversation_participants);
    }

    public ConversationParticipantsFragment(String conversationKey) {
        super(R.layout.fragment_conversation_participants);
        this.conversation = User.getConversation(conversationKey);
    }

    public static ConversationParticipantsFragment newInstance(String conversationKey) {
        ConversationParticipantsFragment fragment = new ConversationParticipantsFragment();
        Bundle args = new Bundle();
        args.putString("conversationKey", conversationKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.conversation = User.getConversation(getArguments().getString("conversationKey"));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recycle_conversation_participants_list);
        contactsAdapter = new ContactsAdapter(conversation.getParticipantsList());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(contactsAdapter);

        //adauga observer pentru lista de conversatii
        contactsViewModel = ViewModelProviders.of(this).get(ContactsViewModel.class);
        contactsViewModel.getContactsLiveData().postValue(conversation.getParticipantsList());
        contactsViewModel.getContactsLiveData().observe(getViewLifecycleOwner(), contactsListUpdateObserver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conversation_participants, container, false);
    }
}