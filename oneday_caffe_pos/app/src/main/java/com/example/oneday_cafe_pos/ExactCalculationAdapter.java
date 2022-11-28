package com.example.oneday_cafe_pos;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ExactCalculationAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<SoldMenu> soldMenuList;
    private LayoutInflater Inflater;
    private int layout;

    public ExactCalculationAdapter(Context c, int _layout, ArrayList<SoldMenu> _data){
        context = c;
        layout = _layout;
        soldMenuList = _data;
        Inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return soldMenuList.size();
    }

    @Override
    public Object getItem(int position) {
        return soldMenuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
            convertView = Inflater.inflate(layout, parent,false);

        TextView temperature = (TextView)convertView.findViewById(R.id.is_hot);
        TextView drinkName = (TextView)convertView.findViewById(R.id.drink_name);
        TextView soldCount = (TextView)convertView.findViewById(R.id.sold_count);
        TextView totalPrice = (TextView)convertView.findViewById(R.id.total_price);

        if(soldMenuList.get(position).getIsHot() == 0) {
            temperature.setText("ICE");
            temperature.setTextColor(Color.parseColor("#CFE8FC"));
        }
        else {
            temperature.setText("HOT");
            temperature.setTextColor(Color.parseColor("#ff0000"));

        }

        drinkName.setText(soldMenuList.get(position).getDrinkName());
        soldCount.setText("" + soldMenuList.get(position).getSaledCount());

        if(soldMenuList.get(position).getTotalPrice() == 0)
            totalPrice.setText("쿠폰");
        else
            totalPrice.setText("" + soldMenuList.get(position).getTotalPrice());

        convertView.setOnTouchListener(new View.OnTouchListener(){
                    public boolean onTouch(View v, MotionEvent e){
                        return true;
                    }
                });
        // 리스트뷰의 터치 막기
        return convertView;
    }

    public void setSoldMenuList(ArrayList<SoldMenu> list){
        soldMenuList = list;
    }
}
