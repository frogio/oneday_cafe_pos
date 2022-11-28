package com.example.oneday_cafe_pos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class SelectOrderMenuActivity extends Activity implements View.OnClickListener {

    private LinearLayout[] Select;
    private static final int SELECT_RECEIVE_MENU = 0;
    private static final int SELECT_SHOW_RECORD = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_order);

        Select = new LinearLayout[2];

        Select[SELECT_RECEIVE_MENU] = (LinearLayout)findViewById(R.id.receive_menu);
        Select[SELECT_SHOW_RECORD] = (LinearLayout)findViewById(R.id.show_record);

        Select[SELECT_RECEIVE_MENU].setOnClickListener(this);
        Select[SELECT_SHOW_RECORD].setOnClickListener(this);

    }

    public void onClick(View v){
        if(v.getId() == R.id.receive_menu){
            Intent i = new Intent(this, ManageOrderActivity.class);
            startActivity(i);
        }
        else if(v.getId() == R.id.show_record){
            Intent i = new Intent(this, ShowRecordActivity.class);
            startActivity(i);
        }

    }

}