import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class OrderCard extends JPanel implements ItemListener{
	
	private OrderedItem orderedItem;
	private JLabel itemInfo;
	private JLabel memo;
	private JCheckBox checkComplete;

	public OrderCard(OrderedItem _orderedItem){
		
		orderedItem = _orderedItem;
		
		// Item Info
		String _menuName = orderedItem.getMenuName();
		String _isHot = orderedItem.getIsHot() ? "HOT" : "ICE";
		int _cupOfCount = orderedItem.getCupOfCount();
	
		// Memo
		String _orderMemo = orderedItem.getOrderMemo();
		
		// isComplete
		//_isComplete = orderedItem.isComplete();
		
		setBorder(BorderFactory.createLineBorder(Color.gray)); 

		Font menuNameFont = new Font(null, Font.BOLD, 25);
		Font memoFont	  = new Font(null, Font.BOLD, 22);
		
		itemInfo = new JLabel(_isHot + " " + _menuName + " " + _cupOfCount + "잔");
		itemInfo.setFont(menuNameFont);		
		memo = new JLabel(_orderMemo);
		memo.setFont(memoFont);
		checkComplete = new JCheckBox("제조 완료");		
		checkComplete.addItemListener(this);

		if(orderedItem.isComplete() == true){
			setBackground(Color.yellow);
			checkComplete.setSelected(true);
		}

	    setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

		add(itemInfo, gbc);
		add(memo, gbc);
		add(checkComplete, gbc);	
	}

	private ArrayList<OrderedItem> curOrderList;
	private JButton				   completeOrder;

	public void GetRefFromMain(ArrayList<OrderedItem> _curOrderList, JButton _completeOrder){
		curOrderList = _curOrderList;
		completeOrder = _completeOrder;
	}


	public void itemStateChanged(ItemEvent e){
		
		if(completeOrder != null){																	// NullPointerException 방지, 생성자와 레퍼런스를 받아오는 함수(GetRefFromMain)
																									// 두 함수를 실행하는 사이에 itemStateChanged가 호출되어, 레퍼런스를 받기도 전에
																									// 참조가 되기 때문에 NullPointerException이 발생함.
			completeOrder.setEnabled(true);															// 주문 완료버튼은 처음엔 무조건 Enable
	
			if(e.getStateChange() == ItemEvent.SELECTED){
				setBackground(Color.yellow);
				orderedItem.checkComplete(true);
			}
			
			else{
				setBackground(null);
				orderedItem.checkComplete(false);
			}
			
			for(int i = 0; i < curOrderList.size(); i++){
				
				if(curOrderList.get(i).isComplete() == false){										// 만약 하나라도 음료 제조가 완료되어있지 않으면. 주문 완료 버튼을 다시 disabled후 break
					completeOrder.setEnabled(false);
					break;
				}	// if
				
			}	// for
			
		}	// check null
	}

}