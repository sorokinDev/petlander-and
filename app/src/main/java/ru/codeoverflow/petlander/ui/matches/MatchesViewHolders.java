package ru.codeoverflow.petlander.ui.matches;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ru.codeoverflow.petlander.R;

import androidx.recyclerview.widget.RecyclerView;
import ru.codeoverflow.petlander.ui.chat.ChatActivity;

/**
 * Created by manel on 10/31/2017.
 */

public class MatchesViewHolders extends RecyclerView.ViewHolder{
    public TextView matchLocation, matchDesc;
    public ImageView matchImage, ivMsg;

    public MatchesViewHolders(View itemView) {
        super(itemView);
        matchImage = itemView.findViewById(R.id.MatchImage);
        matchLocation = itemView.findViewById(R.id.tv_location);
        matchDesc = itemView.findViewById(R.id.tv_desc);
        ivMsg = itemView.findViewById(R.id.iv_msg);
    }

}
