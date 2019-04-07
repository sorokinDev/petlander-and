package ru.codeoverflow.petlander.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import ru.codeoverflow.petlander.MainActivity;
import ru.codeoverflow.petlander.ui.base.BaseFragment;
import ru.codeoverflow.petlander.R;

import static androidx.core.content.PermissionChecker.checkSelfPermission;
import static ru.codeoverflow.petlander.App.getApplication;

public class MapFragment extends BaseFragment implements OnMapReadyCallback,LocationSource.OnLocationChangedListener {

    private GoogleMap mMap;
    private MapView mapView;
    protected Double geoX;
    protected Double geoY;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Double latitude;
    private Double longitude;
    BottomSheetBehavior sheetBehavior;

    @BindView(R.id.bottom_sheet)
    ConstraintLayout layoutBottomSheet;

    public MapFragment() {

    }


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_map;
    }


    @Override
    protected void onSetupView(View rootView, Bundle saved) {
        ((AppCompatActivity) getActivity()).setSupportActionBar(rootView.findViewById(R.id.toolbar));
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        layoutBottomSheet.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);

        mapView = (MapView) rootView.findViewById(R.id.map);
        mapView.onCreate(saved);
        mapView.onResume();
        mapView.getMapAsync(this);
    }


    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude= location.getLatitude();
        longitude=location.getLongitude();

        LatLng loc = new LatLng(latitude, longitude);

        Toast.makeText(getContext(), "Hi", Toast.LENGTH_SHORT).show();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Pets");
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                     if (dataSnapshot.exists()) {
                         for (DataSnapshot data : dataSnapshot.getChildren()) {
                             try{
                                 geoX = (Double) data.child("geoX").getValue();
                                 geoY = (Double) data.child("geoY").getValue();

                                 LatLng position = new LatLng(geoX, geoY);

                                 mMap.addMarker(new MarkerOptions().position(position)).setTag(data);
                                 Log.e("MAP", "OK");
                             }
                             catch (Exception e) {}
                         }
                         mMap.setOnMarkerClickListener(marker -> {
                             DataSnapshot data = (DataSnapshot)marker.getTag();
                             if(sheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                                 sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                             }
                             String description = data.child("description").getValue().toString();
                             String profileImageUrl;
                             ImageView imageView = rootView.findViewById(R.id.iv_pet);
                             Glide.clear(imageView);
                             if(data.child("profileImageUrl").getValue()!=null){
                                 profileImageUrl = data.child("profileImageUrl").getValue().toString();
                                 switch(profileImageUrl){
                                     case "default":
                                         Glide.with(getApplication()).load(R.mipmap.ic_launcher).into(imageView);
                                         break;
                                     default:
                                         Glide.with(getApplication()).load(profileImageUrl).into(imageView);
                                         break;
                                 }
                             }
                             TextView textView_description = (TextView)rootView.findViewById(R.id.tv_desc);
                             textView_description.setText(description);
                             return false;
                         });
                     }

             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        requestLocationPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
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


}
