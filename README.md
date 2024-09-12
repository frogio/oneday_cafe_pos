# aNyang Cafe Pos
안양시 만안구 보건소 정신 재활센터의 프로그램인 내맘아냥 카페를 위한 POS 어플리케이션

## 사용 기술
DB 			: MariaDB
Back-end 	: node js, express js(http, webSocket Trasnsmission)
Front-end	: Vue.js, Vuetify, kotlin

## 구성

### 1. aNyang Cafe Pos Server application
express js와 node js 기반 서버, Front-end 애플리케이션과 DB 컨트롤을 지원함

### 2. aNyang Cafe Pos Client application
Vue.js와 Vuetify 기반으로 제작된 클라이언트

#### 2 - 1. aNyang Cafe Pos Client
서버로 부터 음료 정보를 받아온 후, 사용자로부터 주문을 받는 애플리케이션, 서버와 WebSocket을 통해 실시간으로 주문 정보를 주고받음.
kotlin으로 만들어진 WebView 안드로이드 어플리케이션으로 래핑되어 제공됨. 


#### 2 - 1. aNyang Cafe Pos Management
서버와 통신하며 음료 정보, 주문정보, 정산 기능을 수행하는 애플리케이션, 주로 http통신을 통해 서버와 통신한다.
주문 정보에선 Pos Client가 주문을 받으면 받은 주문을 서버로 부터 WebSocket으로 받아와 사용자에게 띄워준다.

## 개발 및 실행 환경
Window 10, node.js, express.js, vue, vuetify, android kotlin

## 실행 모습

![메뉴관리](https://github.com/user-attachments/assets/e4e15fb7-51e5-456a-b7aa-e30fd17d1600)
![주문관리](https://github.com/user-attachments/assets/14b613de-7c4f-46fb-b99b-a195ac5050d5)
![정산](https://github.com/user-attachments/assets/3fd94290-b403-48d7-bec7-93c4e3b9973e)
![주문](https://github.com/user-attachments/assets/396bbbb2-d38d-4e07-a789-f8c46b67464b)



## 버전 업데이트 내역

#### 1.0 Ver
초기 버전

#### 1.1 Ver
정산 정정기능 추가

# Oneday cafe pos Ver 1.11 (구버전)
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
자동으로 프로그램과 연결 종료
</li>
</ul>
