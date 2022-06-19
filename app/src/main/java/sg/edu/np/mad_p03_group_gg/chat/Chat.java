package sg.edu.np.mad_p03_group_gg.chat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import sg.edu.np.mad_p03_group_gg.R;
import sg.edu.np.mad_p03_group_gg.User;
import sg.edu.np.mad_p03_group_gg.others.MemoryData;

public class Chat extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference databaseReference = database.getReference();

    private static final String TAG = "Chat";
    private List<ChatInfo> chatInfoList = new ArrayList<>();
    private RecyclerView chatRecyclerView;
    private String chatKey;
    private ChatAdapter chatAdapter;
    private User mainUser;
    private boolean loadFirstTime = true;
    String mainUserid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Getting main user object from chatList
        mainUser = getIntent().getParcelableExtra("mainUser");

        // Setting up recycler view for chat
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        // Ensures that height or width does not change
        chatRecyclerView.setHasFixedSize(true);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(Chat.this));
        // Setting chat adapter for recycler view
        chatAdapter = new ChatAdapter(chatInfoList,Chat.this, mainUser);
        chatRecyclerView.setAdapter(chatAdapter);

        ImageView backBtn = findViewById(R.id.backBtn);
        TextView name = findViewById(R.id.name);
        EditText messageToSend = findViewById(R.id.messageToSend);
        CircleImageView profilePic = findViewById(R.id.profilePic);
        ImageView sendBtn = findViewById(R.id.sendButton);

        // Getting data from messages adapter class
        String getName = getIntent().getStringExtra("name");
        String getProfilePic = getIntent().getStringExtra("profilePic");
        chatKey = getIntent().getStringExtra("chatKey");
        String getid = getIntent().getStringExtra("id");

        // Set main user ID
        mainUserid = mainUser.getId();

        // Set Name
        name.setText(getName);
        if(!TextUtils.isEmpty(getProfilePic)){
            Picasso.get().load(getProfilePic).into(profilePic);
        }

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Setting chat key
                    if(chatKey.isEmpty()) {
                        // ChatKey increment by 1 for each chat. Default chat key is 1 (for first 2 users)
                        chatKey = "1";

                        if (snapshot.hasChild("chat")) {
                            chatKey = String.valueOf(snapshot.child("chat").getChildrenCount() + 1);
                        }
                    }

                    // Getting message values (message sent, time, which user sent the message)
                    if(snapshot.hasChild("chat")){
                        if(snapshot.child("chat").child(chatKey).hasChild("messages")){
                            // Clear chatInfoList
                            chatInfoList.clear();
                            // For each message
                            for(DataSnapshot messageSnapshot : snapshot.child("chat").child(chatKey).child("messages").getChildren()){
                                if(messageSnapshot.hasChild("msg") && messageSnapshot.hasChild("id")){
                                    String messageTime = messageSnapshot.getKey();
                                    String getMessage = messageSnapshot.child("msg").getValue(String.class);
                                    String getid = messageSnapshot.child("id").getValue(String.class);

                                    Timestamp timestamp = new Timestamp(Long.parseLong(messageTime));
                                    // get current date
                                    Date date = new Date(timestamp.getTime());
                                    // format for date
                                    SimpleDateFormat DateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                    // format for time
                                    SimpleDateFormat TimeFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                                    // Create new ChatInfo object for each message
                                    ChatInfo chatInfo = new ChatInfo(getid, getName, getMessage, DateFormat.format(date), TimeFormat.format(timestamp));
                                    // Add to chatInfo list
                                    chatInfoList.add(chatInfo);

                                    // Saving message time to memory (txt file)
                                    if(loadFirstTime || Long.parseLong(messageTime) > Long.parseLong(MemoryData.getLastMsgTimeStamp(Chat.this, chatKey))){

                                        //loadFirstTime = false;

                                        MemoryData.saveLastMsgTimeStamp(messageTime, chatKey, Chat.this);
                                        // Update chatInfoList in adapter
                                        chatAdapter.updateChatList(chatInfoList);

                                        // Auto scroll to latest message
                                        chatRecyclerView.scrollToPosition(chatInfoList.size() - 1);
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG,"Failed to read value.", error.toException());
                }
            });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String getTextMessage = messageToSend.getText().toString();

                // Prevent sending empty message
                if (getTextMessage.isEmpty()){
                    return;
                }

                // Get current time (added 28800000 milliseconds to convert to SGT)
                String currentTime = String.valueOf(System.currentTimeMillis() + 28800000);

                databaseReference.child("chat").child(chatKey).child("user1").setValue(mainUserid);
                databaseReference.child("chat").child(chatKey).child("user2").setValue(getid);
                databaseReference.child("chat").child(chatKey).child("messages").child(currentTime).child("msg").setValue(getTextMessage);
                databaseReference.child("chat").child(chatKey).child("messages").child(currentTime).child("id").setValue(mainUserid);

                // Remove text EditText after message is sent
                messageToSend.setText("");
            }
        });

        // Finish activity
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}