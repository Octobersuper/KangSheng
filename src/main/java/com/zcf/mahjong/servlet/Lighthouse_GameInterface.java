package com.zcf.mahjong.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.zcf.mahjong.bean.RoomBean;
import com.zcf.mahjong.bean.UserBean;
import com.zcf.mahjong.dao.M_LoginDao;
import com.zcf.mahjong.dao.Mg_GameDao;
import com.zcf.mahjong.mahjong.Public_State;
import com.zcf.mahjong.util.BaseDao;
import com.zcf.mahjong.util.UtilClass;
import com.zcf.thirteen.bean.T_RoomBean;
import com.zcf.thirteen.comm.Public_State_t;
import com.zcf.thirteen.comm.WebSocket;
import com.zcf.thirteen.service.GameService;

/**
 * 前端接口
 * 
 * @author Administrator
 *
 */
@WebServlet("/out")
public class Lighthouse_GameInterface extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Map<String, Object> returnMap = new HashMap<String, Object>();
	private Gson gson = new Gson();

	public Lighthouse_GameInterface() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 输出转码
		response.setContentType("text/json;charset=UTF-8");
		// 接受转码
		request.setCharacterEncoding("UTF-8");
		String type = request.getParameter("type");
		/* 允许跨域的主机地址 */
		response.setHeader("Access-Control-Allow-Origin", "*");
		/* 允许跨域的请求方法GET, POST, HEAD 等 */
		response.setHeader("Access-Control-Allow-Methods", "*");
		/* 重新预检验跨域的缓存时间 (s) */
		response.setHeader("Access-Control-Max-Age", "3600");
		/* 允许跨域的请求头 */
		response.setHeader("Access-Control-Allow-Headers", "*");
		/* 是否携带cookie */
		response.setHeader("Access-Control-Allow-Credentials", "true");
		BaseDao baseDao = new BaseDao();
		//接口
		Mg_GameDao mg_GameDao = new Mg_GameDao(baseDao);
		// 接收参数(解密后返回)
		Map<String, Object> requestmap = null;
		returnMap.clear();
		if (type != null) {
			// 接收参数(解密后返回)
			requestmap = UtilClass.utilClass.getRequestAdd(request, "/request.properties", type);
			System.out.println(requestmap.toString());
		}
		// 登录
		if ("login".equals(type)) {

			M_LoginDao loginDao = new M_LoginDao(baseDao);
			Map<String, Object> map = new HashMap<String, Object>();
			UserBean userBean = loginDao.getUser(requestmap);
			if (userBean == null) {
				// 添加此用户
				String state = loginDao.adduser(requestmap);
				if (!state.equals("success")) {
					// 记录错误日志
					// 异常
					returnMap.put("state", "999");
				} else {
					// 重新查询此用户
					userBean = loginDao.getUser(requestmap.get("openid").toString());
				}
			}
			if (userBean.getState() == 1) {
				// 账号被封
				map.put("state", "101");
				returnMap.put("user", map);
			} else {
				//删除三天前战绩记录
				mg_GameDao.delRecord();
				returnMap.put("state", "0");
				userBean.getUser_Custom("fId-nickname-avatarurl-userid-diamond-openid-sex", returnMap);
				if(Public_State.clients.get(userBean.getOpenid())!=null){
					returnMap.put("state", "320"); //重复登录
				}
				// 检测是否需要重连
				//String roomno = Public_State.ISUser_Room(userBean.getUserid());
				// 检测是否需要重连
				//String roomno2= Public_State_t.ISUser_Room(userBean.getUserid());
				// 需要重连
				//if (roomno != null || roomno2 != null) {
				//	returnMap.put("state", "310");
				//	returnMap.put("roomno", roomno==null?roomno2:roomno);
				//}
				System.out.println(returnMap);
			}
		}

		// 查看玩家信息
		if ("getUser".equals(type)) {
			returnMap.put("type_s", type);
			int userid = Integer.parseInt(request.getParameter("userid"));
			Map<String, Object> user = mg_GameDao.getUserById(userid);
			returnMap.put("UserBean", user);
			returnMap.put("user_state", user==null?0:1);
			returnMap.put("state", "-1");
		}

		// 绑定邀请码
		if ("selectCode".equals(type)) {
			returnMap.put("type_s", type);
			int userid = Integer.parseInt(request.getParameter("userid"));
			int fId = Integer.parseInt(request.getParameter("fId"));
			returnMap.put("code_state",mg_GameDao.seelctCode(fId,userid));
			returnMap.put("state", "-1");
		}

		// 更改个性签名
		if ("update_remark".equals(type)) {
			returnMap.put("type_s", type);
			int userid = Integer.parseInt(request.getParameter("userid"));
			String remark = request.getParameter("remark");
			mg_GameDao.update_remark(userid,remark);
			returnMap.put("state", "-1");
		}

		// 查看金币商品1
		if ("ckjin".equals(type)) {
			returnMap.put("type_s", type);
			returnMap.put("ckjin", mg_GameDao.ckjin());
		}

		// 查看钻石商品1
		if ("ckzuan".equals(type)) {
			returnMap.put("type_s", type);
			returnMap.put("ckzuan", mg_GameDao.ckzuan());
			returnMap.put("diamond", mg_GameDao.getUserDiamond(Integer.parseInt(request.getParameter("userid"))));
			returnMap.put("service",mg_GameDao.service());
			returnMap.put("state", "-1");
		}

		// 查看客服信息1
		if ("ckservice".equals(type)) {
			returnMap.put("type_s",type);
			returnMap.put("ckservice", mg_GameDao.ckservice());
			returnMap.put("state", "-1");
		}
		// 查看当日任务1
		if ("getdaily".equals(type)) {
			returnMap.put("type_s",type);
			int userid = Integer.parseInt(request.getParameter("userid"));
			returnMap.put("getdaily", mg_GameDao.getdaily(userid));
		}

		// 查看系统消息1
		if ("cksystem".equals(type)) {
			returnMap.put("type_s",type);
			returnMap.put("cksystem", mg_GameDao.cksystem());
		}

		// 签到1
		if ("sign".equals(type)) {
			returnMap.put("type_s",type);
			returnMap.put("sign_type", mg_GameDao.sign(Integer.parseInt(request.getParameter("userid")),
					Integer.parseInt(request.getParameter("signid"))));
			returnMap.put("state", "-1");
		}
		// 查看签到记录1
		if ("signrecord".equals(type)) {
			returnMap.put("type_s", type);
			returnMap.put("signrecord", mg_GameDao.signrecord(Integer.parseInt(request.getParameter("userid"))));
			returnMap.put("awardlist", mg_GameDao.cksign());
			returnMap.put("state", "-1");
		}
		/***************************  分享  ****************************/
		if("getShare".equals(type)){
			returnMap.put("share", mg_GameDao.getShare());
		}
		// 查看轮播图1
		if ("cklun".equals(type)) {
			returnMap.put("cklun", mg_GameDao.cklun());
		}
		// 提现1
		if ("withdraw".equals(type)) {
			int money = Integer.parseInt(request.getParameter("money"));
			int id = Integer.parseInt(request.getParameter("userid"));
			String account = request.getParameter("account");
			String realname = request.getParameter("realname");
			returnMap.put("state", mg_GameDao.withdraw(money, account, realname, id));
		}
		// 查看幸运转盘消耗金币数及规则1
		if ("ckluck".equals(type)) {
			returnMap.put("type_s", type);
			returnMap.put("ckluck", mg_GameDao.ckluck());
		}

		// 查看幸运转盘奖励1
		if ("ckluckpro".equals(type)) {
			returnMap.put("type_s", type);
			returnMap.put("ckluckpro", mg_GameDao.ckluckpro());
		}
		// 幸运转盘抽奖1
		if ("lottery".equals(type)) {
			returnMap.put("type_s", type);
			returnMap.put("ratio", Integer.parseInt(request.getParameter("ratio")));
			returnMap.put("award", mg_GameDao.lottery(Integer.parseInt(request.getParameter("userid")),
					Integer.parseInt(request.getParameter("ratio"))));
		}

		// 查看兑奖商品1
		if ("ckdui".equals(type)) {
			returnMap.put("type_s", type);
			returnMap.put("ckdui", mg_GameDao.ckdui());
		}

		// 兑奖1
		if ("conversion".equals(type)) {
			int userid = Integer.parseInt(request.getParameter("userid"));
			int conversionid = Integer.parseInt(request.getParameter("conversionid"));
			String phone = request.getParameter("phone");
			returnMap.put("state", mg_GameDao.conversion(userid, conversionid, phone));
		}

		// 查看游戏规则1
		if ("ckgui".equals(type)) {
			String gui_type = request.getParameter("gui_type");
			returnMap.put("type_s", type);
			returnMap.put("ckgui", mg_GameDao.ckgui(gui_type));
			returnMap.put("state", "-1");
		}

		// 查看公告声明
		if ("selectNotice".equals(type)) {
			String gong_type = request.getParameter("gong_type");
			returnMap.put("type_s", type);
			returnMap.put("ckgui", mg_GameDao.selectNotcie(gong_type));
			returnMap.put("state", "-1");
		}

		// 查看公告1
		if ("ckgong".equals(type)) {
			String gui_type = request.getParameter("gong_type");
			returnMap.put("type_s",type);
			returnMap.put("ckgong", mg_GameDao.ckgong());
			returnMap.put("state", "-1");
		}

		//查看活动
        if("getActivily".equals(type)){
            returnMap.put("type_s", type);
            returnMap.put("author",mg_GameDao.getActivily());
        }
        //查看招募代理
		if("getAgency".equals(type)){
			returnMap.put("type_s", type);
			returnMap.put("author",mg_GameDao.getAgency());
		}

		//查看
		/*********************************  商城购买待处理 ****************************************/
		if("byMoney".equals(type)){
			returnMap.put("byMoney", mg_GameDao.byMoney());
		}
		if("byDiamond".equals(type)){
			returnMap.put("byDiamond", mg_GameDao.byDiamond());
		}
		/*************************************** 牌友圈 ***************************************/

		//添加俱乐部房卡
		if ("addClub_Money".equals(type)) {
			returnMap.put("type_s", type);
			String circlenumber = request.getParameter("circlenumber");
			int userid = Integer.parseInt(request.getParameter("userid"));
			int diamond = Integer.parseInt(request.getParameter("diamond"));
			returnMap.put("state", mg_GameDao.addClubDiamond(circlenumber,userid,diamond));
		}

		// 创建牌友圈--参数: 牌友圈名称,用户id
		if ("createClub".equals(type)) {
			returnMap.put("type_s", type);
			String circlename = request.getParameter("circlename");
			int userid = Integer.parseInt(request.getParameter("userid"));
			int game_type = Integer.parseInt(request.getParameter("game_type"));
			String rules = request.getParameter("rules");
			returnMap.put("state", mg_GameDao.createClub(circlename, userid,game_type,rules));
		}

		// 查看已加入的牌友圈列表1
		if ("ckclub".equals(type)) {
			returnMap.put("type_s", "ckclub");
			int userid = Integer.parseInt(request.getParameter("userid"));
			returnMap.put("ckclub", mg_GameDao.ckclub(userid));
			returnMap.put("state", "-1");
		}

		// 查看牌友圈详情1
		if ("clubdetails".equals(type)) {
            returnMap.put("clubdetails", mg_GameDao.ckclubPlay(Integer.parseInt(request.getParameter("circlenumber"))));
            returnMap.put("type_s", type);
            returnMap.put("state", "-1");
		}

		// 申请加入牌友圈1
		if ("joinCircleid".equals(type)) {
			returnMap.put("type_s", type);
			int circlenumber = Integer.parseInt(request.getParameter("circlenumber"));
			int userid = Integer.parseInt(request.getParameter("userid"));
			returnMap.put("state", mg_GameDao.joinCircleid(userid, circlenumber));
		}

		// 圈主查看牌友圈申请
		if ("circleApplication".equals(type)) {
			returnMap.put("type_s", type);
			returnMap.put("circleApplication", mg_GameDao.circleApplication(Integer.parseInt(request.getParameter("circlenumber")),
					Integer.parseInt(request.getParameter("userid"))));
			returnMap.put("state", "-1");
		}

		/*************************************************邀请加入俱乐部***********************************************/
		// 查看消息
		if ("get_invite".equals(type)) {
			returnMap.put("type_s", type);
			returnMap.put("invite", mg_GameDao.get_invite(Integer.parseInt(request.getParameter("userid"))));
			returnMap.put("state", "-1");
		}
		// 同意或拒绝邀请
		if ("agree_or_no".equals(type)) {
			returnMap.put("type_s", type);
			Integer yn = Integer.valueOf(request.getParameter("invite_state"));
			Integer userid = Integer.valueOf(request.getParameter("userid"));
			Integer bid = Integer.valueOf(request.getParameter("bid"));
			String club_number = request.getParameter("club_number");
			returnMap.put("state", mg_GameDao.agree_or_no(bid,club_number,userid,yn,Integer.parseInt(request.getParameter("id"))));
		}
		// 邀请加入俱乐部
		if ("invite_join".equals(type)) {
			returnMap.put("type_s", type);
			Integer userid = Integer.valueOf(request.getParameter("userid"));
			Integer bid = Integer.valueOf(request.getParameter("bid"));
			String club_number = request.getParameter("club_number");
			returnMap.put("state", mg_GameDao.invite_join(bid,club_number,userid));
		}



        // 同意加入
        if ("passjoinCard".equals(type)) {
            returnMap.put("state",
                    type+mg_GameDao.passjoinCard(
                            Integer.parseInt(request.getParameter("userid")),
                            Integer.parseInt(request.getParameter("applyid")),
                            Integer.parseInt(request.getParameter("circlenumber"))));
            returnMap.put("type_s", type);
        }

		// 拒绝加入
		if ("downjoinCard".equals(type)) {
			returnMap.put("type_s", type);
			returnMap.put("state", type+mg_GameDao.downjoinCard(Integer.parseInt(request.getParameter("applyid"))));
		}

		// 打样或开张
		if ("open_close".equals(type)) {
			returnMap.put("type_s", type);
			int state = Integer.parseInt(request.getParameter("state"));
			String circlenumber = request.getParameter("circlenumber");
			//开张
			if(state==1){
				if(request.getParameter("game_type").equals("0")){
					GameService gs = new GameService(new com.zcf.thirteen.util.BaseDao());
					int count = gs.getclubrommnumber(circlenumber, Integer.valueOf(request.getParameter("game_type")));
					if(count==0){
						String rules = mg_GameDao.getRules(Integer.valueOf(circlenumber));
						String[] split = rules.split("/");
						Map<String, String> map = new HashMap<>();
						map.put("foundation",split[0]);
						map.put("max_number",split[1]);
						map.put("rule",Integer.valueOf(split[0])==0?"-2":split[2]);
						map.put("room_type",split[3]);
						map.put("baibian",split[4]);
						for (int i = 1; i < 4; i++) {
							for (int j = 0; j < 8; j++) {
								T_RoomBean rb = gs.Esablish(map, null);
								rb.setClub_state(state);
								rb.setClub_number(circlenumber);
								rb.setFloor(i);
							}
						}
					}
				}
			}
			for(Map.Entry<String, WebSocket> entry : Public_State_t.clients_t.entrySet()){
				WebSocket ws = entry.getValue();
				if(ws.userBean.getClub_number().equals(circlenumber) && ws.userBean.getFloor()!=11 && ws.userBean.getFloor()!=-1){
					Map<String, Object> map = new HashMap<>();
					map.put("type",type);
					map.put("club_state",state);
					map.put("club_number",circlenumber);
					ws.sendMessageTo(map,ws.userBean);
				}
			}

			for(Map.Entry<String, RoomBean> entry : Public_State.PKMap.entrySet()){
				RoomBean rb = entry.getValue();
				if(rb.getClub_number().equals(circlenumber)){
					rb.setClub_state(state);
				}
			}
			for(Map.Entry<String, T_RoomBean> entry : Public_State_t.PKMap1.entrySet()){
				T_RoomBean rb = entry.getValue();
				if(rb.getClub_number().equals(circlenumber)){
					rb.setClub_state(state);
				}
			}

			mg_GameDao.closing(Integer.valueOf(circlenumber),state);
			returnMap.put("club_state", state);
			returnMap.put("state", "-1");
			returnMap.clear();
		}

		// 查看牌友圈的成员
		if ("selectcarduser".equals(type)) {
			returnMap.put("type_s", type);
			int circlenumber = Integer.parseInt(request.getParameter("circlenumber"));
			returnMap.put("selectcardUser", mg_GameDao.selectcarduser(circlenumber));
			returnMap.put("state", "-1");
		}

        // 查看俱乐部房间列表
        if ("selectclubroom".equals(type)) {
            returnMap.put("type_s", type);
            int clubid = Integer.parseInt(request.getParameter("clubid"));
            returnMap.put("selectclubroom", mg_GameDao.selectclubroom(clubid));
        }

		// 踢出牌友圈
		if ("downcricle".equals(type)) {
			returnMap.put("type_s", type);
			// 牌友-牌友圈绑定id
			int id = Integer.valueOf(request.getParameter("id"));
			// 自身id
			int userid = Integer.parseInt(request.getParameter("userid"));
			// 牌友圈编号
			int circlenumber = Integer.parseInt(request.getParameter("circlenumber"));
			returnMap.put("state", mg_GameDao.downcricle(id, userid, circlenumber));
		}

		/********************************** 我的战绩 *****************************************/

		// 查看战绩列表
		if ("selectPK".equals(type)) {
			int userid = Integer.valueOf(request.getParameter("userid"));
			System.out.println(request.getParameter("record_type"));
			int record_type = Integer.valueOf(request.getParameter("record_type"));
			List<Map<String, Object>> list = mg_GameDao.getRecordRoom(userid,record_type);
			for (Map<String, Object> map :
					list) {
				List<Map<String, Object>> list2 = mg_GameDao.getRecordByRoomid(map.get("roomno"));
				if (list2.size()!=0) {
					map.put("userlist",list2);
				}
			}
            returnMap.put("type_s",type);
            returnMap.put("state","-1");
            returnMap.put("selectPK",list);
		}

		// 查看对局列表
		if ("selectPK_2".equals(type)) {
			List<Map<String, Object>> list = mg_GameDao.getRecordRoom2(Integer.parseInt(request.getParameter("userid")),Integer.parseInt(request.getParameter("pkid")));
			int i = 0;
			for (Map<String, Object> map :
					list) {
				i++;
				List<Map<String, Object>> list2 = mg_GameDao.getRecordBy2(map.get("roomno"),i);
				map.put("userlist",list2);
				map.put("game_number",i);
			}
			returnMap.put("type_s",type);
			returnMap.put("state","-1");
			returnMap.put("selectPK",list);
		}

		// 查看对局详情
		if ("selectPK_3".equals(type)) {
			List<Map<String, Object>> list = mg_GameDao.getRecordRoom3(Integer.parseInt(request.getParameter("pkid")));
			List<Map<String, Object>> list2 = mg_GameDao.getRecordByRoomid3(list.get(0).get("roomno"),Integer.parseInt(request.getParameter("game_number")));
			list.get(0).put("userlist",list2);
			returnMap.put("type_s",type);
			returnMap.put("state","-1");
			returnMap.put("selectPK",list);
		}

		// 查看战绩详情
		if ("getRecordDetails".equals(type)) {
			returnMap.put("getRecordDetails", mg_GameDao.getRecordDetails(request.getParameter("roomno")));
		}

		//查看牌友圈战绩详情
		if("getClubDetails".equals(type)){
			returnMap.put("getClubDetails", mg_GameDao.getClubDetails(Integer.parseInt(request.getParameter("circlenumber"))));
		}
	
		/********************************************************/
		baseDao.CloseAll();
		if(!"open_close".equals(type)){
			String json = gson.toJson(returnMap).toString();
			System.out.println(json);
			response.getWriter().println(json);
		}
	}

	public void init() throws ServletException {

	}
	
}
