package cn.pumpkin.angrypandarx.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author: zhibao.Liu
 * @version:
 * @date: 2019/1/9 16:36
 * @des:
 * @see {@link }
 */

public class ForeverMarqueeTextView extends android.support.v7.widget.AppCompatTextView {
    public ForeverMarqueeTextView(Context context) {
        super(context);
    }

    public ForeverMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ForeverMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

}
