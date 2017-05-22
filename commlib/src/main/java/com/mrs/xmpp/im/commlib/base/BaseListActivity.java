package com.mrs.xmpp.im.commlib.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.mrs.xmpp.im.commlib.R;
import com.mrs.xmpp.im.commlib.Conf;
import com.mrs.xmpp.im.commlib.recyclerview.DividerItemDecoration;
import com.mrs.xmpp.im.commlib.recyclerview.PullRecycler;
import com.mrs.xmpp.im.commlib.recyclerview.WrapRecyclerAdapter;
import com.mrs.xmpp.im.commlib.recyclerview.WrapRecyclerView;
import com.mrs.xmpp.im.commlib.multisupport.MultiTypeSupport;
import com.mrs.xmpp.im.commlib.multisupport.MultiTypeSupportAdapter;
import com.mrs.xmpp.im.commlib.multisupport.ViewHolder;
import com.mrs.xmpp.im.commlib.recyclerview.onLoadMoreListener;

import java.util.ArrayList;


/**
 * Created by mrs on 2017/4/6.
 */

public abstract class BaseListActivity<T> extends AppCompatActivity implements onLoadMoreListener, Toolbar.OnMenuItemClickListener {

    /**
     * 说明:
     * <p>
     * 这是一个通用的 可以快速搭建 列表界面的 BaseListActivity
     * 同时，支持刷新，加载更多，添加多headerview{@link WrapRecyclerView#addHeaderView}}，footerview{@link WrapRecyclerView#addFooterView(View)}}
     * <p>
     * <p>
     * * 使用实例
     * public class TestActivity extends BaseListActivity<JavaBean> {
     * private ArrayList<JavaBean> list = new ArrayList<>();
     *
     * @Override protected void onActivityInit() {
     * addHeaderView(LayoutInflater.from(this).inflate(R.layout.base_widget_load_more, recycleList, false));
     * addHeaderView(LayoutInflater.from(this).inflate(R.layout.dialog_more_select, recycleList, false));
     * }
     * @Override public int getItemLayoutRes() {
     * return android.R.layout.simple_list_item_1;
     * }
     * @Override public void onBindHolder(ViewHolder holder, JavaBean item, int position) {
     * //链式调用
     * holder.setText(android.R.id.text1, JavaBean.desc)
     * .setImageUrl(android.R.id.imageview, JavaBean.picUrl);
     * .setImageUrl(android.R.id.imageview1, JavaBean.picUrl1);
     * .setImageUrl(android.R.id.imageview2, JavaBean.picUrl2);
     * }
     * @Override public void onRefresh() {
     * mCurPage = 1;
     * loadCompleted(getDatas(true));
     * }
     * @Override public void onLoadMore() {
     * loadCompleted(getDatas(false));
     * }
     * @see WrapRecyclerView#setAdapter(RecyclerView.Adapter)
     * @see android.widget.ListView#addHeaderView(View, Object, boolean)
     * @see WrapRecyclerAdapter
     * <p>
     * <p>
     * 1.实现列表单布局  请复写
     * @see BaseListActivity#getItemLayoutRes()
     * <p>
     * 2.实现多条目布局，同样支持,请复写
     * @see BaseListActivity#getSupportMultiType()
     * <p>
     * 3.默认一页加载数量是20
     * @see Conf#DEFAULT_LIST_ITEM
     * <p>
     * 4.在刷新或者加载更多回调之后调用
     * @see BaseListActivity#loadFailed()
     * @see BaseListActivity#loadCompleted(ArrayList)
     * <p>
     */


    public PullRecycler mPullRecycler;
    public TextView mTitle;
    public Toolbar mToolbar;

    private ListAdapter mAdapter;//列表刷新使用mPullRecycle
    protected ArrayList<T> mDatas = new ArrayList<>();
    protected int mCurPage = 1;//默认初次加载页码为1  如果执行刷新动作,必须置为1,

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_widget_list);


        setUpToolbar();
        setUpRecyclerView();
        onSetUpView();

    }

    private void setUpRecyclerView() {
        mPullRecycler = (PullRecycler) findViewById(R.id.PullRecycler);
        mPullRecycler.setLayoutManger(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ListAdapter(this, mDatas, getItemLayoutRes(), getSupportMultiType());
        mPullRecycler.setHasFixedSize(true);
        mPullRecycler.setAdapter(mAdapter);
        mPullRecycler.setOnLoadMoreListener(this);
        mPullRecycler.setRefresh();
    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toobar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigationClick();
            }
        });
    }


    protected abstract void onSetUpView();

    //想添加别的分割线 复写该方法
    protected RecyclerView.ItemDecoration getItemDecoration() {
        return new DividerItemDecoration(this, R.drawable.base_widget_list_divider);
    }

    //如果你想对导航栏 返回按钮点击时做特殊处理
    //可以复写该方法
    public void onNavigationClick() {
        this.finish();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        /**
         * swtich(item.getid()){
         *     case R.id.search:    do you  want
         *     break;
         *      case R.id.dialog:    show your dialog
         *     break;
         * }
         */
        //当点击某一个item的时候  同样可以使用dialog或者popwinow达到你想要的样子
        return false;
    }

    public void setUpMenu(@MenuRes int menuRes) {
        if (mToolbar == null || menuRes == -1)
            return;
        Menu menu = mToolbar.getMenu();
        if (menu != null)
            menu.clear();
        mToolbar.inflateMenu(menuRes);
        mToolbar.setOnMenuItemClickListener(this);
    }

    public void setUpTitle(String title) {
        if (mTitle == null)
            return;
        mTitle.setText(title);
    }

    public void setUpTitle(@StringRes int title) {
        if (mTitle == null)
            return;
        mTitle.setText(title);
    }

    public void disEnableLoadMore() {
        mPullRecycler.setEnableLoadMore(false);
    }

    public void enableLoadMore() {
        mPullRecycler.setEnableLoadMore(true);
    }

    public void loadFailed() {
        //刷新
        if (mCurPage == 1) {
            mDatas.clear();
            //空数据
            if (mDatas == null || mDatas.size() == 0) {
                mPullRecycler.showEmptyView();
                //设置空白页面
            }
        } else {
            //加载更多
            //mPullRecycler.setLoadMoreEnable(false);
        }

        mPullRecycler.setRefreshing(false);
        mPullRecycler.setOnRefreshFaield();

    }

    public void loadCompleted(ArrayList<T> list) {
        mCurPage++;
        mPullRecycler.showContentView();

        //刷新
        if (mCurPage == 2) {
            mDatas.clear();
            //空数据
            if (list == null || list.size() == 0) {
                //设置空白页面
                mPullRecycler.showEmptyView();

            } else if (list != null && list.size() > 0)
                mDatas.addAll(list);
            if (list != null && list.size() >= Conf.DEFAULT_LIST_ITEM)
                mPullRecycler.setEnableLoadMore(true);
        } else {
            //加载更多
            if (list != null && list.size() > 0)
                mDatas.addAll(list);
            //如果是第二页 而且返回的数据条目少于默认数量，则认为没有更多数据了，禁掉加载更多
            if (list == null || list.size() < Conf.DEFAULT_LIST_ITEM)
                mPullRecycler.setEnableLoadMore(false);
        }
        mPullRecycler.setRefreshing(false);
        mPullRecycler.setOnRefreshCompeleted();
    }


    public class ListAdapter extends MultiTypeSupportAdapter<T> {
        public ListAdapter(Context context, ArrayList<T> list, int layoutRes, MultiTypeSupport typeSupport) {
            super(context, list, layoutRes, typeSupport);
        }

        @Override
        public void onBindNormalHolder(ViewHolder holder, T item, int position) {
            onBindHolder(holder, item, position);
        }
    }

    //如果 是单一条目 那么复写这个方法 设置条目  layout
    public int getItemLayoutRes() {
        return 0;
    }

    //如果是多条目，那么复写这个方法，根据条目类型，给出条目布局
    public MultiTypeSupport getSupportMultiType() {
        /**
         * @see MultiTypeSupport
         *
         */
        return null;
    }

    public abstract void onBindHolder(ViewHolder holder, T item, int position);

}
