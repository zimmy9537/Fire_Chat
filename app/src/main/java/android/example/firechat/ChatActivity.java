package android.example.firechat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private ImageButton chatImagePicker;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;
    private String senderUid;// though it i clear that it is equal to firebaseAuth.getUid();
    private String senderRoom;
    private String receiverRoom;
    private ArrayList<Message> messageArrayList;
    private MessageAdapter messageAdapter;


    private ProgressBar progressBarChat;
    private CircleImageView sendButton;
    private EditText messageEditText;
    private RecyclerView chatRecyclerView;
    private LinearLayout image_back;


    private String ReceiverName;
    private String ReceiverImage;
    private String receiverUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //views and initialisation
        progressBarChat = findViewById(R.id.progressBar);
        progressBarChat.setVisibility(View.VISIBLE);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        sendButton = findViewById(R.id.sendButton);
        messageEditText = findViewById(R.id.messageEditText);
        senderUid = firebaseAuth.getCurrentUser().getUid();
        messageArrayList = new ArrayList<>();
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        image_back=findViewById(R.id.image_back_ll);


        //getIntents
        ReceiverName = getIntent().getStringExtra("ReceiverName");
        ReceiverImage = getIntent().getStringExtra("ReceiverImage");
        receiverUid = getIntent().getStringExtra("ReceiverUID");
        Log.d(ChatActivity.class.getSimpleName(), "receiver name" + ReceiverName);
        Log.d(ChatActivity.class.getSimpleName(), "receiver uid" + receiverUid);

        //setting up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        TextView toolbarName=findViewById(R.id.receiver_name_toolbar);
        CircleImageView toolbarImage=findViewById(R.id.receiver_image_toolbar);
        toolbarName.setText(ReceiverName);
        Glide.with(this).load(Uri.parse(ReceiverImage)).into(toolbarImage);

        //ROOMS
        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;


        progressBarChat.setVisibility(View.GONE);

        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this,MainActivity.class));
                finish();
            }
        });

        DatabaseReference reference = database.getReference().child("user").child(firebaseAuth.getCurrentUser().getUid());
        DatabaseReference chatReference = database.getReference().child("chats").child(senderRoom).child("messages");
        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageArrayList.clear();//this is because if the message persist then those will be resend when receiver or sender sends the message.
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    messageArrayList.add(message);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //here we will see if this eventListener is needed else we may delete this if no need.
                //it was used to get the images of users.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //todo image part is left.
        chatImagePicker = findViewById(R.id.photoPickerButton);
        chatImagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChatActivity.this, "Image icon clicked", Toast.LENGTH_SHORT).show();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(ChatActivity.this, "you can not send a empty message.", Toast.LENGTH_SHORT).show();
                    return;
                }
                messageEditText.setText("");
                Date date = new Date();

                Message messages = new Message(message, senderUid, date.getTime());
                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push()
                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        database.getReference().child("chats")
                                .child(receiverRoom)
                                .child("messages")
                                .push()
                                .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //todo this is empty here.
                            }
                        });
                    }
                });
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(linearLayoutManager);
        messageAdapter = new MessageAdapter(ChatActivity.this, messageArrayList, senderUid, receiverUid);
        chatRecyclerView.setAdapter(messageAdapter);
    }
}