//package OrderInfo;

public class OrderedItem{

    private boolean isHot;
    private int cupOfCount;
    private int price;
    private String menuName;
    private String orderMemo;
    private int isCoupon;
    private int soldID;
	private boolean isComplete;							// 제조 완료 여부

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

    public String toJSONObj(){
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
		isComplete = false;
    }

    public OrderedItem(boolean _isHot, int _price, int _cupOfCount, String _menuName, String _orderMemo, int _isCoupon, int _soldID) {
        isHot = _isHot;
        cupOfCount = _cupOfCount;
        orderMemo = _orderMemo;
        menuName = _menuName;
        price = _price;
        isCoupon = _isCoupon;
        soldID = _soldID;
		isComplete = false;
    }
	
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

	public boolean isComplete(){ return isComplete; }
	
	public void checkComplete(boolean check){ isComplete = check; }

}

