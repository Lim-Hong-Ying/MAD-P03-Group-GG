package sg.edu.np.mad_p03_group_gg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
        EditText fgtEmail = findViewById(R.id.fgtemail);
        TextView passwordStatus= findViewById(R.id.fgtpasswordstatus);
        String ForgetEmail = fgtEmail.toString().trim();
        FirebaseAuth auth;
        FirebaseAuth.getInstance().sendPasswordResetEmail(ForgetEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            passwordStatus.setText("An email has been send to your email address");
                        }
                    }
                });

    }
}