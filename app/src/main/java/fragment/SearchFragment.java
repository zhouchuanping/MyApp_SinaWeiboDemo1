package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zcp_ing.myapp_sinaweibodemo1.R;

import base.BaseFragment;
import utils.TitleBuilder;

/**
 * Created by ZCP_ing on 2016/5/10.
 */
public class SearchFragment extends BaseFragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = View.inflate(activity, R.layout.frag_home, null);

        initView();

        return view;
    }

    private void initView() {
        new TitleBuilder(view)
                .setTitleText("发现")
                .build();
    }
}
