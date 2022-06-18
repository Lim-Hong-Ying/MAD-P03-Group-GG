package sg.edu.np.mad_p03_group_gg;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Parcelable{
    private String name;
    private String email;
    private String phonenumber;
    private String id;

    private String userprofilepic;
    private ArrayList<String> likinglst;

    // Parcelable (To pass objects from activity to activity)
    protected User(Parcel in) {
        likinglst = new ArrayList<String>();
        name = in.readString();
        email = in.readString();
        phonenumber = in.readString();
        id = in.readString();
        userprofilepic=in.readString();
        in.readStringList(likinglst);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(phonenumber);
        parcel.writeString(id);
        parcel.writeString(userprofilepic);
        parcel.writeStringList(likinglst);
    }

    // End

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


    public String getUserprofilepic() {
        return userprofilepic;
    }

    public void setUserprofilepic(String userprofilepic) {
        this.userprofilepic = userprofilepic;
    }

    public ArrayList<String> getLikinglst() {
        return likinglst;
    }

    public void setLikinglst(ArrayList<String> likinglst) {
        this.likinglst = likinglst;
    }

    public User(){
        likinglst = new ArrayList<String>();
    }

    public User(String n, String e,String i){
        setId(i);
        setName(n);
        setEmail(e);
        likinglst = new ArrayList<String>();
    }

    public User(String n, String e, String p,String up){
        setEmail(e);
        setName(n);
        setPhonenumber(p);
        setUserprofilepic(up);
        likinglst = new ArrayList<String>();
    }

    public User(String n, String e, String p,String up, ArrayList<String> lList){
        setEmail(e);
        setName(n);
        setPhonenumber(p);
        setUserprofilepic(up);
        setLikinglst(lList);
    }

}