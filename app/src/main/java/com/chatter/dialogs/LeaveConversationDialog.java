package com.chatter.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.chatter.R;

import java.util.Objects;

public class LeaveConversationDialog extends DialogFragment {
    Button buttonConfirmLeave;
    Button buttonCancelLeave;

    public LeaveConversationDialog() {
    }

    public static LeaveConversationDialog newInstance() {
        return new LeaveConversationDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.leave_conversation_dialog, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonConfirmLeave = view.findViewById(R.id.buttonConfirmLeave);
        buttonCancelLeave = view.findViewById(R.id.buttonCancelLeave);

        buttonConfirmLeave.setOnClickListener(v->confirmLeave());
        buttonCancelLeave.setOnClickListener(v->cancelLeave());

        Objects.requireNonNull(getDialog()).setTitle("Paraseste conversatia");
    }

    private void confirmLeave() {
        leaveConversationDialogListener listener = (leaveConversationDialogListener) getActivity();
        assert listener != null;
        listener.onFinishLeaveConversationDialog(true);
        dismiss();
    }

    private void cancelLeave() {
        leaveConversationDialogListener listener = (leaveConversationDialogListener) getActivity();
        assert listener != null;
        listener.onFinishLeaveConversationDialog(false);
        dismiss();
    }

    public interface leaveConversationDialogListener {
        void onFinishLeaveConversationDialog(Boolean confirm);
    }
}