package sg.edu.np.mad_p03_group_gg;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import sg.edu.np.mad_p03_group_gg.view.ui.MainActivity;

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
        //Get EditText view
        EditText name = findViewById(R.id.setusername);
        EditText Password = findViewById(R.id.enterpassword);
        EditText Email = findViewById(R.id.emailaddr);
        EditText PhoneNumber = findViewById(R.id.phone_number);
        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();
        String ph = PhoneNumber.getText().toString().trim();
        String userName = name.getText().toString().trim();
        String img ="";
        //setonclick listener for tologin button

        Button signup = findViewById(R.id.button);
        auth=FirebaseAuth.getInstance();
        //Check if users has registered


        User u = new User(userName,email,ph,img);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User u = new User();
                if(checkDataEntered(v)){
                    u = Register(v);
                    if(u!=null) {
                        String key = database.getReference("quiz").push().getKey();

                        //do something if not exists
                    }

                }

            }
        });


        tologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Login = new Intent(signupactivity.this, loginpage.class);
                startActivity(Login);//Starts sign up activity
            }
        });

    }
    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }
    boolean isEmail(EditText text) {  // checks if email input field is correct also checks if input field is empty using patterns libary
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public boolean checkDataEntered(View v){  // returns true when all input fields are correct, return false when not correct
        EditText Name = findViewById(R.id.setusername);
        EditText Password = findViewById(R.id.enterpassword);
        EditText Email = findViewById(R.id.emailaddr);
        EditText PhoneNumber = findViewById(R.id.phone_number);
        if (isEmpty(Name)) {
            Name.setError("Username is required!");
            return false;
        }
        if (isEmpty(Password)) {
            Password.setError("Password is required!");
            return false;
        }

        if (isEmail(Email) == false) {
            Email.setError("Enter valid email!");
            return false;
        }
        if (isEmpty(PhoneNumber)) {
            PhoneNumber.setError("Enter a valid number");
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
                if (task.isSuccessful()){
                    Toast.makeText(signupactivity.this,"User created",Toast.LENGTH_SHORT).show();
                    if (u != null) {
                        DatabaseReference myRef = database.getReference();
                        String ID = auth.getUid();
                        u.setId(ID);
                        myRef.child("users").child(u.getId()).setValue(u);
                        Intent Homepage = new Intent(signupactivity.this,
                                MainActivity.class);
                        startActivity(Homepage);
                        signupactivity.this.finish();
                    }
                }

                else {
                    Toast.makeText(signupactivity.this,"Unsuccessful",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return u;
    }

}