package com.example.oneday_cafe_pos;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddMenuActivity extends Activity implements View.OnClickListener {

    private TextView title;
    private EditText menuName;
    private EditText menuPrice1;
    private EditText menuPrice2;
    private Button register;
    private Button cancel;
    private Button changeImage;


    private String selectedCategory;
    private DBManager dbm;
    private MenuItem newMenu;
    private ImageView menuThumb;

    private static final int PICK_IMAGE_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);
        Init();
    }

    void Init() {

        dbm = DBManager._getInstance(this, null, 1);
        selectedCategory = getIntent().getStringExtra("Category");

        title = (TextView) findViewById(R.id.category_title);
        title.setText(selectedCategory + title.getText().toString());

        menuName = (EditText) findViewById(R.id.menu_name);
        menuPrice1 = (EditText) findViewById(R.id.menu_price1);
        menuPrice2 = (EditText) findViewById(R.id.menu_price2);
        menuThumb = (ImageView) findViewById(R.id.menu_img);

        changeImage = (Button) findViewById(R.id.change_menu_img);
        register = (Button) findViewById(R.id.register_menu);
        cancel = (Button) findViewById(R.id.cancel_register);

        changeImage.setOnClickListener(this);
        register.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    public void onClick(View v) {

        if (v.getId() == R.id.change_menu_img) {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(i, PICK_IMAGE_REQUEST);
        } else if (v.getId() == R.id.register_menu) {                                  // 메뉴 등록

            if (menuName.getText().toString().equals("") ||
                    menuPrice1.getText().toString().equals("") ||
                    menuPrice2.getText().toString().equals("")){
                Toast.makeText(this,"메뉴 정보를 다 채워주세요", Toast.LENGTH_SHORT).show();
                return;
            }
            BitmapDrawable drawable = (BitmapDrawable) menuThumb.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            String menu_name = menuName.getText().toString();
            int price1 = Integer.parseInt(menuPrice1.getText().toString());
            int price2 = Integer.parseInt(menuPrice2.getText().toString());

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);            // 비트맵 압축 후
            byte[] bytes = stream.toByteArray();                                        // byte 변수에 저장
            int fk = dbm.getCategoryPKByString(selectedCategory);                               // FK에 저장할 PK값을 불러옴

            newMenu = new MenuItem(menu_name, price1, price2, bytes);
            dbm.addMenu(bytes, menu_name, price1, price2, fk);

            Intent intent = new Intent();
            intent.putExtra("NewMenu", (Parcelable) newMenu);
            setResult(RESULT_OK, intent);
            finish();
        } else if (v.getId() == R.id.cancel_register) {            // 등록 취소
            Toast.makeText(this, "등록을 취소하셨습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                menuThumb.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}