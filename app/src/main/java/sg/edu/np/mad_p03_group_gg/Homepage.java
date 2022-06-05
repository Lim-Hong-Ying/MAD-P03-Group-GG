package sg.edu.np.mad_p03_group_gg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Homepage extends AppCompatActivity {
    private String name, location, time, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        if (Event.eventsList.size() == 0){
            String userId = "123456789"; // temporary, will change to current user
            readFromFireBase(userId);
        }

        ImageView listingtest = findViewById(R.id.imageView2);

        listingtest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent listings = new Intent(Homepage.this, listings.class);
                startActivity(listings);
            }
        });
    }




    // for meeting planner
    public void readFromFireBase(String userId){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference("Planner");
        myRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    int eventId = Integer.parseInt(snapshot.getKey());
                    name = snapshot.child("name").getValue(String.class);
                    location = snapshot.child("location").getValue(String.class);
                    time = snapshot.child("time").getValue(String.class);
                    date = snapshot.child("date").getValue(String.class);
                    LocalDate dt = LocalDate.parse(date, dtf);
                    Event event = new Event(eventId, name, location, dt, time);
                    Event.eventsList.add(event);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("TAG", "Failed to read value.", error.toException());
            }
        });
    }
}