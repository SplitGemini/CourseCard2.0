package com.example.hasee.coursecard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.functions.Action1;

import static android.content.ContentValues.TAG;

public class MyWebViewClient extends WebViewClient {
    String url;
    WebView view;
    Activity activity;
    TextView textView;
    int flag = 0;
    private List<DBCourse> courses = new ArrayList<>();
    public MyWebViewClient(Activity activity)
    {
        //  this.textView =textView;
        this.activity =activity;
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    public void onPageFinished(WebView view, String url) {
        CookieManager cookieManager = CookieManager.getInstance();
        String CookieStr = cookieManager.getCookie(url);
        Common.cookie = CookieStr;
        if(CookieStr.contains("LYSESSIONID") && CookieStr.contains("user")) {
            Log.d("msg", CookieStr);
            String[] weeklys = {"1,10", "11,18"};
            for (String i: weeklys)
                Onclick4Data(i,Common.academic);
        }
    }
    //githubapi接口
//    https://uems.sysu.edu.cn/jwxt/student-status/student-info/student-no-schedule?academicYear=2017-1&weekly=7
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
    public void Onclick4Data(String weekly, final String academic) {
//        https://raw.githubusercontent.com/Gongzq5/TEST/master/no-schedule_academicYear=2018-1_weekly=9
        Log.d("HTTPS","Onclick");
        final String[] week = weekly.split(",");
        OkHttpClient build = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .addInterceptor(new AddCookiesInterceptor(activity))
                .build();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(" https://uems.sysu.edu.cn")
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
                        courses.clear();
                        for (int i = 0; i < jsonRootBean.getData().size(); i++) {
                            Data data = jsonRootBean.getData().get(i);
                            for (int j = Integer.parseInt(week[0]);j < Integer.parseInt(week[1])+1; j++) {
                                if (!data.getMonday().equals("null") && data.getSection() % 2 != 0) {
                                    String[] temp = InfoConvert(data.getMonday());
                                    courses.add(new DBCourse(academic, "星期一", temp[0], temp[1], temp[2], (data.getSection() + 1) / 2, j));
                                }
                                if (!data.getTuesday().equals("null") && data.getSection() % 2 > 0) {
                                    String[] temp = InfoConvert(data.getTuesday());
                                    courses.add(new DBCourse(academic, "星期二", temp[0], temp[1], temp[2], (data.getSection() + 1) / 2, j));
                                }
                                if (!data.getWednesday().equals("null") && data.getSection() % 2 > 0) {
                                    String[] temp = InfoConvert(data.getWednesday());
                                    courses.add(new DBCourse(academic, "星期三", temp[0], temp[1], temp[2], (data.getSection() + 1) / 2, j));
                                }
                                if (!data.getThursday().equals("null") && data.getSection() % 2 > 0) {
                                    String[] temp = InfoConvert(data.getThursday());
                                    courses.add(new DBCourse(academic, "星期四", temp[0], temp[1], temp[2], (data.getSection() + 1) / 2, j));
                                }
                                if (!data.getFriday().equals("null") && data.getSection() % 2 > 0) {
                                    String[] temp = InfoConvert(data.getFriday());
                                    courses.add(new DBCourse(academic, "星期五", temp[0], temp[1], temp[2], (data.getSection() + 1) / 2, j));
                                }
                            }
                        }
                        Log.d("class", Integer.toString(courses.size()));
                    }
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();

                        Toasty.error(activity, "获取数据失败", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onComplete() {
                        Utils.insert(activity,courses);
                        Common.statecode = "OK";
//                        flag++;
//                        if (flag == 2) {
//                            Intent intent = new Intent();
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                            intent.setClass(activity, ListActivity.class);
//                            flag = 0;
//                            activity.startActivity(intent);
//                        }
                    }
                });
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
            //最近在学习RxJava,这里用了RxJava的相关API大家可以忽略,用自己逻辑实现即可
            rx.Observable.just(sharedPreferences.getString("cookie", ""))
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String cookie) {
                            //添加cookie
                            cookie = Common.cookie;
                            Log.d("cookies",cookie);
                            builder.addHeader("Cookie", cookie);
                        }
                    });
            return chain.proceed(builder.build());
        }
    }
}
