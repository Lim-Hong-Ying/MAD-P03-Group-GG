package sg.edu.np.mad_p03_group_gg;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class listingObject {
    protected String lID; //listing ID in DB
    protected String title; //listing title
    protected ArrayList tURLs; //listing images URLs
    protected String sID; //seller ID
    protected String iC; //item condition
    protected String price; //item price
    protected Boolean reserved; //is item reserved?
    //###ISAAC: TIMESTAMP###
    protected String timeStamp;


    public listingObject() {

    }

    public listingObject(String lID, String t, ArrayList<String> tURLs, String sid, String ic, String p, Boolean r, String ts) {
        setlID(lID);
        setTitle(t);
        settURLs(tURLs);
        setSID(sid);
        setiC(ic);
        setPrice(p);
        setReserved(r);
        setTimeStamp(ts);
    }

    public void setTimeStamp(String ts) {
        timeStamp = ts;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setlID(String id) {
        lID = id;
    }

    public String getlID() {
        return lID;
    }

    public void setTitle(String s) {
        title = s;
    }

    public String getTitle() {
        return title;
    }

    public void settURLs(ArrayList<String> s) {
        tURLs = s;
    }

    public ArrayList<String> gettURLs() {
        return tURLs;
    }

    public void setSID(String s) {
        sID = s;
    }

    public String getSID() {
        return sID;
    }

    public void setiC(String s) {
        iC = s;
    }

    public String getiC() {
        return iC;
    }

    public void setPrice(String p) {
        price = p;
    }

    public String getPrice() {
        return price;
    }

    public void setReserved(Boolean r) {
        reserved = r;
    }

    public Boolean getReserved() {
        return reserved;
    }
}
