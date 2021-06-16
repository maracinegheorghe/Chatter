package com.chatter.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.chatter.R;
import com.chatter.classes.Conversation;
import com.chatter.classes.User;
import com.chatter.dialogs.LeaveConversationDialog;
import com.chatter.fragments.ConversationMediaFragment;
import com.chatter.fragments.ConversationParticipantsFragment;
import com.chatter.fragments.MessagesFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//todo: iconita pentru drawer
public class ConversationActivity
        extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                    LeaveConversationDialog.leaveConversationDialogListener {
    Conversation conversation;
    DrawerLayout drawerLayout;

    Fragment messagesFragment;
    Fragment conversationParticipantsFragment;
    Fragment conversationMediaFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        this.conversation = User.getConversation(getIntent().getStringExtra("conversation_key"));
        this.drawerLayout = findViewById(R.id.drawer_layout);
        this.messagesFragment = new MessagesFragment(conversation.getKey());
        this.conversationParticipantsFragment = new ConversationParticipantsFragment(conversation.getKey());
        this.conversationMediaFragment = new ConversationMediaFragment(conversation.getKey());

        Toolbar toolbar = findViewById(R.id.toolbar_conversation);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(conversation.getName());
        }
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBarDrawerToggle actionBarDrawerToggle =
                new ActionBarDrawerToggle(this,drawerLayout, toolbar,
                        R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //actionBarDrawerToggle.setDrawerArrowDrawable(R.drawable);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            showMessages();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showMessages(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.conversation_fragment, this.messagesFragment)
                .setReorderingAllowed(true)
                .addToBackStack("messages")
                .commit();
    }

    private void showParticipants(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.conversation_fragment, this.conversationParticipantsFragment)
                .setReorderingAllowed(true)
                .addToBackStack("participants")
                .commit();
    }

    private void showMedia(){
        if(this.conversationMediaFragment == null){
            this.conversationParticipantsFragment = new ConversationParticipantsFragment(conversation.getKey());
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.conversation_fragment, this.conversationMediaFragment)
                .setReorderingAllowed(true)
                .addToBackStack("media")
                .commit();
    }

    private void showSettings(){

    }

    private void leaveConversation(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        LeaveConversationDialog leaveConversationDialog = LeaveConversationDialog.newInstance();
        leaveConversationDialog.show(fragmentManager, "register_dialog");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_item_messages:
                showMessages();
                break;
            case R.id.nav_item_participants:
                showParticipants();
                break;
            case R.id.nav_item_media:
                showMedia();
                break;
            case R.id.nav_item_settings:
                showSettings();
                break;
            case R.id.nav_item_leave_conversation:
                leaveConversation();
                break;
        }
        drawerLayout.close();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onFinishLeaveConversationDialog(Boolean confirm) {
        if(confirm){
            Toast.makeText(this, "Ai parasit conversatia", Toast.LENGTH_SHORT).show();

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("user_conversations")
                    .child(conversation.getKey());
            finish();
            ref.removeValue();
            User.removeConversation(conversation.getKey());
        }
    }
}