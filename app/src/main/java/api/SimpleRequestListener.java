package api;

import android.app.Dialog;
import android.content.Context;
import android.widget.Toast;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;


import java.io.ByteArrayOutputStream;
import java.io.IOException;


import utils.Logger;
import utils.ToastUtils;

/**
 * Created by ZCP_ing on 2016/5/11.
 */

/**
 * 发起访问请求时的回调接口
 */
public class SimpleRequestListener implements RequestListener {

    private Context context;
    private Dialog progressDialog;

    public SimpleRequestListener(Context context, Dialog progressDialog) {
        this.context = context;
        this.progressDialog = progressDialog;
    }

    @Override
    public void onComplete(String s) {
        onAllDone();
        Logger.show("REQUEST onComplete", s);
    }

    @Override
    public void onComplete4binary(ByteArrayOutputStream responseOS) {
        onAllDone();
        Logger.show("REQUEST onComplete4binary", responseOS.size() + "");
    }

    @Override
    public void onIOException(IOException e) {
        onAllDone();
        ToastUtils.showToast(context, e.getMessage(), Toast.LENGTH_SHORT);
        Logger.show("REQUEST onIOException", e.toString());
    }

    @Override
    public void onError(WeiboException e) {
        onAllDone();
        ToastUtils.showToast(context, e.getMessage(), Toast.LENGTH_SHORT);
        Logger.show("REQUEST onError", e.toString());
    }



    public void onAllDone() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
