package sg.edu.np.mad_p03_group_gg.messages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import sg.edu.np.mad_p03_group_gg.R;
import sg.edu.np.mad_p03_group_gg.chat.Chat;
import sg.edu.np.mad_p03_group_gg.User;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {

    private List<MessageList> messageList;
    private Context context;
    private User mainUser;

    public ChatListAdapter(List<MessageList> messageList, Context context, User mainUser) {
        this.messageList = messageList;
        this.context = context;
        this.mainUser = mainUser;
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

        if(user.getProfilePic() != null && !user.getProfilePic().isEmpty()){
            Picasso.get().load(user.getProfilePic()).into(holder.profilePic);
        }


        holder.name.setText(user.getName());
        holder.lastMessage.setText(user.getLastMessage());

        // Don't display unseen messages
        holder.unseenMessages.setVisibility(View.GONE);

        // If 0 unseen messages, don't show number
//        if(user.getUnseenMessages() == 0){
//            holder.unseenMessages.setVisibility(View.GONE);
//            holder.lastMessage.setTextColor(context.getResources().getColor(R.color.grey));
//        }
        // If there are unseen messages, show number
//        else{
//            holder.unseenMessages.setVisibility(View.VISIBLE);
//            holder.unseenMessages.setText(String.valueOf(user.getUnseenMessages()));
//            holder.lastMessage.setTextColor(context.getResources().getColor(R.color.theme_blue));
//        }

        // Sending name and profile pic to chat activity when user is clicked
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Chat.class);
                // Send selected user's data to chat activity
                intent.putExtra("name",user.getName());
                intent.putExtra("profilePic",user.getProfilePic());
                intent.putExtra("chatKey", user.getChatKey());
                intent.putExtra("id", user.getid());
                // Send main user data to chat activity
                intent.putExtra("mainUser", mainUser);

                context.startActivity(intent);
            }
        });

    }

    // To update messageList
    public void updateData(List<MessageList> updateMessageList){
        //this.messageList = messageList2;

        // looping all the messages to update
        for(MessageList ml: updateMessageList){
            //System.out.println(this.messageList);
            int i = 0;
            boolean foundMessage = false;
            for(MessageList ml2: this.messageList){
                //String chatKey = ml.getChatKey();
                String personId = ml.getid();
                if(personId.equals(ml2.getid())){
                    // update the found message with the latest data
                    this.messageList.set(i, ml);
                    foundMessage = true;
                    break;
                }
                ++i;
            }

            // not found means first time, not inside this.messageList
            if(foundMessage == false){
                this.messageList.add(ml);
            }
        }
        notifyDataSetChanged();
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
}
