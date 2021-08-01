package android.example.firechat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PeoplesAdapter extends RecyclerView.Adapter<PeoplesAdapter.PersonViewHolder> {
    private Context context;
    private List<Users> usersList;
    private ImageView temp;

    public PeoplesAdapter(Context context, List<Users> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_person_main_activity, parent, false);
        return new PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PeoplesAdapter.PersonViewHolder holder, int position) {
        Users user = usersList.get(position);
        holder.person_name.setText(user.getName());
        Glide.with(context).load(Uri.parse(user.getImageURI())).into(holder.person_circular_image);

        holder.personLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("ReceiverName", user.getName());
                intent.putExtra("ReceiverImage", user.getImageURI());
                intent.putExtra("ReceiverUID", user.getUid());
                intent.putExtra("ReceiverStatus",user.getStatus());
                context.startActivity(intent);
            }
        });

        holder.person_circular_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                class customDialog extends Dialog {

                    public ImageView enlargedImage;
                    public Context context;

                    public customDialog(@NonNull Context context) {
                        super(context);
                        this.context = context;
                    }

                    @Override
                    protected void onCreate(Bundle savedInstanceState) {
                        super.onCreate(savedInstanceState);
                        requestWindowFeature(Window.FEATURE_NO_TITLE);
                        setContentView(R.layout.image_dialog);
                        enlargedImage = findViewById(R.id.image_to_be_enlarged);
                        Glide.with(context).load(Uri.parse(user.getImageURI())).into(enlargedImage);
                    }
                }
                customDialog customDialog = new customDialog(context);
                customDialog.show();
                temp = customDialog.enlargedImage;
                temp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class PersonViewHolder extends RecyclerView.ViewHolder {


        private LinearLayout personLinearLayout;
        private CircleImageView person_circular_image;
        private TextView person_name;

        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            personLinearLayout = itemView.findViewById(R.id.person_ll);
            person_circular_image = itemView.findViewById(R.id.person_item_image);
            person_name = itemView.findViewById(R.id.person_item_name);
        }
    }
}
