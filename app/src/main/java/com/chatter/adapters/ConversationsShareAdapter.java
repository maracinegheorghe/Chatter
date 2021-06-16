package com.chatter.adapters;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chatter.R;
import com.chatter.classes.Contact;
import com.chatter.classes.Conversation;
import com.chatter.classes.Message;
import com.chatter.classes.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class ConversationsShareAdapter extends RecyclerView.Adapter<ConversationsShareAdapter.ViewHolder> {
    private final ArrayList<Conversation> conversations;
    private final ArrayList<Bitmap> sharedImages;
    private final String sharedLink;

    public ConversationsShareAdapter(ArrayList<Bitmap> sharedImages, String sharedLink) {
        this.conversations = User.getConversations().getValue();
        this.sharedImages = sharedImages;
        this.sharedLink = sharedLink;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.conversation_view_share, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        Conversation conversation = this.conversations.get(position);
        if (conversation.getParticipantsList().size() == 2) {
            for (Contact contact : conversation.getParticipantsList()) {
                if (!contact.getEmail().equals(User.getEmail())) {
                    viewHolder.textViewConversationTitle.setText(contact.getEmail());
                    break;
                }
            }
        } else {
            viewHolder.textViewConversationTitle.setText(conversation.getName());
        }
        viewHolder.buttonShare.setOnClickListener(v -> {
            //todo: stilizare buton
            viewHolder.buttonShare.setImageResource(R.drawable.fui_ic_check_circle_black_128dp);
            viewHolder.buttonShare.setClickable(false);
            Message newMessage;
            if (sharedLink != null) {
                newMessage = new Message(sharedLink);
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference convRef = database.getReference().child("messages").child(conversation.getKey()).push();
                convRef.setValue(newMessage);
            } else {
                for (Bitmap image:
                     sharedImages) {
                    uploadPhoto(image, conversation.getKey());
                }
            }
            Toast.makeText(v.getContext(), "Click", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return User.getConversations().getValue().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewConversationTitle;
        private final FloatingActionButton buttonShare;

        public ViewHolder(View view) {
            super(view);
            textViewConversationTitle = view.findViewById(R.id.textViewConversationTitle);
            buttonShare = view.findViewById(R.id.buttonShare);
            buttonShare.setImageResource(R.mipmap.ic_launcher_round);
        }
    }

    private void uploadPhoto(Bitmap imageBitmap, String conversationKey){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //referinta la unde se va salva mesajul
        DatabaseReference newMessageRef = database.getReference().child("messages").child(conversationKey).push();
        //referinta la unde se va salva poza care va avea cheia mesajului ca denumire
        StorageReference imageRef = storageRef.child("images").child(newMessageRef.getKey());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(bytes);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    //genereaza uri
                    return imageRef.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Message newMessage = new Message(imageRef.getPath(),true);
                        newMessageRef.setValue(newMessage);
                    } else {
                        // Handle failures
                        // ...
                    }
                });

            }
        });
    }
}