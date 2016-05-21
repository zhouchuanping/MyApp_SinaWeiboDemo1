package com.example.zcp_ing.myapp_sinaweibodemo1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import adapter.EmotionGvAdapter;
import adapter.EmotionPagerAdapter;
import adapter.WriteStatusGridImgsAdapter;
import api.SimpleRequestListener;
import base.BaseActivity;
import entity.Emotion;
import entity.Status;
import utils.DialogUtils;
import utils.DisplayUtils;
import utils.StringUtils;
import utils.TitleBuilder;


public class WriteCommentActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

	// 评论输入框
	private EditText et_write_status;
	// 底部按钮
	private ImageView iv_image;
	private ImageView iv_at;
	private ImageView iv_topic;
	private ImageView iv_emoji;
	private ImageView iv_add;
	// 待评论的微博
	private Status status;

	private LinearLayout ll_emotion_dashboard;
	private ViewPager vp_emotion_dashboard;

	private WriteStatusGridImgsAdapter statusImgsAdapter;
	private ArrayList<Uri> imgUris = new ArrayList<Uri>();
	private EmotionPagerAdapter emotionPagerGvAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_write_status);

		// 获取Intent传入的微博
		status = (Status) getIntent().getSerializableExtra("status");
		
		initView();

	}

	private void initView() {
		new TitleBuilder(this)
				.setTitleText("发评论")
				.setLeftText("取消")
				.setLeftOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 取消发送评论,关闭本页面
						WriteCommentActivity.this.finish();
					}
				})
				.setRightText("发送")
				.setRightOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						sendComment();
					}
				});

		et_write_status = (EditText) findViewById(R.id.et_write_status);
		iv_image = (ImageView) findViewById(R.id.iv_image);
		iv_at = (ImageView) findViewById(R.id.iv_at);
		iv_topic = (ImageView) findViewById(R.id.iv_topic);
		iv_emoji = (ImageView) findViewById(R.id.iv_emoji);
		iv_add = (ImageView) findViewById(R.id.iv_add);

		// 表情选择面板
		ll_emotion_dashboard = (LinearLayout) findViewById(R.id.ll_emotion_dashboard);
		vp_emotion_dashboard = (ViewPager) findViewById(R.id.vp_emotion_dashboard);



		iv_image.setOnClickListener(this);
		iv_at.setOnClickListener(this);
		iv_topic.setOnClickListener(this);
		iv_emoji.setOnClickListener(this);
		iv_add.setOnClickListener(this);

		initEmotion();
	}

	private void sendComment() {
		String comment = et_write_status.getText().toString();
		if(TextUtils.isEmpty(comment)) {
			showToast("评论内容不能为空");
			return;
		}

		try {
			comment = URLEncoder.encode(comment, "UTF-8");

			weiboApi.commentsCreate(status.getId(), comment,
					new SimpleRequestListener(this, null) {

						@Override
						public void onComplete(String response) {
							super.onComplete(response);

							showToast("微博发送成功");

							// 微博发送成功后,设置Result结果数据,然后关闭本页面
							Intent data = new Intent();
							data.putExtra("sendCommentSuccess", true);
							setResult(RESULT_OK, data);

							WriteCommentActivity.this.finish();
						}
					});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 *  初始化表情面板内容
	 */
	private void initEmotion() {
		// 获取屏幕宽度
		int gvWidth = DisplayUtils.getScreenWidthPixels(this);
		// 表情边距
		int spacing = DisplayUtils.dp2px(this, 8);
		// GridView中item的宽度
		int itemWidth = (gvWidth - spacing * 8) / 7;
		int gvHeight = itemWidth * 3 + spacing * 4;

		List<GridView> gvs = new ArrayList<GridView>();
		List<String> emotionNames = new ArrayList<String>();
		// 遍历所有的表情名字
		for (String emojiName : Emotion.emojiMap.keySet()) {
			emotionNames.add(emojiName);
			// 每20个表情作为一组,同时添加到ViewPager对应的view集合中
			if (emotionNames.size() == 20) {
				GridView gv = createEmotionGridView(emotionNames, gvWidth, spacing, itemWidth, gvHeight);
				gvs.add(gv);
				// 添加完一组表情,重新创建一个表情名字集合
				emotionNames = new ArrayList<String>();
			}
		}

		// 检查最后是否有不足20个表情的剩余情况
		if (emotionNames.size() > 0) {
			GridView gv = createEmotionGridView(emotionNames, gvWidth, spacing, itemWidth, gvHeight);
			gvs.add(gv);
		}

		// 将多个GridView添加显示到ViewPager中
		emotionPagerGvAdapter = new EmotionPagerAdapter(gvs);
		vp_emotion_dashboard.setAdapter(emotionPagerGvAdapter);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gvWidth, gvHeight);
		vp_emotion_dashboard.setLayoutParams(params);
	}

	/**
	 * 创建显示表情的GridView
	 */
	private GridView createEmotionGridView(List<String> emotionNames, int gvWidth, int padding, int itemWidth, int gvHeight) {
		// 创建GridView
		GridView gv = new GridView(this);
		gv.setBackgroundResource(R.color.bg_gray);
		gv.setSelector(R.color.transparent);
		gv.setNumColumns(7);
		gv.setPadding(padding, padding, padding, padding);
		gv.setHorizontalSpacing(padding);
		gv.setVerticalSpacing(padding);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(gvWidth, gvHeight);
		gv.setLayoutParams(params);
		// 给GridView设置表情图片
		EmotionGvAdapter adapter = new EmotionGvAdapter(this, emotionNames, itemWidth);
		gv.setAdapter(adapter);
		gv.setOnItemClickListener(this);
		return gv;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_image:
			break;
		case R.id.iv_at:
			break;
		case R.id.iv_topic:
			break;
		case R.id.iv_emoji:
			if(ll_emotion_dashboard.getVisibility() == View.VISIBLE) {
				// 显示表情面板时点击,将按钮图片设为笑脸按钮,同时隐藏面板
				iv_emoji.setImageResource(R.drawable.btn_insert_emotion);
				ll_emotion_dashboard.setVisibility(View.GONE);
			} else {
				// 未显示表情面板时点击,将按钮图片设为键盘,同时显示面板
				iv_emoji.setImageResource(R.drawable.btn_insert_keyboard);
				ll_emotion_dashboard.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.iv_add:
			break;
		}
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Object itemAdapter = parent.getAdapter();

		if (itemAdapter instanceof EmotionGvAdapter) {
			// 点击的是表情
			EmotionGvAdapter emotionGvAdapter = (EmotionGvAdapter) itemAdapter;

			if (position == emotionGvAdapter.getCount() - 1) {
				// 如果点击了最后一个回退按钮,则调用删除键事件
				et_write_status.dispatchKeyEvent(new KeyEvent(
						KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
			} else {
				// 如果点击了表情,则添加到输入框中
				String emotionName = emotionGvAdapter.getItem(position);

				// 获取当前光标位置,在指定位置上添加表情图片文本
				int curPosition = et_write_status.getSelectionStart();
				StringBuilder sb = new StringBuilder(et_write_status.getText().toString());
				sb.insert(curPosition, emotionName);

				// 特殊文字处理,将表情等转换一下
				et_write_status.setText(StringUtils.getWeiboContent(
						this, et_write_status, sb.toString()));

				// 将光标设置到新增完表情的右侧
				et_write_status.setSelection(curPosition + emotionName.length());
			}
		}
	}

}
