package com.example.oneday_cafe_pos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.oneday_cafe_pos.R;

public class MainActivity extends Activity implements View.OnClickListener{

    private LinearLayout [] Select;
    private static final int SELECT_MMG_MENU        = 0;
    private static final int SELECT_CUR_ORDER       = 1;
    private static final int SELECT_CALCUL_INCOME   = 2;
    private static final int SELECT_CONN_NETWORK    = 3;

    private View requestConnect;

    private DBManager dbm;
    private NetworkManager nm;                              // OrderList 프로그램과 연결되는 네트워크 매니져.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();

    }

    private void Init() {
        Select = new LinearLayout[4];

        dbm = DBManager._getInstance(this, null, 1 );
        dbm.getWritableDatabase();
        dbm.close();

        Select[SELECT_MMG_MENU] = (LinearLayout)findViewById(R.id.mmg_menu);
        Select[SELECT_CUR_ORDER] = (LinearLayout)findViewById(R.id.cur_order);
        Select[SELECT_CALCUL_INCOME] = (LinearLayout)findViewById(R.id.calcul_income);
        Select[SELECT_CONN_NETWORK] = (LinearLayout)findViewById(R.id.conn_network);


        Select[SELECT_MMG_MENU].setOnClickListener(this);
        Select[SELECT_CUR_ORDER].setOnClickListener(this);
        Select[SELECT_CALCUL_INCOME].setOnClickListener(this);
            Select[SELECT_CONN_NETWORK].setOnClickListener(this);


    }
    public void onClick(View v){
        Intent i;
        switch(v.getId()){
            case R.id.mmg_menu:
                i = new Intent(this, ManageMenuActivity.class);
                startActivity(i);
                break;
            case R.id.cur_order:
                i = new Intent(this, SelectOrderMenuActivity.class);
                startActivity(i);
                break;
            case R.id.calcul_income:
                i = new Intent(this, ExactCalculationActivity.class);
                startActivity(i);
                break;
            case R.id.conn_network:
                requestConnect = View.inflate(this, R.layout.req_conn_network, null);  // Dialog View Inflate
                AlertDialog.Builder dlg = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);

                dlg.setView(requestConnect);
                dlg.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "연결을 취소하셨습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

                dlg.setNegativeButton("연결", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "PC와의 연동을 시작합니다.", Toast.LENGTH_SHORT).show();

                        nm = NetworkManager._getInstance(MainActivity.this);
                        nm.StartNetwork();
                        //nm.SendJSON("Connect to OrderListProgram...");

                    }
                });

                dlg.setCancelable(false);                           // dialog 이외의 영역 터치시 사라지지 않게 함
                dlg.show();
                break;
        }
        /*Toast.makeText(this, "Test",Toast.LENGTH_SHORT).show();*/

        }
}