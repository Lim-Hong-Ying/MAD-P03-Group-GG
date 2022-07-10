package sg.edu.np.mad_p03_group_gg.chat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    String getid;
    Boolean inchat;
    Boolean hasMessages = false;

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
        TextView statusView = findViewById(R.id.status);

        // Getting data from messages adapter class
        String getName = getIntent().getStringExtra("name");
        String getProfilePic = getIntent().getStringExtra("profilePic");
        chatKey = getIntent().getStringExtra("chatKey");
        getid = getIntent().getStringExtra("id");

        // Set main user ID
        mainUserid = mainUser.getId();

        // Set Name
        name.setText(getName);
        // Set profile pic
        if(!TextUtils.isEmpty(getProfilePic)){
            Picasso.get().load(getProfilePic).into(profilePic);
        }

        // On first entering chat, set all messages to seen
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Set all seen to true
                if (!TextUtils.isEmpty(chatKey)){
                    for (DataSnapshot message : snapshot.child("chat").child(chatKey).child("messages").getChildren()){
                        if (message.hasChild("seen") && TextUtils.equals( message.child("seen").getValue(String.class),"False") && message.getKey() != null){
                            // Get only those messages sent by the other user
                            if (!TextUtils.equals(message.child("id").getValue(String.class), mainUser.getId())){
                                databaseReference.child("chat").child(chatKey).child("messages").child(message.getKey()).child("seen").setValue("True");
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // Set user inchat status to true if chatkey exists
        if (!TextUtils.isEmpty(chatKey)){
            databaseReference.child("chat").child(chatKey).child(mainUser.getId()).child("inChat").setValue("True");
        }

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Get inChat value
                    inchat = Boolean.parseBoolean(snapshot.child("chat").child(chatKey).child(getid).child("inChat").getValue(String.class));

                    // Setting other user's status to top bar
                    if (snapshot.child("users").child(getid).hasChild("userState")){
                        // Get user status
                        String userStatus = snapshot.child("users").child(getid).child("userState").child("type").getValue(String.class);
                        String lastSeenDate;
                        String lastSeenTime;
                        String lastSeen;
                        // If user status is offline, get last seen date & time as well
                        if (TextUtils.equals(userStatus,"offline")){
                            lastSeenDate = snapshot.child("users").child(getid).child("userState").child("date").getValue(String.class);
                            lastSeenTime = snapshot.child("users").child(getid).child("userState").child("time").getValue(String.class);
                            lastSeen = "last seen " + lastSeenDate + " at " + lastSeenTime;
                            // Set colour to grey
                            statusView.setTextColor(Color.parseColor("#676767"));
                            statusView.setText(lastSeen);
                        }
                        else{
                            // If not offline, set other user's status to online
                            // Set Colour to green
                            statusView.setTextColor(Color.parseColor("#0AB605"));
                            statusView.setText("online");
                        }
                    }

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
                if (!TextUtils.isEmpty(chatKey)){
                    String getTextMessage = messageToSend.getText().toString();

                    // Prevent sending empty message
                    if (getTextMessage.isEmpty()){
                        return;
                    }

                    // Get current time (add 28800000 milliseconds to convert to SGT, if emulator timezone is UTC)
                    String currentTime = String.valueOf(System.currentTimeMillis());

                    // Set users
                    databaseReference.child("chat").child(chatKey).child("user1").setValue(mainUserid);
                    databaseReference.child("chat").child(chatKey).child("user2").setValue(getid);
                    databaseReference.child("chat").child(chatKey).child("messages").child(currentTime).child("msg").setValue(getTextMessage);
                    databaseReference.child("chat").child(chatKey).child("messages").child(currentTime).child("id").setValue(mainUserid);

                    // If user inchat status is true, set message seen value to True
                    if (inchat){
                        databaseReference.child("chat").child(chatKey).child("messages").child(currentTime).child("seen").setValue("True");
                    }
                    // If other user is not in current chat, set value to false
                    else{
                        databaseReference.child("chat").child(chatKey).child("messages").child(currentTime).child("seen").setValue("False");
                    }

                    // Remove text EditText after message is sent
                    messageToSend.setText("");

                    // If current user (you) are not already in other user's friend list, add to his friend list
                    databaseReference.child("selectedChatUsers").child(getid).child(mainUser.getId()).setValue("");

                    // Set in chat status to true if message is sent
                    databaseReference.child("chat").child(chatKey).child(mainUser.getId()).child("inChat").setValue("True");

                }

            }
        });

        // Finish activity
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // If chatkey has child messages, return true. (To prevent database set in onPause from setting to a unused key)
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("chat").child(chatKey).hasChild("messages")){
                    hasMessages = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Update user online/offline status
    public void updateUserStatus (String state){
        String saveCurrentDate;
        String saveCurrentTime;

        // Get current date
        Calendar date = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(date.getTime());

        // Get current Time
        Calendar time = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(time.getTime());

        // Create hashmap for storing status data
        Map currentStateMap = new HashMap();
        currentStateMap.put("time", saveCurrentTime);
        currentStateMap.put("date", saveCurrentDate);
        currentStateMap.put("type", state);

        // Update DB with status
        databaseReference.child("users").child(mainUser.getId()).child("userState").updateChildren(currentStateMap);
    }

    // On start of main activity, set user status to Online
    @Override
    protected void onStart() {
        super.onStart();
        // Set user status to online
        updateUserStatus("online");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateUserStatus("offline");
        // Set user inchat status to False is got chatKey
        if (!TextUtils.isEmpty(chatKey) && hasMessages){
            databaseReference.child("chat").child(chatKey).child(mainUser.getId()).child("inChat").setValue("False");
        }
    }
}