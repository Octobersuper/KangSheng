/**
 * 
 */
package com.zcf.thirteen.comm;

import com.zcf.thirteen.bean.T_RoomBean;
import com.zcf.thirteen.bean.T_UserBean;
import com.zcf.thirteen.service.GameService;

/**
 * @author guolele
 * @date 2019年8月2日 上午10:39:40
 * 
 */
public class CreatRoom {

	
	/**
	 * 创建房间
	 * @param
	 * @return    
	 * @throws
	 */
	public static synchronized T_RoomBean EcoSocket() {
		String room_number = "";
		
		while (true) {
			for (int i = 0; i < 5; i++) {
				room_number += (int)(Math.random()*10);
			}
			
			System.out.println("4" + room_number);
			if (Public_State_t.PKMap1.get("4" + room_number) == null) {
				T_RoomBean rb = new T_RoomBean();
				//放入房间号
				rb.setRoom_number("4" + room_number);
				//把房间实例放入房间map
				Public_State_t.PKMap1.put("4" + room_number, rb);
				return rb;
			}
		}
	}

	
	/**
	 *
	 * @param userBean
	 * @param di_fen
	 * @param fen
	 * @param room_type
	 * @param brand_type
	 * @param gameService
	 * @return    
	 * @throws
	 */
	public static T_RoomBean EcoSocket3(T_UserBean userBean, int di_fen, int fen, int room_type, String brand_type,
										GameService gameService) {
		// TODO Auto-generated method stub
		return null;
	}

}
