package sg.edu.np.mad_p03_group_gg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forget_password_activitiy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_activitiy);
        TextView passwordStatus= findViewById(R.id.fgtpasswordstatus);
        Button fgtpassword_btn = findViewById(R.id.fgt_pswrd_btn);
        EditText fgtEmail = findViewById(R.id.fgtemail);

        FirebaseAuth auth;
        fgtpassword_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isEmpty(fgtEmail)) {


                String ForgetEmail = fgtEmail.getText().toString();
                Log.e( "onClick: ",ForgetEmail);


                FirebaseAuth.getInstance().sendPasswordResetEmail(ForgetEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                    passwordStatus.setText("An email has been send to your email address");

                            }
                        });


                }
                else{
                    fgtEmail.setError("Invalid Email");

                }




            }

        });



    }
    private boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }
}