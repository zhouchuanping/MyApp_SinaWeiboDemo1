package widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView.LayoutParams;
import android.widget.GridView;

/**
 * Created by ZCP_ing on 2016/5/10.
 *
 * 高度自适应的控件原理
 * 复写onMeasure方法：用于计算控件的size
 */
public class WrapHeightGridView extends GridView{

    public WrapHeightGridView(Context context) {
        super(context);
    }

    public WrapHeightGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapHeightGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int heightSpec ;
        if (getLayoutParams().height== LayoutParams.WRAP_CONTENT){
            heightSpec=MeasureSpec.makeMeasureSpec(
                    Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        } else{
            heightSpec = heightMeasureSpec;
        }

        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
