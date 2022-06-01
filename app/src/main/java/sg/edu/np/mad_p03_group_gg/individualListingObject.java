package sg.edu.np.mad_p03_group_gg;

public class individualListingObject extends listingObject {
    private String description;
    private String location;
    private Boolean delivery;
    private String deliveryType;
    private int deliveryPrice;
    private int deliveryTime;

    public individualListingObject() {

    }

    public individualListingObject(String desc, String l, Boolean d, String dt, int dp, int dtime) {
        description = desc;
        location = l;
        delivery = d;
        deliveryType = dt;
        deliveryPrice = dp;
        deliveryTime = dtime;
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
}
