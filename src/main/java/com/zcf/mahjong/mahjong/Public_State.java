package com.zcf.mahjong.mahjong;

import com.zcf.mahjong.bean.RoomBean;
import com.zcf.mahjong.bean.UserBean;
import com.zcf.mahjong.dao.M_GameDao;
import com.zcf.mahjong.service.Mahjong_Socket;
import com.zcf.mahjong.util.BaseDao;
import com.zcf.mahjong.util.UtilClass;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Public_State {
	// 客户端的线程池(房卡)
	public static Map<String, Mahjong_Socket> clients = new ConcurrentHashMap<String, Mahjong_Socket>();
	// 客户端的房间
	public static Map<String, RoomBean> PKMap = new LinkedHashMap<String, RoomBean>();
	// windows窗口
	//public static CreaWindow window = new CreaWindow();
	// 是否开启记录
	public static boolean record_bool = false;
	// 解散时间等待
	public static int exit_time;
	// 2人支付
	public static String[] establish_two;
	// 4人支付
	public static String[] establish_four;
	public static int time=Integer.parseInt(UtilClass.utilClass.getTableName("/parameter.properties", "exit_time"));
	// 初始钻石
	public static int diamond;
	public static String[] sorts = new String[] { "1-2-3", "1-3-2", "2-1-3", "2-3-1", "3-1-2", "3-2-1", "4-2-3",
			"4-3-2", "2-4-3", "2-3-4", "3-4-2", "3-2-4" };
	// 操作日志记录类型
	public static String[] types = new String[] { "out_brand", "bump", "endhu", "hide_bar", "show_bar",
			"show_bar_brand", "repair_bar_bump", "get_brand" ,"flow"};
	static {
		// CheckOut.checkOut();
		diamond = Integer.parseInt(UtilClass.utilClass.getTableName("/parameter.properties", "diamond"));
		// 启动windows窗口
		// window.CreateWindow();
		BaseDao baseDao = new BaseDao();
		M_GameDao gameDao = new M_GameDao(baseDao);
		// 读取配置
		gameDao.getConfig();
		baseDao.CloseAll();
		baseDao = null;
		gameDao = null;
	}

	/**
	 * 检测是否在房间中
	 * 
	 * @param userid
	 * @return
	 */
	public static String ISUser_Room(int userid) {
		//麻将
		for (String key : PKMap.keySet()) {
			RoomBean roomBean = PKMap.get(key);
			for (UserBean userBean : roomBean.getGame_userlist()) {
				if (userBean.getUserid() == userid)
					return roomBean.getRoomno();
			}
		}
		return null;
	}
}
