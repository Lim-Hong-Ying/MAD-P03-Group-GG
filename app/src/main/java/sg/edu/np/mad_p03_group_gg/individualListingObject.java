package sg.edu.np.mad_p03_group_gg;

public class individualListingObject {
    private String pID; //listing ID
    private String title;
    private String sID; //seller ID
    private String iC; //item condition
    private int price; //item price
    private String description; //item description
    private String location; //deal location
    private Boolean delivery; //is delivery available?
    private String deliveryType; //if yes, what kind
    private int deliveryPrice; //price for delivery
    private int deliveryTime; //lead time for delivery
    private Boolean reserved; //is item reserved?

    public individualListingObject() {

    }

    public individualListingObject(String pid, String t, String sid, String cond, int p, String desc, String l, Boolean d, String dt, int dp, int dtime, Boolean r) {
        pID = pid;
        title = t;
        sID = sid;
        iC = cond;
        price = p;
        description = desc;
        location = l;
        delivery = d;
        deliveryType = dt;
        deliveryPrice = dp;
        deliveryTime = dtime;
        reserved = r;
    }

    public void setpID(String id) {
        pID = id;
    }

    public String getpID() {
        return pID;
    }

    public void setTitle(String t) {
        title = t;
    }

    public String getTitle() {
        return title;
    }

    public void setsID(String id) {
        sID = id;
    }

    public String getsID() {
        return sID;
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

    public void setDeliveryPrice(int p) {
        deliveryPrice = p;
    }

    public int getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryTime(int t) {
        deliveryTime = t;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public void setReserved(Boolean r) {
        reserved = r;
    }

    public Boolean getReserved() {
        return reserved;
    }
}
