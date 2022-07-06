package sg.edu.np.mad_p03_group_gg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

public class userProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Bundle userid = getIntent().getExtras();
        String uid = userid.getString("uid");

        ImageButton back_button = findViewById(R.id.back_button); //Enables back button function
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        retrieveProfileFromFirebase(uid);
    }

    private void retrieveProfileFromFirebase(String uid) {
        String db = "https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/"; //Points to Firebase Database
        FirebaseDatabase individualdb = FirebaseDatabase.getInstance(db); //Retrieves information

        individualdb.getReference().child("users").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Failed to retrieve information.", Toast.LENGTH_SHORT).show();
                }
                else {
                    User user = new User();
                    DataSnapshot result = task.getResult();

                    String username = result.child("name").getValue(String.class);
                    String profilePictureURL = result.child("userprofilepic").getValue(String.class);

                    TextView usernameHolder = findViewById(R.id.user_name);
                    ImageView profilepictureHolder = findViewById(R.id.profile_picture);

                    usernameHolder.setText(username);

                    if (!profilePictureURL.isEmpty()) {
                        Picasso.get().load(profilePictureURL).into(profilepictureHolder); //External library to download images
                    }
                }
            }
        });
    }
}