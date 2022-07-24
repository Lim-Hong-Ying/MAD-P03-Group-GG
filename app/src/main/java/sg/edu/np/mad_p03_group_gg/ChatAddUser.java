package sg.edu.np.mad_p03_group_gg;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sg.edu.np.mad_p03_group_gg.messages.ChatAddListAdapter;
import sg.edu.np.mad_p03_group_gg.messages.ChatListAdapter;
import sg.edu.np.mad_p03_group_gg.messages.MessageList;

public class ChatAddUser extends AppCompatActivity {

    private static final String TAG = "ChatAddUser";
    private List<MessageList> messagesList = new ArrayList<>();
    private User mainUser;
    private RecyclerView chatAddList;
    private String chatKey = "";
    private List<String> selectedUserList = new ArrayList<>();

    //    private boolean dataSet = false;
    private ChatAddListAdapter chatAddListAdapter;

    // Get firebase reference
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://cashoppe-179d4-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference databaseReference = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_add_user);

        // Get current user from chat list
        mainUser = getIntent().getParcelableExtra("mainUser");

        // Set recyclerView
        chatAddList = findViewById(R.id.chatAddList);
        chatAddList.setHasFixedSize(true);
        chatAddList.setLayoutManager(new LinearLayoutManager(this));

        messagesList.clear();
        chatAddListAdapter = new ChatAddListAdapter(messagesList,ChatAddUser.this,mainUser);
        chatAddList.setAdapter(chatAddListAdapter);

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

        // Populating recycler view with users
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messagesList.clear();
                chatKey = "";

                // Get selected user list from database
                selectedUserList.clear();
                for (DataSnapshot dataSnapshot2 : snapshot.child("selectedChatUsers").child(mainUser.getId()).getChildren()){
                    selectedUserList.add(dataSnapshot2.getKey());
                }

                // Create messageList object for each user that is not the current user
                for(DataSnapshot dataSnapshot : snapshot.child("users").getChildren()){

                    // Get id number stored in database
                    String getid = dataSnapshot.getKey();
                    //dataSet = false;

                    // If id number is not equals to the main user's id number and not in selectedUserList
                    if(!(TextUtils.equals(getid,mainUser.getId())) && !(selectedUserList.contains(getid))){
                        String getName = dataSnapshot.child("name").getValue(String.class);
                        String getProfilePic = dataSnapshot.child("userprofilepic").getValue(String.class);

                        // Create message list object with name and id only
                        MessageList messageList =
                                new MessageList(getName, getid, "", getProfilePic,chatKey,0);
                        // Update messageList will update chat or add non chat friends
                        // Buffer list
                        List<MessageList> bufferList1 = new ArrayList();
                        bufferList1 = messagesList;
                        bufferList1.add(messageList);
                        messagesList = bufferList1;
//                        chatAddListAdapter.updateData(messagesList);
                    }
                }
                chatAddListAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG,"Failed to read value.", error.toException());
            }
        });

    }

    // Filter Method for recyclerview
    private void filter(String text){
        ArrayList<MessageList> filteredList = new ArrayList<>();

        for (MessageList item : messagesList){
            if (item.getName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }

        chatAddListAdapter.filterList2(filteredList);
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
    }

}