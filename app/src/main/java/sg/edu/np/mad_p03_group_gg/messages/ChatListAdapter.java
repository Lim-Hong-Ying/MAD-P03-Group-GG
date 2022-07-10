package sg.edu.np.mad_p03_group_gg.messages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.internal.TextDrawableHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import sg.edu.np.mad_p03_group_gg.R;
import sg.edu.np.mad_p03_group_gg.chat.Chat;
import sg.edu.np.mad_p03_group_gg.User;
import sg.edu.np.mad_p03_group_gg.others.RecyclerViewInterface;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {

    private List<MessageList> messageList;
    private Context context;
    private User mainUser;
    private DatabaseReference databaseReference;
    private String selectedUserID;
    private final RecyclerViewInterface recyclerViewInterface;
    private List<String> unseenMessageList = new ArrayList<>();
    private HashMap<String, Boolean> unseenMessageDict = new HashMap<>();

    public ChatListAdapter(List<MessageList> messageList, Context context, User mainUser, DatabaseReference databaseReference,RecyclerViewInterface recyclerViewInterface ) {
        this.messageList = messageList;
        this.context = context;
        this.mainUser = mainUser;
        this.databaseReference = databaseReference;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ChatListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_list_row,null,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.MyViewHolder holder, int position) {
        MessageList user = messageList.get(position);
//        user = messageList.get(position);

        // Load/update profile pic on dataChange
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.profilePic.setImageResource(R.drawable.profile_icon);
                if(user.getProfilePic() != null && !user.getProfilePic().isEmpty()
                        && !snapshot.child("users").child(user.getid()).child("userprofilepic").getValue(String.class).isEmpty()){
//                    String profilePic = snapshot.child("users").child(user.getid()).child("userprofilepic").getValue(String.class);
                    Picasso.get().load(user.getProfilePic()).into(holder.profilePic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read from db
            }
        });

        holder.name.setText(user.getName());
        holder.lastMessage.setText(user.getLastMessage());


        // If 0 unseen messages, don't show number
        if(user.getUnseenMessages() == 0){
            holder.unseenMessages.setVisibility(View.GONE);
            holder.lastMessage.setTextColor(context.getResources().getColor(R.color.grey));
        }
        // If there are unseen messages, show number
        else{
            holder.unseenMessages.setVisibility(View.VISIBLE);
            holder.unseenMessages.setText(String.valueOf(user.getUnseenMessages()));
            holder.lastMessage.setTextColor(context.getResources().getColor(R.color.theme_blue));
        }

        // Sending name and profile pic to chat activity when user is clicked
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Starting chat activity
                Intent intent = new Intent(context, Chat.class);
                // Send selected user's data to chat activity
                intent.putExtra("name",user.getName());
                intent.putExtra("profilePic",user.getProfilePic());
                intent.putExtra("chatKey", user.getChatKey());
                intent.putExtra("id", user.getid());
                // Send main user data to chat activity
                intent.putExtra("mainUser", (Parcelable) mainUser);

                context.startActivity(intent);
            }
        });


        // On long click, delete user
        holder.rootLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (recyclerViewInterface != null){
                    recyclerViewInterface.onItemLongClick(user);
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView profilePic;
        private TextView name;
        private TextView lastMessage;
        private TextView unseenMessages;
        private LinearLayout rootLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profilePic);
            name = itemView.findViewById(R.id.fullName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            unseenMessages = itemView.findViewById(R.id.unseenMessages);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }


    }

    // Filter recyclerview list method
    public void filterList(List<MessageList> filteredList){
//        messageList.clear();
//        for(MessageList m: filteredList){
//            messageList.add(m);
//        }

        messageList = filteredList;
        this.notifyDataSetChanged();
    }
}
