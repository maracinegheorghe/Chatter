package com.chatter.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.chatter.classes.Contact;
import com.chatter.classes.User;

import java.util.ArrayList;

public class ContactsViewModel extends ViewModel {

    MutableLiveData<ArrayList<Contact>> contactsLiveData = new MutableLiveData<>();

    public ContactsViewModel(ArrayList<Contact> contacts) {
        contactsLiveData.postValue(contacts);
    }

    public ContactsViewModel() {
    }
    public void setContactsLiveData(MutableLiveData<ArrayList<Contact>> contactsLiveData) {
        this.contactsLiveData = contactsLiveData;
    }

    public MutableLiveData<ArrayList<Contact>> getContactsLiveData() {
        return contactsLiveData;
    }
}