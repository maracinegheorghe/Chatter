package com.chatter.viewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class MediaLinksViewModel extends ViewModel {

    public MutableLiveData<ArrayList<String>> getMediaLinksLiveData() {
        return mediaLinksLiveData;
    }

    public void setMediaLinksLiveData(MutableLiveData<ArrayList<String>> mediaLinksLiveData) {
        this.mediaLinksLiveData = mediaLinksLiveData;
    }

    MutableLiveData<ArrayList<String>> mediaLinksLiveData = new MutableLiveData<>();

    public MediaLinksViewModel(ArrayList<String> mediaLinks) {
        mediaLinksLiveData.postValue(mediaLinks);
    }

    public MediaLinksViewModel() {
    }

}
