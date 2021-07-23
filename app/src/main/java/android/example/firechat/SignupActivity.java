package android.example.firechat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;


public class SignupActivity extends AppCompatActivity {

    private String LOGTAG = SignupActivity.class.getSimpleName();

    private EditText name_et;
    private EditText email_et;
    private EditText password_et;
    private LinearLayout signUp;
    private LinearLayout logIn;
    private FirebaseAuth auth;
    private CircleImageView user_image_signup;
    private Uri imageUri;// bellow here somewhere we have declared string uri.
    //this imageUri will be the one which will hold the uri of the image the user select form the phone storage.
    public ActivityResultLauncher<Intent> resultLauncher;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private String imageURI;// todo this will be the part of the new model class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        name_et = findViewById(R.id.name_signup_ET);
        email_et = findViewById(R.id.email_signup_ET);
        password_et = findViewById(R.id.password_signup_ET);
        signUp = findViewById(R.id.signUp_ll_signUp);
        user_image_signup = findViewById(R.id.profile_image_signup);
        logIn = findViewById(R.id.login_ll_signup);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();


        signUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String name = name_et.getText().toString().trim();
                final String email = email_et.getText().toString().trim();
                final String password = password_et.getText().toString().trim();
                String emailPattern = ("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "email or password can not be empty", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!email.matches(emailPattern)) {
                    Toast.makeText(SignupActivity.this, "Not a valid email", Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            DatabaseReference databaseReference = database.getReference().child("user").child(auth.getUid());
                            StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());
                            if (imageUri != null) {
                                storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    imageURI = uri.toString();
                                                    Users users = new Users(auth.getUid(), name, email, imageURI);
                                                    //todo below may create problem
                                                    databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(SignupActivity.this, "SignedUp Successfully", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                                                startActivity(intent);
                                                            } else if (task.isCanceled()) {
                                                                Toast.makeText(SignupActivity.this, "Task cancelled", Toast.LENGTH_SHORT).show();
                                                            } else if (!task.isComplete()) {
                                                                Toast.makeText(SignupActivity.this, "Task completed", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(SignupActivity.this, "here", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                        } else {
                                            //todo task not successful
                                            Toast.makeText(SignupActivity.this, "Task unsuccessful", Toast.LENGTH_SHORT).show();
                                            Log.e(LOGTAG, "task exception " + task.getException());
                                        }
                                    }
                                });
                            } else {
                                //todo image not null
                                imageURI = "https://firebasestorage.googleapis.com/v0/b/fire-chat-4dad3.appspot.com/o/user.png?alt=media&token=712b7bd1-8993-4833-ab2a-8ad8163515eb";
                                Users users = new Users(auth.getUid(), name, email, imageURI);
                                databaseReference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SignupActivity.this, "SignedUp successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(SignupActivity.this, "error while signUp", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            email_et.setError("Please Enter Valid Email");
                            Toast.makeText(SignupActivity.this, "Error Occurred while Sign Up", Toast.LENGTH_SHORT).show();
                            Log.e(LOGTAG, "error noted here is :- " + task.getException());
                        }
                    }
                });
            }
        });

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() != null) {
                        imageUri = result.getData().getData();//funny
                        user_image_signup.setImageURI(imageUri);
                    }
                }
            }
        });

        user_image_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                resultLauncher.launch(intent);
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}