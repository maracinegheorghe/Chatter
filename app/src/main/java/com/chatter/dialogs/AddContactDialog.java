package com.chatter.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.chatter.R;
import com.chatter.classes.Contact;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AddContactDialog extends Dialog {
    String newContactEmail;
    Context context;

    public AddContactDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_contact);

        Button buttonAddContact = findViewById(R.id.button_add_contact);
        Button buttonCancel = findViewById(R.id.button_cancel_add_contact);
        buttonAddContact.setOnClickListener(v->addContact());
        buttonCancel.setOnClickListener(v->dismiss());
    }

    private void addContact() {
        EditText editTextEmail = findViewById(R.id.edit_text_new_contact_email);
        newContactEmail = editTextEmail.getText().toString();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        Query query = usersRef.orderByChild("email").equalTo(newContactEmail).limitToFirst(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                    Contact newContact = userSnapshot.getValue(Contact.class);
                    assert newContact != null;
                    newContact.setKey(userSnapshot.getKey());

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference contactsRef = database.getReference("users").child(FirebaseAuth.getInstance().getUid()).child("contacts").child(userSnapshot.getKey()).child("email");

                    contactsRef.setValue(newContact.getEmail());
                    dismiss();
                } else {
                    Toast.makeText(context, "Emailul introdus este gresit sau utilizatorul nu este inregistrat in aplicatie!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}