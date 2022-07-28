package sg.edu.np.mad_p03_group_gg;

import java.util.ArrayList;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class individualListingObject extends listingObject {
    private String description; //item description
    private String category; //item category
    private String location; //deal location
    private Boolean delivery; //is delivery available?
    private String deliveryType; //if yes, what kind
    private String deliveryPrice; //price for delivery
    private String deliveryTime; //lead time for delivery


    public individualListingObject() {

    }

    public individualListingObject(String lid, String t, ArrayList<String> tURLs, String sid, String ic, String p, Boolean r, String category, String desc, String l, Boolean d, String dt, String dp, String dtime, String ts) {
        super(lid, t, tURLs, sid, ic, p, r, ts);
        setDescription(desc);
        setCategory(category);
        setLocation(l);
        setDelivery(d);
        setDeliveryType(dt);
        setDeliveryPrice(dp);
        setDeliveryTime(dtime);
    }

    public void setDescription(String s) {
        description = s;
    }

    public String getDescription() {
        return description;
    }

    public void setCategory(String s) {
        category = s;
    }

    public String getCategory() {
        return category;
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
