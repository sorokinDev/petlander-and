package ru.codeoverflow.petlander.ui.add;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import ru.codeoverflow.petlander.R;
import ru.codeoverflow.petlander.model.Pet;
import ru.codeoverflow.petlander.ui.base.BaseFragment;
import ru.codeoverflow.petlander.ui.map.MapFragment;
import se.arbitur.geocoding.AddressGeocoder;
import se.arbitur.geocoding.Callback;
import se.arbitur.geocoding.CoordinateGeocoder;
import se.arbitur.geocoding.Response;
import se.arbitur.geocoding.Result;
import se.arbitur.geocoding.constants.AddressType;
import se.arbitur.geocoding.constants.LocationType;
import se.arbitur.geocoding.models.Coordinate;

public class AddMapFragment extends BaseFragment implements OnMapReadyCallback {

    @BindView(R.id.map) MapView map;
    @BindView(R.id.btn_add_confirm) Button confirmBtn;
    private GoogleMap mMap;
    private MapView mapView;
    private Marker marker;

    private Uri imageUri;
    private String desc;


    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_add_map;
    }

    @Override
    protected void onSetupView(View rootView, Bundle savedInstanceState) {

        mapView = (MapView) rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);

        String imageUriString = getArguments().getString(KEY_IMAGE_URI);
        if(!TextUtils.isEmpty(imageUriString)) {
            imageUri = Uri.parse(imageUriString);
        }

        desc = getArguments().getString(KEY_DESC);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(latLng -> {
            confirmBtn.setVisibility(View.VISIBLE);
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

    @OnClick(R.id.btn_add_confirm)
    protected void addConfirmClick() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        if(imageUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.show();
            try {
                File compressedImage = new Compressor(getActivity()).compressToFile(new File(getPathFromGooglePhotosUri(imageUri)));
                StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
                ref.putStream(new FileInputStream(compressedImage))
                        .addOnSuccessListener(taskSnapshot -> {
                            progressDialog.dismiss();
                            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                DatabaseReference petsDb = FirebaseDatabase.getInstance().getReference().child("Pets");
                                Pet pet = new Pet();
                                pet.description = desc;
                                pet.name = "Name";
                                pet.profileImageUrl = downloadUrl;
                                if(marker != null) {
                                    pet.geoX = marker.getPosition().latitude;
                                    pet.geoY = marker.getPosition().longitude;
                                }else {
                                    pet.geoX = 45;
                                    pet.geoY = 56;
                                }
                                new CoordinateGeocoder(pet.geoX, pet.geoY,getString(R.string.geocode_key))
                                        .setLanguage("ru")
                                        .setLocationTypes(LocationType.ROOFTOP)
                                        .setResultTypes(AddressType.STREET_ADDRESS)
                                        .fetch(new Callback() {
                                            @Override
                                            public void onSuccess(@NotNull Response response) {
                                                Result result = response.getResults()[0];
                                                Log.e("Suc", result.getFormattedAddress());

                                                String address = result.getFormattedAddress();
                                                pet.address = address;
                                                pet.userID = FirebaseAuth.getInstance().getUid();

                                                petsDb.push().setValue(pet).addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(getContext(), "Added", Toast.LENGTH_LONG).show();
                                                }).addOnFailureListener(e -> {
                                                    Toast.makeText(getContext(), "Failure", Toast.LENGTH_LONG).show();
                                                });
                                            }

                                            @Override
                                            public void onFailure(@Nullable Response response, @Nullable IOException e) {
                                                Log.e("Coord", "Something went wrong");

                                            }
                                        });

                            });

                        })
                        .addOnFailureListener(e -> progressDialog.dismiss())
                        .addOnProgressListener(taskSnapshot -> {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public String getPathFromGooglePhotosUri(Uri uriPhoto) {
        if (uriPhoto == null)
            return null;

        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            ParcelFileDescriptor pfd = getActivity().getContentResolver().openFileDescriptor(uriPhoto, "r");
            FileDescriptor fd = pfd.getFileDescriptor();
            input = new FileInputStream(fd);

            String tempFilename = getTempFilename(getActivity());
            output = new FileOutputStream(tempFilename);

            int read;
            byte[] bytes = new byte[4096];
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }
            return tempFilename;
        } catch (IOException ignored) {
            // Nothing we can do
        } finally {
            closeSilently(input);
            closeSilently(output);
        }
        return null;
    }

    public static void closeSilently(Closeable c) {
        if (c == null)
            return;
        try {
            c.close();
        } catch (Throwable t) {
            // Do nothing
        }
    }

    private static String getTempFilename(Context context) throws IOException {
        File outputDir = context.getCacheDir();
        File outputFile = File.createTempFile("image", "tmp", outputDir);
        return outputFile.getAbsolutePath();
    }

    private static final String KEY_IMAGE_URI = "image_uri", KEY_DESC = "desc";

    public static AddMapFragment newInstance(String imageUri, String description) {

        Bundle args = new Bundle();
        args.putString(KEY_IMAGE_URI, imageUri);
        args.putString(KEY_DESC, description);
        AddMapFragment fragment = new AddMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
