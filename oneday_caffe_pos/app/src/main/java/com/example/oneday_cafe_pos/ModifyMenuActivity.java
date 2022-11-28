package com.example.oneday_cafe_pos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ModifyMenuActivity extends Activity implements View.OnClickListener{

    private EditText menuName;
    private EditText Price1;
    private EditText Price2;
    private ImageView imageThumb;

    private Button changeMenuImage;
    private Button changeMenu;
    private Button cancelChange;

    private ArrayList<String> categoryList;
    private ArrayAdapter<String> arrayAdapter;
    private Spinner categorySelect;

    private DBManager dbm;
    private String prevMenuName;
    private static final int PICK_IMAGE_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_menu);
        Init();
    }


    public void Init(){
        dbm = DBManager._getInstance(this, null, 1);

        Intent i = getIntent();
        MenuItem item = i.getParcelableExtra("selectedMenu");
        categoryList = i.getStringArrayListExtra("categoryList");

        menuName = (EditText)findViewById(R.id.modify_menu_name);
        Price1 = (EditText)findViewById(R.id.modify_menu_price1);
        Price2 = (EditText)findViewById(R.id.modify_menu_price2);
        imageThumb = (ImageView)findViewById(R.id.modify_menu_img);
        changeMenuImage = (Button)findViewById(R.id.change_menu_img);
        changeMenu = (Button)findViewById(R.id.modify_menu);
        cancelChange =(Button)findViewById(R.id.cancel_modify);
        categorySelect = (Spinner)findViewById(R.id.change_category);

        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,categoryList);
        categorySelect.setAdapter(arrayAdapter);

        changeMenuImage.setOnClickListener(this);
        changeMenu.setOnClickListener(this);
        cancelChange.setOnClickListener(this);

        prevMenuName = item.getMenuName();
        menuName.setText(item.getMenuName());
        Price1.setText("" + item.getMenuPrice_1());
        Price2.setText("" + item.getMenuPrice_2());

        Bitmap bitmap = BitmapFactory.decodeByteArray(item.getBitmapThumb(),0,item.getBitmapThumb().length);
        imageThumb.setImageBitmap(bitmap);
    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.modify_menu){

            if (menuName.getText().toString().equals("") ||
                    Price1.getText().toString().equals("") ||
                    Price2.getText().toString().equals("")){
                Toast.makeText(this,"메뉴 정보를 다 채워주세요", Toast.LENGTH_SHORT).show();
                return;
            }


            BitmapDrawable drawable = (BitmapDrawable) imageThumb.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            String menu_name = menuName.getText().toString();
            int price1 = Integer.parseInt(Price1.getText().toString());
            int price2 = Integer.parseInt(Price2.getText().toString());

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);             // 비트맵 압축 후
            byte[] bytes = stream.toByteArray();                                        // byte 변수에 저장
            int fk = dbm.getCategoryPKByString(categorySelect.getSelectedItem().toString());                             // 카테고리 이름으로 FK에 저장할 PK값을 불러옴
            dbm.ModifyMenu(bytes, menu_name, price1, price2, fk, prevMenuName);

            MenuItem newMenu = new MenuItem(menu_name, price1, price2, bytes);

            Intent intent = new Intent();
            intent.putExtra("NewMenu", (Parcelable) newMenu);
            setResult(RESULT_OK, intent);
            finish();

        }
        else if(v.getId() == R.id.cancel_modify){
            Toast.makeText(this, "변경을 취소하셨습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
        else if(v.getId() == R.id.change_menu_img){
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(i, PICK_IMAGE_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageThumb.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}