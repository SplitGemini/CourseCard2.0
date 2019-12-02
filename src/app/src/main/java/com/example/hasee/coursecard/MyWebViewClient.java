package com.example.hasee.coursecard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hasee.coursecard.database.CourseDatabase;
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
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.functions.Action1;


public class MyWebViewClient extends WebViewClient {
    private Activity activity;
    private boolean hasLoaded = false;
    //因为是异步读写，只用一个会造成读写重复
    private List<DBCourse> courses1 = new ArrayList<>();
    private List<DBCourse> courses10 = new ArrayList<>();
    private List<DBCourse> courses11 = new ArrayList<>();
    private List<DBCourse> courses20 = new ArrayList<>();
    public MyWebViewClient(Activity activity)
    {
        //  this.textView =textView;
        this.activity =activity;

    }

    /*  警告: [deprecation]
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
    */

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String urlStr = String.valueOf(request.getUrl());
        /*
        if(urlStr.indexOf("https://uems.sysu.edu.cn/jwxt/") != -1){
            Log.d("web view","shouldOverrideUrlLoading but go uems.sysu.edu.cn/jwxt");
            return false;
        }

         */
        view.loadUrl(urlStr);
        Log.d("web view","shouldOverrideUrlLoading: " + urlStr);
        return false;
    }

    public void onPageFinished(WebView view, String url) {
        Log.d("web view","on page finished");
        CookieManager cookieManager = CookieManager.getInstance();
        String CookieStr = cookieManager.getCookie(url);
        Common.cookie = CookieStr;
        if(CookieStr != null)
            if(CookieStr.contains("LYSESSIONID") && CookieStr.contains("user")) {
                if(!hasLoaded){
                    String[] weeklys = {"1,9","10","11,19","20"};
                    for (String i: weeklys)
                        Onclick4Data(i,Common.academic);
                    hasLoaded = true;
                }

            }

    }


    //githubapi接口
    //该接口已废弃
    //https://uems.sysu.edu.cn/jwxt/student-status/student-info/student-no-schedule?academicYear=2017-1&weekly=7

    public interface GitHubService {
        @GET("/jwxt/student-status/student-info/student-no-schedule")
        Observable<JsonRootBean> getRepo( @Query("weekly") String weekly, @Query("academicYear") String academic);

        @GET("/jwxt/student-status/student-info/student-no-schedule")
        Call<JsonRootBean> getRepo1(@Query("weekly") String weekly, @Query("academicYear") String academic);
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
    //https://raw.githubusercontent.com/Gongzq5/TEST/master/no-schedule_academicYear=2018-1_weekly=9
        final String[] week = weekly.split(",");
        OkHttpClient build = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .addInterceptor(new AddCookiesInterceptor(activity))
                .build();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://uems.sysu.edu.cn")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(build)
                .build();


        GitHubService gitHubService = retrofit.create(GitHubService.class);
        Observable<JsonRootBean> observable = gitHubService.getRepo(week[0],academic);
        Call<JsonRootBean> call = gitHubService.getRepo1(week[0], academic);

        Log.i("Onclick4Data: ", call.request().url().toString());

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
                        int index = Integer.parseInt(week[0]);
                        courses(index).clear();
                        for (int i = 0; i < jsonRootBean.getData().size(); i++) {
                            Data data = jsonRootBean.getData().get(i);
                            Log.d("data info",week[0]+" :"+
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
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toasty.error(activity, "请连接校园网使用", Toast.LENGTH_LONG).show();
                            }
                        },3000);
                        Common.cookie = "";
                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                            @Override
                            public void onReceiveValue(Boolean value) {
                                Log.d("web view error","remove cookie value:" + value);
                            }
                        });
                    }
                    @Override
                    public void onComplete() {
                        Log.d("web view","on complete");
                        final int index = Integer.parseInt(week[0]);
                        int delay = 0;
                        if(index == 11) delay = 2000;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Utils.insert(activity,courses(index));
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

    public class AddCookiesInterceptor implements Interceptor {
        private Context context;
        public AddCookiesInterceptor(Context context) {
            super();
            this.context = context;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            final Request.Builder builder = chain.request().newBuilder();
            SharedPreferences sharedPreferences = context.getSharedPreferences("cookie", Context.MODE_PRIVATE);
            //使用已保存的cookie获取信息，可以省略登录步骤
            rx.Observable.just(sharedPreferences.getString("cookie", ""))
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String cookie) {
                            //添加cookie
                            cookie = Common.cookie;
                            Log.d("intercept cookies",cookie);
                            builder.addHeader("Cookie", cookie);
                        }
                    });
            return chain.proceed(builder.build());
        }
    }
}
