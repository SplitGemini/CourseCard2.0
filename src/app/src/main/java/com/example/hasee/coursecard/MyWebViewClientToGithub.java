package com.example.hasee.coursecard;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.hasee.coursecard.database.DBCourse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.functions.Action1;

public class MyWebViewClientToGithub extends WebViewClient {
    private Activity activity;
    private boolean hasLoaded;
    private List<DBCourse> courses1 = new ArrayList<>();
    private List<DBCourse> courses10 = new ArrayList<>();
    private List<DBCourse> courses11 = new ArrayList<>();
    private List<DBCourse> courses20 = new ArrayList<>();
    public MyWebViewClientToGithub(Activity activity)
    {
        //  this.textView =textView;
        this.activity =activity;
        hasLoaded = false;

    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String urlStr = String.valueOf(request.getUrl());
        view.loadUrl(urlStr);
        Log.d("web view","shouldOverrideUrlLoading: " + urlStr);
        return false;
    }

    public void onPageFinished(WebView view, String url) {
        Log.d("web view","on page finished");
        if(!hasLoaded){
            String[] weeklys = {"1","10","11","20"};
            for (String i: weeklys)
                Onclick4Data(i,Common.academic);
            hasLoaded = true;
        }
    }


    //githubapi接口
    public interface GitHubService {
        @GET("/SplitGemini/Coursecard2.0/master/dashboard/new_content/sample/sample_2018-1_week{weekly}.json")
        Observable<JsonRootBean> getRepo(@Path("weekly") String weekly);

        @GET("/SplitGemini/Coursecard2.0/master/dashboard/new_content/sample/sample_2018-1_week{weekly}.json")
        Call<JsonRootBean> getRepo1(@Path("weekly") String weekly);
    }

    private String[] InfoConvert(String tmp_str) {
        String[] temp = new String[3];
        int cnt = 0;
        int pos = 0;
        for (int k = 0; k < tmp_str.length(); ++k) {
            if (tmp_str.charAt(k) == ',') {
                temp[cnt++] = tmp_str.substring(pos, k);
                ++k;
                pos = k;
            }
        }
        if (pos == tmp_str.length()) {
            temp[2] = "课程安排";
        } else {
            temp[2] = tmp_str.substring(pos, tmp_str.length());
        }
        return temp;
    }

    // 获取json转换DBcourse插入数据库
    public void Onclick4Data(final String weekly, final String academic) {
        OkHttpClient build = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .build();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://raw.githubusercontent.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(build)
                .build();


        MyWebViewClientToGithub.GitHubService gitHubService = retrofit.create(MyWebViewClientToGithub.GitHubService.class);
        Observable<JsonRootBean> observable = gitHubService.getRepo(weekly);
        Call<JsonRootBean> call = gitHubService.getRepo1(weekly);
        String url = call.request().url().toString();
        Log.i("Onclick4Data: ", url);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonRootBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("HTTPx","onSubscribe");
                    }

                    @Override
                    public void onNext(JsonRootBean jsonRootBean) {
                        Log.d("HTTPx", "onNext");
                        if(jsonRootBean.getCode() != 200) {
                            Log.d("HTTPx", "code is not 200");
                            return;
                        }
                        int index = Integer.parseInt(weekly);
                        courses(index).clear();
                        for (int i = 0; i < jsonRootBean.getData().size(); i++) {
                            Data data = jsonRootBean.getData().get(i);
                            Log.d("data info",weekly+" :"+
                                    data.getFriday()+data.getMonday()+data.getThursday()+data.getTuesday()+data.getWednesday()+data.getSection());

                            if (!data.getMonday().equals("null") && data.getSection() % 2 != 0) {
                                String[] temp = InfoConvert(data.getMonday());
                                courses(index).add(new DBCourse(academic, "星期一", temp[0], temp[1], temp[2], (data.getSection() + 1) / 2, index));
                            }
                            if (!data.getTuesday().equals("null") && data.getSection() % 2 > 0) {
                                String[] temp = InfoConvert(data.getTuesday());
                                courses(index).add(new DBCourse(academic, "星期二", temp[0], temp[1], temp[2], (data.getSection() + 1) / 2, index));
                            }
                            if (!data.getWednesday().equals("null") && data.getSection() % 2 > 0) {
                                String[] temp = InfoConvert(data.getWednesday());
                                courses(index).add(new DBCourse(academic, "星期三", temp[0], temp[1], temp[2], (data.getSection() + 1) / 2, index));
                            }
                            if (!data.getThursday().equals("null") && data.getSection() % 2 > 0) {
                                String[] temp = InfoConvert(data.getThursday());
                                courses(index).add(new DBCourse(academic, "星期四", temp[0], temp[1], temp[2], (data.getSection() + 1) / 2, index));
                            }
                            if (!data.getFriday().equals("null") && data.getSection() % 2 > 0) {
                                String[] temp = InfoConvert(data.getFriday());
                                courses(index).add(new DBCourse(academic, "星期五", temp[0], temp[1], temp[2], (data.getSection() + 1) / 2, index));
                            }

                        }
                        Log.d("on next","weekly: "+ weekly +" ,courses number :"+ courses(index).size());
                    }
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toasty.error(activity, "获取数据失败 :\n"+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onComplete() {
                        Log.d("web view","on complete");
                        final int index = Integer.parseInt(weekly);
                        int delay = 0;
                        if(index == 11) delay = 2000;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Utils.insert(activity,courses(index));
                                Toasty.success(activity, "获取数据成功",Toast.LENGTH_LONG).show();
                            }
                        },delay);
                        Common.statecode = "OK";
                    }
                });
    }

    private List<DBCourse> courses(int index){
        if(index == 1) return courses1;
        else if(index == 10) return courses10;
        else if(index == 11) return courses11;
        else if(index == 20) return courses20;
        else return null;
    }


}
