package android.example.firechat;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

public class MessageAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Message> messageArrayList;
    String senderUid;//this is my id. I am the one who is sending the message.
    String receiverUid;

    public MessageAdapter(Context context, ArrayList<Message> messageArrayList, String senderUid, String receiverUid) {
        this.context = context;
        this.messageArrayList = messageArrayList;
        if (messageArrayList == null) {
            Toast.makeText(context, "messageArraylist is empty", Toast.LENGTH_SHORT).show();
        }
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
    }

    int ITEM_SEND = 1;
    int ITEM_RECEIVE = 2;
    int SENDER_DATE_CHANGED = 3;
    int RECEIVER_DATE_CHANGED = 4;
    int SENDER_IMAGE = 5;
    int RECEIVER_IMAGE = 6;
    int SENDER_DATE_IMAGE = 7;
    int RECEIVER_DATE_IMAGE = 8;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //this maybe doubtful here what is being done?
        //u can see a parameter as viewType. this viewType we get from the function getItemViewType().
        //this returns the values as 1 or 2 as it is defined in the method
        if (viewType == 1) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_message_item, parent, false);
            return new SenderViewHolder(view);
        } else if (viewType == 2) {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_message_item, parent, false);
            return new ReceiverViewHolder(view);
        } else if (viewType == 3) {
            View view = LayoutInflater.from(context).inflate(R.layout.date_changed_sender_item, parent, false);
            return new DateSenderHolder(view);
        } else if (viewType == 4) {
            View view = LayoutInflater.from(context).inflate(R.layout.date_changed_receiver_item, parent, false);
            return new DateReceiverHolder(view);
        } else if (viewType == 5) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_image_item, parent, false);
            return new SenderImageHolder(view);
        } else if (viewType == 6) {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_image_item, parent, false);
            return new ReceiverImageHolder(view);
        } else if (viewType == 7) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_image_date_item, parent, false);
            return new SenderDateImageHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_image_date_item, parent, false);
            return new ReceiverDateImageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageArrayList.get(position);
        Date currentMessageDate = new Date(message.getTimeStamp());
        if (holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
            senderViewHolder.senderText.setText(message.getMessage());
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
            String time = dateFormat.format(currentMessageDate);
            senderViewHolder.senderTime.setText(time);
        } else if (holder.getClass() == DateSenderHolder.class) {
            DateSenderHolder dateSenderHolder = (DateSenderHolder) holder;
            dateSenderHolder.senderText.setText(message.getMessage());
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
            String time = dateFormat.format(currentMessageDate);
            dateSenderHolder.senderTime.setText(time);
            dateFormat = new SimpleDateFormat("EEE, MMM d, ''yy");
            time = dateFormat.format(currentMessageDate);
            dateSenderHolder.dateTextView.setText(time);
        } else if (holder.getClass() == DateReceiverHolder.class) {
            DateReceiverHolder dateReceiverHolder = (DateReceiverHolder) holder;
            dateReceiverHolder.receiverText.setText(message.getMessage());
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
            String time = dateFormat.format(currentMessageDate);
            dateReceiverHolder.receiverTime.setText(time);
            dateFormat = new SimpleDateFormat("EEE, MMM d, ''yy");
            time = dateFormat.format(currentMessageDate);
            dateReceiverHolder.dateTextView.setText(time);
        } else if (holder.getClass() == ReceiverViewHolder.class) {
            //this is meant for the receiver class
            ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
            receiverViewHolder.receiverText.setText(message.getMessage());
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
            String time = dateFormat.format(currentMessageDate);
            receiverViewHolder.receiverTime.setText(time);
        } else if (holder.getClass() == SenderImageHolder.class) {
            SenderImageHolder senderImageHolder = (SenderImageHolder) holder;
            senderImageHolder.senderMessage.setText(message.getMessage());
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
            String time = dateFormat.format(currentMessageDate);
            Glide.with(context).load(message.getImageToShare()).into(senderImageHolder.senderImage);
            senderImageHolder.timeTextView.setText(time);
        } else if (holder.getClass() == ReceiverImageHolder.class) {
            ReceiverImageHolder receiverImageHolder = (ReceiverImageHolder) holder;
            receiverImageHolder.receiverMessage.setText(message.getMessage());
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
            String time = dateFormat.format(currentMessageDate);
            receiverImageHolder.timeTextView.setText(time);
            Glide.with(context).load(message.getImageToShare()).into(receiverImageHolder.receiverImage);
        } else if (holder.getClass() == SenderDateImageHolder.class) {
            SenderDateImageHolder senderDateImageHolder = (SenderDateImageHolder) holder;
            senderDateImageHolder.senderMessage.setText(message.getMessage());
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
            String time = dateFormat.format(currentMessageDate);
            senderDateImageHolder.timeTextView.setText(time);
            Glide.with(context).load(message.getImageToShare()).into(senderDateImageHolder.senderImage);
        } else {
            ReceiverDateImageHolder receiverDateImageHolder = (ReceiverDateImageHolder) holder;
            receiverDateImageHolder.receiverMessage.setText(message.getMessage());
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
            String time = dateFormat.format(currentMessageDate);
            receiverDateImageHolder.timeTextView.setText(time);
            Glide.with(context).load(message.getImageToShare()).into(receiverDateImageHolder.receiverImage);
        }
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int getItemViewType(int position) {
        Message messages = messageArrayList.get(position);
        Date currentMessageDate = new Date(messages.getTimeStamp());
        Date previousMessageDate = currentMessageDate;
        if (position > 0) {
            previousMessageDate = new Date(messageArrayList.get(position - 1).getTimeStamp());
        }
        if (messages.getImageToShare() == null) {
            if ((messages.getSenderId().equals(senderUid) && !isSameDay(previousMessageDate, currentMessageDate)) || (messages.getSenderId().equals(senderUid) && position == 0)) {
                return SENDER_DATE_CHANGED;
            } else if ((messages.getSenderId().equals(receiverUid) && !isSameDay(previousMessageDate, currentMessageDate)) || messages.getSenderId().equals(receiverUid) && position == 0) {
                return RECEIVER_DATE_CHANGED;
            } else if (messages.getSenderId().trim().equals(senderUid.trim())) {
                return ITEM_SEND;
            } else {
                return ITEM_RECEIVE;
            }
        } else {
            if ((messages.getSenderId().equals(senderUid) && !isSameDay(previousMessageDate, currentMessageDate)) || (messages.getSenderId().equals(senderUid) && position == 0)) {
                return SENDER_DATE_IMAGE;
            } else if ((messages.getSenderId().equals(receiverUid) && !isSameDay(previousMessageDate, currentMessageDate)) || messages.getSenderId().equals(receiverUid) && position == 0) {
                return RECEIVER_DATE_IMAGE;
            } else if (messages.getSenderId().trim().equals(senderUid.trim())) {
                return SENDER_IMAGE;
            } else {
                return RECEIVER_IMAGE;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean isSameDay(Date date1, Date date2) {
        LocalDate localDate1 = date1.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate localDate2 = date2.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return localDate1.isEqual(localDate2);
    }

    class SenderViewHolder extends RecyclerView.ViewHolder {
        private TextView senderText;
        private TextView senderTime;
        private RelativeLayout senderRelative;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderText = itemView.findViewById(R.id.sender_message);
            senderTime = itemView.findViewById(R.id.sender_time);
            senderRelative = itemView.findViewById(R.id.sender_message_RL);
        }
    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder {
        private TextView receiverText;
        private TextView receiverTime;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverText = itemView.findViewById(R.id.receiver_message);
            receiverTime = itemView.findViewById(R.id.receiver_time);
        }
    }

    class DateSenderHolder extends RecyclerView.ViewHolder {

        private TextView dateTextView;
        private TextView senderText;
        private TextView senderTime;

        public DateSenderHolder(@NonNull View itemView) {
            super(itemView);
            senderText = itemView.findViewById(R.id.sender_message_date);
            senderTime = itemView.findViewById(R.id.sender_time_date);
            dateTextView = itemView.findViewById(R.id.date_specifier_textView_sender);
        }
    }

    class DateReceiverHolder extends RecyclerView.ViewHolder {

        private TextView receiverText;
        private TextView receiverTime;
        private TextView dateTextView;

        public DateReceiverHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date_specifier_textView_receiver);
            receiverText = itemView.findViewById(R.id.receiver_message_date);
            receiverTime = itemView.findViewById(R.id.receiver_time_date);
        }
    }

    class SenderImageHolder extends RecyclerView.ViewHolder {

        private TextView timeTextView;
        private TextView senderMessage;
        private ImageView senderImage;

        public SenderImageHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.sender_time_media);
            senderMessage = itemView.findViewById(R.id.sender_message_media);
            senderImage = itemView.findViewById(R.id.sender_image_media);
        }
    }

    class ReceiverImageHolder extends RecyclerView.ViewHolder {

        private TextView timeTextView;
        private TextView receiverMessage;
        private ImageView receiverImage;

        public ReceiverImageHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.receiver_time_media);
            receiverMessage = itemView.findViewById(R.id.receiver_message_media);
            receiverImage = itemView.findViewById(R.id.receiver_image_media);
        }
    }

    class SenderDateImageHolder extends RecyclerView.ViewHolder {

        private TextView timeTextView;
        private TextView senderMessage;
        private ImageView senderImage;
        private TextView dateTextView;

        public SenderDateImageHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.sender_time_media_date);
            senderMessage = itemView.findViewById(R.id.sender_message_media_date);
            senderImage = itemView.findViewById(R.id.sender_image_media_date);
            dateTextView = itemView.findViewById(R.id.date_specifier_sender_media);
        }
    }

    class ReceiverDateImageHolder extends RecyclerView.ViewHolder {

        private TextView timeTextView;
        private TextView receiverMessage;
        private ImageView receiverImage;
        private TextView dateTextView;

        public ReceiverDateImageHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.receiver_time_date_media);
            receiverMessage = itemView.findViewById(R.id.receiver_message_date_media);
            receiverImage = itemView.findViewById(R.id.receiver_image_date_media);
            dateTextView = itemView.findViewById(R.id.date_specifier_receiver_media);
        }
    }
}