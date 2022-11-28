package com.example.oneday_cafe_pos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ExactCalculationActivity extends Activity implements View.OnClickListener{

    private DBManager dbm;
    private TextView date;
    private ListView incomeList;
    private String selectedDate;
    private ArrayList<Integer> drinkPKList;

    private ArrayList<SoldMenu> soldMenuList;
    private ArrayList<SoldMenu> couponMenuList;
    private ExactCalculationAdapter exactCalculationAdapter;

    private TextView totalCupCount;
    private TextView totalIncome;
    private int _totalCupCount;
    private int _totalIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exact_calculation);
        Init();
    }

    private void Init(){

        dbm = DBManager._getInstance(this,null,1);

        Date o_date = new Date();
        SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("yyyy-MM-dd");          // db와 날짜 포맷을 맞춘다.
        selectedDate = simpleDateFormatter.format(o_date);
        // 첫 실행시 오늘 날짜로 보여준다.

        date = (TextView)findViewById(R.id.selected_date);
        incomeList = (ListView)findViewById(R.id.income_list);

        date.setText(selectedDate);
        date.setOnClickListener(this);

        drinkPKList = dbm.getWholePK();

        soldMenuList = dbm.getSoldMenuList(selectedDate, drinkPKList);
        couponMenuList = dbm.getCouponMenuList(selectedDate, drinkPKList);
        soldMenuList.addAll(couponMenuList);

        exactCalculationAdapter = new ExactCalculationAdapter(this, R.layout.income_element,soldMenuList);
        incomeList.setAdapter(exactCalculationAdapter);


        totalCupCount = (TextView)findViewById(R.id.total_sold_count);
        totalIncome = (TextView)findViewById(R.id.total_income);

        updateTotalCupNIncome();
    }

    public void onClick(View v){

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,AlertDialog.THEME_HOLO_LIGHT,
                                            new DatePickerDialog.OnDateSetListener() {
            @Override                           //DatePicker로 날짜 변경 시,
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                SimpleDateFormat simpleDateFormatter = new SimpleDateFormat();
                c.set(year, month, dayOfMonth);
                simpleDateFormatter.applyPattern("yyyy-MM-dd");
                selectedDate = simpleDateFormatter.format(c.getTime());
                date.setText(selectedDate);

                soldMenuList = dbm.getSoldMenuList(selectedDate, drinkPKList);
                couponMenuList = dbm.getCouponMenuList(selectedDate, drinkPKList);
                soldMenuList.addAll(couponMenuList);

                exactCalculationAdapter.setSoldMenuList(soldMenuList);
                exactCalculationAdapter.notifyDataSetChanged();

                updateTotalCupNIncome();

            }
        }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setCalendarViewShown(false);
        datePickerDialog.show();
    }

    public void updateTotalCupNIncome(){

        _totalIncome = 0;
        _totalCupCount = 0;

        for(int i = 0; i < soldMenuList.size(); i++){
            _totalCupCount += soldMenuList.get(i).getSaledCount();
            _totalIncome += soldMenuList.get(i).getTotalPrice();
        }

        totalCupCount.setText("총 " + _totalCupCount + "잔");
        totalIncome.setText("총 " + _totalIncome + "원");
    }
}