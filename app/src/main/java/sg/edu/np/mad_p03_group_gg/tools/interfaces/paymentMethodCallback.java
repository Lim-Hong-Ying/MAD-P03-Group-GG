package sg.edu.np.mad_p03_group_gg.tools.interfaces;

/**
 * This is a utility class and it contains various methods to help retrive data from Firebase and
 * improve re-usability.
 */

public interface paymentMethodCallback {
    // return user's payment method stored on Firebase
    void userPaymentMethodCallBack(String paymentMethod);
}
