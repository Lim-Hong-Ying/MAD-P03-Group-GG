package sg.edu.np.mad_p03_group_gg.chat;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sg.edu.np.mad_p03_group_gg.R;
import sg.edu.np.mad_p03_group_gg.User;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private List<ChatInfo> chatInfoList;
    private Context context;
    private String mainUserid;

    // Constructor
    public ChatAdapter(List<ChatInfo> chatInfoList, Context context, User mainUser) {
        this.chatInfoList = chatInfoList;
        this.context = context;
        this.mainUserid = mainUser.getPhonenumber();
    }

    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_layout,null,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.MyViewHolder holder, int position) {
        ChatInfo chatInfo = chatInfoList.get(position);

        // If chatInfo id is the same as main user's id
        if (TextUtils.equals(chatInfo.getid(),mainUserid)){
            // Change outgoing layout to visible
            holder.outgoingLayout.setVisibility(View.VISIBLE);
            // Change incoming layout to be invisible
            holder.incomingLayout.setVisibility(View.GONE);

            // Setting text from chatInfo according to position in list
            holder.outgoingMessage.setText(chatInfo.getMessage());
            // Setting time text was sent
            holder.outgoingTime.setText(chatInfo.getDate()+" "+chatInfo.getTime());
        }
        // If chatInfo id is other user's
        else{
            // Change outgoing layout to invisible
            holder.outgoingLayout.setVisibility(View.GONE);
            // Change incoming layout to be visible
            holder.incomingLayout.setVisibility(View.VISIBLE);

            // Setting text from chatInfo according to position in list
            holder.incomingMessage.setText(chatInfo.getMessage());
            // Setting time text was sent
            holder.incomingTime.setText(chatInfo.getDate()+" "+chatInfo.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return chatInfoList.size();
    }

    public void updateChatList(List<ChatInfo> chatInfoList){

        this.chatInfoList = chatInfoList;
    }

    // View holder
    static class MyViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout outgoingLayout;
        private LinearLayout incomingLayout;
        private TextView outgoingMessage;
        private TextView incomingMessage;
        private TextView outgoingTime;
        private TextView incomingTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            outgoingLayout = itemView.findViewById(R.id.outgoingLayout);
            outgoingMessage = itemView.findViewById(R.id.outgoingMessage);
            outgoingTime = itemView.findViewById(R.id.outgoingTime);
            incomingLayout = itemView.findViewById(R.id.incomingLayout);
            incomingMessage = itemView.findViewById(R.id.incomingMessage);
            incomingTime = itemView.findViewById(R.id.incomingTime);

        }
    }
}
