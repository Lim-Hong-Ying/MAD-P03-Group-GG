package sg.edu.np.mad_p03_group_gg.tools.interfaces;

import sg.edu.np.mad_p03_group_gg.individualListingObject;

/**
 * Interface for assigning output from jsonObjectRequest to variables.
 *
 * https://stackoverflow.com/questions/57330766/why-does-my-function-that-calls-an-api-or-launches-a-coroutine-return-an-empty-o
 */
public interface Callback {
    void listingObjectCallback(individualListingObject listingObject); //whatever your return type is: string, integer, etc.
}
