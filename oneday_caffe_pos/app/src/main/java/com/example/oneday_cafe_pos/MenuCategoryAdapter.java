package com.example.oneday_cafe_pos;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.oneday_cafe_pos.R;

import java.util.ArrayList;

public class MenuCategoryAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater Inflater;
    private ArrayList<String> Data;
    private int layout;

    public MenuCategoryAdapter(Context c, int _layout, ArrayList<String> _data){
        context = c;
        layout = _layout;
        Data = _data;
        Inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return Data.size();
    }

    @Override
    public Object getItem(int position) {
        return Data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = Inflater.inflate(layout, parent,false);

        TextView category = (TextView)convertView.findViewById(R.id.category_element);
        category.setText(Data.get(position));

        return convertView;
    }
}
