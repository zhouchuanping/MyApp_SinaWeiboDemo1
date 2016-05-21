package base;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentController;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.zcp_ing.myapp_sinaweibodemo1.MainActivity;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import api.SinaWeiboDemoApi;
import utils.DialogUtils;

/**
 * Created by ZCP_ing on 2016/5/10.
 */
public class BaseFragment extends Fragment {

    protected MainActivity activity;
    protected Dialog progressDialog;;

    protected ImageLoader imageLoader;
    protected SinaWeiboDemoApi weiboApi;
    protected Gson gson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (MainActivity) getActivity();
        progressDialog = DialogUtils.createLoadingDialog(activity);

        imageLoader = ImageLoader.getInstance();
        weiboApi = new SinaWeiboDemoApi(activity);
        gson = new Gson();
    }

    protected void intent2Activity(Class<? extends Activity> tarActivity) {
        Intent intent = new Intent(activity, tarActivity);
        startActivity(intent);
    }
}
