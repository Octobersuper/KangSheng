/**
 * 
 */
package com.zcf.thirteen.comm;

import com.zcf.thirteen.bean.T_RoomBean;
import com.zcf.thirteen.bean.T_UserBean;

/**
 * @author guolele
 * @date 2019年8月2日 上午10:39:55
 * 
 */
public class MatchingRoom {

	
	/**
	 *
	 * @param userBean
	 * @param key
	 * @return    
	 * @throws
	 */
	public static T_RoomBean Matching3(T_UserBean userBean, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 *
	 * @param userBean
	 * @param
	 * @return    
	 * @throws
	 */
	public static T_RoomBean Matching(T_UserBean userBean, String room_number) throws InterruptedException {
		T_RoomBean rb = Public_State_t.PKMap1.get(room_number);
		rb.getLock().lock();
		//当前房间未满
		if (rb.getGame_userList(0).size()<8) {
			rb.getGame_userList(0).add(userBean);
			if (rb.getGame_number() == 1 && rb.getGame_userList(0).size() == 1) {
				rb.setBranker_id(userBean.getUserid());
			}
			//添加自己的座位
			//rb.setUser_positions(userBean);
			//初始化用户
			//userBean.Initialization();
			rb.getLock().unlock();
			return rb;
			
		}
		rb.getLock().unlock();
		return null;
	}

}
