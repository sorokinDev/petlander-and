package ru.codeoverflow.petlander.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;
import ru.codeoverflow.petlander.MainActivity;
import ru.codeoverflow.petlander.ui.login.FirebaseActivity;
import ru.codeoverflow.petlander.ui.login.LoginActivity;

public class NavUtil {

    public static void toLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void toMain(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void logout(Context context) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Log.i("Results", Objects.requireNonNull(mAuth.getCurrentUser()).getPhoneNumber());
        mAuth.signOut();
        SharedPrefUtil.setUserNotAuthorized();
        toLogin(context);

    }

    public static void toFirebase(Context context) {
        Intent intent = new Intent(context, FirebaseActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

}
