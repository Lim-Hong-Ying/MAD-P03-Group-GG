package sg.edu.np.mad_p03_group_gg;

public class listingObject {
    protected int lID; //listing ID in DB
    protected String title; //listing title
    protected String tURL; //thumbnail URL
    protected String sID; //seller ID
    protected String sPPU; //seller profile picture
    protected String iC; //item condition
    protected int price; //item price
    protected Boolean reserved; //is item reserved?

    public listingObject() {

    }

    public listingObject(int lID, String t, String turl, String sid, String sppu, String ic, int p, Boolean r) {
        setlID(lID);
        setTitle(t);
        settURL(turl);
        setSID(sid);
        setSPPU(sppu);
        setiC(ic);
        setPrice(p);
        setReserved(r);
    }

    public void setlID(int id) {
        lID = id;
    }

    public int getlID() {
        return lID;
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
