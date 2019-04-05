package ru.codeoverflow.petlander.ui.feed;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import ru.codeoverflow.petlander.R;
import ru.codeoverflow.petlander.ui.Cards.FindArrayAdapter;
import ru.codeoverflow.petlander.ui.Cards.Cards;
import ru.codeoverflow.petlander.ui.base.BaseFragment;

public class FeedFragment extends BaseFragment {

    private FindArrayAdapter FindArrayAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference petsDb;
    private DatabaseReference userDb;
    private FirebaseUser user;
    private String userID;

    List<Cards> rowItems;

    public FeedFragment() {}

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_feed;
    }

    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    @Override
    protected void onSetupView(View rootView) {
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(rootView.findViewById(R.id.toolbar));

        petsDb = FirebaseDatabase.getInstance().getReference().child("Pets");
        userDb = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();

        userID = user.getUid();

        getPets(userID);

        rowItems = new ArrayList<>();

        FindArrayAdapter = new FindArrayAdapter(getContext() , R.layout.item ,rowItems);
        SwipeFlingAdapterView flingContainer = rootView.findViewById(R.id.frame);
        flingContainer.setAdapter(FindArrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {

            @Override
            public void removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                FindArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                String petsID = obj.getUserId();
                petsDb.child(petsID).child("connections").child("nope").child(userID).setValue(true);
                Toast.makeText(getContext(), "Left", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Cards obj = (Cards) dataObject;
                String petsID = obj.getUserId();
                petsDb.child(petsID).child("connections").child("yeps").child(userID).setValue(true);
                String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
                petsDb.child(petsID).child("connections").child("matches").child(userID).child("ChatId").setValue(key);
                userDb.child(userID).child("connections").child("matches").child(petsID).child("ChatId").setValue(key);
                Toast.makeText(getContext(), "Right", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener((itemPosition, dataObject) ->
                Toast.makeText(getContext(), "Item Clicked", Toast.LENGTH_SHORT).show());
    }

    private void getPets(String userID){
        petsDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.child("userID").getValue() != null) {
                    if (
                            // Connections выступает в роли понравилась собака или нет
                            // Что бы в след раз её не показывать
                            // Connections находится в ячейке DB : Pets
                            dataSnapshot.exists()
                                    && !dataSnapshot.child("connections").child("nope").hasChild(userID)
                                    && !dataSnapshot.child("connections").child("yeps").hasChild(userID)
                                    && !dataSnapshot.child("userID").getValue().toString().equals(userID)
                    ) {
                        //Проверяем картиночку
                        String profileImageUrl = "default";
                        if (!dataSnapshot.child("profileImageUrl").getValue().equals("default")) {
                            profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                        }
                        //
                        Cards item = new Cards(dataSnapshot.getKey(),
                                dataSnapshot.child("name").getValue().toString(), profileImageUrl);

                        rowItems.add(item); // Добавляем
                        FindArrayAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
}
