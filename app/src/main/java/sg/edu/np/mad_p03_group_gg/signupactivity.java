package sg.edu.np.mad_p03_group_gg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

import sg.edu.np.mad_p03_group_gg.view.ui.CheckoutActivity;
import sg.edu.np.mad_p03_group_gg.view.ui.MainActivity;
import sg.edu.np.mad_p03_group_gg.view.ui.PaymentMethodActivity;
import sg.edu.np.mad_p03_group_gg.view.userConsent.PrivacyPolicy;
import sg.edu.np.mad_p03_group_gg.view.userConsent.TermsAndConditions;

public class signupactivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseDatabase database;
    private Boolean isTncAgree = false;
    private Boolean isPrivacyAgree = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupactivity);
        //Get database instance
        database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");


        //Get textview
        TextView tologin= findViewById(R.id.Log_in_page);
        //Get EditText view
        EditText name = findViewById(R.id.setusername);
        EditText Password = findViewById(R.id.enterpassword);
        EditText Email = findViewById(R.id.emailaddr);
        EditText PhoneNumber = findViewById(R.id.phone_number);
        //Get string
        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();
        String ph = PhoneNumber.getText().toString().trim();
        String userName = name.getText().toString().trim();
        String img ="";

        CheckBox tncCheckBox = findViewById(R.id.tncCheckbox);
        CheckBox privacyCheckBox = findViewById(R.id.privacyCheckbox);

        Button signup = findViewById(R.id.button);
        auth=FirebaseAuth.getInstance();

        User u = new User(userName,email,ph,img);
        //Set onclick listner for signup button
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User u = new User();
                if(checkDataEntered(v)){// Use checkdataentered to see if all data fields are entered correctly
                    Log.e("Status",Boolean.toString(checkDataEntered(v)));
                    u = Register(v);
                    if(u!=null) {
                        // check if user does not exist
                        String key = database.getReference("quiz").push().getKey();
                        //if user does nto exist
                    }

                }

            }
        });

        //Set onclick listner to login button
        tologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Login = new Intent(signupactivity.this, loginpage.class);
                //Clear activties on top of stack, prevent duplicate activites
                Login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(Login);//Starts log in activity

            }
        });

        // Get result from PrivacyPolicy Activity
        ActivityResultLauncher<Intent> privacyResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        isPrivacyAgree = Boolean.parseBoolean(data.getStringExtra("isAgree"));
                        if (isPrivacyAgree) {
                            privacyCheckBox.setChecked(true);
                        }
                        else {
                            privacyCheckBox.setChecked(false);
                        }
                    }
                });


        privacyCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent privacyIntent = new Intent(signupactivity.this,
                        PrivacyPolicy.class);
                privacyResultLauncher.launch(privacyIntent);
            }
        });

        // Get result from TermsAndConditions Activity
        ActivityResultLauncher<Intent> tncResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        isTncAgree = Boolean.parseBoolean(data.getStringExtra("isAgree"));
                        if (isTncAgree) {
                            tncCheckBox.setChecked(true);
                        }
                        else {
                            tncCheckBox.setChecked(false);
                        }
                    }
                });


        tncCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tncIntent = new Intent(signupactivity.this,
                        TermsAndConditions.class);
                tncResultLauncher.launch(tncIntent);
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
    public boolean checkDataEntered(View v){  // returns true when all input fields are correct, return false when not correct
        EditText name = findViewById(R.id.setusername);
        EditText Password = findViewById(R.id.enterpassword);
        EditText Email = findViewById(R.id.emailaddr);
        EditText PhoneNumber = findViewById(R.id.phone_number);
        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();
        String ph = PhoneNumber.getText().toString().trim();
        String userName = name.getText().toString().trim();
        //Check name field is empty
        if (isEmpty(name)) {
            name.setError("Username is required!");
            return false;
        }
        //Check password field is empty
        if (isEmpty(Password)) {
            Password.setError("Password is required!");
            return false;
        }
        if(password.length()<6){
            Password.setError("Please have a password that has more than 6 characters.");
            return false;
        }
        //Check email is empty or if it is a valid email, display error message when it is not

        if (isEmail(Email) == false) {
            Email.setError("Enter valid email!");
            return false;
        }
        //Check phonenumber is empty
        if (isEmpty(PhoneNumber)) {
            PhoneNumber.setError("Enter a valid number");
            return false;
        }

        if (isPrivacyAgree == false) {
            Toast.makeText(signupactivity.this, "Please agree to the privacy policy.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        if (isTncAgree == false) {
            Toast.makeText(signupactivity.this, "Please agree to the terms and conditions.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public User Register(View v){
        //Get Views
        EditText name = findViewById(R.id.setusername);
        EditText Password = findViewById(R.id.enterpassword);
        EditText Email = findViewById(R.id.emailaddr);
        EditText PhoneNumber = findViewById(R.id.phone_number);
        //Set variables
        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();
        String ph = PhoneNumber.getText().toString().trim();
        String userName = name.getText().toString().trim();
        String img ="";

        User u = new User(userName,email,ph,img);
        //Create user and create auth user instance

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //Inform user by displaying toast message when sucessful
                    Toast.makeText(signupactivity.this,"User created",Toast.LENGTH_SHORT).show();
                    if (u != null) {
                        DatabaseReference myRef = database.getReference();
                        String ID = auth.getUid();
                        u.setId(ID);
                        myRef.child("users").child(u.getId()).setValue(u);
                        Intent Homepage = new Intent(signupactivity.this,
                                MainActivity.class);
                        startActivity(Homepage);
                        //Finish activity
                        signupactivity.this.finish();
                    }
                }
                //If task is not sucessful, display toast message to inform user

                else {
                    Toast.makeText(signupactivity.this,"Unsuccessful",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return u;
    }

}