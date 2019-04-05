package ru.codeoverflow.petlander.ui.add;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import ru.codeoverflow.petlander.R;
import ru.codeoverflow.petlander.ui.base.BaseFragment;

public class AddFragment extends BaseFragment {
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_add;
    }

    @Override
    protected void onSetupView(View rootView, Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
    }

    public static AddFragment newInstance() {
        
        Bundle args = new Bundle();
        
        AddFragment fragment = new AddFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
