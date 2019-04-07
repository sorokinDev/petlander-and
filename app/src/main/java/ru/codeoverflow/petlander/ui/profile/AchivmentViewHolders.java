package ru.codeoverflow.petlander.ui.profile;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ru.codeoverflow.petlander.R;

public class AchivmentViewHolders extends RecyclerView.ViewHolder  {
    public TextView nameAch;
    public TextView descriptionAch;
    public ImageView imgAch;

    public LinearLayout container;

    public AchivmentViewHolders(@NonNull View itemView) {
        super(itemView);
        nameAch = (TextView)itemView.findViewById(R.id.nameAch);
        descriptionAch = (TextView)itemView.findViewById(R.id.descriptionAch);

        imgAch = (ImageView)itemView.findViewById(R.id.imgAch);

    }
}
