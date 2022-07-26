package sg.edu.np.mad_p03_group_gg.messages;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import sg.edu.np.mad_p03_group_gg.ChatAddUser;
import sg.edu.np.mad_p03_group_gg.ChatList;
import sg.edu.np.mad_p03_group_gg.R;
import sg.edu.np.mad_p03_group_gg.User;
import sg.edu.np.mad_p03_group_gg.chat.Chat;

public class ChatAddListAdapter extends RecyclerView.Adapter<ChatAddListAdapter.MyViewHolder> {

    private List<MessageList> messageList;
    private Context context;
    private User mainUser;
    private List<MessageList> addChatList = new ArrayList<>();

    public ChatAddListAdapter(List<MessageList> messageList, Context context, User mainUser) {
        this.messageList = messageList;
        this.context = context;
        this.mainUser = mainUser;
    }

    @NonNull
    @Override
    public ChatAddListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_list_row,null,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAddListAdapter.MyViewHolder holder, int position) {
        MessageList user = messageList.get(position);

        if(user.getProfilePic() != null && !user.getProfilePic().isEmpty()){
            Picasso.get().load(user.getProfilePic()).into(holder.profilePic);
        }


        holder.name.setText(user.getName());
        holder.lastMessage.setText(user.getLastMessage());

        // Don't display unseen messages
        holder.unseenMessages.setVisibility(View.GONE);

        // Updating chat list once clicked
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update recycler view list in chat List and go back to chat list
                // Add selected user to addChatList
                addChatList.add(user);
                // Create an intent back to the chatList
                Intent intent = new Intent(context, ChatList.class);
                // Give chatListAdapter add new list to update its recyclerview
                intent.putExtra("id",user.getid());
                ((ChatAddUser) context).setResult(ChatAddUser.RESULT_OK, intent);
                ((ChatAddUser)context).finish();
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

    public void filterList2(ArrayList<MessageList> filteredList){
        messageList = filteredList;
        this.notifyDataSetChanged();
    }
}
