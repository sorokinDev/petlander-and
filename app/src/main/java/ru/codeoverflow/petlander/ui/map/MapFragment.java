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
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import ru.codeoverflow.petlander.MainActivity;
import ru.codeoverflow.petlander.ui.base.BaseFragment;
import ru.codeoverflow.petlander.R;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class MapFragment extends BaseFragment implements OnMapReadyCallback,LocationSource.OnLocationChangedListener {

    private GoogleMap mMap;
    private MapView mapView;
    protected Long geoX;
    protected Long geoY;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Double latitude;
    private Double longitude;
    BottomSheetBehavior sheetBehavior;

    @BindView(R.id.bottom_sheet)
    LinearLayout layoutBottomSheet;

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
        locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);

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
        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users");
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                     if (dataSnapshot.exists()) {
                         for (DataSnapshot data : dataSnapshot.getChildren()) {


                             try{
                                 geoX = (Long) data.child("geoX").getValue();
                                 geoY = (Long) data.child("geoY").getValue();

                                 LatLng position = new LatLng(geoX, geoY);

                                 mMap.addMarker(new MarkerOptions().position(position)).setTag(data);
                                 Log.e("MAP", "OK");
                             }catch (Exception e) {}
                         }
                         mMap.setOnMarkerClickListener(marker -> {
                             DataSnapshot data = (DataSnapshot)marker.getTag();
                             sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

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
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    return false;
                }
            });
        }
        else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", 1, perms);
        }
    }


}
