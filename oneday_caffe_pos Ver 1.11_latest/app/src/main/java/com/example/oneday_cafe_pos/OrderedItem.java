package com.example.oneday_cafe_pos;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

public class OrderedItem implements Parcelable {

    private boolean isHot;
    private int cupOfCount;
    private int price;
    private String menuName;
    private String orderMemo;
    private int isCoupon;
    private int soldID;

    public static final int COUPON_NONE = 0;
    public static final int COUPON_NORMAL_USE = 1;

    @Override
    public String toString() {
        return "OrderedItem{" +
                "isHot=" + isHot +
                ", cupOfCount=" + cupOfCount +
                ", price=" + price +
                ", menuName='" + menuName + '\'' +
                ", orderMemo='" + orderMemo + '\'' +
                ", isCoupon=" + isCoupon +
                '}';
    }

    public String toJSONObj(){          // No Answer... so weird
        return  '{' + "\"isHot\":" + isHot + ",\n" +
                "\"cupOfCount\":" + cupOfCount + ",\n" +
                "\"price\":" + price + ",\n" +
                "\"menuName\":" + '\"' +  menuName + '\"' + ",\n" +
                "\"orderMemo\":" + '\"' + orderMemo + '\"' + ",\n" +
                "\"isCoupon\":" + isCoupon + '}';
    }

    public OrderedItem(boolean _isHot, int _price, int _cupOfCount, String _menuName, String _orderMemo, int _isCoupon) {
        isHot = _isHot;
        cupOfCount = _cupOfCount;
        orderMemo = _orderMemo;
        menuName = _menuName;
        price = _price;
        isCoupon = _isCoupon;
    }

    public OrderedItem(boolean _isHot, int _price, int _cupOfCount, String _menuName, String _orderMemo, int _isCoupon, int _soldID) {
        isHot = _isHot;
        cupOfCount = _cupOfCount;
        orderMemo = _orderMemo;
        menuName = _menuName;
        price = _price;
        isCoupon = _isCoupon;
        soldID = _soldID;
    }


    protected OrderedItem(Parcel in) {
        isHot = in.readByte() != 0;
        cupOfCount = in.readInt();
        price = in.readInt();
        menuName = in.readString();
        orderMemo = in.readString();
    }

    public static final Creator<OrderedItem> CREATOR = new Creator<OrderedItem>() {
        @Override
        public OrderedItem createFromParcel(Parcel in) {
            return new OrderedItem(in);
        }

        @Override
        public OrderedItem[] newArray(int size) {
            return new OrderedItem[size];
        }
    };

    public boolean getIsHot() {
        return isHot;
    }

    public int getCupOfCount() {
        return cupOfCount;
    }

    public String getOrderMemo() {
        return orderMemo;
    }

    public String getMenuName() {
        return menuName;
    }

    public int getPrice() { return price; }

    public int getIsCoupon(){ return isCoupon; }

    public int getSoldID(){ return soldID; }

    public void setPrice(int _price) {
        price = _price;
    }

    public void setIsHot(boolean _isHot) {
        isHot = _isHot;
    }

    public void setCupOfCount(int _cupOfCount) {
        cupOfCount = _cupOfCount;
    }

    public void setOrderMemo(String _orderMemo) {
        orderMemo = _orderMemo;
    }

    public void setIsCoupon(int _isCoupon) {
        isCoupon = _isCoupon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeBoolean(isHot);
        dest.writeValue(isHot);
        dest.writeInt(cupOfCount);
        dest.writeInt(price);
        dest.writeString(menuName);
        dest.writeString(orderMemo);

    }

}
