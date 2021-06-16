package com.chatter.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.chatter.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterDialog extends DialogFragment {
    EditText editTextEmailRegister;
    EditText editTextRegisterPassword;
    EditText editTextRegisterPasswordConfirm;

    String newUserEmail;
    String newUserPassword;
    String newUserPasswordConfirm;

    public RegisterDialog() {
    }

    public static RegisterDialog newInstance() {
        return new RegisterDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_register, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editTextEmailRegister = view.findViewById(R.id.editTextEmailRegister);
        editTextRegisterPassword = view.findViewById(R.id.editTextRegisterPassword);
        editTextRegisterPasswordConfirm = view.findViewById(R.id.editTextRegisterPasswordConfirm);

        Button buttonRegisterConfirm = view.findViewById(R.id.buttonRegisterConfirm);
        Button buttonRegisterCancel = view.findViewById(R.id.buttonRegisterCancel);
        buttonRegisterConfirm.setOnClickListener(this::register);

        buttonRegisterCancel.setOnClickListener(v -> dismiss());
        Objects.requireNonNull(getDialog()).setTitle("Inregistrare");
    }

    private void register(View v) {
        newUserEmail = editTextEmailRegister.getText().toString();
        newUserPassword = editTextRegisterPassword.getText().toString();
        newUserPasswordConfirm = editTextRegisterPasswordConfirm.getText().toString();

        if (newUserEmail.isEmpty() || newUserPassword.isEmpty() || newUserPasswordConfirm.isEmpty()) {
            Toast.makeText(v.getContext(), "Nu ati completat toate campurile!", Toast.LENGTH_SHORT).show();
            return;
        }
        //lungimea parolei
        if (newUserPassword.length() < 6) {
            Toast.makeText(v.getContext(), "Parola trebuie sa contina minim 6 caractere!", Toast.LENGTH_SHORT).show();
            return;
        }
        //verifica daca exte reintrodusa parola corect
        if (!newUserPassword.equals(newUserPasswordConfirm)) {
            Toast.makeText(v.getContext(), "Parolele nu corespund!", Toast.LENGTH_SHORT).show();
            return;
        }

        //verifica daca este email
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(newUserEmail);
        if (!matcher.matches()) {
            Toast.makeText(v.getContext(), "Emailul introdus nu este valid!", Toast.LENGTH_SHORT).show();
            return;
        }

        //verifica daca exista emailul in baza de date
        if (false) {
            Toast.makeText(v.getContext(), "Exista deja un cont pentru acest email!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(newUserEmail, newUserPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        finishRegister(mAuth.getCurrentUser());
                    } else {
                        finishRegister(null);
                    }
                });

    }

    private void finishRegister(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            finishRegisterDialogListener listener = (finishRegisterDialogListener) getActivity();
            assert listener != null;
            listener.onFinishRegisterDialog(newUserEmail);
            dismiss();
        }
    }

    public interface finishRegisterDialogListener {
        void onFinishRegisterDialog(String inputText);
    }

}