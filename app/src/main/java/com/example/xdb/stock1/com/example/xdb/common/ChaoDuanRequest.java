package com.example.xdb.stock1.com.example.xdb.common;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChaoDuanRequest {
    /**
     * 参考 https://blog.csdn.net/carson_ho/article/details/73732076
     * method 表示请求的方法，区分大小写，retrofit 不会做处理
     * path表示路径
     * hasBody表示是否有请求体
     * url = 'http://web.sqt.gtimg.cn/q=sh601208?r=0.9884275211413494'
     * https://blog.csdn.net/carson_ho/article/details/73732076
     */
    //private String url;
   // private String stockCode;

    @Headers("User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36")
    @HTTP(method = "GET", path = "/q={stock_code}", hasBody = false)
    Call<ResponseBody> getStockData(@Path("stock_code") String stock_code, @Query("r") String r);

    @POST("csd-credit-da/http")
    Call<JSONObject> credit(@Body JSONObject param);

}
