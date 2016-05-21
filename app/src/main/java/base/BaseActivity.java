package base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import api.SinaWeiboDemoApi;
import constants.CommonConstants;
import utils.DialogUtils;
import utils.Logger;
import utils.ToastUtils;

/**
 * Created by ZCP_ing on 2016/5/8.
 */
public class BaseActivity extends Activity {

    protected String TAG;

    protected BaseApplication application;

    protected SharedPreferences sp;

    protected Intent intent;

    protected Dialog progressDialog;

    protected SinaWeiboDemoApi weiboApi;

    protected ImageLoader imageLoader;

    protected Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TAG = this.getClass().getSimpleName();
        showLog("onCreate()");

        //设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //获取BaseApplication对象
        application = (BaseApplication) getApplication();
        //从sharedPreference中获取token对象
        sp = getSharedPreferences(CommonConstants.SP_NAME, MODE_PRIVATE);

        intent = getIntent();

        progressDialog = DialogUtils.createLoadingDialog(this);

        weiboApi = new SinaWeiboDemoApi(this);
        imageLoader = ImageLoader.getInstance();
        gson = new Gson();
    }

    protected void intent2Activity(Class<? extends Activity> tarActivity) {
        Intent intent = new Intent(this, tarActivity);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showLog("onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        showLog("onResume()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        showLog("onDestroy()");

        application.removeActivity(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        showLog("onStop()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        showLog("onPause()");
    }

    protected void finishActivity() {
        this.finish();
    }


    protected void showToast(String msg) {
        ToastUtils.showToast(this, msg, Toast.LENGTH_SHORT);
    }

    protected void showLog(String msg) {
        Logger.show(TAG,msg);
    }
}
