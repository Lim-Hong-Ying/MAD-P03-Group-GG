package sg.edu.np.mad_p03_group_gg.tools.interfaces;

import sg.edu.np.mad_p03_group_gg.individualListingObject;

/**
 * Interface for setting listObject variables only after it has been completely retrieved from
 * Firebase as it is an asynchronous method.
 *
 * Otherwise, by assigning the variable directly will
 * result in a null pointer exception since the listingObject has not been fully retrieved and
 * created form the Databse
 *
 * https://stackoverflow.com/questions/57330766/why-does-my-function-that-calls-an-api-or-launches-a-coroutine-return-an-empty-o
 */
public interface Callback {
    void listingObjectCallback(individualListingObject listingObject); //whatever your return type is: string, integer, etc.
}
