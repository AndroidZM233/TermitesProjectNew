package com.termites.tools;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * 自动显示EmptyView的ListView
 * 使用时，调用setEmptyView 方法初始化当前ListView 所需要的 empty view
 * Created by LF on 16/10/20.
 */
public class EmptyViewListView extends ListView {

    public EmptyViewListView(Context context) {
        super(context);
        init();
    }

    public EmptyViewListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EmptyViewListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setCacheColorHint(Color.TRANSPARENT);
        setOverScrollMode(ListView.OVER_SCROLL_NEVER);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        View emptyView = getEmptyView();
        if (emptyView != null) {
            ViewGroup.LayoutParams lp = emptyView.getLayoutParams();
            if (lp == null) {
                lp = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            } else {
                lp.width = LayoutParams.MATCH_PARENT;
                lp.height = LayoutParams.MATCH_PARENT;
            }
            emptyView.setLayoutParams(lp);
            emptyView.setClickable(true);
            ViewGroup parent = (ViewGroup) getParent();
            parent.addView(emptyView);
        }
    }
}











