package sg.edu.np.mad_p03_group_gg;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Optional;

public class User implements Parcelable{
    private String name;
    private String email;
    private String phonenumber;
    private String id;

    private String userprofilepic;
    private ArrayList<String> likedList;

    // Parcelable (To pass objects from activity to activity)
    protected User(Parcel in) {
        name = in.readString();
        email = in.readString();
        phonenumber = in.readString();
        id = in.readString();
        userprofilepic=in.readString();
        in.readStringList(likedList);
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
        parcel.writeStringList(likedList);
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
        return likedList;
    }

    public void setLikinglst(ArrayList<String> likedList) {
        this.likedList = likedList;
    }

    public User(){}
    public User(String n, String e,String p){
        setPhonenumber(p);
        setName(n);
        setEmail(e);
    }

    public User(String n, String e, String p, String up){
        setName(n);
        setEmail(e);
        setPhonenumber(p);
        setUserprofilepic(up);
    }

    public User(String n, String e, String p, String up, @NonNull String userID){
        setName(n);
        setEmail(e);
        setPhonenumber(p);
        setUserprofilepic(up);
        setId(userID);
    }

    public User(String n, String e, String p,String up, ArrayList<String> lList){
        setEmail(e);
        setName(n);
        setPhonenumber(p);
        setUserprofilepic(up);
        setLikinglst(lList);
    }

}