package sg.edu.np.mad_p03_group_gg.chat;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

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
        this.mainUserid = mainUser.getId();
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

        // If message sent is not an image
        if (!chatInfo.isImage()){
            // Hide the image layouts
            holder.outgoingLayoutImg.setVisibility(View.GONE);
            holder.incomingLayoutImg.setVisibility(View.GONE);

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
        // If message sent is an image
        else{
            // Hide the image layouts
            holder.outgoingLayout.setVisibility(View.GONE);
            holder.incomingLayout.setVisibility(View.GONE);

            // If chatInfo id is the same as main user's id
            if (TextUtils.equals(chatInfo.getid(),mainUserid)){
                // Change outgoing layout to visible
                holder.outgoingLayoutImg.setVisibility(View.VISIBLE);
                // Change incoming layout to be invisible
                holder.incomingLayoutImg.setVisibility(View.GONE);

                // Load Image from chatInfo according to position in list
                Picasso.get().load(chatInfo.getMessage()).into(holder.outgoingImage);
                // Setting time text was sent
                holder.outgoingTimeImg.setText(chatInfo.getDate()+" "+chatInfo.getTime());
            }
            // If chatInfo id is other user's
            else{
                // Change outgoing layout to invisible
                holder.outgoingLayoutImg.setVisibility(View.GONE);
                // Change incoming layout to be visible
                holder.incomingLayoutImg.setVisibility(View.VISIBLE);

                // Load Image from chatInfo according to position in list
                Picasso.get().load(chatInfo.getMessage()).into(holder.incomingImage);
                // Setting time text was sent
                holder.incomingTimeImg.setText(chatInfo.getDate()+" "+chatInfo.getTime());
            }
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

        private LinearLayout outgoingLayoutImg;
        private LinearLayout incomingLayoutImg;
        private ImageView outgoingImage;
        private ImageView incomingImage;
        private TextView outgoingTimeImg;
        private TextView incomingTimeImg;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Layout for text message
            outgoingLayout = itemView.findViewById(R.id.outgoingLayout);
            outgoingMessage = itemView.findViewById(R.id.outgoingMessage);
            outgoingTime = itemView.findViewById(R.id.outgoingTime);
            incomingLayout = itemView.findViewById(R.id.incomingLayout);
            incomingMessage = itemView.findViewById(R.id.incomingMessage);
            incomingTime = itemView.findViewById(R.id.incomingTime);

            // Layout for image
            outgoingLayoutImg = itemView.findViewById(R.id.outgoingLayoutImg);
            outgoingImage = itemView.findViewById(R.id.outgoingImage);
            outgoingTimeImg = itemView.findViewById(R.id.outgoingTimeImg);
            incomingLayoutImg = itemView.findViewById(R.id.incomingLayoutImg);
            incomingImage = itemView.findViewById(R.id.incomingImage);
            incomingTimeImg = itemView.findViewById(R.id.incomingTimeImg);
        }
    }

}
