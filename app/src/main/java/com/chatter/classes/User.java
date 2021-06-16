package com.chatter.classes;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class User {
    @Exclude
    public static boolean listenerExists = false;
    @Exclude
    private static final MutableLiveData<ArrayList<Contact>> contacts = new MutableLiveData<>(new ArrayList<>());
    @Exclude
    private static final MutableLiveData<ArrayList<Conversation>> conversations = new MutableLiveData<>(new ArrayList<>());
    private static String email;

    private User() {
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        User.email = email;
    }

    public static MutableLiveData<ArrayList<Contact>> getContacts() {
        return contacts;
    }

    public static void setContacts(Map<String, Contact> contactsMap) {
        ArrayList<Contact> contactsAux = new ArrayList<>();
        for (String key : contactsMap.keySet()) {
            Objects.requireNonNull(contactsMap.get(key)).setKey(key);
            contactsAux.add(contactsMap.get(key));
        }
        contacts.postValue(contactsAux);
    }

    public static void addContact(Contact newContact) {
        ArrayList<Contact> newContactList;
        newContactList = contacts.getValue();
        newContactList.add(newContact);
        contacts.postValue(newContactList);
    }

    public static void removeContact(String contactKey) {
        ArrayList<Contact> newContactList;
        newContactList = contacts.getValue();
        newContactList.removeIf(c -> c.getKey().equals(contactKey));
        contacts.postValue(newContactList);
    }

    public static MutableLiveData<ArrayList<Conversation>> getConversations() {
        return User.conversations;
    }

    public static void setConversations(Map<String, Conversation> conversationsMap) {
        ArrayList<Conversation> conversationsAux = new ArrayList<>();
        for (String key : conversationsMap.keySet()) {
            Objects.requireNonNull(conversationsMap.get(key)).setKey(key);
            conversationsAux.add(conversationsMap.get(key));
        }
        conversations.postValue(conversationsAux);
    }

    public static void addConversation(Conversation newConversation) {
        ArrayList<Conversation> newConversationList;
        newConversationList = conversations.getValue();
        newConversationList.add(newConversation);
        conversations.postValue(newConversationList);
    }

    public static void removeConversation(String conversationKey) {
        ArrayList<Conversation> newConversationList;
        newConversationList = conversations.getValue();
        newConversationList.removeIf(c -> c.getKey().equals(conversationKey));
        conversations.postValue(newConversationList);
    }


    public static Map<String, Object> getContactsHashMap() {
        Map<String, Object> contactHashMap = new HashMap<>();
        for (Contact c :
                User.contacts.getValue()) {
            contactHashMap.put(c.getKey(), c);
        }

        return contactHashMap;
    }

    public static Conversation getConversation(String conversationKey) {
        Optional<Conversation> result = conversations.getValue().stream().filter(c -> c.getKey().equals(conversationKey)).findFirst();
        return result.orElse(null);
    }

    public static void logOut() {
        FirebaseAuth.getInstance().signOut();
        contacts.postValue(new ArrayList<>());
        conversations.postValue(new ArrayList<>());
        email = null;
    }

}
