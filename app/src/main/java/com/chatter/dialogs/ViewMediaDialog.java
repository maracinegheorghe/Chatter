package com.chatter.dialogs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.chatter.R;
import com.chatter.classes.Media;
import com.chatter.classes.Message;

import java.util.Objects;

public class ViewMediaDialog extends DialogFragment{
    private Media media;
    private VideoView videoViewMedia;
    private ImageView imageViewMedia;

    public ViewMediaDialog() {
    }

    public static ViewMediaDialog newInstance(Media media) {
        ViewMediaDialog f = new ViewMediaDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putParcelable("media", media);
        f.setArguments(args);

        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.media = getArguments().getParcelable("media");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_view_media, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        videoViewMedia = view.findViewById(R.id.videoViewMedia);
        imageViewMedia = view.findViewById(R.id.imageViewMedia);

        if(this.media.mediaType.contains("image")){
            videoViewMedia.setVisibility(View.INVISIBLE);
            Bitmap image = BitmapFactory.decodeFile(this.media.localPath);
            imageViewMedia.setImageBitmap(image);
        } else if (this.media.mediaType.contains("video")){
            imageViewMedia.setVisibility(View.INVISIBLE);
            videoViewMedia.setVideoPath(this.media.localPath);
            videoViewMedia.start();
        }
        //Objects.requireNonNull(getDialog()).setTitle("Vizualizare media");
    }

}