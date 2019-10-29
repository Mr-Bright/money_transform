package com.example.money_transform;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.jsoup.nodes.Document.OutputSettings.Syntax.html;

public class MainActivity extends AppCompatActivity implements Runnable{
    static float dollar_rate;
    static float euro_rate;
    static float yuan_rate;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SharedPreferences rate = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        dollar_rate = rate.getFloat("dollar_rate",7.1f);
        euro_rate = rate.getFloat("euro_rate",8.1f );
        yuan_rate = rate.getFloat("yuan_rate",1.0f);



        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what==5) {
                    //String str = (String)msg.obj;
                    //Document document = Jsoup.parse(str);
                    Document document = (Document)msg.obj;
                    Elements element = document.select("table").select("tr");
                    for(int i =0;i<element.size()-1;i++){
                        Elements tds = element.get(i).select("td");
                        for(int j=0;j<tds.size()-1;j++){
                            String t = tds.get(j).text();
                            if (t.equals("美元")){
                                dollar_rate = Float.parseFloat(tds.get(j+1).text())/100;
                            }
                            else if (t.equals("欧元")){
                                euro_rate = Float.parseFloat(tds.get(j+1).text())/100;
                            }
                            else if (t.equals("日元")){
                                yuan_rate = Float.parseFloat(tds.get(j+1).text())/100;
                            }

                        }
                    }
                }
                super.handleMessage(msg);
            }
        };

        Thread t = new Thread(this);
        t.start();

    }

    public void click(View v){
        int id = v.getId();
        float money = 0;
        TextView textView = findViewById(R.id.textView);
        EditText editText = findViewById(R.id.editText);
        try {
            money = Float.parseFloat(editText.getText().toString());
        }
        catch (Exception e){
            textView.setText("");
            textView.setHint("worry!!!");
        }

        switch (id){
            case R.id.dollar:
                textView.setText(String.format("%.2f",money*dollar_rate)+"$");
                break;
            case R.id.euro:
                textView.setText(String.format("%.2f",money*euro_rate)+"€");
                break;
            case R.id.yuan:
                textView.setText(String.format("%.2f",money*yuan_rate)+"¥");
                break;
                default:
                    break;
        }
    }

    public void configure(View v){
        Intent intent = new Intent(this,configure.class);
        intent.putExtra("dollar_rate",dollar_rate);
        intent.putExtra("euro_rate",euro_rate);
        intent.putExtra("yuan_rate",yuan_rate);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==1&resultCode==1){
            dollar_rate = data.getFloatExtra("d_rate",7.1f);
            euro_rate = data.getFloatExtra("e_rate",8.1f);
            yuan_rate = data.getFloatExtra("y_rate",1.0f);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.reset){
            Thread t = new Thread(this);
            t.start();
        }
        else if(item.getItemId()==R.id.clean){
            SharedPreferences rate = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            dollar_rate = rate.getFloat("dollar_rate",7.1f);
            euro_rate = rate.getFloat("euro_rate",8.1f );
            yuan_rate = rate.getFloat("yuan_rate",1.0f);
        }
        else if (item.getItemId()==R.id.end){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void run() {
        /*
        URL url = null;
        String html = "NULL";
        try{
            url = new URL("http://www.usd-cny.com/bankofchina.htm");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            InputStream in = http.getInputStream();
            html = inputStream2String(in);
            Log.i("html",html);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        */
        Document document=null;
        try {
            document = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
        }
        catch (Exception e){

        }

        Message msg = handler.obtainMessage(5);
        msg.obj = document;
        handler.sendMessage(msg);
    }

    private String inputStream2String(InputStream inputStream) throws IOException {
        final int buffersize = 1024;
        final char[] buffer = new char[buffersize];
        final StringBuilder out = new StringBuilder();
        Reader in  = new InputStreamReader(inputStream,"gb2312");
        while (true){
            int rsz = in.read(buffer,0,buffer.length);
            if (rsz<0)
                break;
            out.append(buffer,0,rsz);
        }
        return out.toString();
    }
}
