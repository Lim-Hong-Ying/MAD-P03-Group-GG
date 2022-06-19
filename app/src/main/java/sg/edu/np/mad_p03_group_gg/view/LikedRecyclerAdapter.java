package sg.edu.np.mad_p03_group_gg.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sg.edu.np.mad_p03_group_gg.R;

public class LikedRecyclerAdapter extends RecyclerView.Adapter<LikedRecyclerAdapter.LikedRecyclerViewHolder> {
    ArrayList<String> likedList;

    public LikedRecyclerAdapter() { }

    public LikedRecyclerAdapter(ArrayList<String> likedList) {
        this.likedList = likedList;
    }

    @NonNull
    @Override
    public LikedRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.liked_item, parent, false);;

        return new LikedRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LikedRecyclerViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return likedList.size();
    }

    public class LikedRecyclerViewHolder extends RecyclerView.ViewHolder {

        public LikedRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
