import java.io.*;
import java.net.*;
import java.util.*;									// ArrayList
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;									// awt
import javax.swing.*;								// swing

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.util.ArrayList;

public class OrderList extends JFrame implements ActionListener{
	
	// Network
	
	private ServerSocket serverSock;				// 서버 소켓
	private Thread commuThread;

	private Socket clientSock;		
	private BufferedReader reader;
	private PrintWriter writer;						// 출력 스트림	

	private boolean isUpdate;						// Update 필요 여부, MainThread에서 작동함.
	private boolean isSendSomething;				// Thread Sending signal
	private String 	feedBackOrder;					// feedBack할 Order

	private static final String LOG_THREAD_SIDE 	= "CommSide";
	private static final String LOG_RECEIVED_MSG 	= "Received";
	private static final String LOG_SEND_MSG		= "Sent";
	private static final String LOG_MAIN_SIDE 		= "Main";
	
	// UI
	private Container rootPanel;							// JFrame의 최상부
	
	private JPanel orderListPanel;							// orderList의 최상부 Panel 
	private JPanel orderCardBoardPanel;						// orderCard를 나열하는 Panel
	private JLabel totalPrice;
	private JScrollPane cardBoardScrWrapper;				// cardBoard 스크롤 Wrapper
	private JButton cancelOrder;							// 주문 취소
	private JButton completeOrder;							// 주문 완료
	private JLabel orderNo;									//  현재 panel에  띄워져 있는 Order의  번호
	
	//private JPanel emptyPanel;
	
	private JPanel orderNoPanel;
	private JScrollPane orderNoPanelScrWrapper;
	private JLabel emptyOrder;								// 주문이 존재하지 않을 상황에 띄우는 알림
	
	// Data
	private JSONParser parser;
	private JSONArray jarr;
	
	//private ArrayList<OrderCard>				orderCard;	// GUI에 삽입할 주문 카드
	private ArrayList<ArrayList<OrderedItem>> 	orderInfo;
	private ArrayList<Integer> 					noIdx;		// 주문번호와 인덱스의 관계를 나타냄(해쉬)
	// 위 두 리스트는 동기화 되어야 함, 즉 noIdx에서 찾은 인덱스가 orderInfo내의 주문 객체를 올바르게 참조해야 함.
	
	private int orderIdx						= 0;		// 현재 보고있는 주문 묶음 인덱스
	private int orderItemIdx					= 0;		// 주문 묶음 내 아이템 인덱스
	private int curOrderNo						= 0;		// 현재 orderNo
	
	private static final String INIT_CONN_CODE	= "100";
	
	public static final int WINDOW_WIDTH		= 1000;
	public static final int WINDOW_HEIGHT		= 750;
	
	public static void main(String []args){

		OrderList orderList = new OrderList();
		// GUI를 사용해야 하므로 Main Thread에서는 무한루프를 사용할 수 없다.
		
	}
	
	public OrderList(){

		setTitle("Oneday Cafe 주문");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(640, 480);
		setVisible(true);

		JLabel waitConnectMsg;
		Font myFont1 = new Font(null, Font.BOLD, 20);
		
		waitConnectMsg = new JLabel("Application과의 연결을 대기중입니다...");
		waitConnectMsg.setHorizontalAlignment(JLabel.CENTER);
		waitConnectMsg.setFont(myFont1);
		getContentPane().add(waitConnectMsg);
		
		try{
			
			parser = new JSONParser();												// NullPointer 방지

			serverSock = new ServerSocket(5000);
			printLog(LOG_MAIN_SIDE, "server waiting for client...");
			clientSock = serverSock.accept();										// 클라이언트 요청 대기, 요청은 한번만 받음.
			printLog(LOG_MAIN_SIDE,"got a connection...");
			
			commuThread = new Thread(new CommSide());
			commuThread.start();	
			
			writer = new PrintWriter(clientSock.getOutputStream());	
			
			setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
			
			rootPanel = getContentPane();
			
			orderListPanel = new JPanel();
			orderCardBoardPanel = new JPanel();
			
			orderListPanel.setLayout(new BorderLayout());
			cardBoardScrWrapper = new JScrollPane(orderCardBoardPanel);			
			//orderCardBoardPanel.setBackground(Color.GREEN);
			
			JPanel buttonArea = new JPanel();
			buttonArea.setLayout(new BorderLayout());
			cancelOrder = new JButton("주문 취소");
			completeOrder = new JButton("주문 준비 완료");
			orderNo = new JLabel();
			totalPrice = new JLabel();
			
			cancelOrder.addActionListener(this);
			completeOrder.addActionListener(this);
			
			Font orderNoFont  = new Font(null, Font.BOLD, 20);
			
			cancelOrder.setMargin(new Insets(5,30,5,30));
			completeOrder.setMargin(new Insets(5,30,5,30));
			orderNo.setHorizontalAlignment(JLabel.CENTER);
			orderNo.setFont(orderNoFont);
			
			buttonArea.add(BorderLayout.WEST, cancelOrder);
			buttonArea.add(BorderLayout.EAST, completeOrder);			
			buttonArea.add(BorderLayout.CENTER, orderNo);
			
			Font totalPriceFont  = new Font(null, Font.BOLD, 20);
			
			cancelOrder.setMargin(new Insets(5,30,5,30));
			completeOrder.setMargin(new Insets(5,30,5,30));
			totalPrice.setHorizontalAlignment(JLabel.CENTER);
			totalPrice.setFont(totalPriceFont);
			
			orderListPanel.add(BorderLayout.CENTER, cardBoardScrWrapper);
			orderListPanel.add(BorderLayout.SOUTH, buttonArea);
			orderListPanel.add(BorderLayout.NORTH, totalPrice);
			
			orderNoPanel = new JPanel();
			orderNoPanel.setLayout(new BoxLayout(orderNoPanel, BoxLayout.Y_AXIS));		
			
			orderNoPanelScrWrapper = new JScrollPane(orderNoPanel);
			orderNoPanelScrWrapper.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			// 주문 번호 스크롤, 가로 스크롤 제거, 스크롤 래퍼
			
			rootPanel.add(BorderLayout.CENTER, orderListPanel);
			rootPanel.add(BorderLayout.EAST, orderNoPanelScrWrapper);
			
			Font fontObj = new Font(null, Font.BOLD, 40);
			
			emptyOrder = new JLabel("대기중인 주문이 없습니다.");
			emptyOrder.setHorizontalAlignment(JLabel.CENTER);
			emptyOrder.setFont(fontObj);
			
			rootPanel.add(BorderLayout.CENTER, emptyOrder);
			
			IsHasOrder(false);
			// 주문이 현재 존재하지 않으므로, GUI를 swap.
			
			//orderCard = new ArrayList<OrderCard>();
			orderInfo = new ArrayList<ArrayList<OrderedItem>>();
			noIdx = new ArrayList<Integer>();
			
			remove(waitConnectMsg);
			revalidate();
			repaint();			
			
		}
		catch(Exception ex){	
			ex.printStackTrace();
		}		
		
	}
	
	private void printJSON(JSONArray jarr){
		int size = jarr.size();
		for(int i = 0; i < size; i++){
			JSONObject obj = (JSONObject)jarr.get(i);
	
			printLog(LOG_THREAD_SIDE,"-------------------------------------------------");
			
			printLog(LOG_THREAD_SIDE, "isHot : " + obj.get("isHot"));
			printLog(LOG_THREAD_SIDE, "price : " + obj.get("price"));
			printLog(LOG_THREAD_SIDE, "cupOfCount : " + obj.get("cupOfCount"));
			printLog(LOG_THREAD_SIDE, "menuName : " + obj.get("menuName"));
			printLog(LOG_THREAD_SIDE, "orderMemo : " + obj.get("orderMemo"));
			printLog(LOG_THREAD_SIDE, "isCoupon : " + obj.get("isCoupon"));
			
		}
		
	}
	
	private void IsHasOrder(boolean isHas){													// 주문 존재 여부에 따라 Panel을 swap
		
		if(isHas){
			remove(emptyOrder);
			add(orderListPanel);
		}
		else{
			add(emptyOrder);
			remove(orderListPanel);
		}
		
		revalidate();
		repaint();
	
	}
	
	private /*synchronized*/ void UpdateOrderBoard(){										// 주문 보드 업데이트
		
		orderCardBoardPanel.removeAll();
		orderNoPanel.removeAll();
		
		completeOrder.setEnabled(true);														// 주문 완료버튼은 처음엔 무조건 enable
		
		if(noIdx.isEmpty() == false){
			
			for(int i = 0; i < noIdx.size(); i++){
				JButton orderButton = new JButton("" + noIdx.get(i));
				orderButton.setMargin(new Insets(20,100,20,100));							// 버튼 내부의 패딩.
				orderNoPanel.add(orderButton);
				orderButton.addActionListener(this);
				
				if(i == noIdx.size() - 1)
					orderButton.setBackground(Color.yellow);
					//orderButton.setForeground(Color.RED);
				
				
			}
			orderNo.setText("주문 번호 : "  + noIdx.get(orderIdx ));
			
		}
		
		if(orderInfo.isEmpty() == false){
		// 주문 아이템 리스트 추가
			ArrayList<OrderedItem> cardList = orderInfo.get(orderIdx);						// 현재 표시할 주문번호의 리스트, 가장 최신것
			orderCardBoardPanel.setLayout(new BoxLayout(orderCardBoardPanel, BoxLayout.Y_AXIS));
			int price = 0;
			
			JPanel orderRibbon;
			orderRibbon = new JPanel();
			for(int i = 0; i < cardList.size(); i++){
				
				price += cardList.get(i).getPrice();
				
				if(i % 2 == 0){
					orderRibbon = new JPanel();												// Panel 하나당 2개의 주문을 넣는다.
					orderRibbon.setLayout(new GridLayout(1, 2));							// 1행 2열로 생성
					orderCardBoardPanel.add(orderRibbon);
				}
				
				OrderCard orderCard = new OrderCard(cardList.get(i));
				orderCard.GetRefFromMain(cardList, completeOrder);							// 주문 완료 버튼을 컨트롤 하기 위해 레퍼런스를 넘김
				orderRibbon.add(orderCard);
				
				if(cardList.get(i).isComplete() == false)									// 만약 하나라도 음료 제조가 완료되어있지 않으면. 주문 완료 버튼을 다시 disable
					completeOrder.setEnabled(false);
				
				else continue;
					
			}
			
			
			totalPrice.setText(price + " 원");
			
			// GUI에 orderCard 삽입
			IsHasOrder(true);																// 주문이 존재하므로 OrderBoard를 보여줌
		
		}
		else IsHasOrder(false);
				
		revalidate();
		repaint();
		
	}


	public void actionPerformed(ActionEvent event) {
        
		if(orderInfo.isEmpty() == false && event.getSource() == cancelOrder){				// 주문 취소 버튼
				
			int reply = JOptionPane.showConfirmDialog(null, "주문을 삭제하시겠습니까?", "주문 삭제", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null);
			if(reply == 0){
				orderInfo.remove(orderIdx);
				noIdx.remove(orderIdx);
				orderIdx = 0;																// 주문을 취소하면 첫번째 주문창으로 돌아간다.
			}		
		}
		
		else if(event.getSource() == completeOrder){										// 주문 완료 버튼
			
			int reply = JOptionPane.showConfirmDialog(null, "음료가 다 준비되었습니까?", "음료 준비", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null);
			
			if(reply == 0){
				new PlayNoticeSound();
				printLog(LOG_MAIN_SIDE, "order was completed, try to feedback to client...");
				isSendSomething = true;
			
			}
		}
		else{																				// 주문 내역 버튼
			
			for(int i = noIdx.get(0); i <= noIdx.get(noIdx.size() - 1); i++){				// 루프를 돌면서 클릭된 버튼을 찾는다.
				
				if(event.getActionCommand().equals("" + i))
					orderIdx = noIdx.indexOf(i);											// 현재 보여지는 주문번호의 인덱스를 찾는다.
				
			}	// for
			
		}	// else

		//UpdateOrderBoard();
		isUpdate = true;
	}

	
	private void JSONParse(String received){

		try{
			// 받은 데이터를 파싱하여 리스트에 업데이트한다.
			
			jarr = (JSONArray)parser.parse(received);
			//printJSON(jarr);			
			ArrayList<OrderedItem> orderSet = new ArrayList<OrderedItem>();
			
			int size = jarr.size();
			for(int i = 0; i < size; i++){
				JSONObject obj = (JSONObject)jarr.get(i);
				orderSet.add(new OrderedItem((Boolean)obj.get("isHot"), 
											(int)(long)obj.get("price"),
											(int)(long)obj.get("cupOfCount"),
											(String)obj.get("menuName"),
											(String)obj.get("orderMemo"),
											(int)(long)obj.get("isCoupon") ));	
			}	// 데이터 추가 완료.
			
			noIdx.add(Integer.valueOf(curOrderNo));
			orderInfo.add(orderSet);
			//UpdateOrderBoard();
			isUpdate = true;					// 보드 초기화
			curOrderNo++;						// 다음 주문의 번호를 가리킴. (0번 주문이 들어오면 1번이 됨)
	
		}catch(ParseException pe){
		
			printLog(LOG_THREAD_SIDE,"error occured! while parse json...");
			pe.printStackTrace();
			
		}
			
	}

	private void printLog(String side, String msg){
		System.out.println(side + "> " + msg);
	}

	private class CommSide implements Runnable{	// 네트워크 쓰레드
		
		public void run(){

			printLog(LOG_THREAD_SIDE, "thread is activate...");
			
			try{

				InputStreamReader isReader = new InputStreamReader(clientSock.getInputStream(), "UTF-8");
				reader = new BufferedReader(isReader);	
				
				while(true){

					String receivedText = "";
					
					if(reader.ready()){														// 전송받은 데이터가 존재할 경우
						receivedText = reader.readLine();
						printLog(LOG_THREAD_SIDE,"received json from client...");
						printLog(LOG_RECEIVED_MSG, receivedText);

						if(receivedText.equals(INIT_CONN_CODE)){
							printLog(LOG_THREAD_SIDE, "got a init code " + INIT_CONN_CODE);
							continue;
						}
						
						JSONParse(receivedText);
					}
					
					if(isSendSomething){

						//printLog(LOG_THREAD_SIDE, "isWorking?");
						
						ArrayList<OrderedItem> feedbackList = orderInfo.get(orderIdx);
						JSONArray json = new JSONArray();
							
						for (int i = 0; i < feedbackList.size(); i++){
							JSONObject obj = new JSONObject();
							obj.put("isHot", feedbackList.get(i).getIsHot());
							obj.put("cupOfCount", feedbackList.get(i).getCupOfCount());
							obj.put("menuName", feedbackList.get(i).getMenuName());
							obj.put("isCoupon", feedbackList.get(i).getIsCoupon());
							
							json.add(obj);
						}
						writer.println(json.toString());
						writer.flush();
						
						printLog(LOG_SEND_MSG, json.toString());
						
						orderInfo.remove(orderIdx);
						noIdx.remove(orderIdx);
						orderIdx = 0;
						//UpdateOrderBoard();
						isUpdate = true;
						isSendSomething = false;						
					}
					
					if(isUpdate){
						UpdateOrderBoard();
						isUpdate=false;
					}
					// feedback, 음료 제조가 완료되어 주문이 종료되면 다시 클라이언트에게 주문 정보를 JSON양식으로 보내 SQLite에 저장하게 한다.
					
					// echo test, 받은 데이터를 그대로 클라이언트에게 전달함.


					// To do... Descript Send Algorithm
					//printLog(LOG_THREAD_SIDE,"wait POS Application request...");
				
					if(receivedText.equals("QUIT"))
						break;
					
				}
				
				reader.close();
				writer.close();
				clientSock.close();
				
			}	// try
			catch(IOException io){
				printLog(LOG_THREAD_SIDE,"error occured! while read stream...");
				io.printStackTrace();
			}	// catch
		}	// run	
	}	// CommSide
}
	
