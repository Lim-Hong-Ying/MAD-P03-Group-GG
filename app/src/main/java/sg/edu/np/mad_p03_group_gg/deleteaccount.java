package sg.edu.np.mad_p03_group_gg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.CountDownLatch;

public class deleteaccount extends AppCompatActivity {
    private DatabaseReference mDataref;
    private FirebaseUser fbUser;
    private CountDownLatch final1 = new CountDownLatch(1);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleteaccount);
        //connect to db
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");
        mDataref = database.getReference();
        //get views
        EditText reemail = (EditText) findViewById(R.id.ccredentialemail);
        EditText repassword = (EditText) findViewById(R.id.repassword);
        Button cnrmuser = (Button) findViewById(R.id.changedtls);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        fbUser = auth.getCurrentUser();
        cnrmuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmpty(reemail)) {
                    reemail.setError("Email is required!");
                    return;
                }
                //Check password field is empty
                if (isEmpty(repassword)) {
                    repassword.setError("Password is required!");
                    return;
                }
                // get credentials


                AuthCredential credential = EmailAuthProvider
                        .getCredential(reemail.getText().toString(), repassword.getText().toString());
                // reauthenticate user, deleting of account required reauthentication
                fbUser.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            // on reauthentication complete
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mDataref.addListenerForSingleValueEvent(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            //Get authenticated user from an instance

                                            String uid = fbUser.getUid();
                                            String email = fbUser.getEmail();
                                            Log.e("test4","test4");
                                            //Use uid to find the user in database
                                            for (DataSnapshot dataSnapshot : snapshot.child("users").getChildren()) {
                                                String foundID = dataSnapshot.child("id").getValue(String.class);
                                                Log.e("foundID",foundID.toString());
                                                if (foundID.equalsIgnoreCase(uid)) {
                                                    //Create intent
                                                    Log.e("Passdatachange","snapshot2");
                                                    Intent intent = new Intent(deleteaccount.this, loginpage.class);
                                                    //If sign out have problem, create toast message informing user of problem
                                                    try {
                                                        Log.e("Passdatachange","snapshot1");
                                                        mDataref.child("individual-listing").addListenerForSingleValueEvent(new ValueEventListener(){

                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                Log.e("Passdatachange","snapshot");
                                                                Log.e("ref", mDataref.toString());

                                                                for(DataSnapshot dataSnapshot1 :snapshot.getChildren()){
                                                                    Log.e("Snapshot",dataSnapshot1.toString());
                                                                    individualListingObject foundID2 = dataSnapshot1.getValue(individualListingObject.class);

                                                                    if (foundID2.sID.equals(uid)) {
                                                                        Log.e("Categpry",foundID2.getCategory());

                                                                        Log.e("snapshot",dataSnapshot1.getRef().toString());

                                                                        mDataref.child("category").child(foundID2.getCategory()).child(foundID2.lID).getRef().removeValue();
                                                                        dataSnapshot1.getRef().removeValue();
                                                                        Log.e("pass","pass");
                                                                    }
                                                                }
                                                                //Get user instance from database and set user
                                                                Log.e("Usersnapshot",dataSnapshot.getRef().toString());


                                                                dataSnapshot.getRef().removeValue();
                                                                fbUser.delete();
                                                                Event.eventsList.clear();
                                                                //sign out from user
                                                                auth.signOut();
                                                                //Inform user activity finished
                                                                Toast.makeText(deleteaccount.this, "Account Deletion sucessful!", Toast.LENGTH_SHORT).show();
                                                                //Got to login page activity
                                                                startActivity(intent);
                                                                //Finish activity
                                                                deleteaccount.this.finish();


                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });



                                                    } catch (Exception e) {
                                                        //Error message
                                                        Toast.makeText(deleteaccount.this, "Something went wrong. Please check your internet connection", Toast.LENGTH_SHORT).show();
                                                    }

                                                    break;
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                            Log.w("Failed to read value.", error.toException());
                                        }
                                    });

                                } else {
                                    // Tell user if wrong email or password
                                    Toast.makeText(deleteaccount.this, "Wrong Email/Password", Toast.LENGTH_SHORT).show();

                                }
                            }

                        });

            }
        });



    }
    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString().trim();
        return TextUtils.isEmpty(str);
    }
    boolean isEmail(EditText text) {  // checks if email input field is correct also checks if input field is empty using patterns libary
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
}