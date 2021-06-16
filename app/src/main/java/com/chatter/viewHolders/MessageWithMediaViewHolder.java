package com.chatter.viewHolders;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.chatter.R;


public class MessageWithMediaViewHolder extends MessageViewHolder {
    private final ImageView imageView;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
        //this.imageView.setImageBitmap(this.image);
    }

    private Bitmap image;

    private final RelativeLayout relativeLayout;

    public MessageWithMediaViewHolder(@NonNull View view) {
        super(view);
        imageView = view.findViewById(R.id.imageView);
        relativeLayout = itemView.findViewById(R.id.layout_message_view);
    }

    public ImageView getImageView() {
        return imageView;
    }

}