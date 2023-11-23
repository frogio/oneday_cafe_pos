package com.example.oneday_cafe_pos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

import java.io.PipedReader;
import java.sql.PreparedStatement;
import java.util.ArrayList;

public class DBManager extends SQLiteOpenHelper {

    private static DBManager dbm;

    private static final String DB_NAME = "cafeDB.db";
    private final static String CREATE_MENU_CATEGORY = "CREATE TABLE MENU_CATEGORY" +
                                                        "(cate_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                                        "categoryName TEXT)";

    private final static String CREATE_DRINK = "CREATE TABLE DRINK " +
                                                "(drink_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                                "image BLOB," +
                                                "drinkName TEXT," +
                                                "price1 INTEGER," +
                                                "price2 INTEGER," +
                                                "cate_id INTEGER," +
                                                "CONSTRAINT CATE_ID FOREIGN KEY(cate_id) " +
                                                "REFERENCES MENU_CATEGORY(cate_id))";
                                                // SQLite는 외래키 제약 조건을 변경해주는 기능을 제공해주지 않는다.

    private final static String CREATE_SALE_RECORD = "CREATE TABLE SALE_RECORD" +
                                                        "(sale_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                                        "date TEXT," +
                                                        "count INTEGER," +
                                                        "is_hot INTEGER," +
                                                        "drink_id INTEGER," +
                                                        "coupon INTEGER," +
                                                        "CONSTRAINT RECORD_ID FOREIGN KEY(drink_id) " +
                                                        "REFERENCES DRINK(drink_id))";
        // DDL
    private final static String INIT_CATEGORY = "INSERT INTO MENU_CATEGORY VALUES(0, \"deleted\")";
        // InitCategory             category 0번은 삭제된 메뉴의 집합.

    private final static String ADD_MENU_CATEGORY = "INSERT INTO MENU_CATEGORY values(null,\"";
    private final static String GET_CATEGORY_PK = "SELECT cate_id FROM MENU_CATEGORY WHERE categoryName = \"";
    private final static String SELECT_CATEGORY = "SELECT * FROM MENU_CATEGORY WHERE cate_id != 0";
    private final static String DELETE_CATEGORY = "DELETE FROM MENU_CATEGORY WHERE categoryName=\"";
    private final static String DELETE_CATEGORY_WHOLE_MENU = "UPDATE DRINK SET cate_id = 0 where cate_id = ";

    private final static String ADD_MENU = "INSERT INTO DRINK VALUES(null, ?, ?, ?, ?, ?)";
    private final static String SELECT_WHOLE_MENU = "SELECT * FROM DRINK WHERE cate_id != 0";
    private final static String SELECT_MENU_BY_CETEGORY  = "SELECT image, drinkName, price1, price2 FROM DRINK a, MENU_CATEGORY b " +
                                                            "WHERE a.cate_id = b.cate_id AND b.categoryName = \"";

    private final static String DELETE_MENU = "UPDATE DRINK SET cate_id = 0 WHERE drinkName = \"";
    // drink 테이블에서 cate_id 컬럼값이 0인것은 삭제된 메뉴를 의미.
    // 0값은 MENU_CATEGORY 테이블에서 deleted와 대응됨.

    private final static String UPDATE_MENU  = "UPDATE DRINK SET (image, drinkName, price1 , price2, cate_id)=" +
                                                    "(?, ?, ?, ?, ?) " +
                                                    "WHERE drinkName = \"";
    private final static String ADD_SALE_RECORD = "INSERT INTO SALE_RECORD VALUES(null,(select strftime('%Y-%m-%d','now','localtime')), ";
    private final static String GET_DRINK_PK = "SELECT drink_id FROM DRINK WHERE drinkName = \"";


    private final static String CALCULATION_MENU_INCOME =
            "SELECT " +
                    "drinkName, " +                                     // 음료 이름
                    "sold_count, " +                                    // 판매 갯수
                    "case when(is_hot==1) " +                           // 온도 여부에 따라 음료값이 달라짐.
                        "then sold_count * d.price2 " +                 // 뜨거운 음료
                        "else sold_count * d.price1 " +                 // 차가운 음료
                    "end as price " +                                   // 온도 여부에 따른 가격을
                    "FROM " +                                           // 중첩된 테이블로부터 가져온다.
                    "(SELECT sum(count) as sold_count, is_hot FROM SALE_RECORD rcd, DRINK d " +
                    // 중첩된 테이블은 sold_count, is_hot 컬럼으로 이루어짐, 이를 SALE_RECORD, DRINK 테이블로부터 가져와 가공한다.
                    "WHERE rcd.date = ! " +
                    "AND rcd.is_hot = @ " +
                    "AND rcd.drink_id = d.drink_id " +
                    "AND rcd.drink_id = # " +
                    "AND rcd.coupon = 0" +
                    "), DRINK d where d.drink_id = $ ";

    private final static String CALCULATION_MENU_COUPON =
            "SELECT " +
                    "drinkName, " +
                    "sold_count " +
                    "FROM " +
                    "(SELECT sum(count) as sold_count, is_hot FROM SALE_RECORD rcd, DRINK d " +
                    "WHERE rcd.date = ! " +
                    "AND rcd.is_hot = @ " +
                    "AND rcd.drink_id = d.drink_id " +
                    "AND rcd.drink_id = # " +
                    "AND rcd.coupon = 1" +
                    "), DRINK d where d.drink_id = $ ";

    private final static String GET_ALL_DRINK_PK = "SELECT drink_id FROM drink";

    private final static String GET_ALL_SOLD_RECORD =
            "SELECT rec.sale_id, " +
                    "rec.date," +
                    "rec.count, " +
                    "rec.is_hot, " +
                    "rec.coupon, " +
                    "dnk.drinkName, " +
                    "case when(rec.coupon==1) " +
                    "then 0 " +
                        "else case when(rec.is_hot==1) " +
                            "then rec.count * dnk.price2 " +
                            "else rec.count * dnk.price1 " +
                        "end " +
                    "end as price " +
                    "FROM SALE_RECORD rec, " +
                    "DRINK dnk " +
                    "WHERE " +
                    "date = \"#\" AND " +
                    "rec.drink_id = dnk.drink_id " +
                    "ORDER BY sale_id DESC";

    private final static String DELETE_SOLD_RECORD = "DELETE FROM SALE_RECORD WHERE sale_id = ";


    public static DBManager _getInstance(Context context, SQLiteDatabase.CursorFactory cursorfactory, int version){
        if(dbm == null)
            dbm = new DBManager(context, DB_NAME, cursorfactory, version);

        return dbm;
    }

    private DBManager(Context context, String name,
                     SQLiteDatabase.CursorFactory cursorfactory, int version) {
        super(context, name, cursorfactory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MENU_CATEGORY);
        db.execSQL(CREATE_DRINK);
        db.execSQL(CREATE_SALE_RECORD);
        db.execSQL(INIT_CATEGORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addCategory(String s){
        SQLiteDatabase db = getWritableDatabase();
        String query = ADD_MENU_CATEGORY + s + "\")";
        db.execSQL(query);
        db.close();
    }

    public ArrayList<String> selectCategory(){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> data = new ArrayList<String>();
        Cursor cursor = db.rawQuery(SELECT_CATEGORY, null);

        while(cursor.moveToNext())
            data.add(cursor.getString(1));

        db.close();
        return data;
    }

    public ArrayList<MenuItem> selectWholeMenu(){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<MenuItem> data = new ArrayList<MenuItem>();
        Cursor cursor = db.rawQuery(SELECT_WHOLE_MENU, null);

        while(cursor.moveToNext())
            data.add(new MenuItem(cursor.getString(2),
                                        cursor.getInt(3),
                                        cursor.getInt(4),
                                        cursor.getBlob(1)));
        db.close();

        return data;
    }

    public void deleteCategory(String name){
        SQLiteDatabase db = getWritableDatabase();
        String query = DELETE_CATEGORY + name + '\"';
        db.execSQL(query);
        db.close();
    }

    public void addMenu(byte []  image, String drinkName, int price1, int price2, int cate_id){
        SQLiteStatement p = getWritableDatabase().compileStatement(ADD_MENU);
        p.bindBlob(1, image);
        p.bindString(2, drinkName);
        p.bindLong(3, price1);
        p.bindLong(4, price2);
        p.bindLong(5,cate_id);
        p.execute();
    }


    public void ModifyMenu(byte []  image, String drinkName, int price1, int price2, int cate_id, String prevMenuName){
        String query = UPDATE_MENU + prevMenuName + "\"";
        SQLiteStatement p = getWritableDatabase().compileStatement(query);
        p.bindBlob(1, image);
        p.bindString(2, drinkName);
        p.bindLong(3, price1);
        p.bindLong(4, price2);
        p.bindLong(5,cate_id);
        p.execute();
    }

    public int getCategoryPKByString(String categoryName){

        if(categoryName == null) {
            categoryName = "";
//            Log.i("Error Query", "isNull? : " + categoryName);
        }

        String query =  GET_CATEGORY_PK + categoryName + "\"";
//        Log.i("Error Query : ", "Category Name : " + categoryName);
//        Log.i("Error Query : ", "" + query);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        int pk = -1;

        cursor.moveToNext();

        pk =  cursor.getInt(0);

        db.close();

        return pk;
    }

    public ArrayList<MenuItem> getMenuByCategory(String category){
        ArrayList<MenuItem> data = new ArrayList<MenuItem>();
        SQLiteDatabase db = getReadableDatabase();
        String query =  SELECT_MENU_BY_CETEGORY + category + "\"";

        Cursor cursor = db.rawQuery(query, null);

        while(cursor.moveToNext())
            data.add(new MenuItem(cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    cursor.getBlob(0)));


        db.close();
        return data;
    }

    public void deleteMenu(String menuName){
        SQLiteDatabase db = getReadableDatabase();
        String query = DELETE_MENU + menuName + "\"";

        db.execSQL(query);
        db.close();
    }

    public void deleteCategoryWholeMenu(int cate_id){
        SQLiteDatabase db = getWritableDatabase();
        String query = DELETE_CATEGORY_WHOLE_MENU + cate_id;
        db.execSQL(query);
        db.close();
    }

    public int getDrinkPKByName(String drinkName){
        String query =  GET_DRINK_PK + drinkName + "\" and cate_id != 0";
        // 버그 발생 방지, 유효한 cate_id를 갖는 아이템만 받아옴
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        //Log.i("Reader", "Drink Name : " + drinkName);

        int pk = 0;
        if ( cursor != null && cursor.moveToFirst()) {
            pk = cursor.getInt(0);
            cursor.close();
        }
        //else
        //    Log.i("Reader", "cursor is " + (cursor == null));

        return pk;
    }


    public void isServeComplete(int _count, int _is_hot, int _drink_id, int _using_coupon)
    {

        Log.i("Reader", "Function is Working?");

        SQLiteDatabase db = getWritableDatabase();


        String query = ADD_SALE_RECORD + _count + ", " +
                                            _is_hot + ", " +
                                            _drink_id + ", "+
                                            _using_coupon + ")";

        //Log.i("Reader", "Query : " + query);
        db.execSQL(query);
        db.close();
        //Log.i("Reader", "Success");
    }

    public ArrayList<Integer> getWholePK(){
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<Integer> drinkIdList = new ArrayList<Integer>();
        Cursor cursor = db.rawQuery(GET_ALL_DRINK_PK, null);

        while(cursor.moveToNext())
            drinkIdList.add(cursor.getInt(0));

        db.close();
        return drinkIdList;
    }


    public ArrayList<SoldMenu> getSoldMenuList(String date, ArrayList<Integer> drinkId){

        SQLiteDatabase db = getWritableDatabase();
        ArrayList<SoldMenu> soldMenuList = new ArrayList<SoldMenu>();


        // Hot으로 팔린 제품을 먼저 정산한다.
        for(int i = 0; i < drinkId.size(); i++) {
            String query = cvtQueryParameter(date,1, drinkId.get(i), 0);
            //Log.i("Error Query", "" + query);
            Cursor cursor = db.rawQuery(query, null);

            cursor.moveToNext();            // 비어있는 레코드일 경우(단 한번도 팔리지 않은 메뉴일 때) 정산에서 제외한다.
            int soldCount = cursor.getInt(1);
            if(soldCount == 0)
                continue;

            String drinkName = cursor.getString(0);
            int price = cursor.getInt(2);

            soldMenuList.add(new SoldMenu(drinkName,soldCount,price,1));


        }

        // 다음 ICE로 팔린 제품을 정산한다.
        for(int i = 0; i < drinkId.size(); i++) {
            String query = cvtQueryParameter(date,0, drinkId.get(i),0);
            //Log.i("Error Query", "" + query);
            Cursor cursor = db.rawQuery(query, null);

            cursor.moveToNext();            // 비어있는 레코드일 경우(단 한번도 팔리지 않은 메뉴일 때) 정산에서 제외한다.
            int soldCount = cursor.getInt(1);
            if(soldCount == 0)
                continue;

            String drinkName = cursor.getString(0);
            int price = cursor.getInt(2);

            soldMenuList.add(new SoldMenu(drinkName,soldCount,price,0));
        }

        db.close();
        return soldMenuList;
    }


    public ArrayList<SoldMenu> getCouponMenuList(String date, ArrayList<Integer> drinkId){

        SQLiteDatabase db = getWritableDatabase();
        ArrayList<SoldMenu> soldMenuList = new ArrayList<SoldMenu>();


        // Hot으로 팔린 제품을 먼저 정산한다.
        for(int i = 0; i < drinkId.size(); i++) {
            String query = cvtQueryParameter(date,1, drinkId.get(i), 1);
            //Log.i("Error Query", "" + query);
            Cursor cursor = db.rawQuery(query, null);

            cursor.moveToNext();            // 비어있는 레코드일 경우(단 한번도 팔리지 않은 메뉴일 때) 정산에서 제외한다.
            int soldCount = cursor.getInt(1);
            if(soldCount == 0)
                continue;

            String drinkName = cursor.getString(0);

            soldMenuList.add(new SoldMenu(drinkName,soldCount,0,1));


        }

        // 다음 ICE로 팔린 제품을 정산한다.
        for(int i = 0; i < drinkId.size(); i++) {
            String query = cvtQueryParameter(date,0, drinkId.get(i),1);
            //Log.i("Error Query", "" + query);
            Cursor cursor = db.rawQuery(query, null);

            cursor.moveToNext();            // 비어있는 레코드일 경우(단 한번도 팔리지 않은 메뉴일 때) 정산에서 제외한다.
            int soldCount = cursor.getInt(1);
            if(soldCount == 0)
                continue;

            String drinkName = cursor.getString(0);

            soldMenuList.add(new SoldMenu(drinkName,soldCount,0,0));
        }

        db.close();
        return soldMenuList;
    }

    // 쿼리를 조립한다.
    private String cvtQueryParameter(String date, int isHot , int drinkId, int usingCoupon){
        String query;

        if(usingCoupon == 0)
            query = CALCULATION_MENU_INCOME;
        else
            query = CALCULATION_MENU_COUPON;

        String _date = '\"' + date + '\"';

        query = query.replace("!", _date);
        query = query.replace("@", "" + isHot);
        query = query.replace("#","" + drinkId);
        query = query.replace("$", "" + drinkId);
        return query;
    }

    public ArrayList<OrderedItem> getTodaySoldRecord(String date){

        SQLiteDatabase db = getWritableDatabase();
        ArrayList<OrderedItem> record = new ArrayList<OrderedItem>();
        String query = GET_ALL_SOLD_RECORD;
        query = query.replace("#", date);

        Cursor cursor = db.rawQuery(query, null);
        while(cursor.moveToNext()){
            int sold_id = cursor.getInt(0);
            boolean is_hot =  (cursor.getInt(3) == 1) ? true : false;
            int cup_of_count = cursor.getInt(2);
            String drink_name = cursor.getString(5);
            int price = cursor.getInt(6);
            int is_coupon = cursor.getInt(4);

            OrderedItem item = new OrderedItem(is_hot,
                                                price,
                                                cup_of_count,
                                                drink_name,
                                                "",
                                                is_coupon,
                                                sold_id);
            record.add(item);

        }
        db.close();
        return record;
    }
    public void deleteSoldRecord(int sold_id){
        //DELETE_SOLD_RECORD
        SQLiteDatabase db = getWritableDatabase();
        String query = DELETE_SOLD_RECORD + sold_id;
        db.execSQL(query);
        db.close();
    }
}
