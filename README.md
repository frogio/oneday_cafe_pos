# Oneday cafe pos Ver 1.11
안양시 만안구 보건소 정신 재활센터의 프로그램인 원데이 카페를 위한 POS 어플리케이션

## 구성

### 1. Oneday cafe POS Tablet application
Android 기반으로 카페에서 주문을 입력받는 어플리케이션, 태블릿 환경에서 작동함.

### 2. Oneday cafe POS OrderList application
Android 어플리케이션에서 주문을 입력받으면, 받은 주문의 음료 내역들을 띄워주는 프로그램
이 프로그램으로 띄워진 주문된 음료 내역들을 제조자들이 보고 음료들을 준비함.

## 개발 및 실행 환경
Window 10, Ubuntu 16.02, Android(Java), Android Studio, Samsung Galaxy Tab S7 (FE), Java 8.0 / Swing, Notepad++

## 실행 모습
![20230907_112619](https://github.com/frogio/oneday_cafe_pos/assets/12217092/0821170a-454a-42e9-89e6-4e86f3ba90c9)
![20230907_112637](https://github.com/frogio/oneday_cafe_pos/assets/12217092/893f5e21-e597-4c58-b20f-a2c0a8a9db7c)
![20230907_112644](https://github.com/frogio/oneday_cafe_pos/assets/12217092/60e24274-dc28-4167-b119-b0549207db3f)

## 버전 업데이트 내역

#### 1.0 Ver
초기 버전

#### 1.1 Ver
<ul>
<li>클라이언트 애플리케이션과 서버 프로그램(OrderList) 통신간 인코딩이 통일되지 않아</br>
글자가 깨지면서 발생하는 복합적인 버그 해결</br></li>
<li>OrderList 프로그램 UI 개선</li>
</ul>

#### 1.11 Ver
<ul>
<li>OrderList 프로그램과 연동 중 애플리케이션이 포그라운드에서 나간 상태가 2분간 지속되면</br>
자동으로 OrderList 프로그램과 연결 종료
</li>
</ul>

