package sg.edu.np.mad_p03_group_gg;

public class User {
    private String name;
    private String email;
    private String phonenumber;
    private String id;
    private String profilepicture;

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

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProfilePicture(String url) {
        profilepicture = url;
    }

    public String getProfilePicture() {
        return profilepicture;
    }

    public User(){}

    public User(String n, String e, String p) {
        setEmail(e);
        setName(n);
        setPhonenumber(p);
    }

    public User(String i, String n, String e, String p) {
        setId(i);
        setEmail(e);
        setName(n);
        setPhonenumber(p);
    }
}
