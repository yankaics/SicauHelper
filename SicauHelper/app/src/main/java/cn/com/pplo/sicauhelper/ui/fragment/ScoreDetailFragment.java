package cn.com.pplo.sicauhelper.ui.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.application.SicauHelperApplication;
import cn.com.pplo.sicauhelper.model.Score;
import cn.com.pplo.sicauhelper.model.Student;
import cn.com.pplo.sicauhelper.provider.SicauHelperProvider;
import cn.com.pplo.sicauhelper.provider.TableContract;
import cn.com.pplo.sicauhelper.service.ScoreService;
import cn.com.pplo.sicauhelper.util.NetUtil;
import cn.com.pplo.sicauhelper.util.StringUtil;


public class ScoreDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ListView listView;
    private ScoreListAdapter scoreListAdapter;

    public static ScoreDetailFragment newInstance(List<Score> scoreList) {
        ScoreDetailFragment fragment = new ScoreDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("list", (java.io.Serializable) scoreList);
        fragment.setArguments(bundle);
        return fragment;
    }

    public ScoreDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        ((MainActivity) activity).onSectionAttached("成绩");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_score_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUp(view);
    }

    private void setUp(View view) {
        listView = (ListView) view.findViewById(R.id.score_listView);
        scoreListAdapter = new ScoreListAdapter(getActivity());
        scoreListAdapter.setData(null);
        listView.setAdapter(scoreListAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("winson", ScoreDetailFragment.class.getSimpleName() + "Loader创建");
        return new CursorLoader(getActivity(), Uri.parse(SicauHelperProvider.URI_SCORE_ALL), null, null, null, null){
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        data.setNotificationUri(getActivity().getContentResolver(), Uri.parse(SicauHelperProvider.URI_SCORE_ALL));
        Log.d("winson", ScoreDetailFragment.class.getSimpleName() + "Loader加载完成");
        if(data != null) {
            if(data.getCount() > 0){
                Log.d("winson",ScoreDetailFragment.class.getSimpleName() +  "这次加载了" + data.getCount() + "条数据");
                scoreListAdapter.setData(data);
                scoreListAdapter.notifyDataSetChanged();
            }
            else {
                Intent scoreIntent = new Intent(getActivity(), ScoreService.class);
                getActivity().startService(scoreIntent);
            }

        }
        else {
            Log.d("winson", ScoreDetailFragment.class.getSimpleName() + "没有取到数据");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d("winson", ScoreDetailFragment.class.getSimpleName() + "Loader重置");
    }

    private class ScoreListAdapter extends BaseAdapter {

        private Context context;
        private List<Score> data = new ArrayList<Score>();

        public ScoreListAdapter(Context context) {
            this.context = context;
        }

        public void setData(Cursor cursor) {
//            this.data = data;
            if(cursor != null){
                data.clear();
                while(cursor.moveToNext()){
                    Score score = new Score();
                    score.setCategory(cursor.getString(cursor.getColumnIndex(TableContract.TableScore._CATEGORY)));
                    score.setCourse(cursor.getString(cursor.getColumnIndex(TableContract.TableScore._COURSE)));
                    score.setCredit(cursor.getFloat(cursor.getColumnIndex(TableContract.TableScore._CREDIT)));
                    score.setGrade(cursor.getFloat(cursor.getColumnIndex(TableContract.TableScore._GRADE)));
                    score.setId(cursor.getInt(cursor.getColumnIndex(TableContract.TableScore._ID)));
                    score.setMark(cursor.getString(cursor.getColumnIndex(TableContract.TableScore._MARK)));
                    data.add(score);
                }
            };
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Score getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //若是上半学期则在左边，反之则在右边
            if(String.valueOf(getItem(position).getGrade()).contains(".1")){
                    convertView = View.inflate(context, R.layout.item_fragment_score_list, null);
            }
            else {
                convertView = View.inflate(context, R.layout.item_fragment_score_list_right, null);
            }
            TextView categoryTv = (TextView) convertView.findViewById(R.id.category_tv);
            TextView gradeTv = (TextView) convertView.findViewById(R.id.grade_tv);
            final TextView scoreView = (TextView) convertView.findViewById(R.id.score_tv);
            TextView courseTv = (TextView) convertView.findViewById(R.id.course_tv);
            RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.rating_bar);
            TextView creditTv = (TextView) convertView.findViewById(R.id.credit_tv);

            String category = getItem(position).getCategory();
            int circleShape = 0;
            int color = 0;
            if(category.equals("必修")){
                circleShape = R.drawable.circle_blue;
                color = getResources().getColor(android.R.color.holo_blue_light);
            }
            else if(category.equals("公选")){
                circleShape = R.drawable.circle_red;
                color = getResources().getColor(android.R.color.holo_red_light);
            }
            else if(category.equals("任选")){
                circleShape = R.drawable.circle_green;
                color = getResources().getColor(android.R.color.holo_green_light);
            }
            else if(category.equals("推选")){
                circleShape = R.drawable.circle_orange;
                color = getResources().getColor(android.R.color.holo_orange_light);
            }
            else if(category.equals("实践")){
                circleShape = R.drawable.circle_purple;
                color = getResources().getColor(android.R.color.holo_purple);
            }
            scoreView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RotateAnimation animation = new RotateAnimation(0, 360);
                    animation.setDuration(500);

                    Log.d("winson", "点击了...");
                    scoreView.startAnimation(animation);
                }
            });

            categoryTv.setTextColor(color);
            scoreView.setBackgroundResource(circleShape);
            scoreView.setTextColor(Color.WHITE);
            gradeTv.setText(getItem(position).getGrade() + "");
            scoreView.setText(getItem(position).getMark() + "");
            courseTv.setText(getItem(position).getCourse() + "");
            creditTv.setText(getItem(position).getCredit() + "学分");
            categoryTv.setText("#" + getItem(position).getCategory() + "");
            return convertView;
        }
    }

}