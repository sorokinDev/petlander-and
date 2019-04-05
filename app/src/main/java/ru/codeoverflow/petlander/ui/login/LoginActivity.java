package ru.codeoverflow.petlander.ui.login;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.OnClick;
import ru.codeoverflow.petlander.R;
import ru.codeoverflow.petlander.ui.base.BaseActivity;
import ru.codeoverflow.petlander.util.NavUtil;
import ru.codeoverflow.petlander.util.SharedPrefUtil;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @OnClick(R.id.btn_login)
    protected void loginClick() {
        NavUtil.toFirebase(this);
    }
}
