package com.example.oneday_cafe_pos;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MenuOrderAdapter extends BaseAdapter {
    private Context c;
    private ArrayList<OrderedItem> orderList;
    private int layout;
    private LayoutInflater Inflater;

    public MenuOrderAdapter(Context _c, ArrayList<OrderedItem> _orderList, int _layout){
        c = _c;
        orderList = _orderList;
        layout = _layout;
        Inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = Inflater.inflate(layout, parent, false);

        TextView orderTemperature = (TextView)convertView.findViewById(R.id.ordered_temperature);
        TextView orderMenuName = (TextView)convertView.findViewById(R.id.ordered_menu_name);
        TextView orderPrice = (TextView)convertView.findViewById(R.id.ordered_price);
        TextView orderMemo = (TextView)convertView.findViewById(R.id.order_memo);
        TextView orderCupCount = (TextView)convertView.findViewById(R.id.ordered_count);
        //TextView markOrderEnd = (TextView)convertView.findViewById(R.id.mark_order_end);

        if(orderList.get(position).getIsHot() == true) {            // isHot == true // 뜨거운 음료 기준
            orderTemperature.setText("HOT");
            orderTemperature.setTextColor(Color.parseColor("#FF0000"));
        }
        else {
            orderTemperature.setText("ICE");
            orderTemperature.setTextColor(Color.parseColor("#CFE8FC"));
        }

        orderCupCount.setText("" + orderList.get(position).getCupOfCount());
        orderMenuName.setText(orderList.get(position).getMenuName());

        if(orderList.get(position).getIsCoupon() == 0)
            orderPrice.setText(orderList.get(position).getPrice() + "원");
        else
            orderPrice.setText("쿠폰 사용");

        String MenuOpts = orderList.get(position).getOrderMemo();

        if(MenuOpts.equals(""))
            orderMemo.setText("-");
        else
            orderMemo.setText(orderList.get(position).getOrderMemo());

        return convertView;
    }

    public void setList(ArrayList<OrderedItem> list){ orderList = list; }
}
