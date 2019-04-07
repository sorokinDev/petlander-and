package ru.codeoverflow.petlander.ui.login;

import butterknife.BindView;
import butterknife.OnClick;
import ru.codeoverflow.petlander.R;
import ru.codeoverflow.petlander.ui.base.BaseActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import ru.codeoverflow.petlander.util.NavUtil;
import ru.codeoverflow.petlander.util.SharedPrefUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FirebaseActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 1787;

    @BindView(R.id.til_name)
    TextInputLayout tilName;

    @BindView(R.id.login_info_layout)
    View loginInfoLayout;

    @BindView(R.id.et_name)
    TextInputEditText etName;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);

        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(), RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).
                        child("achivment").child("first_step").setValue(false);
                FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).
                        child("achivment").child("cash_hurt").setValue(false);
                FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).
                        child("achivment").child("society").setValue(false);
                FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).
                        child("achivment").child("get_in_taste").setValue(false);
                if (user != null){
                    FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).
                            child("achivment").child("first_step").setValue(true);
                    Toast.makeText(this,"OK",Toast.LENGTH_SHORT).show();
                    onSuccessAuth(user);
                }
            }else{
                NavUtil.toLogin(this);
            }
        }
    }

    protected void onSuccessAuth(FirebaseUser user) {
        this.user = user;
        if (TextUtils.isEmpty(user.getDisplayName())) {
            loginInfoLayout.setVisibility(View.VISIBLE);

        }else {
            onAuthInfoFilled();
        }
    }

    protected void onAuthInfoFilled() {
        SharedPrefUtil.setUserAuthorized();
        NavUtil.toMain(this);
    }

    @OnClick(R.id.btn_continue)
    protected void onBtnContinue() {
        if(TextUtils.isEmpty(etName.getText())) {
            tilName.setError(getString(R.string.error_fill_name));
            return;
        }
        String userName = Objects.requireNonNull(etName.getText()).toString();

        user.updateProfile(new UserProfileChangeRequest.Builder().
                setDisplayName(userName).build()).addOnSuccessListener(aVoid -> {
                    onAuthInfoFilled();
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}