package com.example.oneday_cafe_pos;

import android.os.Parcel;
import android.os.Parcelable;

public class MenuItem implements Parcelable{
    private String menuName;
    private int menuPrice_1;                    // ICE Price
    private int menuPrice_2;                    // HOT Price
    private byte []  menuThumb;

    public MenuItem(String _menuName, int _menuPrice_1, int _menuPrice_2, byte [] _menuThumb){
        menuName = _menuName;
        menuPrice_1 = _menuPrice_1;
        menuPrice_2 = _menuPrice_2;

        menuThumb = _menuThumb;
    }

    public MenuItem(MenuItem _item){
        menuName = _item.menuName;
        menuPrice_1 = _item.menuPrice_1;
        menuPrice_2 = _item.menuPrice_2;
        menuThumb = _item.menuThumb;
    }


    protected MenuItem(Parcel in) {
        menuName = in.readString();
        menuPrice_1 = in.readInt();
        menuPrice_2 = in.readInt();
        menuThumb = in.createByteArray();
    }

    public static final Creator<MenuItem> CREATOR = new Creator<MenuItem>() {
        @Override
        public MenuItem createFromParcel(Parcel in) {
            return new MenuItem(in);
        }

        @Override
        public MenuItem[] newArray(int size) {
            return new MenuItem[size];
        }
    };

    public void setMenuName(String _menuName){
        menuName = _menuName;
    }

    public void setMenuPrice_1(int _menuPrice_1){
        menuPrice_1 = _menuPrice_1;
    }

    public void setMenuPrice_2(int _menuPrice_2){
        menuPrice_2 = _menuPrice_2;
    }


    public void setMenuThumb(byte [] _menuThumb){
        menuThumb = _menuThumb;
    }

    public String getMenuName(){
        return menuName;
    }

    public int getMenuPrice_1(){
        return menuPrice_1;
    }

    public int getMenuPrice_2(){
        return menuPrice_2;
    }

    public byte []  getBitmapThumb(){
        return menuThumb;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(menuName);
        dest.writeInt(menuPrice_1);
        dest.writeInt(menuPrice_2);
        dest.writeByteArray(menuThumb);
    }

    public String toString(){
        return "MenuName : " + menuName;
    }


}
