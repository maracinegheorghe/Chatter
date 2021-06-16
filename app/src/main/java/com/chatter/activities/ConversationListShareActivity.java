package com.chatter.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatter.R;
import com.chatter.adapters.ConversationsShareAdapter;
import com.chatter.classes.Conversation;
import com.chatter.classes.Message;
import com.chatter.classes.User;
import com.chatter.viewModels.ConversationsViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

//todo: rezolvat cu redimensionarea imaginii din share
public class ConversationListShareActivity extends AppCompatActivity {

    ConversationsShareAdapter conversationsAdapter;
    ConversationsViewModel conversationsViewModel;
    RecyclerView recyclerView;
    ArrayList<Bitmap> imagesToShare = new ArrayList<>();
    String linkToShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list_share);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        }
        conversationsAdapter = new ConversationsShareAdapter(imagesToShare, linkToShare);
        recyclerView = findViewById(R.id.recycle_conversation_list_share);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(conversationsAdapter);
        //adauga observer pentru lista de conversatii
        conversationsViewModel = ViewModelProviders.of(this).get(ConversationsViewModel.class);
        conversationsViewModel.getConversationsLiveData().observe(this,conversationsListUpdateObserver);

    }
    Observer<ArrayList<Conversation>> conversationsListUpdateObserver = new Observer<ArrayList<Conversation>>() {
        @Override
        public void onChanged(ArrayList<Conversation> conversationsArrayList) {
            conversationsAdapter.notifyDataSetChanged();
        }
    };

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
           this.linkToShare = sharedText;
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            try
            {
                this.imagesToShare.add(MediaStore.Images.Media.getBitmap(this.getContentResolver() , Uri.parse(imageUri.toString())));
            }
            catch (Exception e)
            {
            }
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            for (Uri imageUri:
                 imageUris) {
                try {
                    this.imagesToShare.add(MediaStore.Images.Media.getBitmap(this.getContentResolver() , Uri.parse(imageUri.toString())));
                }
                catch (Exception e)
                {
                }
            }
        }
    }
}