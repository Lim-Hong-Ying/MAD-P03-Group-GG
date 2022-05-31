package sg.edu.np.mad_p03_group_gg;

public class User {
    private String name;
    private String email;
    private String phonenumber;
    private int id;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public User(){}

    public User(String n, String e, String p){
        setEmail(e);
        setName(n);
        setPhonenumber(p);
    }
}
