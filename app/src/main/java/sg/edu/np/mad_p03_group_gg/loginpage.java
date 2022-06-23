package sg.edu.np.mad_p03_group_gg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;

import sg.edu.np.mad_p03_group_gg.view.ui.MainActivity;


public class loginpage extends AppCompatActivity {
    FirebaseAuth auth;
    //Set storage code for getting external storage permission
    private int EXTERNAL_STORAGE_PERMISSION_CODE = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);
        //Get current authenticated instance
        auth = FirebaseAuth.getInstance(); // singleton
        //Check for permissions and ask permissions
        if (ContextCompat.checkSelfPermission(loginpage.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(loginpage.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    23);
        }
        //find views and buttons
        Button button = (Button) findViewById(R.id.fgt_pswrd_btn);
        TextView signup = findViewById(R.id.Sign_up);
        TextView fgtpassword = findViewById(R.id.forgetpsswrdbtn);
        //On click listener to bring user to forget password activity when clicked
        fgtpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fgtpassword = new Intent(loginpage.this, forget_password_activitiy.class);

                startActivity(fgtpassword);
            }
        });
        // On click for sigin button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Sign in
                Log_in(view);

            }
        });
        //On click, bring user to signup activity
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signup = new Intent(loginpage.this,
                        signupactivity.class);
                startActivity(signup);//Starts sign up activity
            }
        });

        //Authenticate users

    }
    boolean isEmail(EditText text) {  // checks if email input field is correct also checks if input field is empty using patterns libary
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }



    public void Log_in(View v){
        TextView error = findViewById(R.id.siginerror);
        EditText Email = (EditText) findViewById(R.id.fgtemail);
        EditText password = (EditText) findViewById(R.id.password_toggle);
        String email = Email.getText().toString().trim();
        String Password = password.getText().toString();
        error.setVisibility(View.INVISIBLE);
        //If email is missing, set error
        if (!isEmail(Email)) {
            Email.setError("Invalid email");
            return;

        }
        // If password is missing, set error message
        if(TextUtils.isEmpty(Password)){
            password.setError("Missing Password");
            return;

        }

        //Get auth user from firebase
            auth.signInWithEmailAndPassword(email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //When user's sign in is sucessful, it will automatically go to the next page
                        Intent mainActivity = new Intent(loginpage.this, MainActivity.class);

                        startActivity(mainActivity); //Starts up main activity
                        //Finish current activity
                        loginpage.this.finish();
                    } else {
                        //If email/Password is wrong, set error message visible

                        error.setVisibility(View.VISIBLE);
                    }
                }
            });


    }
    public void request(View view) {
        // Requesting Permission to access External Storage
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                EXTERNAL_STORAGE_PERMISSION_CODE);
    }
}