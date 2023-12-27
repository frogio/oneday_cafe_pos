package com.example.oneday_cafe_pos;

import android.app.Application;
import android.util.Log;

public class OnedayCafeApp extends Application {

    //private CheckForeground checkForeground;

    @Override
    public void onCreate(){
        super.onCreate();
        CheckForeground.init(this);             // 앱이 Foreground인지 체크하는 객체 생성
    }

}
