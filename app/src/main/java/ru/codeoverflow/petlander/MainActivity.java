package ru.codeoverflow.petlander;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import ru.codeoverflow.petlander.ui.base.BaseActivity;
import ru.codeoverflow.petlander.ui.feed.FeedFragment;
import ru.codeoverflow.petlander.ui.profile.ProfileFragment;
import ru.codeoverflow.petlander.util.NavUtil;
import ru.codeoverflow.petlander.util.SharedPrefUtil;

public class MainActivity extends BaseActivity {

    @BindView(R.id.navigation)
    BottomNavigationView bottomNav;

    @BindView(R.id.fl_content)
    FrameLayout contentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!SharedPrefUtil.isUserAuthorized()) {
            NavUtil.toLogin(this);
            return;
        }

        bottomNav.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_account:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, ProfileFragment.newInstance()).commit();
                    return true;
            }
            switch (menuItem.getItemId()) {
                case R.id.nav_feed:
                    getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, FeedFragment.newInstance()).commit();
                    return true;
            }
            return true;
        });

    }

}
