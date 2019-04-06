package ru.codeoverflow.petlander.ui.add;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import ru.codeoverflow.petlander.R;
import ru.codeoverflow.petlander.ui.base.BaseFragment;
import ru.codeoverflow.petlander.ui.map.MapFragment;

public class AddMapFragment extends BaseFragment implements OnMapReadyCallback {

    @BindView(R.id.map) MapView map;

    private GoogleMap mMap;
    private MapView mapView;
    private Marker marker;
    protected Long geoX;
    protected Long geoY;


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_add_map;
    }

    @Override
    protected void onSetupView(View rootView, Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).setSupportActionBar(rootView.findViewById(R.id.toolbar));

        mapView = (MapView) rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(latLng -> {
            if(marker != null) {
                marker.remove();
            }
            marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Местоположение собаки"));
        });

        requestLocationPermission();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(1)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if(EasyPermissions.hasPermissions(getContext(), perms)) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(() -> false);
            FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
            mFusedLocationClient
                    .getLastLocation()
                    .addOnSuccessListener(getActivity(), location -> {
                        if (location != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),15));
                        }
                    });
        }
        else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", 1, perms);
        }
    }

    public static AddMapFragment newInstance() {

        Bundle args = new Bundle();

        AddMapFragment fragment = new AddMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
