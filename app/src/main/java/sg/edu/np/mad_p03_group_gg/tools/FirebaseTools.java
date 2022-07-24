package sg.edu.np.mad_p03_group_gg.tools;


import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;

import sg.edu.np.mad_p03_group_gg.User;
import sg.edu.np.mad_p03_group_gg.individualListingObject;
import sg.edu.np.mad_p03_group_gg.tools.interfaces.Callback;
import sg.edu.np.mad_p03_group_gg.tools.interfaces.ConnectStripeCallback;
import sg.edu.np.mad_p03_group_gg.tools.interfaces.ThumbnailCallback;
import sg.edu.np.mad_p03_group_gg.tools.interfaces.paymentMethodCallback;

public  class FirebaseTools {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");
    private static DatabaseReference databaseReference = database.getReference();

    // Retrieve UserID from Authenticated User Session and get Payment Method of the User
    public static void getCurrentUserPaymentMethod(String userId, Context context,
                                                   paymentMethodCallback callback) {
        databaseReference.child("users").child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(context, "Error: Check your internet connection.", Toast.LENGTH_SHORT).show();
                } else {
                    //Builds individualListingObject from data retrieved
                    DataSnapshot result = task.getResult();

                    String paymentMethod;
                    try {
                        paymentMethod = result.child("paymentMethod").getValue(String.class);
                    }
                    catch (NullPointerException nullPointerException)
                    {
                        // If null. means user haven't choose payment method
                        paymentMethod = null;
                    }

                    callback.userPaymentMethodCallBack(paymentMethod);

                }
            }
        });
    }


    public static void getStripeConnectId(String sellerId, Context context,
                                          ConnectStripeCallback callback) {
        databaseReference.child("users").child(sellerId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(context, "Error: Check your internet connection.", Toast.LENGTH_SHORT).show();
                } else {
                    //Builds individualListingObject from data retrieved
                    DataSnapshot result = task.getResult();

                    String stripeConnectId;
                    try {
                        stripeConnectId = result.child("paymentMethod").getValue(String.class);
                    }
                    catch (NullPointerException nullPointerException)
                    {
                        // If null means seller haven't onboarded
                        stripeConnectId = null;
                    }

                    callback.connectIdCallback(stripeConnectId);

                }
            }
        });
    }

    public static void writePaymentInfo(String sellerId, @Nullable String connectStripeId,
                                        @Nullable String paynowPhone, @Nullable String cardanoWalletAddress) {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference ref = databaseReference.push();

                if (dataSnapshot.hasChild("paymentMethod"))
                {
                    if (connectStripeId != null) {
                        ref.child("users").child(sellerId).child("paymentMethod").
                                setValue(connectStripeId);
                    }
                    if (paynowPhone != null) {
                        ref.child("users").child(sellerId).child("paymentMethod").
                                setValue(paynowPhone);
                    }
                    if (cardanoWalletAddress != null) {
                        ref.child("users").child(sellerId).child("paymentMethod").
                                setValue(cardanoWalletAddress);
                    }
                }
                else {
                    databaseReference.child("users").child(sellerId).setValue("paymentMethod");

                    if (connectStripeId != null) {
                        ref.child("users").child(sellerId).child("paymentMethod").
                                setValue(connectStripeId);
                    }
                    if (paynowPhone != null) {
                        ref.child("users").child(sellerId).child("paymentMethod").
                                setValue(paynowPhone);
                    }
                    if (cardanoWalletAddress != null) {
                        ref.child("users").child(sellerId).child("paymentMethod").
                                setValue(cardanoWalletAddress);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("loadPost:onCancelled", databaseError.toException());
            }
        };
        databaseReference.addValueEventListener(postListener);
    }

    /**
     * Download ALL files in a certain directory (specified with the parameter, folder) from
     * the Firebase Storage and store them into a folder located within the cache (named after
     * the parameter)
     *
     * eg. downloadFiles("advertisement"), expect to find your files in the cache directory of
     * /advertisement
     *
     * @param folder
     */
    public static void downloadFiles(String folder, Context context, Activity activity) {
        // Init Firebase Storage instance
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://cashoppe-179d4.appspot.com/");

        // Create a storage reference, basically a pointer to a file in the Firebase cloud
        StorageReference storageRef = storage.getReference();

        // Create a child reference
        // imagesRef now points to "images"
        StorageReference filesRef = storageRef.child(folder);

        // List all images in /<folder> eg. can be /advertisement
        filesRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference fileRef : listResult.getItems()) {
                            // Download files from the folder, eg. images from /advertisement
                            try {
                                File outputDirectory = new File(context.getCacheDir(), folder);
                                if (!outputDirectory.exists()) {
                                    outputDirectory.mkdirs();
                                }

                                File localFile = File.createTempFile(folder, ".jpg", outputDirectory);
                                fileRef.getFile(localFile).addOnSuccessListener(
                                        new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                    }
                                });
                            }
                            catch (Exception e) {
                                Log.e("Downlaod image failed.", String.valueOf(e));
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                        Toast.makeText(activity,
                                "An Error Occured: Unable to List Items from Firebase",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void createListingObjectFromFirebase(String productId,
                                                       Context context,
                                                       Callback callback) {
        databaseReference.child("individual-listing").child(productId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(context, "Error: Check your internet connection.", Toast.LENGTH_SHORT).show();
                } else {
                    //Builds individualListingObject from data retrieved
                    DataSnapshot result = task.getResult();
                    individualListingObject individualListing;

                    String listingid = result.getKey();
                    String title = result.child("title").getValue(String.class);
                    String thumbnailurl = result.child("tURL").getValue(String.class);
                    long thumbnailurlsize = result.child("tURLs").getChildrenCount();
                    ArrayList<String> tURLs = new ArrayList<>();
                    for (int i = 0; i < thumbnailurlsize; i++) {
                        tURLs.add(result.child("tURLs").child(String.valueOf(i)).getValue(String.class));
                    }
                    String sellerid = result.child("sid").getValue(String.class);
                    String itemcondition = result.child("iC").getValue(String.class);
                    String price = result.child("price").getValue(String.class);
                    Boolean reserved = result.child("reserved").getValue(Boolean.class);
                    String desc = result.child("description").getValue(String.class);
                    String location = result.child("location").getValue(String.class);
                    Boolean delivery = result.child("delivery").getValue(Boolean.class);
                    String deliverytype = result.child("deliveryType").getValue(String.class);
                    String deliveryprice = result.child("deliveryPrice").getValue(String.class);
                    String deliverytime = result.child("deliveryTime").getValue(String.class);

                    String timeStamp;
                    try
                    {
                        timeStamp = result.child("timeStamp").getValue(String.class);
                    }
                    catch (NullPointerException e)
                    {
                        timeStamp = null;
                    }

                    individualListing = new individualListingObject(listingid, title, tURLs, sellerid,
                            itemcondition, price, reserved, desc, location,
                            delivery, deliverytype, deliveryprice, deliverytime, timeStamp);

                    callback.listingObjectCallback(individualListing);

                }
            }
        });
    }

    /**
     * Send message to user to confirm something.
     * E.g. when payment is done, auto send a chat message to seller.
     *
     * @param productId
     */
    public void sendConfirmationMessage(String productId) {
        // Get database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference databaseReference = database.getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get seller id
                for(DataSnapshot dataSnapshot : snapshot.child("individual-listing").getChildren()) {
                    String foundPID = dataSnapshot.getKey();
                    if(productId.equalsIgnoreCase(foundPID)){
                        sID = dataSnapshot.child("sid").getValue(String.class);
                        break;
                    }
                }

                // Get chatkey
                for (DataSnapshot dataSnapshotCurrentChat : snapshot.child("chat").getChildren()){
                    // Get id number of each user
                    String getUserOne = dataSnapshotCurrentChat.child("user1").getValue(String.class);
                    String getUserTwo = dataSnapshotCurrentChat.child("user2").getValue(String.class);

                    // If id numbers are the same as main user and selected user's id number
                    if((TextUtils.equals(getUserOne,sID) && TextUtils.equals(getUserTwo,uID))
                            || (TextUtils.equals(getUserOne,uID) && TextUtils.equals(getUserTwo, sID))){
                        chatKey = dataSnapshotCurrentChat.getKey();
                    }
                }

                // Create seller and main user Object
                for (DataSnapshot dataSnapshotUser : snapshot.child("users").getChildren()){
                    // If id matches main user ID Create main user object
                    if (TextUtils.equals(dataSnapshotUser.getKey(),uID)){
                        mainUser = new User(dataSnapshotUser.child("name").getValue(String.class)
                                ,dataSnapshotUser.child("email").getValue(String.class),uID);
                    }
                    // If id matches main SELLER ID Create seller user object
                    if (TextUtils.equals(dataSnapshotUser.getKey(),sID)){
                        seller = new User (dataSnapshotUser.child("name").getValue(String.class)
                                ,dataSnapshotUser.child("email").getValue(String.class)
                                ,dataSnapshotUser.child("phonenumber").getValue(String.class)
                                ,dataSnapshotUser.child("userprofilepic").getValue(String.class)
                                ,sID);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read from db
            }
        });

        // Get current time (add 28800000 milliseconds to convert to SGT, if emulator timezone is UTC)
        String currentTime = String.valueOf(System.currentTimeMillis());

        // Set users
        databaseReference.child("chat").child(chatKey).child("user1").setValue(mainUserid);
        databaseReference.child("chat").child(chatKey).child("user2").setValue(getid);
        // Set message and who sent the message
        databaseReference.child("chat").child(chatKey).child("messages").child(currentTime).child("msg").setValue(getTextMessage);
        databaseReference.child("chat").child(chatKey).child("messages").child(currentTime).child("id").setValue(mainUserid);

        // If user inchat status is true, set message seen value to True
        if (inchat){
            databaseReference.child("chat").child(chatKey).child("messages").child(currentTime).child("seen").setValue("True");
        }
        // If other user is not in current chat, set value to false
        else{
            databaseReference.child("chat").child(chatKey).child("messages").child(currentTime).child("seen").setValue("False");
        }

        // Remove text EditText after message is sent
        messageToSend.setText("");

        // If current user (you) are not already in other user's friend list, add to his friend list
        databaseReference.child("selectedChatUsers").child(getid).child(mainUser.getId()).setValue("");

        // Set in chat status to true if message is sent
        databaseReference.child("chat").child(chatKey).child(mainUser.getId()).child("inChat").setValue("True");

    }

}