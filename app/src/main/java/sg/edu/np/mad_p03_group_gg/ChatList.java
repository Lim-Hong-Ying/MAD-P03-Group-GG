package sg.edu.np.mad_p03_group_gg;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import sg.edu.np.mad_p03_group_gg.messages.ChatListAdapter;
import sg.edu.np.mad_p03_group_gg.messages.MessageList;
import sg.edu.np.mad_p03_group_gg.others.RecyclerViewInterface;

public class ChatList extends AppCompatActivity implements RecyclerViewInterface {
    private static final String TAG = "ChatList";
    private List<MessageList> messagesList = new ArrayList<>();
    private User mainUser;
    private RecyclerView chatList;
    private FirebaseAuth auth;
    private int unseenMessages = 0;
    private String lastMessage = "";
    private String chatKey = "";
    private String selectedUserID = "";
    private String currentUserID;

    private int replaceIndex;

    // List to store selected users of the main user
    private List<String> selectedUserList = new ArrayList<>();

    private ChatListAdapter chatListAdapter;

    private ActivityResultLauncher<Intent> startForResult;

    FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference databaseReference = database.getReference();
    boolean onCreateTriggered = false;
    ArrayList<ValueEventListener> firebaseListener = new ArrayList<ValueEventListener>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        onCreateTriggered = false;
        // Get current user id
        auth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = auth.getCurrentUser();
        currentUserID = fbUser.getUid();

        // Getting main profile pic from chat list
        CircleImageView mainProfilePic = findViewById(R.id.uprofilepic);

        // Adding back button
        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Search bar
        EditText editText = findViewById(R.id.search);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        ValueEventListener tempListener = null;
        // Get current user details and Get profile picture from firebase db
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Create main use object using current ID
                for(DataSnapshot dataSnapshot : snapshot.child("users").getChildren()) {
                    String foundId = dataSnapshot.child("id").getValue(String.class);
                    if(currentUserID.equalsIgnoreCase(foundId)){
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String displayName = dataSnapshot.child("name").getValue(String.class);
                        mainUser = new User(displayName, email, currentUserID);
                        break;
                    }
                }
                messagesList.clear();
                if(onCreateTriggered == false){
                    onCreateTriggered = true;
                    chatListAdapter = new ChatListAdapter(messagesList ,ChatList.this,mainUser, databaseReference, ChatList.this);
                }

                chatList.setAdapter(chatListAdapter);

                String profilePicUrl = snapshot.child("users").child(mainUser.getId())
                        .child("userprofilepic").getValue(String.class);

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
        // End of single value listener

        // Set recyclerView
        chatList = findViewById(R.id.chatList);
        chatList.setHasFixedSize(true);
        chatList.setLayoutManager(new LinearLayoutManager(this));

        // Add user to recycler view button
        ImageView addNewChat = findViewById(R.id.addChatBtn);


        // Creates activity (ChatAddUser) gets result and finishes the activity
         startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result != null && result.getResultCode() == RESULT_OK){
                    if(result.getData() != null && result.getData().getStringExtra("id") != null){
                        // Get selectedUserID from chat add page
                        selectedUserID = result.getData().getStringExtra("id");

                        // Set selected user list to firebase database
                        if (!TextUtils.isEmpty(selectedUserID)){
                            // Add selected user id to list
                            selectedUserList.add(selectedUserID);

                            databaseReference.child("selectedChatUsers").child(mainUser.getId())
                                    .child(selectedUserID).setValue("");
                        }
                    }
                }
            }
        });

        // Go to add new chat page
        addNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create intent to add chat page
                Intent intent = new Intent(ChatList.this, ChatAddUser.class);
                // Send main user data to add chat activity
                intent.putExtra("mainUser", (Parcelable) mainUser);
                startForResult.launch(intent);
            }
        });


        // Populating recycler view with users
        tempListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messagesList.clear();
//                unseenMessages = 0;
                lastMessage = "";
                chatKey = "";

                selectedUserList.clear();
                // Get selected user list from database
                for (DataSnapshot dataSnapshot2 : snapshot.child("selectedChatUsers").child(mainUser.getId()).getChildren()){
                    selectedUserList.add(dataSnapshot2.getKey());
                }

                // Create messageList object for each user that is not the current user
                for(DataSnapshot dataSnapshot : snapshot.child("users").getChildren()){

                    // Get id number stored in database
                    String getid = dataSnapshot.getKey();

                    // If id is found in selected UserList
                    if(selectedUserList.contains(getid)){
                        String getName = dataSnapshot.child("name").getValue(String.class);
                        String getProfilePic = dataSnapshot.child("userprofilepic").getValue(String.class);

                        // load our friends in, chat or without chat.
                        MessageList messageList =
                                new MessageList(getName, getid, "", getProfilePic,chatKey, 0);
                        // Buffer list
                        List<MessageList> bufferList1 = new ArrayList();
                        bufferList1 = messagesList;
                        bufferList1.add(messageList);
                        messagesList = bufferList1;
                        chatListAdapter.notifyDataSetChanged();

                        // Create messageList object for those who have a chat
                        databaseReference.child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int getChatCount = (int) snapshot.getChildrenCount();
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
                                            if((TextUtils.equals(getUserOne,getid) && TextUtils.equals(getUserTwo,mainUser.getId()))
                                                    || (TextUtils.equals(getUserOne,mainUser.getId()) && TextUtils.equals(getUserTwo, getid))){
                                                // Reset unseenMessages
                                                unseenMessages = 0;
                                                for (DataSnapshot chatDataSnapshot : dataSnapshotCurrentChat.child("messages").getChildren()){

                                                    // Get unseenMessages
                                                    if (chatDataSnapshot.hasChild("seen")){
                                                        // If message is sent by the other user (not main user)
                                                        if (!TextUtils.equals(chatDataSnapshot.child("id").getValue(String.class), mainUser.getId())){
                                                            boolean seen = Boolean.parseBoolean(chatDataSnapshot.child("seen").getValue(String.class));
                                                            // If message is not seen
                                                            if (!seen){
                                                                unseenMessages++;
                                                            }
                                                        }
                                                    }

                                                    lastMessage = chatDataSnapshot.child("msg").getValue(String.class);

                                                }
                                                MessageList messageList2 =
                                                        new MessageList(getName, getid, lastMessage, getProfilePic,chatKey, unseenMessages);

                                                // Getting index of messageList object to replace
                                                for (MessageList user : messagesList){
                                                    if(TextUtils.equals(user.getid(),messageList2.getid())){
                                                        replaceIndex = messagesList.indexOf(user);
                                                    }
                                                }
                                                // Replace those the users in messageList who have a already have a chat
                                                // Buffer list
                                                List<MessageList> bufferList2 = new ArrayList();
                                                bufferList2 = messagesList;
                                                bufferList2.set(replaceIndex, messageList2);
                                                messagesList = bufferList2;
                                                chatListAdapter.notifyDataSetChanged();

                                            }
                                        }
                                    }

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
                chatListAdapter.notifyDataSetChanged();

                chatListAdapter.filterList(messagesList);

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG,"Failed to read value.", error.toException());
            }
        });
        firebaseListener.add(tempListener);

    }

    // Delete user after long click
    @Override
    public void onItemLongClick (MessageList selectedUser){
        // Get ID of main user
        String mainId = mainUser.getId();
        // Remove user from DB and notify adapter class (message list will automatically be updated because of onDataChange() above)
        databaseReference.child("selectedChatUsers").child(mainId).child(selectedUser.getid()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                chatListAdapter.notifyDataSetChanged();
            }
        });


        chatListAdapter.filterList(messagesList);

        Toast.makeText(ChatList.this, "Successfully Removed", Toast.LENGTH_SHORT).show();
    }

    // Filter Method for recyclerview
    private void filter(String text){
        List<MessageList> filteredList = new ArrayList<>();

        for (MessageList item : messagesList){
            if (item.getName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }

        chatListAdapter.filterList(filteredList);
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
        databaseReference.child("users").child(currentUserID).child("userState").updateChildren(currentStateMap);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateUserStatus("offline");
        for(ValueEventListener listener: firebaseListener){
            databaseReference.removeEventListener(listener);
        }
    }
}