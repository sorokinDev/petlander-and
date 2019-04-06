package ru.codeoverflow.petlander.ui.add;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.OnClick;
import ru.codeoverflow.petlander.R;
import ru.codeoverflow.petlander.ui.base.BaseFragment;

public class AddFragment extends BaseFragment {
    private static final int RC_PICK_IMAGE = 732;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.layout_add_photo) View layoutAddPhoto;
    @BindView(R.id.iv_selected) ImageView ivSelected;
    @BindView(R.id.til_description) TextInputLayout tilDesc;
    @BindView(R.id.et_description) TextInputEditText etDesc;

    protected Uri imageUri;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_add;
    }

    @Override
    protected void onSetupView(View rootView, Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
    }

    @OnClick(R.id.layout_add_photo)
    protected void addPhotoClick() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RC_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                layoutAddPhoto.setVisibility(View.GONE);

                ivSelected.setVisibility(View.VISIBLE);
                ivSelected.setImageBitmap(bitmap);
                imageUri = uri;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.btn_add)
    protected void onAddClick() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        if(imageUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                    }
                });
        }
    }

    public static AddFragment newInstance() {
        
        Bundle args = new Bundle();
        
        AddFragment fragment = new AddFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
