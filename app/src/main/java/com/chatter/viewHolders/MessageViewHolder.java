package com.chatter.viewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.chatter.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    private final TextView textViewMessageContent;
    private final TextView textViewMessageSender;
    private final TextView textViewMessageTimestamp;
    private final CardView cardView;


    private final RelativeLayout relativeLayout;

    public MessageViewHolder(@NonNull View view) {
        super(view);
        textViewMessageContent = view.findViewById(R.id.textViewMessageContent);
        textViewMessageSender = view.findViewById(R.id.textViewMessageSender);
        textViewMessageTimestamp = view.findViewById(R.id.textViewMessageTimestamp);
        cardView = view.findViewById(R.id.card_view_message);
        relativeLayout = itemView.findViewById(R.id.layout_message_view);
    }

    public CardView getCardView() {
        return cardView;
    }

    public TextView getTextViewMessageContent() {
        return textViewMessageContent;
    }

    public TextView getTextViewMessageSender() {
        return textViewMessageSender;
    }

    public TextView getTextViewMessageTimestamp() {
        return textViewMessageTimestamp;
    }

    public RelativeLayout getRelativeLayout() {
        return relativeLayout;
    }
}