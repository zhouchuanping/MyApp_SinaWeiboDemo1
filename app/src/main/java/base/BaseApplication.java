package base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.LinkedList;
import java.util.List;

import entity.User;
import utils.ImageOptHelper;

/**
 * Created by ZCP_ing on 2016/5/8.
 */
public class BaseApplication extends Application {

    private List<Activity> activityList = new LinkedList<Activity>();

    private static BaseApplication instance;
    // 单例模式中获取唯一的ExitApplication 实例
    public static BaseApplication getInstance() {
        if (null == instance) {
            instance = new BaseApplication();
        }
        return instance;
    }

    public User currentUser;

    @Override
    public void onCreate() {
        super.onCreate();

        initImageLoader(this);
    }

    //初始化图片处理，Universal_image_loader框架的使用
    private void initImageLoader(Context context) {

//		HttpParams params = new BasicHttpParams();
//		// Turn off stale checking. Our connections break all the time anyway,
//		// and it's not worth it to pay the penalty of checking every time.
//		HttpConnectionParams.setStaleCheckingEnabled(params, false);
//		// Default connection and socket timeout of 10 seconds. Tweak to taste.
//		HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
//		HttpConnectionParams.setSoTimeout(params, 10 * 1000);
//		HttpConnectionParams.setSocketBufferSize(params, 8192);
//
//		// Don't handle redirects -- return them to the caller. Our code
//		// often wants to re-POST after a redirect, which we must do ourselves.
//		HttpClientParams.setRedirecting(params, false);
//		// Set the specified user agent and register standard protocols.
//		HttpProtocolParams.setUserAgent(params, "some_randome_user_agent");
//		SchemeRegistry schemeRegistry = new SchemeRegistry();
//		schemeRegistry.register(new Scheme("http", PlainSocketFactory
//				.getSocketFactory(), 80));
//		schemeRegistry.register(new Scheme("https", SSLSocketFactory
//				.getSocketFactory(), 443));
//
//		ClientConnectionManager manager = new ThreadSafeClientConnManager(
//				params, schemeRegistry);

        /**
         * This configuration tuning is custom.You can tune every option,
         * you may tune some of them,
         * or you can create default configuration by ImageLoaderConfiguration.createDefault(this);
         * method.
         */
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.
                Builder(context).
                threadPriority(Thread.NORM_PRIORITY-2)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(ImageOptHelper.getImgOptions())
//				.imageDownloader(new HttpClientImageDownloader(
//						context, new DefaultHttpClient(manager, params)))
                .build();
        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    // 添加Activity 到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    // 从容器中删除Activity
    public void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    // 遍历所有Activity 并finish
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }
}
