package com.example.oneday_cafe_pos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ShowRecordActivity extends Activity {

    DBManager dbm;
    ListView recordList;
    MenuOrderAdapter soldApdater;
    ArrayList<OrderedItem> soldData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_record);
        init();
    }

    private void init(){
        dbm = DBManager._getInstance(this, null, 1);
        recordList = (ListView)findViewById(R.id.sold_record);

        Date o_date = new Date();
        SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("yyyy-MM-dd");          // db와 날짜 포맷을 맞춘다.
        String Today = simpleDateFormatter.format(o_date);
        soldData = dbm.getTodaySoldRecord(Today);

        soldApdater = new MenuOrderAdapter(this, soldData, R.layout.order_element);
        recordList.setAdapter(soldApdater);

        SwipeDismissListViewTouchListener touchListener =                                   // Swipe 액션 리스너
                new SwipeDismissListViewTouchListener(
                        recordList,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, final int[] reverseSortedPositions) {
                                AlertDialog.Builder dlg = new AlertDialog.Builder(ShowRecordActivity.this, AlertDialog.THEME_HOLO_LIGHT);

                                dlg.setMessage("기록을 지우시겠습니까?");
                                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        for (int position : reverseSortedPositions) {
                                            dbm.deleteSoldRecord(soldData.get(position).getSoldID());
                                            soldData.remove(position);
                                        }
                                        soldApdater.notifyDataSetChanged();
                                        Toast.makeText(ShowRecordActivity.this, "기록이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                });
                                dlg.setCancelable(false);                           // dialog 이외의 영역 터치시 사라지지 않게 함
                                dlg.show();

                            }
                        });

        recordList.setOnTouchListener(touchListener);
        recordList.setOnScrollListener(touchListener.makeScrollListener());

    }
}