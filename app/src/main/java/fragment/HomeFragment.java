package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;



import com.example.zcp_ing.myapp_sinaweibodemo1.R;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import adapter.StatusAdapter;
import api.SimpleRequestListener;

import base.BaseFragment;
import entity.Status;
import entity.response.StatusTimeLineResponse;
import utils.TitleBuilder;


/**
 * Created by ZCP_ing on 2016/5/10.
 */

/**
 * 注意：
 *      由于我们希望在数据返回的时候直接添加到集合中去，
 *      所以不能在数据返回的时候重新new一次适配器，
 *      这样会使整个列表进行一次重置，也会使得当前位置跳会到开始。
 *      我们希望当列表加载完下一页数据以后还停留在加载之前的位置，
 *      所以正确的做法是：
 *              1、开始先新建一个空的微博的列表集合
 *              2、然后直接去new一个适配器，设置到列表上去
 *              3、在数据返回的时候，将新的数据添加到原有集合中
 *              4、利用适配器的notifyDataSetChanged()方法，进行数据的更新
 *              原则是：保证整个列表的适配器一直是同一个，并且适配器中的集合对象也是同一个，而集合中的数据就可以进行相应的变化了
 */
public class HomeFragment extends BaseFragment {

    private View view;
    private PullToRefreshListView plv_home;
    private View footView;

    private StatusAdapter adapter;
    private List<Status> statuses = new ArrayList<Status>();
    private long curPage = 1;
    private boolean isLoadingMore;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = View.inflate(getActivity(), R.layout.frag_home, null);

        initView();
        initData();

        return view;

    }

    private void initView() {

        new TitleBuilder(view).setTitleText("首页").build();

        plv_home = (PullToRefreshListView) view.findViewById(R.id.plv_home);
        plv_home = (PullToRefreshListView) view.findViewById(R.id.plv_home);

        //下拉刷新监听
        plv_home.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                loadData(1);
            }
        });

        //上拉加载监听
        plv_home.setOnLastItemVisibleListener(
                new OnLastItemVisibleListener() {

                    @Override
                    public void onLastItemVisible() {
                        loadData(curPage + 1);
                    }
                });

        footView = View.inflate(activity, R.layout.footview_loading, null);
    }

    private void addFootView(PullToRefreshListView plv_home, View footView) {
        ListView lv = plv_home.getRefreshableView();
        if (lv.getFooterViewsCount() == 1) {
            lv.addFooterView(footView);
        }
    }

    private void removeFootView(PullToRefreshListView plv_home, View footView) {
        ListView lv = plv_home.getRefreshableView();
        if (lv.getFooterViewsCount() > 1) {
            lv.removeFooterView(footView);
        }
    }


    public void initData() {
        statuses = new ArrayList<Status>();
        adapter = new StatusAdapter(activity, statuses);
        plv_home.setAdapter(adapter);

        loadData(1);
    }

    private void loadData(final long requestPage) {
        if(isLoadingMore) {
            return;
        }

        isLoadingMore = true;

        //查询首页微博
        weiboApi.statusesHome_timeline(curPage,
                //发起访问时的请求回调类
                new SimpleRequestListener(activity, progressDialog){

                    @Override
                    public void onComplete(String response) {
                        super.onComplete(response);

                        if(requestPage == 1) {
                            statuses.clear();
                        }
                        curPage = requestPage;
                        //获取首页微博信息，并传递给微博页面信息类
                        addData(gson.fromJson(response, StatusTimeLineResponse.class));
                        Log.i("TAG","main---->>" + response.toString());
                    }

                    @Override
                    public void onAllDone() {
                        super.onAllDone();

                        isLoadingMore = false;
                        plv_home.onRefreshComplete();
                    }
                });
    }

    //从微博页面信息类中将获取的微博信息添加到信息队列中
    private void addData(StatusTimeLineResponse resBean) {
        for (Status status : resBean.getStatuses()) {
            //防止数据的重复添加
            if (!statuses.contains(status)) {
                statuses.add(status);
            }
        }

        adapter.notifyDataSetChanged();

        if (curPage < resBean.getTotal_number()) {
            addFootView(plv_home, footView);
        } else {
            removeFootView(plv_home, footView);
        }
    }

}
