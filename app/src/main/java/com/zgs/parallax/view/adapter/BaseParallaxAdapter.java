package com.zgs.parallax.view.adapter;


import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * User: zgsHighwin
 * Email: 799174081@qq.com Or 799174081@gmail.com
 * Description:
 * Create-Time: 2016/8/25 8:51
 */
public abstract class BaseParallaxAdapter<T> extends RecyclerView.Adapter<BaseParallaxAdapter.ParallaxHolder> {

    private View mHeaderView;
    protected View mFooterView;
    private LayoutInflater mInflater;

    private View mConvertView;
    private List<T> mList;
    private
    @LayoutRes
    int mLayout;

    public BaseParallaxAdapter(List<T> list) {
        this(0, list);
        this.mList = list;
    }

    public BaseParallaxAdapter(@LayoutRes int layout, List<T> list) {
        mLayout = layout;
        mList = list;
    }

    public BaseParallaxAdapter(@NonNull View view, List<T> list) {
        mConvertView = view;
        mList = list;
    }

    @Override
    public BaseParallaxAdapter.ParallaxHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == BaseParallaxAdapter.HEADER_VIEW) {
            return new ParallaxHolder(mHeaderView);
        }

        if (mFooterView != null && viewType == BaseParallaxAdapter.FOOTER_VIEW) {
            return new ParallaxHolder(mFooterView);
        }
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        mConvertView = mInflater.inflate(mLayout, parent, false);
        return new ParallaxHolder(mConvertView);
    }

    @Override
    public void onBindViewHolder(BaseParallaxAdapter.ParallaxHolder holder, int position) {
        if (getItemViewType(position) == BaseParallaxAdapter.NORMAL_VIEW) {
            convertView(holder, mList.get(position - 1));
        } else if (getItemViewType(position) == BaseParallaxAdapter.HEADER_VIEW) {
            return;
        } else if (getItemViewType(position) == BaseParallaxAdapter.FOOTER_VIEW) {
            return;
        }
    }

    @Override
    public int getItemCount() {
        if (mHeaderView == null && mFooterView == null) {
            return mList.size();
        } else if (mHeaderView == null && mFooterView != null) {
            return mList.size() + 1;
        } else if (mHeaderView != null && mFooterView == null) {
            return mList.size() + 1;
        } else {
            return mList.size() + 2;
        }
    }

    public class ParallaxHolder extends RecyclerView.ViewHolder {
        public ParallaxHolder(View itemView) {
            super(itemView);
            if (itemView == mHeaderView || itemView == mFooterView) {
                return;
            } else {
                mConvertView = itemView;
            }
        }
    }


    private static final int HEADER_VIEW = 0X00000001;
    private static final int FOOTER_VIEW = 0X00000002;
    private static final int NORMAL_VIEW = 0X00000003;

    @IntDef({HEADER_VIEW, FOOTER_VIEW, NORMAL_VIEW})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TYPE_MODE {
    }

    @TYPE_MODE
    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null && mFooterView == null) {
            return BaseParallaxAdapter.NORMAL_VIEW;
        }

        if (mHeaderView != null && position == 0) {
            return BaseParallaxAdapter.HEADER_VIEW;
        }

        if (mFooterView != null && position == getItemCount() - 1) {
            return BaseParallaxAdapter.FOOTER_VIEW;
        }

        return BaseParallaxAdapter.NORMAL_VIEW;
    }

    /**
     * add header view
     *
     * @param v
     */
    public void addHeaderView(View v) {
        if (v == null) {
            throw new IllegalArgumentException("header view must be not null");
        }
        this.mHeaderView = v;
        notifyItemInserted(0);
    }

    /**
     * add footer view
     *
     * @param v
     */
    public void addFooterView(View v) {
        if (v == null) {
            throw new IllegalArgumentException("footer view must be not null");
        }
        if (mList == null) {
            throw new NullPointerException("list is null");
        }
        this.mFooterView = v;

        if (mList.size() == 0) {
            notifyItemInserted(0);
        } else {
            notifyItemInserted(mList.size() - 1);
        }
    }

    protected abstract void convertView(ParallaxHolder holder, T t);

    public View getFooterView() {
        return mFooterView;
    }
}
