package ru.codeoverflow.petlander.ui.add;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
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

    @BindView(R.id.layout_add_photo) View layoutAddPhoto;
    @BindView(R.id.iv_selected) ImageView ivSelected;
    @BindView(R.id.til_description) TextInputLayout tilDesc;
    @BindView(R.id.et_description) TextInputEditText etDesc;

    private String itIsPet;

    protected Uri imageUri;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_add;
    }

    @Override
    protected void onSetupView(View rootView, Bundle savedInstanceState) {

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

                if(bitmap != null) {
                    FirebaseVisionLabelDetectorOptions options = new FirebaseVisionLabelDetectorOptions.Builder()
                            .setConfidenceThreshold(0.7f)
                            .build();
                    FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
                    FirebaseVisionLabelDetector detector = FirebaseVision.getInstance().getVisionLabelDetector(options);
                    detector.detectInImage(image)
                            .addOnSuccessListener(labels -> itIsPet = labels.get(0).getLabel())
                            .addOnFailureListener(e -> Log.e("ML-Kit", e.getMessage()));
                }

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
        if(getActivity() != null) {
            String desc = "";
            if(etDesc.getText() != null) {
                desc = etDesc.getText().toString();
            }

            if(imageUri == null) {
                Toast.makeText(getContext(), "Пожалуйста, выберите фото", Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(desc)) {
                Toast.makeText(getContext(), "Пожалуйста, добавьте описание", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!itIsPet.equals("Dog") && !itIsPet.equals("Cat")) {
                Toast.makeText(getContext(), "Пожалуйста, добавьте фото животного", Toast.LENGTH_SHORT).show();
                return;
            }

            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_content, AddMapFragment.newInstance(imageUri.toString(), desc))
                    .addToBackStack("addMap").commit();
        }

        /*FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        if(imageUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> progressDialog.dismiss())
                .addOnFailureListener(e -> progressDialog.dismiss())
                .addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot
                            .getTotalByteCount());
                });
        }*/
    }

    public static AddFragment newInstance() {
        
        Bundle args = new Bundle();
        
        AddFragment fragment = new AddFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
