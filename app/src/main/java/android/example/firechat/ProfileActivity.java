package android.example.firechat;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String userName;
    private String userImage;
    private String userStatus;

    private TextView userNameTextView;
    private CircleImageView userImageView;
    private TextView userStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userName=getIntent().getStringExtra("my_name");
        userImage=getIntent().getStringExtra("my_image");
        userStatus=getIntent().getStringExtra("my_status");

        userNameTextView=findViewById(R.id.user_name_details);
        userImageView=findViewById(R.id.user_image_details);
        userStatusTextView=findViewById(R.id.user_status);

        userNameTextView.setText(userName);
        Glide.with(this).load(Uri.parse(userImage)).into(userImageView);
        userStatusTextView.setText(userStatus);
    }
}