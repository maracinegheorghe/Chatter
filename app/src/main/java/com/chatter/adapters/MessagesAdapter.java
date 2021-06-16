package com.chatter.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.chatter.DAO.ChatterDatabase;
import com.chatter.DAO.MediaDAO;
import com.chatter.R;
import com.chatter.classes.Media;
import com.chatter.classes.Message;
import com.chatter.classes.User;
import com.chatter.viewHolders.MessageViewHolder;
import com.chatter.viewHolders.MessageWithLocationViewHolder;
import com.chatter.viewHolders.MessageWithMediaViewHolder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

//TODO: BUFFER GLOBAL IN ACTIVITATE PENTRU A TRIMITE TEXT SI POZA IN ACELAS TIMP
public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final int SIMPLE_TEXT = 0, WITH_MEDIA = 1, LOCATION = 2;
    private final ArrayList<Message> messages;
    public MessagesAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case WITH_MEDIA:
                View mediaView = inflater.inflate(R.layout.message_with_media_view, viewGroup, false);
                viewHolder = new MessageWithMediaViewHolder(mediaView);
                break;
            case LOCATION:
                View locationView = inflater.inflate(R.layout.message_with_location_view, viewGroup, false);
                viewHolder = new MessageWithLocationViewHolder(locationView);
                break;
            default:
                View defaultView = inflater.inflate(R.layout.message_view, viewGroup, false);
                viewHolder = new MessageViewHolder(defaultView);
                break;
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case SIMPLE_TEXT:
                MessageViewHolder vhs = (MessageViewHolder) viewHolder;
                bindSimpleText(vhs, position);
                break;
            case WITH_MEDIA:
                MessageWithMediaViewHolder vhm = (MessageWithMediaViewHolder) viewHolder;
                bindWithMedia(vhm, position);
                break;
            case LOCATION:
                MessageWithLocationViewHolder vhl = (MessageWithLocationViewHolder) viewHolder;
                bindLocation(vhl, position);
                break;

        }
    }

    private void bindWithMedia(MessageWithMediaViewHolder viewHolder, int position){
        Message message = messages.get(position);
        viewHolder.getTextViewMessageSender().setText(message.getSenderEmail());
        viewHolder.getTextViewMessageContent().setText(message.getTextContent());

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm");
        viewHolder.getTextViewMessageTimestamp().setText(dateFormat.format(messages.get(position).getTimestamp()));
        if(position != 0 ){
            if(messages.get(position -1 ).getSenderEmail().equals(message.getSenderEmail())) {
                viewHolder.getTextViewMessageSender().setVisibility(View.INVISIBLE);
            }
        }

        if (User.getEmail().equals(message.getSenderEmail())) {
            viewHolder.getTextViewMessageSender().setVisibility(View.GONE);//ascunde numele meu
            RelativeLayout.LayoutParams params =
                    (RelativeLayout.LayoutParams) viewHolder.getRelativeLayout().getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);//aliniere la dreapta
            viewHolder.getRelativeLayout().setLayoutParams(params);
        } else {
            RelativeLayout.LayoutParams params =
                    (RelativeLayout.LayoutParams) viewHolder.getRelativeLayout().getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);//aliniere la dreapta
            viewHolder.getRelativeLayout().setLayoutParams(params);
        }

        //verificare daca exista in room
        //daca exista afiseaza
        //daca nu exista, descarca, salveaza si afiseaza
        final Bitmap[] img = new Bitmap[1];
        Thread getImageThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Context context = viewHolder.itemView.getContext();
                ChatterDatabase db = Room.databaseBuilder(context, ChatterDatabase.class, "media-database").build();
                MediaDAO mediaDAO = db.mediaDAO();
                Media media = mediaDAO.getByLink(messages.get(position).getMediaKey());
                if (media != null) {
                    if(media.mediaType.contains("image")){
                        //daca exista local
                        img[0] = BitmapFactory.decodeFile(media.localPath);

                    } else {
                        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                        mediaMetadataRetriever.setDataSource(media.localPath);
                        img[0] = mediaMetadataRetriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST);
                    }
                    viewHolder.setImage(img[0]);

                } else {
                    getMediaFromFirebase(messages.get(position).getMediaKey(), viewHolder);
                }

            }
        });
        getImageThread.start();
        try {
            getImageThread.join();
            viewHolder.getImageView().setImageBitmap(img[0]);
        } catch ( Exception e ){

        }
        viewHolder.getImageView().setVisibility(View.VISIBLE);

    }

    private void bindSimpleText(MessageViewHolder viewHolder, int position){
        Message message = messages.get(position);
        viewHolder.getTextViewMessageSender().setText(message.getSenderEmail());
        viewHolder.getTextViewMessageContent().setText(message.getTextContent());

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm");
        viewHolder.getTextViewMessageTimestamp().setText(dateFormat.format(messages.get(position).getTimestamp()));
        if(position != 0 ){
            if(messages.get(position -1 ).getSenderEmail().equals(message.getSenderEmail())) {
                viewHolder.getTextViewMessageSender().setVisibility(View.INVISIBLE);
            }
        }

        if (User.getEmail().equals(message.getSenderEmail())) {
            viewHolder.getTextViewMessageSender().setVisibility(View.GONE);//ascunde numele meu
            RelativeLayout.LayoutParams params =
                    (RelativeLayout.LayoutParams) viewHolder.getRelativeLayout().getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);//aliniere la dreapta
            viewHolder.getRelativeLayout().setLayoutParams(params);
        } else {
            RelativeLayout.LayoutParams params =
                    (RelativeLayout.LayoutParams) viewHolder.getRelativeLayout().getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);//aliniere la dreapta
            viewHolder.getRelativeLayout().setLayoutParams(params);
        }
    }

    private void bindLocation(MessageWithLocationViewHolder viewHolder, int position){
        Message message = messages.get(position);
        viewHolder.getTextViewMessageSender().setText(message.getSenderEmail());
        viewHolder.getTextViewMessageContent().setText(message.getTextContent());

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm");
        viewHolder.getTextViewMessageTimestamp().setText(dateFormat.format(messages.get(position).getTimestamp()));

        /*if(position != 0 ){
            if(messages.get(position -1 ).getSenderEmail().equals(message.getSenderEmail())) {
                viewHolder.getTextViewMessageSender().setVisibility(View.INVISIBLE);
            }
        }*/

        if (User.getEmail().equals(message.getSenderEmail())) {
            viewHolder.getTextViewMessageSender().setVisibility(View.GONE);//ascunde numele meu
            RelativeLayout.LayoutParams params =
                    (RelativeLayout.LayoutParams) viewHolder.getRelativeLayout().getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);//aliniere la dreapta
            viewHolder.getRelativeLayout().setLayoutParams(params);
        } else {
            RelativeLayout.LayoutParams params =
                    (RelativeLayout.LayoutParams) viewHolder.getRelativeLayout().getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);//aliniere la dreapta
            viewHolder.getRelativeLayout().setLayoutParams(params);
        }

        MapView mapView = (MapView)viewHolder.getMapView();

        mapView.onCreate(null);
        mapView.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng coordinates = new LatLng(message.getLocation().getLatitude(), message.getLocation().getLatitude());
                googleMap.addMarker(new MarkerOptions().position(coordinates));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
                mapView.onResume();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getContainsMedia()) {
            return WITH_MEDIA;
        } else if (messages.get(position).getContainsLocation()){
            return LOCATION;
        } else {
            return SIMPLE_TEXT;
        }
    }

    public void getMediaFromFirebase(String mediaLink, MessageWithMediaViewHolder viewHolder){
        //descarcare din firebase si salvare in local
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference mediaRef = storage.getReference().child(mediaLink);

        //se preiau metadatele
        mediaRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                String mediaType = storageMetadata.getContentType();
                String suffix = "." + mediaType.split("/")[1];
                File localFile = null;
                try {
                    localFile = File.createTempFile(mediaLink.replace("/media/",""), suffix);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File finalLocalFile = localFile;
                mediaRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                        Bitmap img = BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath());
                        viewHolder.getImageView().setImageBitmap(img);

                        Media media = new Media(mediaRef.getPath(), mediaType, finalLocalFile.getAbsolutePath());
                        saveMedia( viewHolder.itemView.getContext(), media);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //TODO:afiseaza ceva standard
                    }
                });

            }
        });





    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    //salvarea fisierelor media in baza de date room
    private void saveMedia(Context context, Media media) {
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


}