package com.example.money_transform;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class configure extends AppCompatActivity {
    float d_rate;
    float e_rate;
    float y_rate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        Intent intent  = getIntent();
        d_rate = intent.getFloatExtra("dollar_rate",0.0f);
        e_rate = intent.getFloatExtra("euro_rate",0.0f);
        y_rate = intent.getFloatExtra("yuan_rate",0.0f);
        EditText d = findViewById(R.id.d_rate);
        EditText e = findViewById(R.id.e_rate);
        EditText y = findViewById(R.id.y_rate);
        d.setHint("$: "+d_rate);
        e.setHint("€: "+e_rate);
        y.setHint("¥: "+y_rate);
    }

    public void save(View v){
        EditText d = findViewById(R.id.d_rate);
        EditText e = findViewById(R.id.e_rate);
        EditText y = findViewById(R.id.y_rate);
        try {
            d_rate = Float.parseFloat(d.getText().toString());
        }
        catch (Exception ex){

        }
        try {
            e_rate = Float.parseFloat(e.getText().toString());
        }
        catch (Exception ex){

        }
        try {
            y_rate = Float.parseFloat(y.getText().toString());
        }
        catch (Exception ex){

        }

        Intent main = new Intent(this,MainActivity.class);
        main.putExtra("d_rate",d_rate);
        main.putExtra("e_rate",e_rate);
        main.putExtra("y_rate",y_rate);
        setResult(1,main);

        SharedPreferences rate = getSharedPreferences("myrate", Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = rate.edit();
        editor.putFloat("dollar_rate",d_rate);
        editor.putFloat("euro_rate",e_rate);
        editor.putFloat("yuan_rate",y_rate);
        editor.apply();

        finish();
    }
}
