package adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zcp_ing.myapp_sinaweibodemo1.R;
import com.example.zcp_ing.myapp_sinaweibodemo1.UserInfoActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import entity.Comment;
import entity.User;
import utils.DateUtils;
import utils.StringUtils;

public class StatusCommentAdapter extends BaseAdapter {
	
	private Context context;
	private List<Comment> comments;
	private ImageLoader imageLoader;

	public StatusCommentAdapter(Context context, List<Comment> comments) {
		this.context = context;
		this.comments = comments;
		this.imageLoader = ImageLoader.getInstance();
	}
	
	@Override
	public int getCount() {
		return comments.size();
	}

	@Override
	public Comment getItem(int position) {
		return comments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolderList holder;
		if (convertView == null) {
			holder = new ViewHolderList();
			convertView = View.inflate(context, R.layout.item_comment, null);
			holder.ll_comments = (LinearLayout) convertView
					.findViewById(R.id.ll_comments);
			holder.iv_avatar = (ImageView) convertView
					.findViewById(R.id.iv_avatar);
			holder.tv_name = (TextView) convertView
					.findViewById(R.id.tv_name);
			holder.tv_time = (TextView) convertView
					.findViewById(R.id.tv_time);
			holder.tv_comment = (TextView) convertView
					.findViewById(R.id.tv_comment);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolderList) convertView.getTag();
		}

		// set data
		Comment comment = getItem(position);
		final User user = comment.getUser();
		
		imageLoader.displayImage(user.getProfile_image_url(), holder.iv_avatar);
		holder.iv_avatar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, UserInfoActivity.class);
				intent.putExtra("userName", user.getName());
				context.startActivity(intent);
			}
		});
		holder.tv_name.setText(user.getName());
		holder.tv_name.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, UserInfoActivity.class);
				intent.putExtra("userName", user.getName());
				context.startActivity(intent);
			}
		});
		holder.tv_time.setText(DateUtils.getDateTime(comment.getCreated_at()));
		SpannableString weiboContent = StringUtils.getWeiboContent(
				context, holder.tv_comment, comment.getText());
		holder.tv_comment.setText(weiboContent);
		
		holder.ll_comments.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
		return convertView;
	}
	
	public static class ViewHolderList {
		public LinearLayout ll_comments;
		public ImageView iv_avatar;
		public TextView tv_name;
		public TextView tv_time;
		public TextView tv_comment;
	}

}
