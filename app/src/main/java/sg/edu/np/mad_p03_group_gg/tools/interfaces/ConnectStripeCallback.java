package sg.edu.np.mad_p03_group_gg.tools.interfaces;

/**
 * Interface for setting stripeAccountId to variables only after it has been completely retrieved
 * from Firebase as it is an asynchronous method.
 *
 * Otherwise, by assigning the variable directly will
 * result in a null pointer exception since the listingObject has not been fully retrieved and
 * created form the Databse
 *
 * */
public interface ConnectStripeCallback {
    void stripeAccountIdCallback(String stripeAccountId);
}
