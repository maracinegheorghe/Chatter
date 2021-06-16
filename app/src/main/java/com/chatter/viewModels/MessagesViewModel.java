package com.chatter.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.chatter.classes.Message;

import java.util.ArrayList;

public class MessagesViewModel extends ViewModel {
    MutableLiveData<ArrayList<Message>> messagesLiveData;

    public MessagesViewModel() {
        messagesLiveData = new MutableLiveData<>();

    }

    public void setMessagesLiveData(MutableLiveData<ArrayList<Message>> messagesLiveData) {
        this.messagesLiveData = messagesLiveData;
    }

    public MutableLiveData<ArrayList<Message>> getMessagesLiveData() {
        return messagesLiveData;
    }
}
