package com.chatter.fragments;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.chatter.DAO.ChatterDatabase;
import com.chatter.DAO.MediaDAO;
import com.chatter.R;
import com.chatter.activities.MapsActivity;
import com.chatter.adapters.MessagesAdapter;
import com.chatter.classes.Conversation;
import com.chatter.classes.Location;
import com.chatter.classes.Media;
import com.chatter.classes.Message;
import com.chatter.classes.User;
import com.chatter.viewModels.MessagesViewModel;
import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.app.Activity.RESULT_OK;


//TODO: incarcarea doar a unei parti din elemente
//TODO: incarcarea a mai multe elemente la scroll in sus

public class MessagesFragment extends Fragment {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_VIDEO_CAPTURE = 2;
    static final int REQUEST_LOCATION = 10;
    static final int REQUEST_CAMERA_PERMISSION = 1;
    File uploadFile;

    Conversation conversation;
    MessagesAdapter messagesAdapter;
    MessagesViewModel messagesViewModel;
    RecyclerView recyclerView;

    Observer<ArrayList<Message>> messagesListUpdateObserver = new Observer<ArrayList<Message>>() {
        @Override
        public void onChanged(ArrayList<Message> messagesArrayList) {
            if (conversation.getMessages().getValue().size() != 0) {
                messagesAdapter.notifyItemInserted(conversation.getMessages().getValue().size() - 1);
                recyclerView.smoothScrollToPosition(conversation.getMessages().getValue().size() - 1);
            }
        }
    };

    public MessagesFragment() {
        super(R.layout.fragment_conversation_messages);
    }

    public MessagesFragment(String conversationKey) {
        super(R.layout.fragment_conversation_messages);
        this.conversation = User.getConversation(conversationKey);
    }

    public static MessagesFragment newInstance(String conversationKey) {
        MessagesFragment fragment = new MessagesFragment();
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

    private void uploadMedia(String uploadFilePath, String mediaType) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //referinta la unde se va salva mesajul
        DatabaseReference newMessageRef = database.getReference().child("messages").child(conversation.getKey()).push();
        //referinta la unde se va salva poza care va avea cheia mesajului ca denumire
        StorageReference mediaRef = storageRef.child("media").child(newMessageRef.getKey());

        byte[] mediaBytes = new byte[0];
        if(mediaType.equals("image/jpeg")){
            Bitmap imageBitmap = BitmapFactory.decodeFile(uploadFilePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            mediaBytes = baos.toByteArray();
        }
        if(mediaType.equals("video/mp4")) {
            try {
                FileInputStream is = new FileInputStream(uploadFilePath);
                mediaBytes = IOUtils.toByteArray(is);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        UploadTask uploadTask = mediaRef.putBytes(mediaBytes);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    //genereaza uri
                    return mediaRef.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Message newMessage = new Message(mediaRef.getPath(), true);
                        newMessageRef.setValue(newMessage);

                        Media media = new Media(newMessageRef.getKey(), mediaType, uploadFilePath);
                        saveMediaRefference(getActivity().getBaseContext(), media);
                    } else {
                        // Handle failures
                        // ...
                    }
                });
            }
        });
    }

    private void sendMedia(String mediaType) {
        String suffix;
        String action;
        int requestType;

        if(mediaType.contains("image")){
            suffix = ".jpg";
            action = MediaStore.ACTION_IMAGE_CAPTURE;
            requestType = REQUEST_IMAGE_CAPTURE;
        } else {
            suffix = ".mp4";
            action = MediaStore.ACTION_VIDEO_CAPTURE;
            requestType = REQUEST_VIDEO_CAPTURE;
        }

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            File file = null;
            try {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                file = File.createTempFile(timeStamp, suffix, storageDir);
            } catch (Exception e) {
                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
            if (file != null) {
                Uri uriForFile = FileProvider.getUriForFile(getContext(),"com.example.android.fileprovider", file);

                Intent getMediaIntent = new Intent(action);
                getMediaIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile);
                try {
                    uploadFile = file;
                    startActivityForResult(getMediaIntent, requestType);
                } catch (ActivityNotFoundException e) {
                    // display error state to the user
                }
            }

        }  else {
            ActivityCompat.requestPermissions(this.getActivity(), new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    private void sendLocation() {
        Intent sendLocationIntent = new Intent(this.getContext(), MapsActivity.class);
        try {
            startActivityForResult(sendLocationIntent, REQUEST_LOCATION);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    private void sendMessage() {
        EditText inputEditTextMessage = getView().findViewById(R.id.editTextMessage);
        String messageContent = inputEditTextMessage.getText().toString();

        Message newMessage = new Message(messageContent);
        sendMessageToFirebase(newMessage);
        inputEditTextMessage.setText("");
    }
    private void sendMessageToFirebase( Message newMessage) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference messagesRef = database.getReference().child("messages").child(conversation.getKey()).push();
        messagesRef.setValue(newMessage);

        DatabaseReference lastMessageRef = database.getReference()
                .child("conversations").child(conversation.getKey())
                .child("lastMessage");
        lastMessageRef.setValue(newMessage);
    }
    private void sendLocationMessage(LatLng coordonates) {
        Message newMessage = new Message(new Location(coordonates));
        sendMessageToFirebase(newMessage);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        FloatingActionButton buttonSendMessage = view.findViewById(R.id.buttonSendMessage);
        buttonSendMessage.setOnClickListener(v -> sendMessage());

        FloatingActionButton buttonSendOthers = view.findViewById(R.id.buttonSendOthers);
        buttonSendOthers.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this.getContext(), v);
            MenuInflater inflater = popup.getMenuInflater();
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.itemSendPhoto:
                            sendMedia("image/jpeg");
                            break;
                        case R.id.itemSendLocation:
                            sendLocation();
                            break;
                        case R.id.itemSendVideo:
                            sendMedia("video/mp4");
                            break;
                    }
                    return true;
                }
            });
            inflater.inflate(R.menu.conversation_send_additional_items, popup.getMenu());
            popup.show();
        });

        recyclerView = view.findViewById(R.id.recycle_message_list);
        messagesAdapter = new MessagesAdapter(conversation.getMessages().getValue());

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(messagesAdapter);
        recyclerView.smoothScrollToPosition(messagesAdapter.getItemCount());
        ((LinearLayoutManager) recyclerView.getLayoutManager()).setStackFromEnd(true);
        //adauga observer pentru lista de conversatii
        messagesViewModel = ViewModelProviders.of(this).get(MessagesViewModel.class);
        messagesViewModel.setMessagesLiveData(conversation.getMessages());
        messagesViewModel.getMessagesLiveData().observe(getViewLifecycleOwner(), messagesListUpdateObserver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversation_messages, container, false);
    }

    //salvarea fisierelor media in baza de date room
    private void saveMediaRefference(Context context, Media media) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ChatterDatabase db = Room.databaseBuilder(context,
                        ChatterDatabase.class, "media-database").build();

                MediaDAO mediaDAO = db.mediaDAO();
                mediaDAO.insertMedia(media);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            uploadMedia(uploadFile.getAbsolutePath(), "image/jpeg");
        }
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            uploadMedia(uploadFile.getAbsolutePath(),"video/mp4");
        }
        if (requestCode == REQUEST_LOCATION && resultCode == 1) {
            Bundle extras = data.getExtras();
            LatLng location = (LatLng) extras.get("selectedLocation");
            sendLocationMessage(location);
        }
    }


}