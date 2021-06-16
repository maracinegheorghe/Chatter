package com.chatter.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.chatter.DAO.ChatterDatabase;
import com.chatter.DAO.MediaDAO;
import com.chatter.R;
import com.chatter.activities.ConversationActivity;
import com.chatter.classes.Media;
import com.chatter.dialogs.ViewMediaDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaHolder> {

    public ArrayList<String> mediaLinks;
    public MediaAdapter(ArrayList<String> mediaLinks) {
        this.mediaLinks = mediaLinks;
    }

    public static class MediaHolder extends RecyclerView.ViewHolder {
        private final ImageView imageViewMediaList;
        private final TextView textViewMediaLink;

        public MediaHolder(View view) {
            super(view);
            imageViewMediaList = view.findViewById(R.id.imageViewMediaList);
            textViewMediaLink = view.findViewById(R.id.textViewMediaLink);
        }

        public ImageView getImageViewMediaList() {
            return imageViewMediaList;
        }

        public TextView getTextViewMediaLink() {
            return textViewMediaLink;
        }

    }

    @NonNull
    @Override
    public MediaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.media_view, parent, false);

        return new MediaHolder(view);
    }

    @Override
    public int getItemCount() {
        return mediaLinks.size();
    }

    @Override
    public void onBindViewHolder(@NonNull MediaHolder viewHolder, int position) {
        final Bitmap[] img = new Bitmap[1];
        Thread getImageThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Context context = viewHolder.itemView.getContext();
                ChatterDatabase db = Room.databaseBuilder(context, ChatterDatabase.class, "media-database").build();
                MediaDAO mediaDAO = db.mediaDAO();
                Media media = mediaDAO.getByLink(mediaLinks.get(position));
                if (media != null) {
                    viewHolder.getImageViewMediaList().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentManager fragmentManager = ((ConversationActivity)v.getContext()).getSupportFragmentManager();
                            ViewMediaDialog viewMediaDialog = ViewMediaDialog.newInstance(media);
                            viewMediaDialog.show(fragmentManager, "viewMediaDialog");
                        }
                    });
                    if(media.mediaType.contains("image")){
                        //daca exista local
                        img[0] = BitmapFactory.decodeFile(media.localPath);

                    } else {
                        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                        mediaMetadataRetriever.setDataSource(media.localPath);
                        img[0] = mediaMetadataRetriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST);
                        // Load thumbnail of a specific media item.
                        /*Bitmap thumbnail =
                                context.getContentResolver().loadThumbnail(
                                        Uri.parse(media.localPath), new Size(640, 480), null);*/
                    }
                } else {
                    getMediaFromFirebase(mediaLinks.get(position), viewHolder);
                }

            }
        });
        getImageThread.start();
        try {
            getImageThread.join();
            viewHolder.getImageViewMediaList().setImageBitmap(img[0]);
        } catch ( Exception e ){

        }

    }

    public void getMediaFromFirebase(String mediaLink, MediaHolder viewHolder){

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference imageRef = storage.getReference().child(mediaLink);

        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        File finalLocalFile = localFile;
        imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Bitmap img = BitmapFactory.decodeFile(finalLocalFile.getAbsolutePath());
                viewHolder.getImageViewMediaList().setImageBitmap(img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
}
