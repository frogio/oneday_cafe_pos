package com.example.oneday_cafe_pos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NetworkManager{

    private CheckForeground checkForeground;
    private static NetworkManager networkManager;
    private static boolean isConnNetwork    = false;                    // 로컬 또는, PC와 연동여부를 나타내는 플래그
    private Context c;

    private boolean isBackground            = false;


    private static final String IP_ADDRESS  = "192.168.0.7";
    private static final int    PORT_NUM    = 5000;
    private boolean isSendSomthing          = false;

    private Thread CommuThread;

    private Socket sock;
    private BufferedReader reader;                                      // 서버로 부터 데이터 받기
    private PrintWriter writer;                                         // 서버로 부터 데이터 주기

    private String outputStream;                                        // 전송 텍스트
    private String inputStream;                                         // 수신 텍스트

    private static final String CODE_ALERT_CONN = "100";                 // 프로그램이 연결되었음을 알리는 코드
    private static final int HANDLER_AUTO_DISCONN = -1;                  // 애플리케이션이 Background 상태일 때 자동 종료 코드
    private boolean AUTO_DISCONN = false;

    private DBManager dbm;

    private NetworkManager(Context _c){
        checkForeground = CheckForeground._getInstance();
        c = _c;
    }

    public static NetworkManager _getInstance(Context _c){
        if(networkManager == null)
            networkManager = new NetworkManager(_c);

        return networkManager;
    }

    public void StartNetwork(){

        isConnNetwork = true;
        CommuThread = new Thread(new NetworkThread());
        CommuThread.start();

        dbm = DBManager._getInstance(c, null, 1);

        SendConnStatus(CODE_ALERT_CONN);

    }

    private void SetupNetwork(){
        try {
            sock = new Socket(IP_ADDRESS, PORT_NUM);                            // 연결 요청
            writer = new PrintWriter(new OutputStreamWriter(sock.getOutputStream(),"UTF-8"), true);                   // 출력용 Stream

            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream(), "UTF-8");
                                                                                            // 통신 인코딩을 UTF-8로 맞춘다!!!!! 중요!!!!!
            reader = new BufferedReader(streamReader);                          // socket으로 부터 데이터를 입력받음. (입력용 스트림)
            // socket -> InputStreamReader -> BufferedReader
        }
        catch(IOException io){
            Log.i("Connect Error", io.toString());
        }
    }

    public static boolean IsConnNetwork()  {return isConnNetwork;}                     // PC와 연동되었는지에 대한 여부 검사

    private class NetworkThread implements Runnable{

        @Override
        public void run() {
            SetupNetwork();                                                     // 네트워크 연결
            try {
                while(true) {

                    if(checkForeground.getAppStatus() >= 1){                    // 앱이 포그라운드일 경우, 1보다 클 경우 Foreground
                        if(isBackground) {                                      // 2분 안에 앱이 백그라운드에서 포그라운드로 들어올 경우
                            isBackground = false;                               // 쓰레드 타이머 종료
                            mHandler.removeMessages(HANDLER_AUTO_DISCONN);
                        }
                    }
                    else if(checkForeground.getAppStatus() < 1){                // 앱이 백그라운드일 경우 Thread 종료
                       // SendConnStatus(CODE_ALERT_APP_IS_BACK);
                        if(isBackground == false){
                            mHandler.sendEmptyMessageDelayed(HANDLER_AUTO_DISCONN, 120 * 1000);      // 2분 뒤 쓰레드 종료
                            isBackground = true;
                        }
                    }

                    if(AUTO_DISCONN) {
                        Log.i("Disconn","Disconnect");
                        break;
                    }

                    if(isSendSomthing) { // 뭔가 보낼 정보가 존재할 경우
                        writer.println(outputStream);
                        writer.flush();
//                        Log.i("isSending?", outputStream);
                        isSendSomthing = false;
                    }
                    // writer.write("isWrite?\n");
                    // \n가 보낼 문자열의 끝임을 알림.
                    // println은 자동으로 '\n'이 붙음. 따라서 \n을 따로 붙일 필요 없음.
                    // \n을 붙이고 flush를 해주어야 제대로 전송이 됨.

                    else if(reader.ready()){	            // 전송받을 데이터가 존재할 경우
                        inputStream = reader.readLine();
                        JSONArray json = new JSONArray(inputStream);

                        try {
                            for (int i = 0; i < json.length(); i++) {
                                //Log.i("Reader", "isInLoop?");
                                JSONObject obj = json.getJSONObject(i);
                                //Log.i("Reader", "isInLoop?2");
                                int fk = dbm.getDrinkPKByName(obj.getString("menuName"));
                                //Log.i("Reader", "isInLoop?3");

                                dbm.isServeComplete(
                                        obj.getInt("cupOfCount"),
                                        obj.getBoolean("isHot") ? 1 : 0,
                                        fk,
                                        obj.getInt("isCoupon"));
                            } // for


                        } // try
                        catch(JSONException je){
                            Log.e("JSONException", je.toString());
                        } // catch JSONException

                        Log.i("DEBUG_RECEIVED_TEXT", inputStream);
                    }

                    if(outputStream.equals("QUIT"))
                        break;

                }

                writer.close();
                reader.close();
                sock.close();       // 소켓먼저 닫아버리면, 전송이 제대로 안됨... 반드시 마지막에 처리해야 함.
                Log.i("Disconn","Exit");
                AUTO_DISCONN = false;
            }
            catch(Exception ex){
                    Log.e("Error of Receive Text", ex.toString());
            }


        }   // run

    }   // inner class Receive

    private void SendConnStatus(String Code){
        outputStream = Code;
        isSendSomthing = true;
    }
/*
    private void SendInitCode(){
        outputStream = CODE_ALERT_CONN;
        isSendSomthing = true;
    }*/

    public void SendJSON(String str){
        outputStream = str;
        isSendSomthing = true;
    }
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == HANDLER_AUTO_DISCONN) {
                if(isBackground)
                    AUTO_DISCONN = true;
                else
                    AUTO_DISCONN = false;
            }
        }

    };

}
