package android.example.firechat;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReceiverDetails extends AppCompatActivity {

    private TextView receiverNameDetails;
    private CircleImageView receiverImageDetails;
    private TextView receiverStatusDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_details);
        receiverImageDetails = findViewById(R.id.receiver_image_details);
        receiverNameDetails = findViewById(R.id.receiver_name_details);
        receiverStatusDetails=findViewById(R.id.receiver_status);

        String receiverName = getIntent().getStringExtra("ReceiverName");
        String receiverImage = getIntent().getStringExtra("ReceiverImage");
        String receiverStatus=getIntent().getStringExtra("ReceiverStatus");

        receiverNameDetails.setText(receiverName);
        Glide.with(this).load(Uri.parse(receiverImage)).into(receiverImageDetails);
        receiverStatusDetails.setText(receiverStatus);
    }
}