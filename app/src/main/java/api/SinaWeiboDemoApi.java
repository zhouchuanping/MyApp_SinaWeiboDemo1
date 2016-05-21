package api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboParameters;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.WeiboAPI;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import constants.AccessTokenKeeper;
import constants.URLs;
import utils.Logger;

/**
 * Created by ZCP_ing on 2016/5/11.
 */
public class SinaWeiboDemoApi extends WeiboAPI {

    /**
     * request方法进行主线程的封装：
     *      1、新建一个Handler，并传入了MainLooper对象，即主线程轮循器
     *      2、创建了一个requestInMainLooper方法，
     *          参数与WeiBoApi中request方法的参数一样，
     *          在其中调用request方法，并一一传入参数值；
     *          最后新建一个RequestListener回调类
     *      3、在WeiboApi的request的回调方法中进行主线程的包装
     *      4、最后通过自己requestInMainLooper的回调监听，将事件暴露出去
     *
     *  使得监听方法运行在主线程中，更新Ui的方法直接在主线程中进行操作
     */

    //获取主线程轮循器
    private Handler mainLooperHandler = new Handler(Looper.getMainLooper());

    /**
     * 构造函数，使用各个API接口提供的服务前必须先获取Oauth2AccessToken
     *
     * @param oauth2AccessToken
     */
    public SinaWeiboDemoApi(Oauth2AccessToken oauth2AccessToken) {
        super(oauth2AccessToken);
    }

    /**
     * 创建一个构造方法，传入Context对象，利用AccessTokenKeeper获取保存的AccessToken信息
     * @param context
     */
    public SinaWeiboDemoApi(Context context) {
        this(AccessTokenKeeper.readAccessToken(context));
    }

    public void requestInMainLooper(String url, WeiboParameters params,
                                    String httpMethod, final RequestListener listener) {
        Logger.show("API", "url = " + parseGetUrlWithParams(url, params));
        // 主线程处理
        request(url, params, httpMethod, new RequestListener() {

            public void onIOException(final IOException e) {
                mainLooperHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onIOException(e);
                    }
                });
            }

            public void onError(final WeiboException e) {
                mainLooperHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onError(e);
                    }
                });
            }

            public void onComplete4binary(final ByteArrayOutputStream responseOS) {
                mainLooperHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onComplete4binary(responseOS);
                    }
                });
            }


            @Override
            public void onComplete(final String s) {
                mainLooperHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onComplete(s);
                    }
                });
            }
        });
    }

    @Override
    protected void request(String url, WeiboParameters params, String httpMethod, RequestListener listener) {
        super.request(url, params, httpMethod, listener);
    }

    /**
     * 转换get方式的url,将get参数与url拼接
     *
     * @param url
     *            原url
     * @param getParams
     *            需要拼接的参数map集合
     * @return 拼装完成的url
     */
    public static String parseGetUrlWithParams(String url, WeiboParameters getParams) {
        StringBuilder newUrl = new StringBuilder(url);
        if (getParams != null && getParams.size() > 0) {
            newUrl.append("?");
            for (int i=0; i<getParams.size(); i++) {
                newUrl.append(getParams.getKey(i) + "=" + getParams.getValue(i) + "&");
            }
            newUrl.substring(0, newUrl.length() - 2);
        }
        return newUrl.toString();
    }

    /**
     * 根据用户ID获取用户信息(uid和screen_name二选一)
     *
     * @param uid
     *            根据用户ID获取用户信息
     * @param screen_name
     *            需要查询的用户昵称。
     * @param listener
     */
    public void usersShow(String uid, String screen_name, RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        if(!TextUtils.isEmpty(uid)) {
            params.add("uid", uid);
        } else if(!TextUtils.isEmpty(screen_name)) {
            params.add("screen_name", screen_name);
        }
        requestInMainLooper(URLs.usersShow, params, WeiboAPI.HTTPMETHOD_GET, listener);
    }

    /**
     * 上传图片并发布一条新微博
     *
     * @param -context
     * @param status
     *            要发布的微博文本内容。
     * @param imgFilePath
     *            要上传的图片绝对路径。
     * @param listener
     */
    public void statusesUpload(String status, String imgFilePath, RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        params.add("status", status);
        params.add("pic", imgFilePath);
        requestInMainLooper(URLs.statusesUpload, params, WeiboAPI.HTTPMETHOD_POST, listener);
    }

    /**
     * 查询首页微博
     * 请求参数封装在WeiboParameters中，里面包含一个个的键-值对，分别对应参数名和参数值
     * @param page
     * @param listener
     */
    public void statusesHome_timeline(long page, RequestListener listener) {
        WeiboParameters parameters = new WeiboParameters();
        parameters.add("page", page);
        requestInMainLooper(URLs.statusesHome_timeline,parameters,HTTPMETHOD_GET,listener);
    }


    /**
     * 获取某个用户最新发表的微博列表(uid和screen_name二选一)
     *
     * @param uid
     *            需要查询的用户ID。
     * @param screen_name
     *            需要查询的用户昵称。
     * @param page
     *            返回结果的页码。(单页返回的记录条数，默认为20。)
     * @param listener
     */
    public void statusesUser_timeline(long uid, String screen_name, long page, RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        if(uid > 0) {
            params.add("uid", uid);
        } else if(!TextUtils.isEmpty(screen_name)) {
            params.add("screen_name", screen_name);
        }
        params.add("page", page);
        requestInMainLooper(URLs.statusesUser_timeline, params , WeiboAPI.HTTPMETHOD_GET, listener);
    }

    /**
     * 根据微博ID返回某条微博的评论列表
     *
     * @param id
     *            需要查询的微博ID。
     * @param page
     *            返回结果的页码。(单页返回的记录条数，默认为50。)
     * @param listener
     */
    public void commentsShow(long id, long page, RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        params.add("id", id);
        params.add("page", page);
        requestInMainLooper(URLs.commentsShow, params , WeiboAPI.HTTPMETHOD_GET, listener);
    }

    /**
     * 转发微博
     *
     * @param id
     *            要转发的微博ID。
     * @param status
     *            添加的转发文本，必须做URLencode，内容不超过140个汉字，不填则默认为“转发微博”。
     * @param is_comment
     *            是否在转发的同时发表评论，0：否、1：评论给当前微博、2：评论给原微博、3：都评论，默认为0 。
     * @param listener
     */
    public void statusesRepost(long id, String status, int is_comment, RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        params.add("id", id);
        params.add("status", status);
        params.add("is_comment", is_comment);
        requestInMainLooper(URLs.statusesRepost, params , WeiboAPI.HTTPMETHOD_POST, listener);
    }


    /**
     * 对一条微博进行评论
     *
     * @param id
     *            需要评论的微博ID。
     * @param comment
     *            评论内容
     * @param listener
     */
    public void commentsCreate(long id, String comment, RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        params.add("id", id);
        params.add("comment", comment);
        requestInMainLooper(URLs.commentsCreate, params , WeiboAPI.HTTPMETHOD_POST, listener);
    }

    /**
     * 发布或转发一条微博
     *
     * @param -context
     * @param status
     *            要发布的微博文本内容。
     * @param imgFilePath
     *            要上传的图片文件路径(为空则代表发布无图微博)。
     * @param retweetedStatsId
     *            要转发的微博ID(<=0时为原创微博)。
     * @param listener
     */
    public void statusesSend(String status, String imgFilePath, long retweetedStatsId, RequestListener listener) {
        String url;
        WeiboParameters params = new WeiboParameters();
        params.add("status", status);
        if(retweetedStatsId > 0) {
            // 如果是转发微博,设置被转发者的id
            params.add("id", retweetedStatsId);
            url = URLs.statusesRepost;
        } else if(!TextUtils.isEmpty(imgFilePath)) {
            // 如果有图片,则调用upload接口且设置图片路径
            params.add("pic", imgFilePath);
            url = URLs.statusesUpload;
        } else {
            // 如果无图片,则调用update接口
            url = URLs.statusesUpdate;
        }
        requestInMainLooper(url, params, WeiboAPI.HTTPMETHOD_POST, listener);
    }

    /**
     * 获取指定微博的转发微博列表
     *
     * @param id
     *            要转发的微博ID。
     * @param page
     *            返回结果的页码(单页返回的记录条数，默认为20。)
     * @param listener
     */
    public void statusesRepostTimeline(long id, int page, RequestListener listener) {
        WeiboParameters params = new WeiboParameters();
        params.add("id", id);
        params.add("page", page);
        requestInMainLooper(URLs.statusesRepost_timeline, params , WeiboAPI.HTTPMETHOD_GET, listener);
    }
}
