package sg.edu.np.mad_p03_group_gg;

import java.util.ArrayList;

public class listingObject {
    private String title; //listing title
    private String tURL; //thumbnail URL
    private String sID; //seller ID
    private String sPPU; //seller profile picture
    private String iC; //item condition
    private int price; //item price
    private Boolean reserved; //is item reserved?

    public listingObject() {

    }

    public listingObject(String t, String turl, String sid, String sppu, String ic, int p, Boolean r) {
        setTitle(t);
        settURL(turl);
        setSID(sid);
        setSPPU(sppu);
        setiC(ic);
        setPrice(p);
        setReserved(r);
    }

    public void setTitle(String s) {
        title = s;
    }

    public String getTitle() {
        return title;
    }

    public void settURL(String s) {
        tURL = s;
    }

    public String getttURL() {
        return tURL;
    }

    public void setSID(String s) {
        sID = s;
    }

    public String getSID() {
        return sID;
    }

    public void setSPPU(String s) {
        sPPU = s;
    }

    public String getSPPU() {
        return sPPU;
    }

    public void setiC(String s) {
        iC = s;
    }

    public String getiC() {
        return iC;
    }

    public void setPrice(int p) {
        price = p;
    }

    public int getPrice() {
        return price;
    }

    public void setReserved (Boolean r) {
        reserved = r;
    }

    public Boolean getReserved() {
        return reserved;
    }
}
