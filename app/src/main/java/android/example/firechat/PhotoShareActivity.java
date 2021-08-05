package android.example.firechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class PhotoShareActivity extends AppCompatActivity {

    private String imageSelected;
    private String senderRoom;
    private String receiverRoom;
    private String senderUID;

    //Firebase
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private String firebaseUriString;

    //layout content
    private ImageView imageToShare;
    private EditText textMessage;
    private CircleImageView sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_share);
        //Firebase
        storage=FirebaseStorage.getInstance();
        database=FirebaseDatabase.getInstance();

        //getIntent()
        imageSelected=getIntent().getStringExtra("imageUri");
        senderRoom=getIntent().getStringExtra("senderROOM");
        receiverRoom=getIntent().getStringExtra("receiverROOM");
        senderUID=getIntent().getStringExtra("senderUID");

        //image to share
        imageToShare=findViewById(R.id.imageToShare);
        imageToShare.setImageURI(Uri.parse(imageSelected));//this image is already taken up from the storage.

        //this image needs to be uploaded with the textMessage.
        //this image needs to be uploaded to the storage.
        //Fire base REFERENCES
        DatabaseReference databaseReferenceSender=database.getReference().child("chats").child(senderRoom).child("messages");
        DatabaseReference databaseReferenceReceiver=database.getReference().child("chats").child(receiverRoom).child("messages");
        StorageReference storageReference=storage.getReference().child("chats").child(senderRoom).child("mediaMessage");

        //implement onClickList.. for the send button.
        //when clicked i need to send this message to the chat activity.
        //the message needs to added to the database also.

        textMessage=findViewById(R.id.messageEditTextShare);
        sendButton=findViewById(R.id.sendButtonShare);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=textMessage.getText().toString().trim();
                //point to note is that message can be empty here.
                //if user wants to share only the image part.

                //senderId we already have.

                Date date=new Date();
                long timeStamp=date.getTime();

                //store the image in the firebase storage.
                storageReference.putFile(Uri.parse(imageSelected)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    firebaseUriString=uri.toString();
                                    //this is the firebase Uri of the image.
                                    Message message1=new Message(message,senderUID,timeStamp,firebaseUriString);
                                    databaseReferenceSender.push().setValue(message1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            databaseReferenceReceiver.push().setValue(message1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    //todo empty here.
                                                    Intent intent=new Intent();
                                                    intent.putExtra("message",message1.getMessage());
                                                    intent.putExtra("timeStamp",message1.getTimeStamp());
                                                    intent.putExtra("photoUrlString",message1.getImageToShare());
                                                    intent.putExtra("senderUID",message1.getSenderId());
                                                    setResult(RESULT_OK,intent);
                                                    finish();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                        else
                        {
                            //todo change this toast
                            Toast.makeText(PhotoShareActivity.this, "couldn't upload to firebase storage", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}