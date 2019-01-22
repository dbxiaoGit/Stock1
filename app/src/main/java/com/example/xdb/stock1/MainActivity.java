package com.example.xdb.stock1;

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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SoundPool spPool;//声明一个SoundPool
    private int audio;//声明一个变量 || 可以理解成用来储存歌曲的变量
    private int global;//声明一个变量 || 可以理解成用来储存歌曲的变量
    private int msg;//声明一个变量 || 可以理解成用来储存歌曲的变量
    private int shake;//声明一个变量 || 可以理解成用来储存歌曲的变量
    private int system;//声明一个变量 || 可以理解成用来储存歌曲的变量
    private int tweet;//声明一个变量 || 可以理解成用来储存歌曲的变量
    int currStreamId;// 当前正播放的streamId
    private EditText editText;
    private Button button;
    private TextView textView;
    private String stockCode;
    private String stockcName;
    private StringBuilder stockData;
    private NotificationCompat.Builder nb;
    private boolean is_finished;
    private boolean is_stop_clicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        is_finished = false;
        is_stop_clicked = false;
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
        stockCode = editText.getText().toString();
        if (stockCode.startsWith("6")) {
            stockCode = "sh" + stockCode;
        } else {
            stockCode = "sz" + stockCode;
        }
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

    @Override
    public void onClick(View v) {
        Log.d("stock1","into onClick");
        is_stop_clicked = false;
        button.setText("stop!");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_stop_clicked = true;
                button.setText("开始监控");
                button.setOnClickListener(MainActivity.this);
            }
        });
        while (!is_finished && !is_stop_clicked) {
            Log.d("stock1","into while");
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://web.sqt.gtimg.cn/") // 设置网络请求的Url地址
                    .build();
            // 创建 网络请求接口 的实例
            ChaoDuanRequest request = retrofit.create(ChaoDuanRequest.class);
            //对 发送请求 进行封装
            Call call = request.getStockData(stockCode, RandomNumber.getRandomNumber(16));
            call.enqueue(new Callback() {
                //请求成功时回调
                @Override
                public void onResponse(Call call, Response response) {
                    //请求处理,输出结果
                    String res = response.body().toString();
                    Log.d("stock1","res:" + res);
                    String[] plate_date_strikes = res.split("~");
                    stockcName = plate_date_strikes[1];
                    Log.d("stock1","stockcName:" + stockcName);
                    String plate_date_strike_trades_str = "";
                    for (String plate_date_strike : plate_date_strikes) {
                        if (plate_date_strike.contains("|")) {
                            plate_date_strike_trades_str = plate_date_strike;
                            break;
                        }
                    }
                    Log.d("stock1","plate_date_strike_trades_str:" + plate_date_strike_trades_str);
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
                        playSound(system, 2);
                        is_finished = true;
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

}
