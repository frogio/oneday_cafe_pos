package com.example.oneday_cafe_pos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.oneday_cafe_pos.R;

public class MainActivity extends Activity implements View.OnClickListener{

    private LinearLayout [] Select;
    private static final int SELECT_MMG_MENU = 0;
    private static final int SELECT_CUR_ORDER = 1;
    private static final int SELECT_CALCUL_INCOME = 2;
    private DBManager dbm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Init();

    }

    private void Init() {
        Select = new LinearLayout[3];

        dbm = DBManager._getInstance(this, null, 1 );
        dbm.getWritableDatabase();
        dbm.close();

        Select[SELECT_MMG_MENU] = (LinearLayout)findViewById(R.id.mmg_menu);
        Select[SELECT_CUR_ORDER] = (LinearLayout)findViewById(R.id.cur_order);
        Select[SELECT_CALCUL_INCOME] = (LinearLayout)findViewById(R.id.calcul_income);

        Select[SELECT_MMG_MENU].setOnClickListener(this);
        Select[SELECT_CUR_ORDER].setOnClickListener(this);
        Select[SELECT_CALCUL_INCOME].setOnClickListener(this);


    }
    public void onClick(View v){

        if(v.getId() == R.id.mmg_menu) {
            Intent i = new Intent(this, ManageMenuActivity.class);
            startActivity(i);
//            Toast.makeText(this, "메뉴 관리",Toast.LENGTH_SHORT).show();

        }
        else if(v.getId() == R.id.cur_order) {
            Intent i = new Intent(this, SelectOrderMenuActivity.class);
            startActivity(i);

        }
        else if(v.getId() == R.id.calcul_income){
            Intent i = new Intent(this, ExactCalculationActivity.class);
            startActivity(i);

        }

    }

}