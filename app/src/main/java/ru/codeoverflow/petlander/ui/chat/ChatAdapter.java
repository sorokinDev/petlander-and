package ru.codeoverflow.petlander.ui.chat;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import ru.codeoverflow.petlander.R;



import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by manel on 10/31/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolders>{
    private List<ChatObject> chatList;
    private Context context;
    private RecyclerView recyclerView;


    public ChatAdapter(List<ChatObject> matchesList, Context context){
        this.chatList = matchesList;
        this.context = context;
    }

    @Override
    public ChatViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        ChatViewHolders rcv = new ChatViewHolders(layoutView);


        return rcv;
    }

    @Override
    public void onBindViewHolder(ChatViewHolders holder, int position) {
        holder.mMessage.setText(chatList.get(position).getMessage());
        if(chatList.get(position).getCurrentUser()){
            holder.mImageViewRight.setVisibility(View.VISIBLE);
            holder.mImageViewLeft.setVisibility(View.GONE);
            holder.mContainer.setGravity(Gravity.RIGHT);
            holder.mMessage.setBackground(ContextCompat.getDrawable(context,R.drawable.back_message_me));

        }else{
            holder.mImageViewRight.setVisibility(View.GONE);
            holder.mImageViewLeft.setVisibility(View.VISIBLE);
            holder.mContainer.setGravity(Gravity.LEFT);
            holder.mMessage.setBackground(ContextCompat.getDrawable(context,R.drawable.back_message_you));

        }



    }

    @Override
    public int getItemCount() {
        return this.chatList.size();
    }
}
