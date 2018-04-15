package com.example.lenovo.mweather;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;


public class MainActivity extends Activity implements View.OnClickListener{

    private ImageView UpdateBtn;

    private TextView cityT,timeT,humidityT,
            weekT,pmDataT,pmQualityT,temperatureT,
            climateT,windT,cityNameT;
    private ImageView weatherStateImg,pmStateImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UpdateBtn = (ImageView) findViewById(R.id.title_city_update);
        UpdateBtn.setOnClickListener(this);

        initView();//初始化控件

        //检查网络连接状态
        if (CheckNet.getNetState(this)==CheckNet.NET_NONE)
        {
            Log.d("MWeather", "网络不通");
            Toast.makeText(MainActivity.this,"网络不通",Toast.LENGTH_LONG).show();
        }else
        {
            Log.d("MWeather","网络ok");
            Toast.makeText(MainActivity.this,"网络ok",Toast.LENGTH_LONG).show();
            getWeatherDatafromNet("101010100");
        }
    }


    private void getWeatherDatafromNet(String cityCode)
    {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey="+cityCode;
        Log.d("Address:", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
            try{
                URL url = new URL(address);
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(8000);
                urlConnection.setReadTimeout(8000);
                InputStream in = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuffer sb = new StringBuffer();
                String str;
                while((str = reader.readLine())!=null)
                {
                    sb.append(str);
                    Log.d("data from url",str);
                }
                String response = sb.toString();
                Log.d("response",response);
                parseXML(response);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            }
        }).start();
    }

    private void parseXML(String xmlData)
    {
        int fengliCount=0;
        int fengxiangCount=0;
        int dataCount=0;
        int highCount=0;
        int lowCount=0;
        int typeCount=0;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));

            int eventType = xmlPullParser.getEventType();
            Log.d("MWeather","start parse xml");

            while(eventType!=xmlPullParser.END_DOCUMENT)
            {
                switch(eventType)
                {
                    //文档开始位置
                    case XmlPullParser.START_DOCUMENT:
                        Log.d("parse","start doc");
                        break;
                    //标签元素开始位置
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("city"))
                        {
                            eventType = xmlPullParser.next();
                            Log.d("city",xmlPullParser.getText());
                        }else if (xmlPullParser.getName().equals("updatetime"))
                        {
                            eventType = xmlPullParser.next();
                            Log.d("updatetime",xmlPullParser.getText());
                        }else if (xmlPullParser.getName().equals("wendu"))
                        {
                            eventType = xmlPullParser.next();
                            Log.d("wendu",xmlPullParser.getText());
                        }else if (xmlPullParser.getName().equals("fengli"))
                        {
                            eventType = xmlPullParser.next();
                            Log.d("fengli",xmlPullParser.getText());
                            fengliCount++;
                        }else if (xmlPullParser.getName().equals("shidu"))
                        {
                            eventType = xmlPullParser.next();
                            Log.d("shidu",xmlPullParser.getText());
                        }else if (xmlPullParser.getName().equals("fengxiang"))
                        {
                            eventType = xmlPullParser.next();
                            Log.d("fengxiang",xmlPullParser.getText());
                            fengxiangCount++;
                        }else if (xmlPullParser.getName().equals("pm25"))
                        {
                            eventType = xmlPullParser.next();
                            Log.d("pm25",xmlPullParser.getText());
                        }else if (xmlPullParser.getName().equals("quality"))
                        {
                            eventType = xmlPullParser.next();
                            Log.d("quality",xmlPullParser.getText());
                        }else if (xmlPullParser.getName().equals("data"))
                        {
                            eventType = xmlPullParser.next();
                            Log.d("data",xmlPullParser.getText());
                            dataCount++;
                        }else if (xmlPullParser.getName().equals("high"))
                        {
                            eventType = xmlPullParser.next();
                            Log.d("high",xmlPullParser.getText());
                            highCount++;
                        }
                        else if (xmlPullParser.getName().equals("low"))
                        {
                            eventType = xmlPullParser.next();
                            Log.d("low",xmlPullParser.getText());
                            lowCount++;
                        }else if (xmlPullParser.getName().equals("type"))
                        {
                            eventType = xmlPullParser.next();
                            Log.d("type",xmlPullParser.getText());
                            typeCount++;
                        }
                        break;
                    //标签结束位置?
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType=xmlPullParser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //初始化控件
    private void initView()
    {
        //title
        cityNameT = (TextView) findViewById(R.id.title_city_name);

        //today weather
        cityT = (TextView) findViewById(R.id.todayinfo1_cityName);
        timeT = (TextView) findViewById(R.id.todayinfo1_updateTime);
        humidityT = (TextView) findViewById(R.id.todayinfo1_humidity);
        weekT = (TextView) findViewById(R.id.todayinfo2_week);
        pmDataT = (TextView) findViewById(R.id.todayinfo1_pm25);
        pmQualityT = (TextView) findViewById(R.id.todayinfo1_pm25status);
        temperatureT = (TextView) findViewById(R.id.todayonfo_temperature);
        climateT = (TextView) findViewById(R.id.todayinfo2_weatherState);
        windT = (TextView) findViewById(R.id.todayinfo2_wind);

        weatherStateImg = (ImageView) findViewById(R.id.weatherStatusImg);
        pmStateImg = (ImageView) findViewById(R.id.todayinfo1_pm25img);

        cityNameT.setText("N/A");

        cityT.setText("N/A");
        timeT.setText("N/A");
        humidityT.setText("N/A");
        weekT.setText("N/A");
        pmDataT.setText("N/A");
        pmQualityT.setText("N/A");
        temperatureT.setText("N/A");
        climateT.setText("N/A");
        windT.setText("N/A");


    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.title_city_update)
        {
            getWeatherDatafromNet("101010100");
        }
    }
}
