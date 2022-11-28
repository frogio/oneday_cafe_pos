package com.example.oneday_cafe_pos;

public class SoldMenu {

    private String drinkName;
    private int soldCount;
    private int totalPrice;
    private int isHot;

    public SoldMenu(String _drinkName, int _soldCount, int _totalPrice, int _isHot){
        drinkName = _drinkName;
        soldCount = _soldCount;
        totalPrice = _totalPrice;
        isHot = _isHot;
    }

    public void setDrinkName(String _drinkName) { drinkName = _drinkName;}
    public void setSaledCount(int _soldCount) { soldCount = _soldCount; }
    public void setTotalPrice(int _totalPrice) { totalPrice = _totalPrice;}
    public void setIsHot(int _isHot){ isHot = _isHot;}

    public String getDrinkName(){ return drinkName;}
    public int getSaledCount(){ return soldCount;}
    public int getTotalPrice(){ return totalPrice;}
    public int getIsHot(){ return isHot; }

    public String toString(){
        return "isHot : " + isHot + " Drink Name : " + drinkName + " Sold Count : " + soldCount + " Total Price : " + totalPrice;
    }

}
