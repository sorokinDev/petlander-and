package ru.codeoverflow.petlander.ui.matches;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import ru.codeoverflow.petlander.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.codeoverflow.petlander.ui.base.BaseFragment;
import ru.codeoverflow.petlander.ui.map.MapFragment;

public class MatchesFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;
    private String userID;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_matches;
    }

    public static MatchesFragment newInstance() {
        return new MatchesFragment();
    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;

        public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
        }
    }

    @Override
    protected void onSetupView(View rootView,Bundle saved) {

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView = rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mMatchesLayoutManager);
        mMatchesAdapter = new MatchesAdapter(getDataSetMatches(), getContext());
        mRecyclerView.setAdapter(mMatchesAdapter);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.favorite_cards_spacing)));

        getUserMatchId();
    }

    private void getUserMatchId() {

        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userID).child("connections").child("matches");

        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for(DataSnapshot match : dataSnapshot.getChildren()){
                        FetchMatchInformation(match.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void FetchMatchInformation(String key) {
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        DatabaseReference petsDb = FirebaseDatabase.getInstance().getReference().child("Pets").child(key);
        petsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    getUserMatch(dataSnapshot);
                }
                else{
                    userDB.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshots) {
                            if (dataSnapshots.exists()){
                                getUserMatch(dataSnapshots);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseErrors) { }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    private void getUserMatch(DataSnapshot dataSnapshot){
        String petsID = dataSnapshot.getKey();
        String name = "";
        String profileImageUrl = "";
        String desc = "";
        String location = "";
        if(dataSnapshot.child("name").getValue()!=null){
            name = dataSnapshot.child("name").getValue().toString();
        }
        if(dataSnapshot.child("profileImageUrl").getValue()!=null){
            profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
        }
        if(dataSnapshot.child("description").getValue()!=null){
            desc = dataSnapshot.child("description").getValue().toString();
        }
        if(dataSnapshot.child("address").getValue()!=null){
            location = dataSnapshot.child("address").getValue().toString();
        }

        MatchesObject obj = new MatchesObject(petsID, name, profileImageUrl, desc, location);
        resultsMatches.add(obj);
        mMatchesAdapter.notifyDataSetChanged();
    }

    private ArrayList<MatchesObject> resultsMatches = new ArrayList<MatchesObject>();
    private List<MatchesObject> getDataSetMatches() {
        return resultsMatches;
    }
}