package com.chatter.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.chatter.R;
import com.chatter.classes.Contact;
import com.chatter.classes.Conversation;
import com.chatter.classes.Message;
import com.chatter.classes.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class StarterActivity extends AppCompatActivity {
    private static final int RC_REQUEST_USER_LOGIN = 1001;
    private static final int RC_USER_LOGGED_IN = 1005;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);
    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser mUser= FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null) {
            Intent logInIntent = new Intent(this, LoginActivity.class);
            startActivityForResult(logInIntent,RC_REQUEST_USER_LOGIN);
        } else {
            User.setEmail(mUser.getEmail());
            Toast.makeText(this,User.getEmail(),Toast.LENGTH_LONG).show();
            Intent intent = getIntent();
            //database.setPersistenceEnabled(true);
            //verifica de ce a deschis aplicatia
            if(intent.getExtras() == null){
                goToConversationsList();
            } else {
                Toast.makeText(this,"Share",Toast.LENGTH_LONG).show();
                goToShareConversationsList();
            }
        }
        //finish();//termin activitatea altfel la share nu o sa intre in on create
    }
    private void goToShareConversationsList(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userConvRef = database.getReference()
                .child("users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .child("user_conversations")
                .getRef();
        //deschisa pentru share
        //listener pentru conversatii fara sa preia mesajele pentru ca nu avem nevoie

        if(User.getConversations().getValue().size() == 0) {
            //daca sunt incarcate deja conversatiile le dubleaza daca adaugam alt listener, asa ca
            //va fi adaugat doar daca nu exista
            userConvRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot userConversationSnapshot, @Nullable String previousChildName) {
                    //pentru fiecare conversatie care apare se preia cheia
                    String value = userConversationSnapshot.getValue(String.class);
                    //referinta catre conversatie din root-ul "conversations"
                    assert value != null;
                    DatabaseReference conversationsRef = database.getReference().child("conversations").child(value).getRef();

                    //pentru fiecare conversatie aparuta, preia datele
                    conversationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot conversationSnapshot) {
                            Conversation c = conversationSnapshot.getValue(Conversation.class);
                            assert c != null;
                            c.setKey(conversationSnapshot.getKey());
                            User.addConversation(c);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot userConversationSnapshot) {

                    //todo; de facut curat
                    //pentru fiecare conversatie care dispare se preia cheia pentru a putea sterge
                    String key = userConversationSnapshot.getValue(String.class);
                    User.removeConversation(key);
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        //ii pases direct intent-ul primit cu clasa schimbata
        Intent shareIntent = getIntent();
        shareIntent.setClass(this,ConversationListShareActivity.class);
        startActivity(shareIntent);
    }
    private void goToConversationsList(){
        if(!User.listenerExists)
        {
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            //listener pentru contacte
            DatabaseReference userContactsRef = database.getReference("users").child(FirebaseAuth.getInstance().getUid()).child("contacts");
            userContactsRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Contact newContact = snapshot.getValue(Contact.class);
                    assert newContact != null;
                    newContact.setKey(snapshot.getKey());
                    User.addContact(newContact);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    User.removeContact(snapshot.getKey());
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //listener pentru conversatii
            DatabaseReference userConvRef = database.getReference()
                    .child("users")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                    .child("user_conversations")
                    .getRef();
            //listener pentru conversatii cu tot cu mesaje
            userConvRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot userConversationSnapshot, @Nullable String previousChildName) {
                    //pentru fiecare conversatie care apare se preia cheia
                    String key = userConversationSnapshot.getKey();
                    //referinta catre conversatie din root-ul "conversations"
                    assert key != null;
                    DatabaseReference conversationsRef = database.getReference().child("conversations").child(key).getRef();

                    //pentru fiecare conversatie aparuta, preia datele
                    conversationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot conversationSnapshot) {
                            Conversation c = conversationSnapshot.getValue(Conversation.class);
                            assert c != null;
                            c.setKey(conversationSnapshot.getKey());
                            User.addConversation(c);

                            //de adaugat listener pentru mesaje
                            DatabaseReference messagesRef = database.getReference().child("messages").child(conversationSnapshot.getKey()).getRef();
                            messagesRef.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                    Message newMessage = snapshot.getValue(Message.class);
                                    newMessage.setKey(snapshot.getKey());
                                    c.addMessage(newMessage);
                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                }

                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot userConversationSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            User.listenerExists = true;
        }

        Intent conversationsIntent = new Intent(this, ConversationsListActivity.class);
        startActivity(conversationsIntent);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001) {
            if(resultCode == RC_USER_LOGGED_IN){
                Intent conversationListIntent = new Intent(this, LoginActivity.class);
                startActivityForResult(conversationListIntent,RC_REQUEST_USER_LOGIN);
            } else {
                //retrimitere la login
                Intent logInIntent = new Intent(this, LoginActivity.class);
                startActivityForResult(logInIntent,RC_REQUEST_USER_LOGIN);
            }
        }
    }
}