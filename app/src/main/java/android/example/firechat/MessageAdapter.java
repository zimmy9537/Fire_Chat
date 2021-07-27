package android.example.firechat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Message> messageArrayList;
    String senderUid;

    public MessageAdapter(Context context, ArrayList<Message> messageArrayList, String senderUid) {
        this.context = context;
        this.messageArrayList = messageArrayList;
        if (messageArrayList == null) {
            Toast.makeText(context, "messageArraylist is empty", Toast.LENGTH_SHORT).show();
        }
        this.senderUid = senderUid;
    }

    int ITEM_SEND = 1;
    int ITEM_RECEIVE = 2;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //this maybe doubtful here what is being done?
        //u can see a parameter as viewType. this viewType we get from the function getItemViewType().
        //this returns the values as 1 or 2 as it is defined in the method
        if (viewType == 1) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_message_item, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_message_item, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageArrayList.get(position);
        if (holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
            senderViewHolder.senderText.setText(message.getMessage());
        } else {
            ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
            receiverViewHolder.receiverText.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message messages = messageArrayList.get(position);
        Log.d(ChatActivity.class.getSimpleName(), "we got guffed up here");
        if (senderUid == null) {
            Log.d(ChatActivity.class.getSimpleName(), "senderID is null");
            return 1;
        } else if (messages.getSenderId() == null) {//todo here is the problem.
            Log.d(ChatActivity.class.getSimpleName(), "senderID is null 2");
            return 1;
        }
        if (messages.getSenderId().trim().equals(senderUid.trim())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECEIVE;
        }
    }

    class SenderViewHolder extends RecyclerView.ViewHolder {
        private TextView senderText;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderText = itemView.findViewById(R.id.sender_message);
        }
    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder {
        private TextView receiverText;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverText = itemView.findViewById(R.id.receiver_message);
        }
    }
}