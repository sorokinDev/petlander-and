package ru.codeoverflow.petlander.ui.matches;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import ru.codeoverflow.petlander.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import ru.codeoverflow.petlander.ui.chat.ChatActivity;
import ru.codeoverflow.petlander.util.NavUtil;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesViewHolders>{
    private List<MatchesObject> matchesList;
    private Context context;


    public MatchesAdapter(List<MatchesObject> matchesList, Context context){
        this.matchesList = matchesList;
        this.context = context;
    }

    @Override
    public MatchesViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matches, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MatchesViewHolders rcv = new MatchesViewHolders(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(MatchesViewHolders holder, int position) {
        MatchesObject match = matchesList.get(position);
        holder.matchDesc.setText(match.getDescription());
        holder.matchLocation.setText(match.getLocation());
        holder.ivMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), ChatActivity.class);
                Bundle b = new Bundle();
                b.putString("matchId", match.getUserId());
                intent.putExtras(b);
                holder.itemView.getContext().startActivity(intent);
            }
        });
        if(!match.getProfileImageUrl().equals("default")){
            Glide.with(context).load(match.getProfileImageUrl()).into(holder.matchImage);
        }
    }

    @Override
    public int getItemCount() {
        return this.matchesList.size();
    }
}
