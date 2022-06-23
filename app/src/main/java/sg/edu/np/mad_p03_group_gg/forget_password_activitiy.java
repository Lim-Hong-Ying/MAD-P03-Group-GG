package sg.edu.np.mad_p03_group_gg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forget_password_activitiy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_activitiy);
        TextView passwordStatus= findViewById(R.id.fgtpasswordstatus);
        Button fgtpassword_btn = findViewById(R.id.fgt_pswrd_btn);
        EditText fgtEmail = (EditText) findViewById(R.id.fgtemail);

        FirebaseAuth auth;
        fgtpassword_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordStatus.setText("");

                if (isEmail(fgtEmail)) {
                    Log.e("Hi",Boolean.toString(isEmail(fgtEmail)));


                String ForgetEmail = fgtEmail.getText().toString().trim();

                FirebaseAuth.getInstance().sendPasswordResetEmail(ForgetEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                    passwordStatus.setText("An email has been sent to your email address");
                            }
                        });


                }
                else{
                    fgtEmail.setError("Invalid Email");

                }




            }

        });



    }
    //Checks if its a email or not

    boolean isEmail(EditText text) {  // checks if email input field is correct also checks if input field is empty using patterns libary
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
}