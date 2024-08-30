package com.example.oneday_cafe_pos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class ManageMenuActivity extends Activity implements View.OnClickListener,
                                            AdapterView.OnItemClickListener,
                                            AdapterView.OnItemLongClickListener{

    private Button newMenuCategory;
    private Button newMenu;
    private ListView categoryList;
    private ListView menuList;

    private MenuCategoryAdapter categoryAdapter;
    private ArrayList<String> categoryListData;

    private MenuDataAdapter menuAdpater;
    private ArrayList<MenuItem> menuDataList;

    private String selectedCategoryName;
    private DBManager dbm;

    private View catergoryAddWindow;

    private String controlMenu;
    private static final int REQUEST_REGISTER_MENU = 1;
    private static final int REQUEST_MDOFIED_MENU = 2;
    int updateMenuIdx = -1;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_menu);

        Init();

    }

    private void Init() {

        dbm = DBManager._getInstance(this, null, 1);

        newMenuCategory = (Button) findViewById(R.id.new_menu_category);
        newMenu = (Button) findViewById(R.id.new_menu);

        newMenuCategory.setOnClickListener(this);
        newMenu.setOnClickListener(this);

        categoryList = (ListView) findViewById(R.id.category_list);
        menuList = (ListView) findViewById(R.id.menu_list);

        categoryListData = dbm.selectCategory();
        categoryAdapter = new MenuCategoryAdapter(this, R.layout.category_element, categoryListData);
        // 메뉴 카테고리의 어댑터와 데이터 준비

        categoryList.setAdapter(categoryAdapter);
        categoryList.setOnItemClickListener(this);
        categoryList.setOnItemLongClickListener(this);

        menuDataList = dbm.selectWholeMenu();
        menuAdpater = new MenuDataAdapter(this, R.layout.menu_element, menuDataList);
        menuList.setAdapter(menuAdpater);

        // 리스트 어댑터 설정
    }

    public void onClick(View v) {
        if (v.getId() == R.id.new_menu_category) {              // 새로운 메뉴 그룹 추가
            //Toast.makeText(this, "새로운 메뉴 카테고리", Toast.LENGTH_SHORT).show();
            catergoryAddWindow = View.inflate(this, R.layout.add_menu_category, null);

            AlertDialog.Builder dlg = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);
            dlg.setView(catergoryAddWindow);
            dlg.setPositiveButton("등록", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText InputName = (EditText) catergoryAddWindow.findViewById(R.id.category_name);
                    String newCategoryName = InputName.getText().toString();
                    dbm.addCategory(newCategoryName);
                    categoryListData.add(newCategoryName);
                    categoryAdapter.notifyDataSetChanged();
                }
            });

            dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(ManageMenuActivity.this, "입력을 취소하셨습니다.", Toast.LENGTH_SHORT).show();
                }
            });
            dlg.show();

        }
        else if (v.getId() == R.id.new_menu) {                  // 새로운 메뉴 추가

            Intent i = new Intent(this, AddMenuActivity.class);
            i.putExtra("Category", selectedCategoryName);
            startActivityForResult(i,REQUEST_REGISTER_MENU);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_REGISTER_MENU) {

            MenuItem NewMenuItem = data.getParcelableExtra("NewMenu");
            // AddMenuActivty에서 parcel로 포장된 데이터를 읽어온다.

            menuDataList.add(new MenuItem(NewMenuItem));
            menuAdpater.notifyDataSetChanged();
        }

        else if(requestCode == REQUEST_MDOFIED_MENU){

            if(updateMenuIdx > -1) {
                MenuItem NewMenuItem = data.getParcelableExtra("NewMenu");
                menuDataList.get(updateMenuIdx).setMenuName(NewMenuItem.getMenuName());
                menuDataList.get(updateMenuIdx).setMenuPrice_1(NewMenuItem.getMenuPrice_1());
                menuDataList.get(updateMenuIdx).setMenuPrice_2(NewMenuItem.getMenuPrice_2());
                menuDataList.get(updateMenuIdx).setMenuThumb(NewMenuItem.getBitmapThumb());
                menuDataList = dbm.getMenuByCategory(selectedCategoryName);
                menuAdpater.SetMenuList(menuDataList);
                menuAdpater.notifyDataSetChanged();
                updateMenuIdx = -1;
            }

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        newMenu.setEnabled(true);
        selectedCategoryName = ((TextView)view.findViewById(R.id.category_element)).getText().toString();
        newMenu.setText( selectedCategoryName + "에 메뉴 추가");

        menuDataList.clear();
        menuDataList = dbm.getMenuByCategory(selectedCategoryName);

        menuAdpater.SetMenuList(menuDataList);              // DataList의 레퍼런스가 달라졌으므로, Setter로 데이터 리스트를 새로 지정
        menuAdpater.notifyDataSetChanged();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        // 카테고리 삭제

        final String name = ((TextView)view.findViewById(R.id.category_element)).getText().toString();
        // 삭제할 카테고리 이름을 가져온다 => selectedCategoryName이 아님!

        Log.i("Error Query","MenuName : " + name);

        AlertDialog.Builder dlg = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);

        dlg.setMessage("해당 카테고리를 삭제하시겠습니까?");
        dlg.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ManageMenuActivity.this, "취소되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        dlg.setNegativeButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("Error Query","selectedCategoryName : " + selectedCategoryName);

                int menu_fk = dbm.getCategoryPKByString(name);

//                if(menu_fk == -1) {
//                    Toast.makeText(ManageMenuActivity.this, name + " 삭제 오류" + "fk : " + menu_fk, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                else
                Toast.makeText(ManageMenuActivity.this, name + "을 삭제했습니다",Toast.LENGTH_SHORT).show();

//
                dbm.deleteCategoryWholeMenu(menu_fk);
                dbm.deleteCategory(name);
                // 먼저 카테고리에 속한 메뉴들을 제거한 뒤,
                // 카테고리 삭제

                menuDataList = dbm.selectWholeMenu();
                menuAdpater.SetMenuList(menuDataList);
                menuAdpater.notifyDataSetChanged();
                // 메뉴 카테고리에 포함된 리스트 내용을 먼저 지운 뒤 전체 메뉴 리스트를 불러온다.

                categoryListData.remove(position);
                categoryAdapter.notifyDataSetChanged();
                // 메뉴 카테고리를 지운다.

                newMenu.setText("메뉴 추가");
                newMenu.setEnabled(false);
                // 메뉴 추가 버튼을 비활성화 시킨다 -> 전체 카테고리를 불러온다.
            }
        });
        dlg.show();

        return true;
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        //view는 롱클릭한 위젯
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater mi = getMenuInflater();

        if(v.getId() == R.id.menu_elem_id){
            menu.setHeaderTitle("메뉴 수정/삭제");
            mi.inflate(R.menu.delete_or_modify_menu, menu);

            TextView t = (TextView)v.findViewById(R.id.menu_elem_name);
            controlMenu = t.getText().toString();                               // 수정 또는 삭제할 메뉴 이름을 가져온다.

        }

    }//end of ContextMenu()
    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {

        if(item.getItemId() == R.id.delete_menu) {                              // 메뉴 삭제
            for (int i = 0; i < menuDataList.size(); i++) {                     // 일치하는 이름이 나올때 까지 루프를 돈다.
                if (controlMenu.equals(menuDataList.get(i).getMenuName())) {    // 일치하는 이름이 존재할 경우
                    menuDataList.remove(i);                                     // 삭제한다.
                    menuAdpater.notifyDataSetChanged();
                    dbm.deleteMenu(controlMenu);                                // DB에서도 삭제
                    return true;
                }
            }
        }
        else if(item.getItemId() == R.id.modify_menu){
            Intent i = new Intent(this, ModifyMenuActivity.class);

            for(updateMenuIdx = 0; updateMenuIdx < menuDataList.size(); updateMenuIdx++){                            // 수정할 메뉴의 인덱스를 이름으로 알아온다.
                if(controlMenu.equals(menuDataList.get(updateMenuIdx).getMenuName()))
                    break;
            }

            i.putExtra("selectedMenu",menuDataList.get(updateMenuIdx));
            i.putExtra("categoryList",categoryListData);

            startActivityForResult(i,REQUEST_MDOFIED_MENU);

        }
        return false;
    }

}
