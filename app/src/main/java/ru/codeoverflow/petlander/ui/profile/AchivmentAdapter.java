package ru.codeoverflow.petlander.ui.profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ru.codeoverflow.petlander.R;

import static ru.codeoverflow.petlander.App.getApplication;

public class AchivmentAdapter extends RecyclerView.Adapter<AchivmentViewHolders> {

    List<Achivment> list;
    Context context;

    public AchivmentAdapter(List<Achivment> list, Context context){
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public AchivmentViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_achivment, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        AchivmentViewHolders rcv = new AchivmentViewHolders(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull AchivmentViewHolders holder, int position) {
        holder.nameAch.setText(list.get(position).getNameAch());
        holder.descriptionAch.setText(list.get(position).getDescriptionAch());
        Glide.clear(holder.imgAch);
           String profileImageUrl = list.get(position).getUrl();
            switch(profileImageUrl){
                case "default":
                    Glide.with(getApplication()).load(R.mipmap.ic_launcher).into(holder.imgAch);
                    break;
                default:
                    Glide.with(getApplication()).load(profileImageUrl).into(holder.imgAch);
                    break;

        }

    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }
}
