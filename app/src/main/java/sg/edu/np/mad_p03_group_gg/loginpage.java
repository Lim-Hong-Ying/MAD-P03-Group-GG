package sg.edu.np.mad_p03_group_gg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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
    private int EXTERNAL_STORAGE_PERMISSION_CODE = 23;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);
        auth = FirebaseAuth.getInstance(); // singleton
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
        fgtpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent fgtpassword = new Intent(loginpage.this, forget_password_activitiy.class);

                startActivity(fgtpassword);
            }
        });
        //On click of sign in button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log_in(view);

            }
        });
        //on click listner for sign up button
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signup = new Intent(loginpage.this,
                        signupactivity.class);
                startActivity(signup);//Starts sign up activity
            }
        });

        //Authenticate users
        auth = FirebaseAuth.getInstance();
    }


    public void Log_in(View v){
        EditText Email = (EditText) findViewById(R.id.fgtemail);
        EditText password = (EditText) findViewById(R.id.password_toggle);
        String email = Email.getText().toString();
        String Password = password.getText().toString();
        if (TextUtils.isEmpty(email)) {
            Email.setError("Missing email");
            return;

        }
        if(TextUtils.isEmpty(Password)){
            password.setError("Missing Password");
            return;

        }
        // If email is not a  email address
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Email.setError("Invalid Email Address");
            return;
        }
            auth.signInWithEmailAndPassword(email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //When user's sign in is sucessful, it will automatically go to the next page
                        Intent mainActivity = new Intent(loginpage.this, MainActivity.class);

                        startActivity(mainActivity); //Starts up main activity
                        loginpage.this.finish();
                    } else {
                        TextView error = findViewById(R.id.siginerror);
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