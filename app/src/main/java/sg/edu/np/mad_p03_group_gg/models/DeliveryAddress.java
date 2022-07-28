package sg.edu.np.mad_p03_group_gg.models;

/**
 * Process and parse the buyer's delivery address during a POST request to the backend.
 *
 * This will allow a payment intent to be generated with a customer address on Stripe.
 * The information collected is processed and stored by Stripe, Cashshope does not
 * store customer's delivery addrss on our own database.
 */
public class DeliveryAddress {
    private String line1;
    private String line2;
    private String postalCode;
    private String shippingName;

    public DeliveryAddress() {

    }

    public DeliveryAddress(String line1, String line2, String postalCode, String shippingName)
    {
        this.line1 = line1;
        this.line2 = line2;
        this.postalCode = postalCode;
        this.shippingName = shippingName;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getShippingName() {
        return shippingName;
    }

    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }
}
