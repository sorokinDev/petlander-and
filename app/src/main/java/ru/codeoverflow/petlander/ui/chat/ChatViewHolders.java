package ru.codeoverflow.petlander.ui.chat;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.codeoverflow.petlander.R;



import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by manel on 10/31/2017.
 */

public class ChatViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView mMessage;
    public LinearLayout mContainer;
    public CircleImageView mImageViewLeft;
    public CircleImageView mImageViewRight;
    public ChatViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMessage = itemView.findViewById(R.id.message);
        mContainer = itemView.findViewById(R.id.container);
        mImageViewLeft = itemView.findViewById(R.id.imageCircleLeft);
        mImageViewRight = itemView.findViewById(R.id.imageCircleRight);
    }

    @Override
    public void onClick(View view) {
    }
}
