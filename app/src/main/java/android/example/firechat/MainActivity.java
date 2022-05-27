package android.example.firechat;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private RecyclerView personRecyclerView;
    private FirebaseDatabase database;
    private PeoplesAdapter adapter;
    List<Users> usersList;
    private ImageView profileImage;
    private TextView profileName;
    private LinearLayout profileLayout;

    private Users currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar_main);
        progressBar.setVisibility(View.VISIBLE);

        profileImage=findViewById(R.id.my_image);
        profileName=findViewById(R.id.my_name);
        profileLayout=findViewById(R.id.my_profile);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        usersList = new ArrayList<>();

        DatabaseReference userReference = database.getReference().child("user");

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users user = dataSnapshot.getValue(Users.class);
                    assert user != null;
                    if (!user.getUid().equals(auth.getUid())) {
                        usersList.add(user);//here we keep the user outside the userList, because he can't send the message to himself.
                    } else {
                        currentUser = user;
                        profileName.setText(currentUser.getName());
                        Glide.with(MainActivity.this).load(currentUser.getImageURI()).into(profileImage);
                    }
                }
                progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        personRecyclerView = findViewById(R.id.main_activity_recyclerview);
        personRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PeoplesAdapter(MainActivity.this, usersList);
        personRecyclerView.setAdapter(adapter);
    }

}