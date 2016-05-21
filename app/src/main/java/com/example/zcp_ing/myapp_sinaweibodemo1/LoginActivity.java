package com.example.zcp_ing.myapp_sinaweibodemo1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;


import com.google.gson.Gson;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import java.io.IOException;

import api.SimpleRequestListener;
import base.BaseActivity;
import constants.AccessTokenKeeper;
import constants.WeiboConstants;
import entity.User;


/**
 * Created by ZCP_ing on 2016/5/8.
 */


/**
 * 授权回调逻辑：
 * 1、新建了一个mAuthInfo和SsoHandler对象；
 * 2、利用SsoHandler的授权方法，并传入了一个回调：mSsoHandler.authorize(new AuthListener())；
 * 3、在回调的完成中，将返回信息解析成了一个AccessToken对象
 * 4、对AccessToken对象进行可用性的判断：
 *        如果是可用的，利用AccessTokenKeeper将其写入到本地的SharedPreferences中；
 *        否则提示错误信息；
 */
public class LoginActivity extends BaseActivity {

    private WeiboAuth mWeiboAuth;

    /**
     * 封装了 "access_token","expires_in","refresh_token",
     * 并提供了他们的管理功能
     */
    private Oauth2AccessToken mAccessToken;

    //注意：SsoHandler 仅当 SDK 支持 SSO 时有效
    //sso授权，指的是通过一键点击的简单方式唤起微博客户端行为的授权的方式
    private SsoHandler mSsoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
        //创建微博实例
        mWeiboAuth = new WeiboAuth(this, WeiboConstants.APP_KEY, WeiboConstants.REDIRECT_URL, WeiboConstants.SCOPE);

        // 通过单点登录 (SSO) 获取 Token
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mSsoHandler = new SsoHandler(LoginActivity.this, mWeiboAuth);
                mSsoHandler.authorize(new AuthListener());
            }
        });

    }

    /**
     * 当SSO授权 Activity 退出时，该函数被调用。
     */

    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //SSO授权回调
        //重要：发起SSO登陆的Activity 必须重写 onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode,resultCode,data);
        }
    }

    /**
     * 微博认证授权回调类。
     * 1.SSO授权时，需要在OnActivityResult中，
     * 调用SSOHandler#authorizeCallBack后，
     * 该回调才会被执行。
     * 2.非SSO授权时，当授权结束后，该回调就会被执行
     *
     * 当授权成功后，请保存该access_token\expires_in、uid等信息
     * 到SharedPreferences中。
     */

    //微博认证授权回调类
     class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            //从Bundle中解析Token
            mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(LoginActivity.this,
                        mAccessToken);
                Toast.makeText(LoginActivity.this,
                        R.string.weibosdk_demo_toast_auth_success, Toast.LENGTH_SHORT).show();
                login();
            } else {
                /**
                 * 以下几种情况，您会收到Code
                 * 1.当你未在平台上注册应用程序的包名与签名时；
                 * 2.当您注册的应用程序的包名与签名不正确时；
                 * 3.当您在平台上注册的包名与签名与您当前测试的应用的包名和签名不匹配时。
                 */

                // 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
                String code = values.getString("code");
                String message = getString(R.string.weibosdk_demo_toast_auth_failed);
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code:" + code;
                }
                Toast.makeText(LoginActivity.this,message,Toast.LENGTH_LONG).show();
            }
        }

        //手动取消
        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(LoginActivity.this,
                    "Auth exception :"+e.getMessage(),Toast.LENGTH_LONG).show();
        }
        //出错
        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this,
                    R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_LONG).show();
        }
    }

    private void login() {
        //根据用户id获取用户信息
        weiboApi.usersShow(mAccessToken.getUid(), "",
                new SimpleRequestListener(this, progressDialog) {

                    @Override
                    public void onComplete(String response) {
                        super.onComplete(response);
                        //获取用户信息，并传递给User类
                        application.currentUser = new Gson().fromJson(response, User.class);
                        //跳转到主界面
                        intent2Activity(MainActivity.class);
                    }

                    @Override
                    public void onIOException(IOException e) {
                        super.onIOException(e);

                        showToast(e.getMessage());
                    }

                    @Override
                    public void onError(WeiboException e) {
                        super.onError(e);

                        showToast(e.getMessage());
                    }
                });
    }
}
