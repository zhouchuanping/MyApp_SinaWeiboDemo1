package utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;

import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.zcp_ing.myapp_sinaweibodemo1.R;
import com.example.zcp_ing.myapp_sinaweibodemo1.UserInfoActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import entity.Emotion;

/**
 * Created by ZCP_ing on 2016/5/12.
 */

/**
 * getWeiboContent():
 *      1、首先将语言字符串封装成一个SpannableString:可拓展对象；
 *      2、利用正则去字符串中寻找可匹配的具体字符，并进行相应的替换；
 *
 *      以emoji为例：
 *          1、传入正则规则和源字符串；
 *          2、循环遍历整个字符串，如果可以找到可匹配的表情字符：利用group（）将匹配的具体字符获取出来
 *
 *
 */

/**
 * SpannableString 使用:
 *      代码示例：
 *      SpannableString spanStr = new SpannableString(source);	// 将源字符串包装成扩展性字符串
 *      ImageSpan span = new ImageSpan(context, bitmap);	// 创建一个拓展对象，这里是图片拓展
 *      spanStr.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);	// 设置拓展内容

 */

public class StringUtils {

    public static SpannableString getWeiboContent(final Context context, final TextView tv, String source) {
        return getWeiboContent(context, tv, source, true);
    }

    public static SpannableString getWeiboContent(
            final Context context, final TextView tv, String source, boolean clickable) {

        SpannableString spannableString = new SpannableString(source);

        Resources res = context.getResources();

        String regexLink = "(@[\u4e00-\u9fa5\\w]+)|(#[\u4e00-\u9fa5\\w]+#)";
        Pattern patternLink = Pattern.compile(regexLink);
        Matcher matcherLink = patternLink.matcher(spannableString);

        String regexEmoji = "\\[([\u4e00-\u9fa5\\w])+\\]";
        Pattern patternEmoji = Pattern.compile(regexEmoji);
        Matcher matcherEmoji = patternEmoji.matcher(spannableString);

        if(matcherLink.find() && clickable) {
            tv.setMovementMethod(new LinkTouchMovementMethod());
            matcherLink.reset();
        }

        for(;;) { // 如果可以匹配到
            if(matcherLink.find()) {
                final String key = matcherLink.group(); // 获取匹配到的具体字符
                int start = matcherLink.start(); // 匹配字符串的开始位置

                if(clickable) {
                    // @和#可点击
                    TouchableSpan clickableSpan = new TouchableSpan(
                            context.getResources().getColor(R.color.txt_at_blue),
                            context.getResources().getColor(R.color.txt_at_blue),
                            context.getResources().getColor(R.color.bg_at_blue)) {
                        @Override
                        public void onClick(View widget) {
                            if(key.startsWith("@")) {
                                Intent intent = new Intent(context, UserInfoActivity.class);
                                intent.putExtra("userName", key.substring(1));
                                context.startActivity(intent);
                            } else if(key.startsWith("#")) {
                                ToastUtils.showToast(context, "查看话题 :" + key, 0);
                            } else if(tv.getParent() instanceof LinearLayout){
                                ((LinearLayout) tv.getParent()).performClick();
                            }
                        }
                    };
                    spannableString.setSpan(clickableSpan, start, start + key.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    // @和#不可点击
                    int blueColor = context.getResources().getColor(R.color.txt_at_blue);
                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(blueColor);
                    spannableString.setSpan(colorSpan, start, start + key.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

            } else if(matcherEmoji.find()) {
                String key = matcherEmoji.group(); // 获取匹配到的具体字符
                int start = matcherEmoji.start(); // 匹配字符串的开始位置

                Integer imgRes = Emotion.getImgByName(key);
                if(imgRes != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeResource(res, imgRes, options);

                    int scale = (int) (options.outWidth / 32);
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = scale;
                    Bitmap bitmap = BitmapFactory.decodeResource(res, imgRes, options);

                    ImageSpan span = new ImageSpan(context, bitmap);
                    spannableString.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
                break;
            }
        }
        return spannableString;
    }

    private abstract static class TouchableSpan extends ClickableSpan {
        private boolean mIsPressed;
        private int mPressedBackgroundColor;
        private int mNormalTextColor;
        private int mPressedTextColor;

        public TouchableSpan(int normalTextColor, int pressedTextColor,
                             int pressedBackgroundColor) {
            mNormalTextColor = normalTextColor;
            mPressedTextColor = pressedTextColor;
            mPressedBackgroundColor = pressedBackgroundColor;
        }

        public void setPressed(boolean isSelected) {
            mIsPressed = isSelected;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(mIsPressed ? mPressedTextColor : mNormalTextColor);
            ds.bgColor = mIsPressed ? mPressedBackgroundColor : Color.TRANSPARENT;
            ds.setUnderlineText(false);
        }
    }

    static class LinkTouchMovementMethod extends LinkMovementMethod {
        private TouchableSpan mPressedSpan;

        @Override
        public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mPressedSpan = getPressedSpan(textView, spannable, event);
                if (mPressedSpan != null) {
                    mPressedSpan.setPressed(true);
                    Selection.setSelection(spannable, spannable.getSpanStart(mPressedSpan),
                            spannable.getSpanEnd(mPressedSpan));
                }
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                TouchableSpan touchedSpan = getPressedSpan(textView, spannable, event);
                if (mPressedSpan != null && touchedSpan != mPressedSpan) {
                    mPressedSpan.setPressed(false);
                    mPressedSpan = null;
                    Selection.removeSelection(spannable);
                }
            } else {
                if (mPressedSpan != null) {
                    mPressedSpan.setPressed(false);
                    super.onTouchEvent(textView, spannable, event);
                }
                mPressedSpan = null;
                Selection.removeSelection(spannable);
            }
            return true;
        }

        private TouchableSpan getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {

            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= textView.getTotalPaddingLeft();
            y -= textView.getTotalPaddingTop();

            x += textView.getScrollX();
            y += textView.getScrollY();

            Layout layout = textView.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            TouchableSpan[] link = spannable.getSpans(off, off, TouchableSpan.class);
            TouchableSpan touchedSpan = null;
            if (link.length > 0) {
                touchedSpan = link[0];
            }
            return touchedSpan;
        }

    }
}
