package com.example.zcp_ing.myapp_sinaweibodemo1;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;

import java.io.IOException;

import api.SimpleRequestListener;
import base.BaseActivity;
import constants.AccessTokenKeeper;
import entity.User;
import utils.ImageOptHelper;

/**
 * Created by ZCP_ing on 2016/5/10.
 */
public class SplashActivity extends BaseActivity {

    private static final int HANDLER_WHAT_INTENT2LOGIN = 1;
    private static final int HANDLER_WHAT_INTENT2MAIN = 2;
    private static final int HANDLER_WHAT_SHOW_IMAGE = 3;
    private static final long SPLASH_DUR_TIME = 2000;

    private ImageView iv_slogan;
    private ImageView iv_portrait;
    private ImageViewAware iva_portrait;
    private TextView tv_welcome;

    private long startTimeMillis;

    private Oauth2AccessToken mAccessToken;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case HANDLER_WHAT_INTENT2LOGIN:
                    intent2Activity(LoginActivity.class);
                    finish();
                    break;
                case HANDLER_WHAT_INTENT2MAIN:
                    intent2Activity(MainActivity.class);
                    finish();
                    break;
                case HANDLER_WHAT_SHOW_IMAGE:
                    showAvatar();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        initView();

        startTimeMillis = System.currentTimeMillis();
        //从SharedPreferences读取Token信息
        mAccessToken = AccessTokenKeeper.readAccessToken(this);

        if (mAccessToken.isSessionValid()) {
            //根据用户id获取用户信息
            weiboApi.usersShow(mAccessToken.getUid(), "",
                    new SimpleRequestListener(this, progressDialog) {
                    //发起访问请求时的回调接口

                        @Override
                        public void onComplete(String response) {
                            super.onComplete(response);
                            //将请求用户信息返回结果传递给用户类
                            application.currentUser = new Gson().fromJson(response, User.class);
                            //异步加载头像图片
                            loadAvatar();
                        }

                        @Override
                        public void onIOException(IOException e) {
                            super.onIOException(e);

                            delayHandler(HANDLER_WHAT_INTENT2LOGIN, SPLASH_DUR_TIME);
                        }

                        @Override
                        public void onError(WeiboException e) {
                            super.onError(e);

                            delayHandler(HANDLER_WHAT_INTENT2LOGIN, SPLASH_DUR_TIME);
                        }
                    });

        } else {
            delayHandler(HANDLER_WHAT_INTENT2LOGIN, SPLASH_DUR_TIME);
        }

    }

    private void initView() {
        iv_slogan = (ImageView) findViewById(R.id.iv_slogan);
        iv_portrait = (ImageView) findViewById(R.id.iv_portrait);
        iva_portrait = new ImageViewAware(iv_portrait);
        tv_welcome = (TextView) findViewById(R.id.tv_welcome);
    }

    private void loadAvatar() {
        //进行头像图片异步加载
        imageLoader.displayImage(application.currentUser.getProfile_image_url(),
                iva_portrait,
                ImageOptHelper.getAvatarOptions(),
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        delayHandler(HANDLER_WHAT_INTENT2MAIN, SPLASH_DUR_TIME);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        delayHandler(HANDLER_WHAT_SHOW_IMAGE, SPLASH_DUR_TIME / 2);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        delayHandler(HANDLER_WHAT_INTENT2MAIN, SPLASH_DUR_TIME);
                    }
                });
    }

    //显示头像图片
    private void showAvatar() {
        iv_slogan.setVisibility(View.GONE);
        iv_portrait.setVisibility(View.VISIBLE);;

        //透明度动画

        final AlphaAnimation tvWelAnim = new AlphaAnimation(0.5f, 1f);
        tvWelAnim.setDuration(200);
        tvWelAnim.setFillAfter(true);
        tvWelAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                delayHandler(HANDLER_WHAT_INTENT2MAIN, SPLASH_DUR_TIME);
            }
        });

        Animation avatarAnim = AnimationUtils.loadAnimation(this, R.anim.splash_show_avatar);
        avatarAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_welcome.setVisibility(View.VISIBLE);
                tv_welcome.setAnimation(tvWelAnim);
            }
        });
        iv_portrait.setAnimation(avatarAnim);
    }

    private void delayHandler(int what, long allDurTime) {
        long delayTime = allDurTime - (System.currentTimeMillis() - startTimeMillis);
        if (delayTime < 0) {
            delayTime = 0;
        }
        handler.sendEmptyMessageDelayed(what, delayTime);
    }

}
