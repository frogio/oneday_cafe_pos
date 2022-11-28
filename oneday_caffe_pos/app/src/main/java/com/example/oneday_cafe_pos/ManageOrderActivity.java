package com.example.oneday_cafe_pos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

public class ManageOrderActivity extends Activity implements ListView.OnItemClickListener,
                                                          View.OnClickListener {

    private ListView menuCategory;
    private ListView menu;
    private ListView order;
    private MenuCategoryAdapter categoryAdapter;
    private MenuDataAdapter menuAdapter;

    private View OrderWindow;
    private View DecideWindow;

    private ArrayList<String> categoryList;
    private ArrayList<MenuItem> menuList;
    private DBManager dbm;

    // Dialog View
    private Button temperature;
    private boolean isHot;
    private Button plusCount;
    private Button minusCount;
    private TextView countOfCup;
    private int _countOfCup;
    private EditText orderMemo;
    private String _orderMemo;
    private Switch usingCoupon;
    private TextView totalOrderPrice;
    private Button decideOrder;
    private Button initOrder;

    private int _totalOrderPrice;
    private int isCoupon;

    private static final String ORDER_KEY = "ORDER_LIST";
    private static final String ORDER_MENU_NAME = "MENU_NAME";
    private static final String ORDER_PRICE = "MENU_PRICE";
    private static final String ORDER_CUP_OF_COUNT = "MENU_CUP_OF_COUNT";
    private static final String ORDER_IS_HOT = "MENU_IS_HOT";
    private static final String ORDER_IS_COUPON = "MENU_IS_COUPON";
    private static final String ORDER_MEMO = "MENU_ORDER_MEMO";
    private static final String ORDER_TOTAL_PRICE = "MENU_TOTAL_PRICE";
    private static final String ORDER_LIST = "MENU_ORDER_LIST";

    private ArrayList<OrderedItem> orderList;
    private MenuOrderAdapter orderAdpater;
    private boolean isSelectedItem = false;                 // 주문 dialog창 중복실행 방지
    private boolean isPushBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_order);
        Init();
    }

    protected void onStart(){
        super.onStart();
        orderList = getStringArrayPref(this, ORDER_KEY);
        orderAdpater.setList(orderList);
        // List의 레퍼런스 대상이 완전히 바뀌었으므로 새 List를 다시 설정해주어야 한다.
        orderAdpater.notifyDataSetChanged();
    }


    protected void onPause(){
        super.onPause();
        setStringArrayPref(this,ORDER_KEY, orderList);
    }


    private ArrayList<OrderedItem> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<OrderedItem> urls = new ArrayList<OrderedItem>();
        if (json != null) {
            try {
                JSONObject obj = new JSONObject(json);
                int _price = obj.getInt(ORDER_TOTAL_PRICE);
                _totalOrderPrice = _price;
                showTotalOrderPrice();

                JSONArray a = obj.getJSONArray(ORDER_LIST);
                for (int i = 0; i < a.length(); i++) {
                    JSONObject jObj = a.getJSONObject(i);

                    String menu_name = jObj.getString(ORDER_MENU_NAME);
                    int price = jObj.getInt(ORDER_PRICE);
                    int cup_of_count = jObj.getInt(ORDER_CUP_OF_COUNT);
                    boolean is_hot = jObj.getBoolean(ORDER_IS_HOT);
                    int is_coupon = jObj.getInt(ORDER_IS_COUPON);
                    String memo = jObj.getString(ORDER_MEMO);

                    urls.add(new OrderedItem(is_hot, price, cup_of_count, menu_name, memo, is_coupon));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }


    private void setStringArrayPref(Context context, String key, ArrayList<OrderedItem> values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONObject order_info = new JSONObject();
        JSONArray a = new JSONArray();

        try {
            order_info.put(ORDER_TOTAL_PRICE, _totalOrderPrice);

        }catch(JSONException e){
            e.getStackTrace();
        }

        for (int i = 0; i < values.size(); i++) {
            JSONObject json = new JSONObject();

            try {
                json.put(ORDER_MENU_NAME, values.get(i).getMenuName());
                json.put(ORDER_PRICE, values.get(i).getPrice());
                json.put(ORDER_CUP_OF_COUNT, values.get(i).getCupOfCount());
                json.put(ORDER_IS_HOT, values.get(i).getIsHot());
                json.put(ORDER_IS_COUPON, values.get(i).getIsCoupon());
                json.put(ORDER_MEMO,values.get(i).getOrderMemo());

                a.put(json);
            }
            catch(JSONException e){
                e.getStackTrace();
            }
        }

        try{
            order_info.put(ORDER_LIST, a);

        }catch(JSONException e){
            e.getStackTrace();
        }


        if (!values.isEmpty()) {
            editor.putString(key, order_info.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();
    }



    private void Init() {

        dbm = DBManager._getInstance(this, null, 1);

        menuCategory = (ListView) findViewById(R.id.menu_category);
        menu = (ListView) findViewById(R.id.menu);
        order = (ListView) findViewById(R.id.order);

        categoryList = dbm.selectCategory();
        categoryAdapter = new MenuCategoryAdapter(this, R.layout.category_element, categoryList);
        menuCategory.setAdapter(categoryAdapter);
        menuCategory.setOnItemClickListener(this);

        menuList = dbm.selectWholeMenu();
        menuAdapter = new MenuDataAdapter(this, R.layout.menu_element, menuList, false);
        menu.setAdapter(menuAdapter);
        menu.setOnItemClickListener(this);

        orderList = new ArrayList<OrderedItem>();
        orderAdpater = new MenuOrderAdapter(this, orderList, R.layout.order_element);
        order.setAdapter(orderAdpater);
        order.setOnItemClickListener(this);

        SwipeDismissListViewTouchListener touchListener =                                   // Swipe 액션 리스너
                new SwipeDismissListViewTouchListener(
                        order,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, final int[] reverseSortedPositions) {
                                AlertDialog.Builder dlg = new AlertDialog.Builder(ManageOrderActivity.this, AlertDialog.THEME_HOLO_LIGHT);

                                dlg.setMessage("주문을 취소하시겠습니까?");
                                dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        for (int position : reverseSortedPositions) {

                                            if(orderList.get(position).getIsCoupon() == OrderedItem.COUPON_NONE)
                                                _totalOrderPrice -= orderList.get(position).getPrice();

                                            showTotalOrderPrice();
                                            orderList.remove(position);

                                        }
                                        orderAdpater.notifyDataSetChanged();
                                        Toast.makeText(ManageOrderActivity.this, "주문이 취소되었습니다.", Toast.LENGTH_SHORT).show();
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

        order.setOnTouchListener(touchListener);
        order.setOnScrollListener(touchListener.makeScrollListener());

        totalOrderPrice = (TextView)findViewById(R.id.show_total_price);
        decideOrder = (Button)findViewById(R.id.decide_order);
        decideOrder.setOnClickListener(this);

        initOrder = (Button)findViewById(R.id.init_order);
        initOrder.setOnClickListener(this);
    }

    private void InitDialog(final int position){

        temperature = (Button) OrderWindow.findViewById(R.id.toggle_temperature);
        plusCount = (Button) OrderWindow.findViewById(R.id.plus_cup_count);
        minusCount = (Button) OrderWindow.findViewById(R.id.minus_cup_count);
        countOfCup = (TextView) OrderWindow.findViewById(R.id.count_of_cup);
        orderMemo = (EditText) OrderWindow.findViewById(R.id.order_memo);
        usingCoupon = (Switch) OrderWindow.findViewById(R.id.using_coupon);

        // price_2가 HOT price_1이 ice
        if (menuList.get(position).getMenuPrice_2() == 0) {
            isHot = false;
            temperature.setEnabled(false);
            temperature.setText("ICE");
            temperature.setTextColor(Color.parseColor("#CFE8FC"));
        } else if (menuList.get(position).getMenuPrice_1() == 0) {
            isHot = true;
            temperature.setEnabled(false);
            temperature.setText("HOT");
            temperature.setTextColor(Color.parseColor("#FF0000"));
        }       // 시즌메뉴에 적용되는 온도

        temperature.setOnClickListener(this);
        plusCount.setOnClickListener(this);
        minusCount.setOnClickListener(this);
        usingCoupon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCoupon = (isChecked) ? OrderedItem.COUPON_NORMAL_USE : OrderedItem.COUPON_NONE;
            }
        });


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

        if (parent.getId() == R.id.menu_category) {          // 메뉴 카테고리 아이템 선택
            menuList = dbm.getMenuByCategory(categoryList.get(position));
            menuAdapter.SetMenuList(menuList);
            menuAdapter.notifyDataSetChanged();

        } else if (isSelectedItem == false && parent.getId() == R.id.menu) {               // 메뉴 선택 / 주문 등록
            _countOfCup = 1;
            _orderMemo = "";
            isHot = true;
            // 주문내역 초기화
            isSelectedItem = true;                                                        // dialog 중복실행 방지
            isCoupon = OrderedItem.COUPON_NONE;


            OrderWindow = View.inflate(this, R.layout.receive_order, null);  // Dialog View Inflate
            AlertDialog.Builder dlg = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);

            InitDialog(position);

            dlg.setTitle(menuList.get(position).getMenuName() + " 주문");
            dlg.setView(OrderWindow);

            final String menuName = menuList.get(position).getMenuName();
            final int pos = position;

            dlg.setPositiveButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Toast.makeText(ManageOrderActivity.this, "주문을 취소하셨습니다.", Toast.LENGTH_SHORT).show();
                    isSelectedItem = false;

                }
            });

            dlg.setNegativeButton("주문", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String memo = orderMemo.getText().toString();
                    int cupCount = Integer.parseInt(countOfCup.getText().toString());

                    int price = isHot ? cupCount * menuList.get(pos).getMenuPrice_2()
                            : cupCount * menuList.get(pos).getMenuPrice_1();
                    // 주문 버튼을 누를 때 최종 컵의 개수와 가격을 계산한다.

                    orderList.add(new OrderedItem(isHot, price, cupCount, menuName, memo, isCoupon));
                    orderAdpater.notifyDataSetChanged();

                    if(isCoupon == OrderedItem.COUPON_NONE)
                        _totalOrderPrice += price;

                    showTotalOrderPrice();
                    isSelectedItem = false;
                }
            });
            dlg.setCancelable(false);                           // dialog 이외의 영역 터치시 사라지지 않게 함
            dlg.show();
        }

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggle_temperature:
                isHot = !isHot;
                if (isHot) {
                    temperature.setText("HOT");
                    temperature.setTextColor(Color.parseColor("#FF0000"));
                } else if (isHot == false) {
                    temperature.setText("ICE");
                    temperature.setTextColor(Color.parseColor("#CFE8FC"));
                }
                break;
            case R.id.plus_cup_count:
                _countOfCup++;
                countOfCup.setText("" + _countOfCup);
                break;
            case R.id.minus_cup_count:
                if (_countOfCup > 1) {
                    _countOfCup--;
                    countOfCup.setText("" + _countOfCup);
                }
                break;

            case R.id.decide_order:
                AlertDialog.Builder dlg = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
                DecideWindow = View.inflate(this, R.layout.decide_order_dialog, null);


                dlg.setView(DecideWindow);

                TextView decideOrderList = (TextView)DecideWindow.findViewById(R.id.decide_order_list);
                TextView decideOrderPrice = (TextView)DecideWindow.findViewById(R.id.decide_order_price);
                for(int i = 0; i < orderList.size(); i++){

                    String menu_name = orderList.get(i).getMenuName();
                    int price = orderList.get(i).getPrice();
                    int cup_of_count = orderList.get(i).getCupOfCount();
                    int is_coupon = orderList.get(i).getIsCoupon();
                    boolean is_hot = orderList.get(i).getIsHot();

                    String item = "" + menu_name + " "
                                    + cup_of_count + " "
                                    + ((is_hot) ? "HOT" : "ICE") + " "
                                    + ((is_coupon == OrderedItem.COUPON_NONE) ? price : "쿠폰") + '\n';
                                    // 아이템 리스트


                    decideOrderList.append(item);

                }

                decideOrderPrice.setText("총 " + _totalOrderPrice + " 원");


                dlg.setMessage("주문을 접수하시겠습니까?");
                dlg.setNegativeButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // 주문리스트 객체에서 주문 정보를 가져와 db에 기록한다.
                        for(int i = 0; i < orderList.size(); i++) {
                            int fk = dbm.getDrinkPKByName(orderList.get(i).getMenuName());
                            dbm.isServeComplete(
                                    orderList.get(i).getCupOfCount(),
                                    orderList.get(i).getIsHot() ? 1 : 0,
                                    fk,
                                    orderList.get(i).getIsCoupon());
                        }

                        Toast.makeText(ManageOrderActivity.this, "주문 완료", Toast.LENGTH_SHORT).show();
                        initTotalOrderPrice();
                    }
                });

                dlg.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });

                dlg.setCancelable(false);                           // dialog 이외의 영역 터치시 사라지지 않게 함
                dlg.show();
                break;
            case R.id.init_order:
                AlertDialog.Builder init_dlg = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
                init_dlg.setMessage("주문을 모두 취소하시겠습니까?");
                init_dlg.setNegativeButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initTotalOrderPrice();
                        Toast.makeText(ManageOrderActivity.this,"주문이 전부 취소되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

                init_dlg.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });

                init_dlg.setCancelable(false);                           // dialog 이외의 영역 터치시 사라지지 않게 함
                init_dlg.show();


                break;
        }

    }

    private void showTotalOrderPrice(){
        totalOrderPrice.setText("총 " + _totalOrderPrice  + " 원");
    }
    private void initTotalOrderPrice(){
        _totalOrderPrice = 0;
        showTotalOrderPrice();
        orderList.clear();
        orderAdpater.notifyDataSetChanged();
    }

    public boolean onKeyDown(int KeyCode, KeyEvent keyEvent) {

        if (isPushBack == false && KeyCode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            check_network.sendEmptyMessageDelayed(0, 1500);
            isPushBack = true;
        } else if (isPushBack && KeyCode == KeyEvent.KEYCODE_BACK)
            finish();

        return true;

    }


    Handler check_network = new Handler() {
        public void handleMessage(Message msg) {
            isPushBack = false;
        }
    };

}