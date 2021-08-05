package android.example.firechat;

public class Message {

    private String message;
    private String senderId;
    private long timeStamp;
    private String ImageToShare;

    public Message() {
    }

    public Message(String message, String senderId, long timeStamp, String imageToShare) {
        this.message = message;
        this.senderId = senderId;
        this.timeStamp = timeStamp;
        ImageToShare = imageToShare;
    }
    public String getImageToShare() {
        return ImageToShare;
    }

    public void setImageToShare(String imageToShare) {
        ImageToShare = imageToShare;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}