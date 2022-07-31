package sg.edu.np.mad_p03_group_gg.messages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Message;
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
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import sg.edu.np.mad_p03_group_gg.R;
import sg.edu.np.mad_p03_group_gg.chat.Chat;
import sg.edu.np.mad_p03_group_gg.User;
import sg.edu.np.mad_p03_group_gg.listing_adapter;
import sg.edu.np.mad_p03_group_gg.others.RecyclerViewInterface;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {

    public List<MessageList> messageList;
    private Context context;
    private User mainUser;
    private DatabaseReference databaseReference;
    private final RecyclerViewInterface recyclerViewInterface;
//    private List<String> unseenMessageList = new ArrayList<>();
//    private HashMap<String, Boolean> unseenMessageDict = new HashMap<>();

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
    public void onViewRecycled(@NonNull ChatListAdapter.MyViewHolder holder) {

    }
    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.MyViewHolder holder, int position) {
        MessageList user = messageList.get(position);

        holder.name.setText(user.getName());
        holder.lastMessage.setText(user.getLastMessage());

        // Load user image
        holder.picUrl = user.getProfilePic();
        holder.LoadProfileImage();


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
        public String picUrl;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profilePic);
            name = itemView.findViewById(R.id.fullName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            unseenMessages = itemView.findViewById(R.id.unseenMessages);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            setIsRecyclable(false);
        }

        public void LoadProfileImage(){
            if(picUrl != null && !picUrl.isEmpty()){
                Picasso.get().load(picUrl).placeholder(R.drawable.profile_icon).into(profilePic);
            }
            else{
                profilePic.setImageResource(R.drawable.profile_icon);
            }
        }
    }



    // Filter recyclerview list method
    public void filterList(List<MessageList> filteredList){
        messageList = filteredList;
        this.notifyDataSetChanged();
    }
}
