package sg.edu.np.mad_p03_group_gg.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    // Values
    private String id;
    private String name;
    private String email;

    // Constructor
    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Part of Parcelable
    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        email = in.readString();
    }
    // Part of Parcelable
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

    // Getters
    public String getid() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    // Setters
    public void setid(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Part of Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    // Part of Parcelable
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(email);
    }
}
