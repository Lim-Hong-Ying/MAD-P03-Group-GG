package sg.edu.np.mad_p03_group_gg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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

import java.util.concurrent.CountDownLatch;

public class changeaccdetails extends AppCompatActivity {
    private CountDownLatch finishgetuser = new CountDownLatch(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");
            DatabaseReference mDataref = database.getReference();
            setContentView(R.layout.activity_changeaccdetails);
            //Get editext views
            EditText Username = (EditText) findViewById(R.id.changeusername);
            EditText Email = (EditText) findViewById(R.id.changedemail);
            EditText Phonenumber = (EditText) findViewById(R.id.changedphone);
            Button ChangeButton = (Button) findViewById(R.id.changedtls);
            EditText Changepassword = (EditText) findViewById((R.id.changepassword));
            EditText Cnfrmpassword = (EditText) findViewById(R.id.changecnfrmpassword);
            EditText EmailCredentials = (EditText) findViewById(R.id.ccredentialemail);
            EditText PasswordCredentials = (EditText) findViewById(R.id.ccredentialpwrd);
            // Current Login Credentials
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            User userdata = (User) getIntent().getParcelableExtra("User");
            //Set edittextview text to given name

            Username.setText(userdata.getName().toString());
            Email.setText(userdata.getEmail());
            Phonenumber.setText(userdata.getPhonenumber().toString());
            Log.e("Text", userdata.getName());
            ChangeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isEmail(Email) && isPhone(Phonenumber) && isText(Username) && isEmail(EmailCredentials) && isText(PasswordCredentials) && isPasswordSame(Changepassword, Cnfrmpassword) && isText(Changepassword) && isText(Cnfrmpassword)) {

                        AuthCredential credential = EmailAuthProvider.getCredential(EmailCredentials.getText().toString(), PasswordCredentials.getText().toString());
                        //Reautenthicate user, needed to change password and for better seciroty
                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {


                                    //----------------Code for Changing Email Address----------\\
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();// Since it takes a while to get the current user, we use the
                                    finishgetuser.countDown();// reduce countdown by 1. When countdown is reduced by 1, the below threads will be allowed to continue on which means only adter the above threads are done than will the below threads be run
                                    Log.e("User Email", user.getEmail().toString());
                                    user.updateEmail(Email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            // If task is suceessful, change user details by changing into user object
                                            if (task.isSuccessful()) {
                                                String ID = user.getUid();
                                                Log.e("ndatref",mDataref.toString());
                                                mDataref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot dataSnapshot : snapshot.child(("users")).getChildren()) {
                                                        Log.e("ID",dataSnapshot.getValue().toString());
                                                        User U = dataSnapshot.getValue(User.class);
                                                        Log.e("UID",ID);
                                                            if(U.getId().equals(ID)){

                                                            DatabaseReference ndataref = mDataref.child("users").child(ID);
                                                            Log.e("ndatref",ndataref.toString());

                                                            ndataref.child("email").setValue(Email.getText().toString());
                                                            ndataref.child("name").setValue(Username.getText().toString());
                                                            ndataref.child("phonenumber").setValue(Phonenumber.getText().toString());
                                                        }

                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                                //Change user password
                                                user.updatePassword(Changepassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(changeaccdetails.this, "Details changed successfully", Toast.LENGTH_LONG).show();
                                                            FirebaseAuth.getInstance().signOut();
                                                            Intent gototlogin = new Intent(changeaccdetails.this,loginpage.class);
                                                            startActivity(gototlogin);
                                                            finish();
                                                        } else {
                                                            Toast.makeText(changeaccdetails.this, "Something went wrong, check your email and password.", Toast.LENGTH_LONG).show();
                                                        }

                                                    }


                                                });
                                            } else {
                                                Toast.makeText(changeaccdetails.this, "Something went wrong.", Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    });

                                } else {
                                    Toast.makeText(changeaccdetails.this, "Username or password is wrong", Toast.LENGTH_SHORT).show();
                                }
                            }


                        });


                    } else {
                        if (isText(Username) == false) {
                            Username.setError("Invalid username");
                        }
                        if (isPhone(Phonenumber) == false) {
                            Phonenumber.setError("Invalid Phonenumber");
                        }
                        if (isEmail(Email) == false) {
                            Email.setError("Invalid Email");
                        }
                        if (isText(PasswordCredentials) == false) {
                            PasswordCredentials.setError("Password field is empty");
                        }
                        if (isEmail(EmailCredentials) == false) {
                            EmailCredentials.setError("Invalid Email");
                        }
                        if (isPasswordSame(Changepassword, Cnfrmpassword) == false) {
                            Changepassword.setError("Passwords do not match");
                            Cnfrmpassword.setError("Passwords do not match");
                        }
                        if (isText(Changepassword) == false) {
                            Changepassword.setError("Invalid Password");
                        }
                        if (isText(Cnfrmpassword) == false) {
                            Cnfrmpassword.setError("Invalid Password");
                        }


                        Toast.makeText(changeaccdetails.this, "Input fields not entered correctly", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        catch(Exception e){// If a serious error occur(Any other error that was unplanned) this will catch it and tell the user
            Toast.makeText(this, "Something went wrong, please try again",Toast.LENGTH_SHORT).show();
        }

    }

    //*****Methods to check items*****//
    boolean isText(EditText text){
        CharSequence Text = text.getText().toString();
        return(!TextUtils.isEmpty(Text));
    }
    boolean isEmail(EditText text) {  // checks if email input field is correct also checks if input field is empty using patterns libary
        CharSequence email = text.getText().toString();

        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
    boolean isPhone(EditText Phone){
        CharSequence phone = Phone.getText().toString();
        return(!TextUtils.isEmpty(phone)&& Patterns.PHONE.matcher(phone).matches());
    }
    boolean isPasswordSame(EditText p1, EditText p2){
        CharSequence P1 = p1.getText().toString();
        CharSequence P2 = p2.getText().toString();

        if(P1.equals(P2)){
            Log.e("True","True");
            return true;
        }
        else{

            Log.e("False","False");
            return false;
        }
    }
}