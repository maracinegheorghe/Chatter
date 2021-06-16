package com.chatter.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chatter.R;
import com.chatter.activities.ConversationActivity;
import com.chatter.classes.Contact;
import com.chatter.classes.Conversation;
import com.chatter.classes.User;

import java.util.ArrayList;


public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {

    public ConversationsAdapter() {
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.conversation_view, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        Conversation conversation = User.getConversations().getValue().get(position);
        if (conversation.getParticipantsList().size() == 2) {
            for (Contact contact : conversation.getParticipantsList()) {
                if (!contact.getEmail().equals(User.getEmail())) {
                    viewHolder.getTextViewConversationTitle().setText(contact.getEmail());
                    break;
                }
            }
        } else {
            viewHolder.getTextViewConversationTitle().setText(conversation.getName());
        }

        viewHolder.getTextViewLastMessageSender().setText(conversation.getLastMessage().getSenderEmail());
        viewHolder.getTextViewLastMessageContent().setText(conversation.getLastMessage().getTextContent());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openConversationIntent = new Intent(v.getContext(), ConversationActivity.class);
                openConversationIntent.putExtra("conversation_key", conversation.getKey());
                v.getContext().startActivity(openConversationIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return User.getConversations().getValue().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewConversationTitle;
        private final TextView textViewLastMessageSender;
        private final TextView textViewLastMessageContent;
        private final TextView textViewLastMessageTimestamp;

        public ViewHolder(View view) {
            super(view);
            textViewConversationTitle = view.findViewById(R.id.textViewConversationTitle);
            textViewLastMessageSender = view.findViewById(R.id.textViewLastMessageSender);
            textViewLastMessageContent = view.findViewById(R.id.textViewLastMessageContent);
            textViewLastMessageTimestamp = view.findViewById(R.id.textViewLastMessageTimestamp);
        }

        public TextView getTextViewConversationTitle() {
            return textViewConversationTitle;
        }

        public TextView getTextViewLastMessageSender() {
            return textViewLastMessageSender;
        }

        public TextView getTextViewLastMessageContent() {
            return textViewLastMessageContent;
        }

        public TextView getTextViewLastMessageTimestamp() {
            return textViewLastMessageTimestamp;
        }
    }

}