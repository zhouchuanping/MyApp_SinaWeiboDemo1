package widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.CheckBox;

/**
 * Created by ZCP_ing on 2016/5/16.
 */
public class InsideCheckBox extends CheckBox {
    public InsideCheckBox(Context context) {
        super(context);
    }

    public InsideCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InsideCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
