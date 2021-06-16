package com.chatter.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.chatter.classes.Conversation;
import com.chatter.classes.User;

import java.util.ArrayList;

public class ConversationsViewModel extends ViewModel {
    MutableLiveData<ArrayList<Conversation>> conversationsLiveData;

    public ConversationsViewModel() {
        conversationsLiveData = new MutableLiveData<>();
        conversationsLiveData = User.getConversations();
    }

    public MutableLiveData<ArrayList<Conversation>> getConversationsLiveData() {
        return conversationsLiveData;
    }
}
