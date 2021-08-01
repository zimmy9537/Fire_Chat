package android.example.firechat;

public class Users {
    private String name;

    public Users(String uid, String name, String email, String imageURI, String status) {
        this.name = name;
        this.email = email;
        this.imageURI = imageURI;
        this.uid = uid;
        this.status = status;
    }

    private String email;
    private String imageURI;
    private String uid;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Users() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
