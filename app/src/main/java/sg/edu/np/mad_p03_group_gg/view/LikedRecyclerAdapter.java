package sg.edu.np.mad_p03_group_gg.view;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LikedRecyclerAdapter extends RecyclerView.Adapter<LikedRecyclerAdapter.LikedRecyclerViewHolder> {
    ArrayList<String> likedList;

    public LikedRecyclerAdapter() { }

    public LikedRecyclerAdapter(ArrayList<String> likedList) {
        this.likedList = likedList;
    }

    @NonNull
    @Override
    public LikedRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull LikedRecyclerViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class LikedRecyclerViewHolder extends RecyclerView.ViewHolder {

        public LikedRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
