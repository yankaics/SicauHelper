package cn.com.pplo.sicauhelper.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import cn.com.pplo.sicauhelper.R;
import cn.com.pplo.sicauhelper.util.UIUtil;

public class MapActivity extends BaseActivity {

    public static final String EXTRA_LATITUDE = "latitude";
    public static final String EXTRA_LONGITUDE = "longitude";
    public static final String EXTRA_NAME = "name";

    private String name;
    private double latitude = 0d;
    private double longitude = 0d;

    private MapView mapView;
    private AMap aMap;

    public static void startMapActivity (Context context, double latitude, double longitude, String name) {
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra(EXTRA_LATITUDE, latitude);
        intent.putExtra(EXTRA_LONGITUDE, longitude);
        intent.putExtra(EXTRA_NAME, name);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        latitude = getIntent().getDoubleExtra(EXTRA_LATITUDE, 0d);
        longitude = getIntent().getDoubleExtra(EXTRA_LONGITUDE, 0d);
        name = getIntent().getStringExtra(EXTRA_NAME);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 必须要写
        init();
        //移到指定位置
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17));
        MarkerOptions markerOptions = new MarkerOptions();
        //标记的位置
        markerOptions.position(new LatLng(latitude, longitude));
        //标记的标题
        markerOptions.title(name + " 所在地");
        Marker marker = aMap.addMarker(markerOptions);
        //显示标记
        marker.showInfoWindow();

    }

    private void setUp() {

    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
