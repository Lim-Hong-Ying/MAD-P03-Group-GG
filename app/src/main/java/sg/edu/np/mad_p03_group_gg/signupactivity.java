package sg.edu.np.mad_p03_group_gg;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class signupactivity extends AppCompatActivity {
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupactivity);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference();

        //Get textview
        TextView tologin= findViewById(R.id.Log_in_page);
        //setonclick listener for tologin button

        Button signup = findViewById(R.id.button);
        auth=FirebaseAuth.getInstance();
        //Check if users has registered

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User u = new User();
                u = Register(v);

                if (u != null) {
                    myRef.child("users").child(u.getPhonenumber()).setValue(u);
                    Intent Homepage = new Intent(signupactivity.this, Homepage.class);
                    startActivity(Homepage);
                }
                //do something if not exists
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
    public User Register(View v){
        EditText name = findViewById(R.id.setusername);
        EditText Password = findViewById(R.id.enterpassword);
        EditText Email = findViewById(R.id.emailaddr);
        EditText PhoneNumber = findViewById(R.id.phone_number);
        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();
        String ph = PhoneNumber.getText().toString().trim();
        String userName = name.getText().toString().trim();
        User u = new User(userName,email,ph);

        if (TextUtils.isEmpty(email)) {
            Email.setError("Email Required");
            return null;
        }

        if (TextUtils.isEmpty(password)) {
            Password.setError("Password required");
            return null;
        }

        if (password.length()<6){
            Password.setError("Password lesser then 6 characters");
            return null;
        }

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(signupactivity.this,"User created",Toast.LENGTH_SHORT).show();
                }

                else {
                    Toast.makeText(signupactivity.this,"Unsuccessful",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return u;




    }

}