package com.chatter.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.chatter.R;

import java.util.Objects;

public class InsertConversationTitleDialog extends DialogFragment {

    EditText editTextNewConversationTitle;
    String newConversationTitle;

    public InsertConversationTitleDialog() {
    }

    public static InsertConversationTitleDialog newInstance() {
        return new InsertConversationTitleDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_insert_conversation_title, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextNewConversationTitle = view.findViewById(R.id.editTextNewConversationTitle);

        Button buttonSetNewConversationTitle = view.findViewById(R.id.buttonSetNewConversationTitle);
        Button buttonCancelNewConversationTitle = view.findViewById(R.id.buttonCancelNewConversationTitle);
        buttonSetNewConversationTitle.setOnClickListener(v -> {
            newConversationTitle = editTextNewConversationTitle.getText().toString();
            finishTitleInsertionDialogListener listener = (finishTitleInsertionDialogListener) getActivity();
            assert listener != null;
            if (!newConversationTitle.isEmpty()) {
                listener.onFinishTitleInsertionDialog(newConversationTitle);
                dismiss();
            }
        });
        buttonCancelNewConversationTitle.setOnClickListener(v -> dismiss());
        Objects.requireNonNull(getDialog()).setTitle("Introduceti Titlul");
    }

    public interface finishTitleInsertionDialogListener {
        void onFinishTitleInsertionDialog(String newConversationTitle);
    }

}