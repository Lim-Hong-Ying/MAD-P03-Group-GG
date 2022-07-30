package sg.edu.np.mad_p03_group_gg.tools.interfaces;

/**
 * Interface for setting a boolean value to variables and is used in validating if the seller has
 * completely onboarded through requesting Account details from Stripe and checking the payment
 * status of the account. This will ensure the assignment of values only occur after it has been
 * completely retrieved from the backend server as it is an asynchronous method.
 *
 * Otherwise, by assigning the variable directly will
 * result in a null pointer exception since the listingObject has not been fully retrieved.
 *
 * */
public interface OnboardStatusCallback {
    void isOnboardCallback(Boolean isOnboard);
}
