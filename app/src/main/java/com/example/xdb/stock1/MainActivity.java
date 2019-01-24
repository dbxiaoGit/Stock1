package com.example.xdb.stock1;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xdb.stock1.com.example.xdb.common.ChaoDuanRequest;
import com.example.xdb.stock1.com.example.xdb.common.RandomNumber;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SoundPool spPool;//声明一个SoundPool
    private int audio;//声明一个变量 || 可以理解成用来储存歌曲的变量
    private int global;//声明一个变量 || 可以理解成用来储存歌曲的变量
    private int msg;//声明一个变量 || 可以理解成用来储存歌曲的变量
    private int shake;//声明一个变量 || 可以理解成用来储存歌曲的变量
    private int system;//声明一个变量 || 可以理解成用来储存歌曲的变量
    private int tweet;//声明一个变量 || 可以理解成用来储存歌曲的变量
    private int currStreamId;// 当前正播放的streamId
    private EditText editText;
    private Button button;
    private TextView textView;
    private String stockCode;
    private String stockcName;
    private StringBuilder stockData;
    private NotificationCompat.Builder nb;
    private NotificationManager notiManager;
    private Notification notification;
    private boolean monitor_status;
    private boolean is_monitor_button_clicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        is_monitor_button_clicked = false;
        notiManager =   (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
/*        notification = new NotificationCompat.Builder(this,"default")
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(stockcName + "动了")
                .setContentText(stockcName + "动了")
                .setWhen(System.currentTimeMillis())
                .build()
        ;*/
        initSoundPool(); // 初始化声音池的方法
/*        try {
            Thread.sleep(5000l);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        playSound(system, 3);*/
        editText = (EditText) findViewById(R.id.sotck_code);
        button = (Button) findViewById(R.id.start_monitor);
        textView = (TextView) findViewById(R.id.text1);


        button.setOnClickListener(this);
    }

    // 初始化声音池的方法
    public void initSoundPool() {
        //下面一行写法被api21废弃了
        //spPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0); // 创建SoundPool对象
        spPool = new SoundPool.Builder().setMaxStreams(6).build();
        audio = spPool.load(this, R.raw.audio, 1);
        global = spPool.load(this, R.raw.global, 1);
        msg = spPool.load(this, R.raw.msg, 1);
        shake = spPool.load(this, R.raw.shake, 1);
        system = spPool.load(this, R.raw.system, 1);
        tweet = spPool.load(this, R.raw.tweet, 1);

    }

    // 播放声音的方法
    public void playSound(int sound, int loop) { // 获取AudioManager引用
        AudioManager am = (AudioManager) this
                .getSystemService(Context.AUDIO_SERVICE);
        // 获取当前音量
        float streamVolumeCurrent = am
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        // 获取系统最大音量
        float streamVolumeMax = am
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 计算得到播放音量
        float volume = streamVolumeCurrent / streamVolumeMax;
        // 调用SoundPool的play方法来播放声音文件
        currStreamId = spPool.play(sound, volume, volume, 1, loop, 1.0f);
    }

    public static void main(String[] a) {
        TreeSet t = new TreeSet();
        t.add("aaa");
        t.add("bbb");
        t.add("111");
        t.add("000");
        Iterator i = t.iterator();
        while (i.hasNext()) {
            System.out.println(i.next());
        }
    }


    //private boolean isTrueOrfalse = false;
    @Override
    public void onClick(View v) {
        Log.d("onClick", "into onClick");
        stockCode = editText.getText().toString();
        Log.d("stockCode0", stockCode);
        if (stockCode.startsWith("6")) {
            stockCode = "sh" + stockCode;
        } else {
            stockCode = "sz" + stockCode;
        }
        Log.d("stockCode1", stockCode);
        if (!is_monitor_button_clicked){
            button.setText("stop");
            monitor_status = true;
            is_monitor_button_clicked = !is_monitor_button_clicked;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (monitor_status) {
                            Log.d("while", "into while");
                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl("http://web.sqt.gtimg.cn/")
                                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                                    .build();
                            final ChaoDuanRequest request = retrofit.create(ChaoDuanRequest.class);
                            Call<ResponseBody> call = request.getStockData(stockCode, RandomNumber.getRandomNumber(16));

                            call.enqueue(new Callback<ResponseBody>() {
                                //请求成功时回调
                                @Override
                                public void onResponse(Call<ResponseBody>  call, Response<ResponseBody> response) {
                                    //请求处理,输出结果
                                    String res = null;
                                    try {
                                        res = response.body().string();
                                    } catch (IOException e) {
                                        Log.e("error","onResponse",e);
                                    }
                                    Log.d("res",  res);
                                    String[] plate_date_strikes = res.split("~");
                                    stockcName = plate_date_strikes[1];
                                    Log.d("stockcName",  stockcName);
                                    String plate_date_strike_trades_str = "";
                                    for (String plate_date_strike : plate_date_strikes) {
                                        if (plate_date_strike.contains("|")) {
                                            plate_date_strike_trades_str = plate_date_strike;
                                            break;
                                        }
                                    }
                                    Log.d("plate_date_strike_trades_str", plate_date_strike_trades_str);
                                    plate_date_strike_trades_str = plate_date_strike_trades_str.replaceAll("S", "卖出");
                                    plate_date_strike_trades_str = plate_date_strike_trades_str.replaceAll("B", "买入");
                                    Log.d("plate_date_strike_trades_str", plate_date_strike_trades_str);
                                    String[] plate_date_strike_trades_arr = plate_date_strike_trades_str.split("\\|");
                                    Log.d("plate_date_strike_trades_arr.length", String.valueOf(plate_date_strike_trades_arr.length));
                                    for (String plate_date_strike_trades_data : plate_date_strike_trades_arr) {
                                        Log.d("plate_date_strike_trades_data", plate_date_strike_trades_data);
                                    }
                                    stockData = new StringBuilder();
                                    ArrayList<BigDecimal> buyTrades = new ArrayList<BigDecimal>();
                                    for (String plate_date_strike_trades_data : plate_date_strike_trades_arr) {
                                        Log.d("plate_date_strike_trades_data",  plate_date_strike_trades_data);
                                        stockData = stockData.append(plate_date_strike_trades_data.split("/")[0]);
                                        stockData = stockData.append(plate_date_strike_trades_data.split("/")[3]);
                                        String tradeNum = plate_date_strike_trades_data.split("/")[4];
                                        BigDecimal bigDecimal = new BigDecimal(tradeNum);
                                        BigDecimal bigDecimal_res = bigDecimal.divide(new BigDecimal(10000));
                                        stockData = stockData.append(bigDecimal_res);
                                        stockData = stockData.append("万元\r\n");
                                        if (plate_date_strike_trades_data.contains("买入")) {
                                            buyTrades.add(bigDecimal);
                                        }
                                    }
                                    if (Collections.max(buyTrades).intValue() > 800000) {
                                        Toast.makeText(MainActivity.this, stockcName + "动了", Toast.LENGTH_SHORT);
                                        //tongzhi
                                        notification = new NotificationCompat.Builder(MainActivity.this,"default")
                                                .setSmallIcon(R.drawable.icon)
                                                .setContentTitle(stockcName + "动了")
                                                .setContentText(stockcName + "动了")
                                                .setWhen(System.currentTimeMillis())
                                                .build();
                                        notiManager.notify(1,notification);
                                        playSound(system, 2);
                                        monitor_status = false;
                                    }
                                    textView.setText(stockData.toString());
                                }

                                //请求失败时候的回调
                                @Override
                                public void onFailure(Call call, Throwable throwable) {
                                    Log.e("error", "连接失败");
                                    Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT);
                                }
                            });

                            try {
                                Thread.sleep(3000L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
        }else {
            button.setText("start");
            monitor_status = false;
            is_monitor_button_clicked = !is_monitor_button_clicked;
        }

   /*     while (monitor_status) {
            Log.d("while", "into while");
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://web.sqt.gtimg.cn/")
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            final ChaoDuanRequest request = retrofit.create(ChaoDuanRequest.class);
            Call<ResponseBody> call = request.getStockData(stockCode, RandomNumber.getRandomNumber(16));

            call.enqueue(new Callback<ResponseBody>() {
                //请求成功时回调
                @Override
                public void onResponse(Call<ResponseBody>  call, Response<ResponseBody> response) {
                    //请求处理,输出结果
                    String res = null;
                    try {
                        res = response.body().string();
                    } catch (IOException e) {
                        Log.e("error","onResponse",e);
                    }
                    Log.d("onResponse", "res:" + res);
                    String[] plate_date_strikes = res.split("~");
                    stockcName = plate_date_strikes[1];
                    Log.d("onResponse", "stockcName:" + stockcName);
                    String plate_date_strike_trades_str = "";
                    for (String plate_date_strike : plate_date_strikes) {
                        if (plate_date_strike.contains("|")) {
                            plate_date_strike_trades_str = plate_date_strike;
                            break;
                        }
                    }
                    Log.d("onResponse", "plate_date_strike_trades_str:" + plate_date_strike_trades_str);
                    plate_date_strike_trades_str.replaceAll("S", "卖出");
                    plate_date_strike_trades_str.replaceAll("B", "买入");
                    String[] plate_date_strike_trades_arr = plate_date_strike_trades_str.split("|");
                    stockData = new StringBuilder();
                    ArrayList<BigDecimal> buyTrades = new ArrayList<BigDecimal>();
                    for (String plate_date_strike_trades_data : plate_date_strike_trades_arr) {
                        stockData = stockData.append(plate_date_strike_trades_data.split("/")[0]);
                        stockData = stockData.append(plate_date_strike_trades_data.split("/")[3]);
                        String tradeNum = plate_date_strike_trades_data.split("/")[4];
                        BigDecimal bigDecimal = new BigDecimal(tradeNum);
                        BigDecimal bigDecimal_res = bigDecimal.divide(new BigDecimal(10000));
                        stockData = stockData.append(bigDecimal_res);
                        stockData = stockData.append("万元\r\n");
                        if (plate_date_strike_trades_data.contains("买入")) {
                            buyTrades.add(bigDecimal);
                        }
                    }
                    if (Collections.max(buyTrades).intValue() > 800000) {
                        Toast.makeText(MainActivity.this, stockcName + "动了", Toast.LENGTH_SHORT);
                        //tongzhi
                        notification = new NotificationCompat.Builder(MainActivity.this,"default")
                                .setSmallIcon(R.drawable.icon)
                                .setContentTitle(stockcName + "动了")
                                .setContentText(stockcName + "动了")
                                .setWhen(System.currentTimeMillis())
                                .build();
                        notiManager.notify(1,notification);
                        playSound(system, 2);
                        monitor_status = false;
                    }
                    textView.setText(stockData.toString());
                }

                //请求失败时候的回调
                @Override
                public void onFailure(Call call, Throwable throwable) {
                    Log.e("error", "连接失败");
                    Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT);
                }
            });

            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        */

    }

}
