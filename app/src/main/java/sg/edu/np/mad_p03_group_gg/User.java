package sg.edu.np.mad_p03_group_gg;

public class User {
    private String name;
    private String email;
    private String phonenumber;
    private String id;
    private String image;

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public User(){}

    public User(String n, String e, String p,String I){
        setEmail(e);
        setName(n);
        setPhonenumber(p);
        setImage(I);
    }
}
