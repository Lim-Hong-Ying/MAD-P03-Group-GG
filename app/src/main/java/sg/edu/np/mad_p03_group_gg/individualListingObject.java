package sg.edu.np.mad_p03_group_gg;

public class individualListingObject extends listingObject {
    /*private String lID; //listing ID in DB
    private String title; //listing title
    private String tURL; //thumbnail URL
    private String sID; //seller ID
    private String sPPU; //seller profile picture
    private String iC; //item condition
    private int price; //item price
    private Boolean reserved; //is item reserved?*/
    private String description; //item description
    private String location; //deal location
    private Boolean delivery; //is delivery available?
    private String deliveryType; //if yes, what kind
    private String deliveryPrice; //price for delivery
    private String deliveryTime; //lead time for delivery

    public individualListingObject() {

    }

    public individualListingObject(String lid, String t, String turl, String sid, String sppu, String ic, String p, Boolean r, String desc, String l, Boolean d, String dt, String dp, String dtime) {
        /*setlID(lID);
        setTitle(t);
        settURL(turl);
        setSID(sid);
        setSPPU(sppu);
        setiC(ic);
        setPrice(p);
        setReserved(r);*/
        super(lid, t, turl, sid, sppu, ic, p, r);
        setDescription(desc);
        setLocation(l);
        setDelivery(d);
        setDeliveryType(dt);
        setDeliveryPrice(dp);
        setDeliveryTime(dtime);
    }

    /*public void setlID(String id) {
        lID = id;
    }

    public String getlID() {
        return lID;
    }

    public void setTitle(String t) {
        title = t;
    }

    public String getTitle() {
        return title;
    }

    public void settURL(String s) {
        tURL = s;
    }

    public String gettURL() {
        return tURL;
    }

    public void setSID(String id) {
        sID = id;
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

    public void setiC(String c) {
        iC = c;
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

    public void setReserved(Boolean r) {
        reserved = r;
    }

    public Boolean getReserved() {
        return reserved;
    }
     */

    public void setDescription(String s) {
        description = s;
    }

    public String getDescription() {
        return description;
    }

    public void setLocation(String s) {
        location = s;
    }

    public String getLocation() {
        return location;
    }

    public void setDelivery(Boolean d) {
        delivery = d;
    }

    public Boolean getDelivery() {
        return delivery;
    }

    public void setDeliveryType(String s) {
        deliveryType = s;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryPrice(String p) {
        deliveryPrice = p;
    }

    public String getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryTime(String t) {
        deliveryTime = t;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }
}
