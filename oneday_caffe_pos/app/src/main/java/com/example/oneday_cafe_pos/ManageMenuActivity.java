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
        // ?????? ??????????????? ???????????? ????????? ??????

        categoryList.setAdapter(categoryAdapter);
        categoryList.setOnItemClickListener(this);
        categoryList.setOnItemLongClickListener(this);

        menuDataList = dbm.selectWholeMenu();
        menuAdpater = new MenuDataAdapter(this, R.layout.menu_element, menuDataList);
        menuList.setAdapter(menuAdpater);

        // ????????? ????????? ??????
    }

    public void onClick(View v) {
        if (v.getId() == R.id.new_menu_category) {
            //Toast.makeText(this, "????????? ?????? ????????????", Toast.LENGTH_SHORT).show();
            catergoryAddWindow = View.inflate(this, R.layout.add_menu_category, null);

            AlertDialog.Builder dlg = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);
            dlg.setView(catergoryAddWindow);
            dlg.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EditText InputName = (EditText) catergoryAddWindow.findViewById(R.id.category_name);
                    String newCategoryName = InputName.getText().toString();
                    dbm.addCategory(newCategoryName);
                    categoryListData.add(newCategoryName);
                    categoryAdapter.notifyDataSetChanged();
                }
            });

            dlg.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(ManageMenuActivity.this, "????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                }
            });
            dlg.show();

        }
        else if (v.getId() == R.id.new_menu) {                  // ????????? ?????? ??????

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
            // AddMenuActivty?????? parcel??? ????????? ???????????? ????????????.

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
        newMenu.setText( selectedCategoryName + "??? ?????? ??????");

        menuDataList.clear();
        menuDataList = dbm.getMenuByCategory(selectedCategoryName);

        menuAdpater.SetMenuList(menuDataList);              // DataList??? ??????????????? ??????????????????, Setter??? ????????? ???????????? ?????? ??????
        menuAdpater.notifyDataSetChanged();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        // ???????????? ??????

        final String name = ((TextView)view.findViewById(R.id.category_element)).getText().toString();
        // ????????? ???????????? ????????? ???????????? => selectedCategoryName??? ??????!

        Log.i("Error Query","MenuName : " + name);

        AlertDialog.Builder dlg = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);

        dlg.setMessage("?????? ??????????????? ?????????????????????????");
        dlg.setPositiveButton("?????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ManageMenuActivity.this, "?????????????????????.", Toast.LENGTH_SHORT).show();
            }
        });

        dlg.setNegativeButton("???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("Error Query","selectedCategoryName : " + selectedCategoryName);

                int menu_fk = dbm.getCategoryPKByString(name);

//                if(menu_fk == -1) {
//                    Toast.makeText(ManageMenuActivity.this, name + " ?????? ??????" + "fk : " + menu_fk, Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                else
                Toast.makeText(ManageMenuActivity.this, name + "??? ??????????????????",Toast.LENGTH_SHORT).show();

//
                dbm.deleteCategoryWholeMenu(menu_fk);
                dbm.deleteCategory(name);
                // ?????? ??????????????? ?????? ???????????? ????????? ???,
                // ???????????? ??????

                menuDataList = dbm.selectWholeMenu();
                menuAdpater.SetMenuList(menuDataList);
                menuAdpater.notifyDataSetChanged();
                // ?????? ??????????????? ????????? ????????? ????????? ?????? ?????? ??? ?????? ?????? ???????????? ????????????.

                categoryListData.remove(position);
                categoryAdapter.notifyDataSetChanged();
                // ?????? ??????????????? ?????????.

                newMenu.setText("?????? ??????");
                newMenu.setEnabled(false);
                // ?????? ?????? ????????? ???????????? ????????? -> ?????? ??????????????? ????????????.
            }
        });
        dlg.show();

        return true;
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        //view??? ???????????? ??????
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater mi = getMenuInflater();

        if(v.getId() == R.id.menu_elem_id){
            menu.setHeaderTitle("?????? ??????/??????");
            mi.inflate(R.menu.delete_or_modify_menu, menu);

            TextView t = (TextView)v.findViewById(R.id.menu_elem_name);
            controlMenu = t.getText().toString();                               // ?????? ?????? ????????? ?????? ????????? ????????????.

        }

    }//end of ContextMenu()
    @Override
    public boolean onContextItemSelected(android.view.MenuItem item) {

        if(item.getItemId() == R.id.delete_menu) {                              // ?????? ??????
            for (int i = 0; i < menuDataList.size(); i++) {                     // ???????????? ????????? ????????? ?????? ????????? ??????.
                if (controlMenu.equals(menuDataList.get(i).getMenuName())) {    // ???????????? ????????? ????????? ??????
                    menuDataList.remove(i);                                     // ????????????.
                    menuAdpater.notifyDataSetChanged();
                    dbm.deleteMenu(controlMenu);                                // DB????????? ??????
                    return true;
                }
            }
        }
        else if(item.getItemId() == R.id.modify_menu){
            Intent i = new Intent(this, ModifyMenuActivity.class);

            for(updateMenuIdx = 0; updateMenuIdx < menuDataList.size(); updateMenuIdx++){                            // ????????? ????????? ???????????? ???????????? ????????????.
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
