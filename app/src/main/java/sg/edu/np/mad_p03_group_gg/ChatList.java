package sg.edu.np.mad_p03_group_gg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import sg.edu.np.mad_p03_group_gg.classes.User;
import sg.edu.np.mad_p03_group_gg.messages.ChatListAdapter;
import sg.edu.np.mad_p03_group_gg.messages.MessageList;
import sg.edu.np.mad_p03_group_gg.others.MemoryData;

public class ChatList extends AppCompatActivity {
    private static final String TAG = "ChatList";
    private final List<MessageList> messagesList = new ArrayList<>();
    private User mainUser;
    private RecyclerView chatList;

//    private int unseenMessages = 0;
    private String lastMessage = "";
    private String chatKey = "";

//    private boolean dataSet = false;
    private ChatListAdapter chatListAdapter;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference databaseReference = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // Getting main profile pic from chat list
        CircleImageView mainProfilePic = findViewById(R.id.mainProfilePic);

        // TESTING: Code for testing purposes. Remove for final app
        mainUser = new User("12345678","Test Name","test@gmail.com");
        // mainUser = new User("98765432","Test Name 2","test2@gmail.com");
        // TO DO: Add back for final app:
        // Get intent data from main activity
        // mainUser = getIntent().getParcelableExtra("user");

        // Set recyclerView
        chatList = findViewById(R.id.chatList);
        chatList.setHasFixedSize(true);
        chatList.setLayoutManager(new LinearLayoutManager(this));
        // Setting adapter for recyclerView
        chatListAdapter = new ChatListAdapter(new ArrayList<MessageList>(),ChatList.this,mainUser);
        chatList.setAdapter(chatListAdapter);

        // Adding back button
        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Get profile picture from firebase db
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profilePicUrl = snapshot.child("users").child(mainUser.getid())
                        .child("profile_pic").getValue(String.class);

                if(!TextUtils.isEmpty(profilePicUrl)){
                    // Set profile pic to the (circle) image view
                    Picasso.get().load(profilePicUrl).into(mainProfilePic);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG,"Failed to read value.", error.toException());
            }
        });

        // Populating recycler view with users
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messagesList.clear();
//                unseenMessages = 0;
                lastMessage = "";
                chatKey = "";

                // Create messageList object for each user that is not the current user
                for(DataSnapshot dataSnapshot : snapshot.child("users").getChildren()){

                    // Get id number stored in database
                    String getid = dataSnapshot.getKey();
                    //dataSet = false;

                    // If id number is not equals to the main user's id number
                    if(!TextUtils.equals(getid,mainUser.getid())){
                        String getName = dataSnapshot.child("name").getValue(String.class);
                        String getProfilePic = dataSnapshot.child("profile_pic").getValue(String.class);

                        // load our friends in, chat or without chat.
                        MessageList messageList =
                                new MessageList(getName, getid, "", getProfilePic,chatKey);
                        messagesList.add(messageList);
                        // Update messageList will update chat or add non chat friends
                        chatListAdapter.updateData(messagesList);


                        // Getting chat list data
                        databaseReference.child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int getChatCount = (int) snapshot.getChildrenCount();
                                //lastMessage = "";
                                messagesList.clear();
                                //chatKey = "";
                                if(getChatCount > 0){
                                    // For each chat data in database
                                    for (DataSnapshot dataSnapshotCurrentChat : snapshot.getChildren()){
                                        // Get chat key
                                        String getKey = dataSnapshotCurrentChat.getKey();
                                        chatKey = getKey;
                                        lastMessage = "";
                                        if(dataSnapshotCurrentChat.hasChild("user1") && dataSnapshotCurrentChat.hasChild("user2")
                                                && dataSnapshotCurrentChat.hasChild("messages")){

                                            // Get id number of each user
                                            String getUserOne = dataSnapshotCurrentChat.child("user1").getValue(String.class);
                                            String getUserTwo = dataSnapshotCurrentChat.child("user2").getValue(String.class);

                                                // If id numbers are the same as main user and selected user's id number
                                            if((TextUtils.equals(getUserOne,getid) && TextUtils.equals(getUserTwo,mainUser.getid()))
                                                    || (TextUtils.equals(getUserOne,mainUser.getid()) && TextUtils.equals(getUserTwo, getid))){
//                                                unseenMessages = 0;
                                                for (DataSnapshot chatDataSnapshot : dataSnapshotCurrentChat.child("messages").getChildren()){

//                                                    long getMessageKey = Long.parseLong(chatDataSnapshot.getKey());
//                                                    long getLastSeenMessageTimeStamp = Long.
//                                                            parseLong(MemoryData.getLastMsgTimeStamp(ChatList.this, chatKey));

                                                    lastMessage = chatDataSnapshot.child("msg").getValue(String.class);
//                                                    if (getMessageKey > getLastSeenMessageTimeStamp){
//                                                        unseenMessages++;
//                                                    }
                                                }
                                                MessageList messageList =
                                                        new MessageList(getName, getid, lastMessage, getProfilePic,chatKey);
                                                messagesList.add(messageList);

                                            }
                                        }
                                    }
                                    chatListAdapter.updateData(messagesList);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Data base unable to get data.
                                Log.w(TAG,"Failed to read value.", error.toException());
                            }
                        });

                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG,"Failed to read value.", error.toException());
            }
        });

    }
}