package com.chatter.viewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.chatter.R;
import com.google.android.gms.maps.MapView;

public class MessageWithLocationViewHolder extends MessageViewHolder {
    private final MapView mapView;
    private final RelativeLayout relativeLayout;

    public MessageWithLocationViewHolder(@NonNull View view) {
        super(view);
        mapView = view.findViewById(R.id.mapView);
        relativeLayout = itemView.findViewById(R.id.layout_message_view);
    }

    public MapView getMapView() {
        return mapView;
    }


}
