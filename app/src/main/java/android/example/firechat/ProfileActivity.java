package android.example.firechat;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    int IMAGE_CHANGES_MADE = 0;

    private String userName;//this is the part of the intent.
    private String userImage;
    private String userStatus;
    private String userUID;

    private String nameUpdateString;//updated values will be stored here temporarily.
    private String statusUpdateString;//updated values will be stored here temporarily.
    private FirebaseDatabase database;
    private FirebaseStorage storage;

    private ImageView userImageEdit;//this is that pen image on clicking which image edition occurs
    private ImageView userNameEdit;//this is the pen image on clicking which name edition occurs
    private ImageView userStatusEdit;//this is the pen image on clicking which the status edition occurs\
    private ProgressBar progressBar;

    private ImageView userHeadBack;//head back button

    private TextView userNameTextView;//this holds the name text
    private CircleImageView userImageView;//this is for the circleImage in their
    private TextView userStatusTextView;//this holds the status text

    public ActivityResultLauncher<Intent> resultLauncher;
    private Uri imageUriToBeSet;
    private String imageUriString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        progressBar=findViewById(R.id.progressBar_profile);
        progressBar.setVisibility(View.VISIBLE);

        userName = getIntent().getStringExtra("my_name");
        userImage = getIntent().getStringExtra("my_image");
        userStatus = getIntent().getStringExtra("my_status");
        userUID = getIntent().getStringExtra("my_uid");

        userNameTextView = findViewById(R.id.user_name_details);
        userImageView = findViewById(R.id.user_image_details);
        userStatusTextView = findViewById(R.id.user_status);
        userHeadBack = findViewById(R.id.head_back);

        userNameEdit = findViewById(R.id.edit_name);
        userImageEdit = findViewById(R.id.edit_image);
        userStatusEdit = findViewById(R.id.edit_status);

        userNameTextView.setText(userName);
        Glide.with(this).load(Uri.parse(userImage)).into(userImageView);
        userStatusTextView.setText(userStatus);
        progressBar.setVisibility(View.INVISIBLE);

        //creating the custom dialogue for the editing purpose;
        //editing of image will be different that that compared to the status and name;

        DatabaseReference databaseReference = database.getReference().child("user").child(userUID);
        StorageReference storageReference = storage.getReference().child("upload").child(userUID);

        //editing the image
        userImageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                resultLauncher.launch(intent);
            }
        });

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() != null) {
                        imageUriToBeSet = result.getData().getData();
                        IMAGE_CHANGES_MADE = 1;
                        userImageView.setImageURI(imageUriToBeSet);
                    }
                }
            }
        });


        userNameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //name custom dialog
                class CustomNameDialog extends Dialog {
                    public EditText updateNameET;
                    public LinearLayout updateNameClick;

                    public CustomNameDialog(@NonNull Context context) {
                        super(context);
                    }

                    @Override
                    protected void onCreate(Bundle savedInstanceState) {
                        super.onCreate(savedInstanceState);
                        requestWindowFeature(Window.FEATURE_NO_TITLE);
                        setContentView(R.layout.update_name_dialogue);
                        updateNameET = findViewById(R.id.update_name_ET);
                        updateNameClick = findViewById(R.id.update_name_click);
                    }
                }
                CustomNameDialog customNameDialog = new CustomNameDialog(ProfileActivity.this);
                customNameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                customNameDialog.show();
                customNameDialog.updateNameClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(customNameDialog.updateNameET.getText().toString().trim())) {
                            nameUpdateString = customNameDialog.updateNameET.getText().toString().trim();
                            databaseReference.child("name").setValue(nameUpdateString);
                            Toast.makeText(ProfileActivity.this, "updated over firebase", Toast.LENGTH_SHORT).show();
                            userNameTextView.setText(nameUpdateString);
                            customNameDialog.dismiss();
                        } else {
                            Toast.makeText(ProfileActivity.this, "enter proper name", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        userStatusEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //status custom dialog
                class CustomStatusDialog extends Dialog {
                    public EditText updateStatusET;
                    public LinearLayout updateStatusClick;

                    public CustomStatusDialog(@NonNull Context context) {
                        super(context);
                    }

                    @Override
                    protected void onCreate(Bundle savedInstanceState) {
                        super.onCreate(savedInstanceState);
                        requestWindowFeature(Window.FEATURE_NO_TITLE);
                        setContentView(R.layout.update_status_dialogue);
                        updateStatusET = findViewById(R.id.update_status_ET);
                        updateStatusClick = findViewById(R.id.update_status_click);
                    }
                }
                CustomStatusDialog customStatusDialog = new CustomStatusDialog(ProfileActivity.this);
                customStatusDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                customStatusDialog.show();
                customStatusDialog.updateStatusClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(customStatusDialog.updateStatusET.getText().toString().trim())) {
                            statusUpdateString = customStatusDialog.updateStatusET.getText().toString().trim();
                            databaseReference.child("status").setValue(statusUpdateString);
                            Toast.makeText(ProfileActivity.this, "updated over firebase", Toast.LENGTH_SHORT).show();
                            userStatusTextView.setText(statusUpdateString);
                            customStatusDialog.dismiss();
                        } else {
                            Toast.makeText(ProfileActivity.this, "enter proper status", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        userHeadBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IMAGE_CHANGES_MADE == 1) {
                    storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(ProfileActivity.this, "deleted", Toast.LENGTH_SHORT).show();
                        }
                    });
                    if (imageUriToBeSet != null) {
                        storageReference.putFile(imageUriToBeSet).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d(ProfileActivity.class.getSimpleName(), "here1");
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d(ProfileActivity.class.getSimpleName(), "here2");
                                            imageUriString = uri.toString();
                                            databaseReference.child("imageURI").setValue(imageUriString).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Log.d(ProfileActivity.class.getSimpleName(), "here3");
                                                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                                                    finish();
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        imageUriString = "https://firebasestorage.googleapis.com/v0/b/fire-chat-4dad3.appspot.com/o/user.png?alt=media&token=712b7bd1-8993-4833-ab2a-8ad8163515eb";
                        databaseReference.child("imageURI").setValue(imageUriString).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(ProfileActivity.this, "error while uploading the image", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                    finish();
                }
            }
        });
    }
}