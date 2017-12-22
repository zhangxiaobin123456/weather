package com.example.weather.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.MainActivity;
import com.example.weather.R;
import com.example.weather.WeatherActivity;
import com.example.weather.db.City;
import com.example.weather.db.County;
import com.example.weather.db.Province;
import com.example.weather.util.AnalysisDataUtils;
import com.example.weather.util.HttpUtils;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by zxb.
 */
public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    public static final String PROVINCE = "province";
    public static final String CITY = "city";
    public static final String COUNTY = "county";

    private static final String TAG = "ChooseAreaFragment";
    private Button mBackButton;
    private TextView mTitleText;
    private ListView mListView;
    private List<String> dataList = new ArrayList<>();
    private ArrayAdapter<String> mStringArrayAdapter;

    /**
     * 省列表
     */
    private List<Province> mProvinceList;

    /**
     * 市列表
     */
    private List<City> mCityList;

    /**
     * 县列表
     */
    private List<County> mCountyList;

    /**
     * 选中的省份
     */
    private Province mSelectedProvince;

    /**
     * 选中的城市
     */
    private City mSelectedCity;

    /**
     * 当前选中的级别
     */
    private int currentLevel;
    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area_fragment, container,false);
        mBackButton = (Button) view.findViewById(R.id.back_button);
        mTitleText = (TextView) view.findViewById(R.id.title_text);
        mListView = (ListView) view.findViewById(R.id.list_view);
        mStringArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        mListView.setAdapter(mStringArrayAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    //级别为省级别
                    mSelectedProvince =  mProvinceList.get(position);
                    //查询省份下的城市
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    //级别为城市级别
                    mSelectedCity = mCityList.get(position);
                    //查询城市下的数据
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    //级别为县城
                    //所选县城的 id
                    String weatherId = mCountyList.get(position).weatherId;
                    if (getActivity() instanceof MainActivity){
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if(getActivity() instanceof WeatherActivity){
                        WeatherActivity weatherActivity = (WeatherActivity) getActivity();
                        weatherActivity.drawerLayout.closeDrawers();
                        weatherActivity.mSwipeRefreshLayout.setRefreshing(true);
                        weatherActivity.requestWeather(weatherId);
                    }

                }
            }
        });
        //回退事件处理
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentLevel == LEVEL_COUNTY) {
                    //当前为县城，则查询城市并刷新数据
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    //当前为城市，则查询省份并刷新数据
                    queryProvinces();
                }
            }
        });


        queryProvinces();
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryProvinces() {
        //标题
        mTitleText.setText("中国");
        mBackButton.setVisibility(View.GONE);
        mProvinceList = DataSupport.findAll(Province.class);
        if (mProvinceList.size() > 0) {
            dataList.clear();
            for (Province province : mProvinceList) {
                dataList.add(province.provinceName);
            }
            //listView 重新设置数据
            mStringArrayAdapter.notifyDataSetChanged();
            //定位到第一条数据位置
            mListView.setSelection(0);
            //当前级别为省份级别
            currentLevel = LEVEL_PROVINCE;
        } else {
            //数据地址
            String address = "http://guolin.tech/api/china";
            //查询服务器上的数据
            queryFromServer(address, PROVINCE);
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCities() {
        mTitleText.setText(mSelectedProvince.provinceName);
        mBackButton.setVisibility(View.VISIBLE);
        //查询本地数据库
        mCityList = DataSupport.where("provinceid = ?", String.valueOf(mSelectedProvince.id)).find(City.class);
        if (mCityList.size() > 0) {
            dataList.clear();

            for (City city : mCityList) {
                dataList.add(city.cityName);
            }
            //刷新数据
            mStringArrayAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            //级别为 城市
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = mSelectedProvince.provinceCode;
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, CITY);
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCounties() {
        mTitleText.setText(mSelectedCity.cityName);
        mBackButton.setVisibility(View.VISIBLE);
        //查询本地数据库
        mCountyList = DataSupport.where("cityid = ?", String.valueOf(mSelectedCity.id)).find(County.class);
        if (mCountyList.size() > 0) {
            dataList.clear();
            for (County county : mCountyList) {
                dataList.add(county.countyName);
            }
            //刷新数据
            mStringArrayAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            //改变级别为 县城
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = mSelectedProvince.provinceCode;
            int cityCode = mSelectedCity.cityCode;
            //查询服务器数据
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address , COUNTY);
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据。
     */
    private void queryFromServer(String address, final String type) {
        Log.e(TAG, "queryFromServer: "+address);
        showProgressDialog();
        HttpUtils.getInstance().sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //失败
                // 通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //成功
                String responseText = response.body().string();
                boolean result = false;
                //判断类型 并解析数据
                if (PROVINCE.equals(type)){
                    //省份
                    result = AnalysisDataUtils.analysisiProvinceResponse(responseText);
                } else if (CITY.equals(type)) {
                    //城市
                    result = AnalysisDataUtils.analysisiCityResponse(responseText,mSelectedProvince.id);
                }else if(COUNTY.equals(type)){
                    //县
                    result = AnalysisDataUtils.analysisiCountyResponse(responseText, mSelectedCity.id);
                }
                //保存成功
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //关闭dialog
                            closeProgressDialog();
                            //保存之后  判断类型 并 重新查询数据
                            if (PROVINCE.equals(type)) {
                                queryProvinces();
                            } else if (CITY.equals(type)) {
                                queryCities();
                            } else if (COUNTY.equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("正在加载...");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
