package sg.edu.np.mad_p03_group_gg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class signupactivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupactivity);
        database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");


        //Get textview
        TextView tologin= findViewById(R.id.Log_in_page);
        //setonclick listener for tologin button



        Button signup = findViewById(R.id.button);
        auth=FirebaseAuth.getInstance();
        //Check if users has registered
        EditText name = findViewById(R.id.setusername);
        EditText Password = findViewById(R.id.enterpassword);
        EditText Email = findViewById(R.id.emailaddr);
        EditText PhoneNumber = findViewById(R.id.phone_number);
        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();
        String ph = PhoneNumber.getText().toString().trim();
        String userName = name.getText().toString().trim();
        String img ="";

        User u = new User(userName,email,ph,img);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean status = CheckDataEntered(v);
                if(status) {

                    User u = new User();
                    u = Register(v);
                    if (u != null) {
                        String key = database.getReference("quiz").push().getKey();
                        u.setId(key);
                        //do something if not exists
                    } else {
                        name.setError(null);
                    }
                }


            }
        });


        tologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Login = new Intent(signupactivity.this,
                        loginpage.class);
                startActivity(Login);//Starts sign up activity
            }
        });



    }
    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        // checks if it is valid email using patterns libary. Will check if the input field is empty and is the email is valid
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    public boolean CheckDataEntered(View v){// returns true if all data entered is valid, returns false when date entered is not valid
        EditText Name = findViewById(R.id.setusername);
        EditText Password = findViewById(R.id.enterpassword);
        EditText Email = findViewById(R.id.emailaddr);
        EditText PhoneNumber = findViewById(R.id.phone_number);
        //if input fields are valid
        if (isEmpty(Name)) {
            Name.setError("Username Required");
            return false;
        }
        if (isEmail(Email) == false) {
            Email.setError("Enter valid email!");
            return false;
        }
        if (isEmpty(Name)) {
            Password.setError("Username Required");
            return false;
        }
        if (isEmpty(PhoneNumber)) {
            Password.setError("Username Required");
            return false;
        }
        return true;


    }
    public User Register(View v){
        EditText name = findViewById(R.id.setusername);
        EditText Password = findViewById(R.id.enterpassword);
        EditText Email = findViewById(R.id.emailaddr);
        EditText PhoneNumber = findViewById(R.id.phone_number);
        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();
        String ph = PhoneNumber.getText().toString().trim();
        String userName = name.getText().toString().trim();
        String img ="";
        User u = new User(userName,email,ph,img);
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(signupactivity.this,"User created",Toast.LENGTH_SHORT).show();
                    if (u != null) {
                        DatabaseReference myRef = database.getReference();
                        myRef.child("users").child(u.getId()).setValue(u);
                        Intent Homepage = new Intent(signupactivity.this,
                                Homepage.class);
                        startActivity(Homepage);

                    }
                }
                else{
                    Toast.makeText(signupactivity.this,"Unsuccessful",Toast.LENGTH_SHORT).show();


                }




            }
        });
        return u;




    }

}