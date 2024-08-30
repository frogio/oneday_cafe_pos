package com.example.oneday_cafe_pos;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MenuDataAdapter extends BaseAdapter{
    private Context context;
    private LayoutInflater Inflater;
    private ArrayList<MenuItem> Data;
    private int layout;
    private boolean isOptionEnable = false;                  // 메뉴에 수정, 삭제 메뉴 활성화 여부


    public MenuDataAdapter(Context c, int _layout, ArrayList<MenuItem> _data){
        context = c;
        layout = _layout;
        Data = _data;
        Inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        isOptionEnable = true;
    }

    public MenuDataAdapter(Context c, int _layout, ArrayList<MenuItem> _data, boolean _isOptionEnable){
        context = c;
        layout = _layout;
        Data = _data;
        Inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        isOptionEnable = _isOptionEnable;
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

        if(convertView == null) {
            convertView = Inflater.inflate(layout, parent, false);

            if(isOptionEnable)                      // 메뉴 활성화 여부가 true일때 활성화
                ((Activity)context).registerForContextMenu(convertView);
            // 리스트 뷰의 아이템을 long 클릭했을 경우 나타남.
        }

        // ImageView imageView = (ImageView)convertView.findViewById(R.id.menu_elem_img);
        TextView menuName = (TextView) convertView.findViewById(R.id.menu_elem_name);
        TextView menuPrice1 = (TextView)convertView.findViewById(R.id.menu_elem_price1);
        TextView menuPrice2 = (TextView)convertView.findViewById(R.id.menu_elem_price2);

        MenuItem menuItem = Data.get(position);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeByteArray(menuItem.getBitmapThumb(),0 , menuItem.getBitmapThumb().length,options);
        // 1/8로 압축함

       //  imageView.setImageBitmap(bitmap);
        menuName.setText(menuItem.getMenuName());
        menuPrice1.setText("" + menuItem.getMenuPrice_1());
        menuPrice2.setText("" + menuItem.getMenuPrice_2());

        return convertView;
    }

    public void SetMenuList(ArrayList<MenuItem> _menuItem){         // ArrayList를 완전히 새로 바꿀 때 반드시 호출해줘서 올바른 레퍼런스를 갖게 해야 함
        Data = _menuItem;
    }

}
