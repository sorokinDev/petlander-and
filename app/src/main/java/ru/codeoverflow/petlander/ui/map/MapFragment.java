package ru.codeoverflow.petlander.ui.map;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import ru.codeoverflow.petlander.MainActivity;
import ru.codeoverflow.petlander.ui.base.BaseFragment;
import ru.codeoverflow.petlander.R;

public class MapFragment extends BaseFragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapView mapView;
    protected Long geoX;
    protected Long geoY;


    public MapFragment(){

    }


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_map;
    }



    @Override
    protected void onSetupView(View rootView,Bundle saved) {
        ((AppCompatActivity)getActivity()).setSupportActionBar(rootView.findViewById(R.id.toolbar));
        mapView = (MapView) rootView.findViewById(R.id.map);
        mapView.onCreate(saved);
        mapView.onResume();
        mapView.getMapAsync(this);


    }
    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users");
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
                                         @Override
                                         public void onDataChange(DataSnapshot dataSnapshot) {
                                                 if (dataSnapshot.exists()) {
                                                     Toast.makeText(getContext(), "Hi", Toast.LENGTH_SHORT).show();
                                                     for (DataSnapshot data : dataSnapshot.getChildren()) {


                                                         geoX = (Long) data.child("geoX").getValue();
                                                         Toast.makeText(getContext(), ""+geoX, Toast.LENGTH_SHORT).show();
                                                         geoY = (Long) data.child("geoY").getValue();
                                                         Toast.makeText(getContext(), ""+geoY, Toast.LENGTH_SHORT).show();

                                                         LatLng position = new LatLng(geoX, geoY);


                                                         mMap.addMarker(new MarkerOptions().position(position).title("Marker in Sydney"));
                                                         Log.e("MAP", "OK");
                                                 }
                                             }
                                         }

                                         @Override
                                         public void onCancelled(@NonNull DatabaseError databaseError) { }
                                     }
        );

        // Add a marker in Sydney and move the camera




    }
}
