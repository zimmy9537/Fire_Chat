package android.example.firechat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private RecyclerView personRecyclerView;
    private FirebaseDatabase database;
    private PeoplesAdapter adapter;
    List<Users> usersList;

    private Users currentUserJugad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar_main);
        progressBar.setVisibility(View.VISIBLE);

        setUpToolBar();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        usersList = new ArrayList<>();


        DatabaseReference reference = database.getReference().child("user");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    if (!users.getUid().equals(auth.getUid())) {
                        usersList.add(users);//here we keep the user outside the userList, because he can't send the message to himself.
                    } else {
                        currentUserJugad = users;
                    }
                }
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
        progressBar.setVisibility(View.GONE);
    }

    private void setUpToolBar() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out:
                auth.signOut();
                startActivity(new Intent(MainActivity.this, InitialActivity.class));
                finish();
                break;
            case R.id.my_profile:
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("my_name", currentUserJugad.getName());
                intent.putExtra("my_image", currentUserJugad.getImageURI());
                intent.putExtra("my_status", currentUserJugad.getStatus());
                intent.putExtra("my_uid",currentUserJugad.getUid());
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}