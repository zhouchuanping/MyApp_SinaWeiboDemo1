package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.zcp_ing.myapp_sinaweibodemo1.R;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import adapter.MessageAdapter;
import base.BaseFragment;
import entity.Message;
import utils.TitleBuilder;
import utils.ToastUtils;

/**
 * Created by ZCP_ing on 2016/5/10.
 */
public class MessageFragment extends BaseFragment {

    private View view;
    private PullToRefreshListView plv_message;
    private MessageAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = View.inflate(activity, R.layout.frag_message, null);
        initView();

        mockData();

        return view;
    }

    private void initView() {
        new TitleBuilder(view)
                .setTitleText("消息")
                .setRightText("发起聊天")
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .build();

        plv_message = (PullToRefreshListView) view.findViewById(R.id.plv_message);
        adapter = new MessageAdapter(activity, mockData());
        plv_message.setAdapter(adapter);
    }


    private List<Message> mockData() {
        List<Message> msgs = new ArrayList<Message>();

        Message msg1 = new Message();
        msg1.setImg(R.drawable.messagescenter_at);
        msg1.setMessage("@我的");
        msgs.add(msg1);

        Message msg2 = new Message();
        msg2.setImg(R.drawable.messagescenter_comments);
        msg2.setMessage("评论");
        msgs.add(msg2);

        Message msg3 = new Message();
        msg3.setImg(R.drawable.messagescenter_good);
        msg3.setMessage("赞");
        msgs.add(msg3);

        Message msg4 = new Message();
        msg4.setImg(R.drawable.messagescenter_messagebox);
        msg4.setMessage("未关注人私信");
        msgs.add(msg4);

        return msgs;
    }
}
