package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.model.Course;
import cn.com.pplo.sicauhelper.model.News;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;
import cn.com.pplo.sicauhelper.service.CourseService;
import cn.com.pplo.sicauhelper.service.OnRequestFinishListener;
import cn.com.pplo.sicauhelper.service.ScoreService;
import cn.com.pplo.sicauhelper.util.CursorUtil;
import cn.com.pplo.sicauhelper.util.UIUtil;
import cn.com.pplo.sicauhelper.widget.ListViewPadding;

/**
 * Created by winson on 2014/10/4.
 */
public class CourseDateFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private ListView listView;
    private ProgressDialog progressDialog;

    private List<Course> courseList = new ArrayList<Course>();

    //从上一层传来的星期几
    private int datePosition = 0;
    private CourseAdapter courseAdapter;

    public static CourseDateFragment newInstance() {
        CourseDateFragment fragment = new CourseDateFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        datePosition = getArguments().getInt("position");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_date, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUp(view);
    }

    private void setUp(View view) {
        listView = (ListView) view.findViewById(R.id.course_listView);
        progressDialog = UIUtil.getProgressDialog(getActivity(), "我正在从教务系统帮你找课表～");
        //设置空时view
        listView.setEmptyView(view.findViewById(R.id.empty_view));
        //下拉控件
        //listView上下补点间距
//        TextView paddingTv = ListViewPadding.getListViewPadding(getActivity());
//        listView.addHeaderView(paddingTv);
//        listView.addFooterView(paddingTv);

        courseAdapter = new CourseAdapter(getActivity(), courseList);
        listView.setAdapter(courseAdapter);

        //滑动监听
//        setScrollHideOrShowActionBar(listView);

        //下拉监听

        //启动Loader
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.d("winson", "创建Loader" + datePosition);
        return new CursorLoader(getActivity(), Uri.parse(SicauHelperProvider.URI_COURSE_ALL), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data) {

        listView.setEmptyView(null);
        if (data != null  && data.getCount() > 0) {
            notifyDataSetChanged(CursorUtil.parseCourseList(data, datePosition));
        } else {
            Intent intent = new Intent(getActivity(), CourseService.class);
            getActivity().bindService(intent, serviceConn, Context.BIND_AUTO_CREATE);
        }

    }

    /**
     * 通知ListView数据改变
     * @param list
     */
    private void notifyDataSetChanged(List<Course> list){
        if(list != null && list.size() > 0){
            courseList.clear();
            courseList.addAll(list);
            courseAdapter.notifyDataSetChanged();
        }
    }

    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CourseService.CourseServiceBinder courseServiceBinder = (CourseService.CourseServiceBinder) service;
            CourseService courseService = courseServiceBinder.getCourseService();
            progressDialog.show();
            courseService.requestCourseInfo(new OnRequestFinishListener() {
                @Override
                public void onRequestFinish(boolean isSuccess) {
                    getActivity().unbindService(serviceConn);
                    UIUtil.dismissProgressDialog(progressDialog);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private class CourseAdapter extends BaseAdapter {
        private Context context;
        private List<Course> data;

        private CourseAdapter(Context context, List<Course> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Course getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(context, R.layout.item_fragment_course_date_list, null);
                holder.timeTv = (TextView) convertView.findViewById(R.id.time_tv);
                holder.nameTv = (TextView) convertView.findViewById(R.id.name_tv);
                holder.categoryTv = (TextView) convertView.findViewById(R.id.category_tv);
                holder.classroomTv = (TextView) convertView.findViewById(R.id.classroom_tv);
                holder.creditTv = (RatingBar) convertView.findViewById(R.id.credit_tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Course course = getItem(position);
            String time = course.getTime();
            int circleShape = 0;
            int color = 0;
            if (time.equals("1-2")) {
                circleShape = R.drawable.circle_blue;
                color = getResources().getColor(R.color.blue_500);
            } else if (time.equals("3-4")) {
                circleShape = R.drawable.circle_red;
                color = getResources().getColor(R.color.red_500);
            } else if (time.equals("5-6")) {
                circleShape = R.drawable.circle_green;
                color = getResources().getColor(R.color.green_500);
            } else if (time.equals("7-8")) {
                circleShape = R.drawable.circle_orange;
                color = getResources().getColor(R.color.orange_500);
            } else if (time.equals("9-10")) {
                circleShape = R.drawable.circle_purple;
                color = getResources().getColor(R.color.purple_500);
            }

            //设置课程名
            holder.nameTv.setText(course.getName());
            //设置课程时间
            holder.timeTv.setText(course.getTime());
            holder.timeTv.setBackgroundResource(circleShape);

            //设置课程类型
            holder.categoryTv.setText("#" + course.getCategory() + "#");
            //设置教室
            holder.classroomTv.setText(course.getClassroom() + "(" + course.getWeek() + "周)");

            //设置星星个
            if ((getItem(position).getCredit() > 5)) {
                holder.creditTv.setNumStars(10);
            } else {
                holder.creditTv.setNumStars(5);
            }
            //设置星星颜色
            LayerDrawable stars = (LayerDrawable) holder.creditTv.getProgressDrawable();
            stars.getDrawable(0).setColorFilter(Color.parseColor("#cccccc"), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(1).setColorFilter(Color.parseColor("#cccccc"), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(2).setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            holder.creditTv.setRating(getItem(position).getCredit());
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView timeTv;
        TextView nameTv;
        TextView classroomTv;
        RatingBar creditTv;
        TextView categoryTv;
    }
}