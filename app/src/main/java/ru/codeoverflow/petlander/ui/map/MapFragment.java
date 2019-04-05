package ru.codeoverflow.petlander.ui.map;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.appcompat.app.AppCompatActivity;
import ru.codeoverflow.petlander.MainActivity;
import ru.codeoverflow.petlander.ui.base.BaseFragment;
import ru.codeoverflow.petlander.R;

public class MapFragment extends BaseFragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapView mapView;

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

        // Add a marker in Sydney and move the camera

        LatLng sydney = new LatLng(-34,50);


        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        Log.e("MAP","OK");


    }

}
