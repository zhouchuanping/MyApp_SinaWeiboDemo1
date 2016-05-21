package adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.zcp_ing.myapp_sinaweibodemo1.ImageBrowserActivity;
import com.example.zcp_ing.myapp_sinaweibodemo1.R;
import com.example.zcp_ing.myapp_sinaweibodemo1.StatusDetailActivity;
import com.example.zcp_ing.myapp_sinaweibodemo1.UserInfoActivity;
import com.example.zcp_ing.myapp_sinaweibodemo1.WriteCommentActivity;
import com.example.zcp_ing.myapp_sinaweibodemo1.WriteStatusActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import entity.PicUrls;
import entity.Status;
import entity.User;
import utils.DateUtils;
import utils.StringUtils;
//微博信息队列-->适配器
public class StatusAdapter extends BaseAdapter {

	private Context context;
	private List<Status> datas;
	private ImageLoader imageLoader;

	public StatusAdapter(Context context, List<Status> datas) {
		this.context = context;
		this.datas = datas;
		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Status getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.item_status, null);
			holder.ll_card_content = (LinearLayout) convertView
					.findViewById(R.id.ll_card_content);
			holder.iv_avatar = (ImageView) convertView
					.findViewById(R.id.iv_avatar);
			holder.rl_content = (RelativeLayout) convertView
					.findViewById(R.id.rl_content);
			holder.tv_subhead = (TextView) convertView
					.findViewById(R.id.tv_subhead);
			holder.tv_caption = (TextView) convertView
					.findViewById(R.id.tv_caption);

			holder.tv_content = (TextView) convertView
					.findViewById(R.id.tv_content);
			holder.include_status_image = (FrameLayout) convertView
					.findViewById(R.id.include_status_image);
			holder.gv_images = (GridView) holder.include_status_image
					.findViewById(R.id.gv_images);
			holder.iv_image = (ImageView) holder.include_status_image
					.findViewById(R.id.iv_image);

			holder.include_retweeted_status = (LinearLayout) convertView
					.findViewById(R.id.include_retweeted_status);
			holder.tv_retweeted_content = (TextView) holder.include_retweeted_status
					.findViewById(R.id.tv_retweeted_content);
			holder.include_retweeted_status_image = (FrameLayout) holder.include_retweeted_status
					.findViewById(R.id.include_status_image);
			holder.gv_retweeted_images = (GridView) holder.include_retweeted_status_image
					.findViewById(R.id.gv_images);
			holder.iv_retweeted_image = (ImageView) holder.include_retweeted_status_image
					.findViewById(R.id.iv_image);

			holder.ll_share_bottom = (LinearLayout) convertView
					.findViewById(R.id.ll_share_bottom);
			holder.iv_share_bottom = (ImageView) convertView
					.findViewById(R.id.iv_share_bottom);
			holder.tv_share_bottom = (TextView) convertView
					.findViewById(R.id.tv_share_bottom);
			holder.ll_comment_bottom = (LinearLayout) convertView
					.findViewById(R.id.ll_comment_bottom);
			holder.iv_comment_bottom = (ImageView) convertView
					.findViewById(R.id.iv_comment_bottom);
			holder.tv_comment_bottom = (TextView) convertView
					.findViewById(R.id.tv_comment_bottom);
			holder.ll_like_bottom = (LinearLayout) convertView
					.findViewById(R.id.ll_like_bottom);
			holder.cb_like_bottom = (CheckBox) convertView
					.findViewById(R.id.cb_like_bottom);
			holder.tv_like_bottom = (TextView) convertView
					.findViewById(R.id.tv_like_bottom);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// set data
		//获取指定下标位置的微博信息
		final Status status = getItem(position);
		//从单条微博信息中，获取用户信息
		final User user = status.getUser();
		//异步加载用户头像图片
		imageLoader.displayImage(user.getProfile_image_url(), holder.iv_avatar);
		//设置用户头像点击事件，跳转到用户信息页面，传递用户名
		holder.iv_avatar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putSerializable("userName", user.getName());
				Intent intent = new Intent(context, UserInfoActivity.class);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		holder.tv_subhead.setText(user.getName());
		//设置用户个人信息点击事件
		holder.tv_subhead.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putSerializable("userName", user.getName());
				Intent intent = new Intent(context, UserInfoActivity.class);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
		//用户个人辅助信息
		holder.tv_caption.setText(DateUtils.getShortTime(status.getCreated_at()) +
				"  来自" + Html.fromHtml(status.getSource()));
		//微博内容字符串拓展处理
		SpannableString weiboContent = StringUtils.getWeiboContent(
				context, holder.tv_content, status.getText());
		holder.tv_content.setText(weiboContent);

		setImages(status, holder.include_status_image, holder.gv_images, holder.iv_image);

		// retweeted  转发微博信息
		final Status retweetedStatus = status.getRetweeted_status();
		if (retweetedStatus != null) {
			holder.include_retweeted_status.setVisibility(View.VISIBLE);
			String rStatusUser = retweetedStatus.getUser() == null ?
					"" : "@" + retweetedStatus.getUser().getName() + ":";
			String retweetContent = rStatusUser + retweetedStatus.getText();
			//转发微博字符串拓展
			SpannableString retweetWeiboContent = StringUtils.getWeiboContent(
					context, holder.tv_retweeted_content, retweetContent);
			holder.tv_retweeted_content.setText(retweetWeiboContent);
			setImages(retweetedStatus, holder.include_retweeted_status_image,
					holder.gv_retweeted_images, holder.iv_retweeted_image);
			//设置转发微博的点击事件
			holder.include_retweeted_status.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Bundle bundle = new Bundle();
					bundle.putSerializable("status", retweetedStatus);
					Intent intent = new Intent(context, StatusDetailActivity.class);
					intent.putExtras(bundle);
					context.startActivity(intent);
				}
			});
		} else {
			holder.include_retweeted_status.setVisibility(View.GONE);
		}

		// bottom bar	单条微博底部操作栏
		holder.tv_share_bottom.setText(status.getReposts_count() == 0 ?
				"转发" : status.getReposts_count() + "");
		holder.cb_like_bottom.setChecked(status.isLiked());
		//转发点击事件
		holder.ll_share_bottom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, WriteStatusActivity.class);
				intent.putExtra("status", status);
				context.startActivity(intent);
			}
		});

		holder.tv_comment_bottom.setText(status.getComments_count() == 0 ?
				"评论" : status.getComments_count() + "");
		//评论点击事件
		holder.ll_comment_bottom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(status.getComments_count() > 0) {
					Intent intent = new Intent(context, StatusDetailActivity.class);
					intent.putExtra("status", status);
					intent.putExtra("scroll2Comment", true);
					context.startActivity(intent);
				} else {
					Intent intent = new Intent(context, WriteCommentActivity.class);
					intent.putExtra("status", status);
					context.startActivity(intent);
				}
			}
		});
		//点赞点击事件
		holder.tv_like_bottom.setText(status.getAttitudes_count() == 0 ?
				"赞" : status.getAttitudes_count() + "");
		holder.ll_like_bottom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				final ScaleAnimation scaleAnimation2 = new ScaleAnimation(1.5f, 1f, 1.5f, 1f,
						Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				scaleAnimation2.setDuration(150);

				ScaleAnimation scaleAnimation1 = new ScaleAnimation(1f, 1.5f, 1f, 1.5f,
						Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				scaleAnimation1.setDuration(200);
				scaleAnimation1.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						holder.cb_like_bottom.setChecked(!holder.cb_like_bottom.isChecked());
						holder.cb_like_bottom.setAnimation(scaleAnimation2);
					}
				});
				holder.cb_like_bottom.setAnimation(scaleAnimation1);
			}
		});
		//整条微博的点击事件
		holder.ll_card_content.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, StatusDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("status", status);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});

		return convertView;
	}

	private void setImages(final Status status, ViewGroup vgContainer, GridView gvImgs, final ImageView ivImg) {
		if (status == null) {
			return;
		}

		ArrayList<PicUrls> picUrls = status.getPic_urls();
		String picUrl = status.getBmiddle_pic();
		//单图
		if (picUrls != null && picUrls.size() == 1) {
			vgContainer.setVisibility(View.VISIBLE);
			gvImgs.setVisibility(View.GONE);
			ivImg.setVisibility(View.VISIBLE);

			imageLoader.displayImage(picUrl, ivImg);

			ivImg.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, ImageBrowserActivity.class);
					intent.putExtra("status", status);
					intent.putExtra("position", -1);
					context.startActivity(intent);
				}
			});
			//多图
		} else if (picUrls != null && picUrls.size() > 1) {
			vgContainer.setVisibility(View.VISIBLE);
			gvImgs.setVisibility(View.VISIBLE);
			ivImg.setVisibility(View.GONE);

			StatusGridImgsAdapter imagesAdapter = new StatusGridImgsAdapter(context, picUrls, gvImgs);
			gvImgs.setAdapter(imagesAdapter);

			gvImgs.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent intent = new Intent(context, ImageBrowserActivity.class);
					intent.putExtra("status", status);
					intent.putExtra("position", position);
					context.startActivity(intent);
				}
			});
		} else {
			vgContainer.setVisibility(View.GONE);
		}
	}

	//单条微博视图组件信息
	public static class ViewHolder {
		public LinearLayout ll_card_content;
		public ImageView iv_avatar;
		public RelativeLayout rl_content;
		public TextView tv_subhead;
		public TextView tv_caption;

		public TextView tv_content;
		public FrameLayout include_status_image;
		public GridView gv_images;
		public ImageView iv_image;

		public LinearLayout include_retweeted_status;
		public TextView tv_retweeted_content;
		public FrameLayout include_retweeted_status_image;
		public GridView gv_retweeted_images;
		public ImageView iv_retweeted_image;

		public LinearLayout ll_share_bottom;
		public ImageView iv_share_bottom;
		public TextView tv_share_bottom;
		public LinearLayout ll_comment_bottom;
		public ImageView iv_comment_bottom;
		public TextView tv_comment_bottom;
		public LinearLayout ll_like_bottom;
		public CheckBox cb_like_bottom;
		public TextView tv_like_bottom;
	}

}
