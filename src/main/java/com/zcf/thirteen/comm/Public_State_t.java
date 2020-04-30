package com.zcf.thirteen.comm;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.zcf.thirteen.bean.T_RoomBean;
import com.zcf.thirteen.bean.T_UserBean;
import com.zcf.thirteen.util.UtilClass;

public class Public_State_t {
		// 客户端的线程池
		public static Map<String, WebSocket> clients_t = new ConcurrentHashMap<String, WebSocket>();

		// 客户端的房间
		public static Map<String, T_RoomBean> PKMap1 = new LinkedHashMap<String, T_RoomBean>();
		public static Map<String, T_RoomBean> PKMap2 = new LinkedHashMap<String, T_RoomBean>();
		// 是否开启记录
		public static boolean record_bool = false;
		//解散时间等待
		public static int exit_time;
		static{
			exit_time = Integer.parseInt(UtilClass.utilClass.getTableName("/parameter.properties", "exit_time"));
		}
		public static int time;
		//游戏开始等待时间
		public static int start_time;
		public static int start_time1;
		public static int start_time2;
		public static int newstart_time;
		static{
			//用户倒计时
			time = Integer.parseInt(UtilClass.utilClass.getTableName("/parameter.properties", "time"));
			start_time = Integer.parseInt(UtilClass.utilClass.getTableName("/parameter.properties", "start_time"));
			start_time1 = Integer.parseInt(UtilClass.utilClass.getTableName("/parameter.properties", "start_time1"));
			start_time2 = Integer.parseInt(UtilClass.utilClass.getTableName("/parameter.properties", "start_time2"));
			newstart_time = Integer.parseInt(UtilClass.utilClass.getTableName("/parameter.properties", "newstart_time"));
		}
		/**
		 * 检测是否在房间中
		 * @param userid
		 * @return
		 */
		public static String ISUser_Room(int userid){
			for(String key:PKMap1.keySet()){
				T_RoomBean roomBean = PKMap1.get(key);
				for(T_UserBean userBean:roomBean.getGame_userList()){
					if(userBean.getUserid()==userid)return roomBean.getRoom_number();
				}
			}
			return null;
		}
}
