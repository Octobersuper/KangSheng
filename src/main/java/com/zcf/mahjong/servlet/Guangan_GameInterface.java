package com.zcf.mahjong.servlet;

import com.google.gson.Gson;
import com.zcf.mahjong.bean.UserBean;
import com.zcf.mahjong.dao.M_GameDao;
import com.zcf.mahjong.dao.M_LoginDao;
import com.zcf.mahjong.mahjong.Public_State;
import com.zcf.mahjong.util.BaseDao;
import com.zcf.mahjong.util.UtilClass;
import com.zcf.thirteen.comm.Public_State_t;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 前端接口
 * 
 * @author Administrator
 *
 */
@WebServlet("/out2")
public class Guangan_GameInterface extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Map<String, Object> returnMap = new HashMap<String, Object>();
	private Gson gson = new Gson();

	public Guangan_GameInterface() {
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
		// 接口
		M_GameDao gameDao = new M_GameDao(baseDao);
		// 接收参数(解密后返回)
		Map<String, Object> requestmap = null;
		returnMap.clear();
		if (type != null) {
			// 接收参数(解密后返回)
			requestmap = UtilClass.utilClass.getRequestAdd(request, "/request.properties", type);
			System.out.println(requestmap.toString());
			// 检测参数是否合格(不合格则会更改type的值并增加返回map错误提示error)
			UtilClass.utilClass.isRequest("/request.properties", type, requestmap, returnMap);
			if (returnMap.get("error") != null) {
				type = "";
			}
		} else {
			returnMap.put("error", "参数错误");
			returnMap.put("state", "-1");
			type = "";
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
				gameDao.delRecord();
				returnMap.put("state", "0");
				userBean.getUser_Custom("nickname-avatarurl-userid-diamond-openid-sex", returnMap);
				if(Public_State.clients.get(userBean.getOpenid())!=null){
					returnMap.put("state", "320"); //重复登录
				}
				// 检测是否需要重连
				String roomno = Public_State.ISUser_Room(userBean.getUserid());
				// 检测是否需要重连
				String roomno2= Public_State_t.ISUser_Room(userBean.getUserid());
				// 加入登陆日志
				// loginDao.addLogin_Log(userBean.getUserid());
				// 当天首次登陆领取日常任务
				//loginDao.addDaily_User(userBean.getUserid());
				// 需要重连
				if (roomno != null || roomno2 != null) {
					returnMap.put("state", "310");
					returnMap.put("roomno", roomno==null?roomno2:roomno);
				}
				System.out.println(returnMap);
			}
		}
		// 查看玩家
		if ("getUser".equals(type)) {
            returnMap.put("type_s","getUser");
			int userid = Integer.parseInt(request.getParameter("userid"));
			returnMap.put("UserBean", gameDao.getUserById(userid));
            returnMap.put("state", "99");
		}
		// 查看玩家房卡
		if ("getUserDiamond".equals(type)) {
			returnMap.put("type_s",type);
			int userid = Integer.parseInt(request.getParameter("userid"));
			returnMap.put("UserBean", gameDao.getUserDiamond(userid));
			returnMap.put("state", "1001");
		}

		/******************************* 公告 ****************************/
		// 查看公告
		if ("getNotice".equals(type)) {
			returnMap.put("type_s","getNotice");
			returnMap.put("data", gameDao.getNotice());
			returnMap.put("state", "-1");
		}

		/************************** 商城 **********************************/

		// 查看商品
		if ("getDiamond".equals(type)) {
			returnMap.put("type_s","getDiamond");
			returnMap.put("data", gameDao.getDiamond());
			returnMap.put("state", "-1");
		}

		/************************* 俱乐部 **********************************/
		//添加俱乐部房卡
        if ("addClub_Money".equals(type)) {
            returnMap.put("type_s", type);
            String circlenumber = request.getParameter("circlenumber");
            int userid = Integer.parseInt(request.getParameter("userid"));
            int diamond = Integer.parseInt(request.getParameter("diamond"));
            returnMap.put("state", gameDao.addClub_Money(circlenumber, userid,diamond));
        }

		// 创建俱乐部--参数: 俱乐部名称,用户id
		if ("createClub".equals(type)) {
			String circlename = request.getParameter("circlename");
			int userid = Integer.parseInt(request.getParameter("userid"));
			returnMap.put("state", gameDao.createClub(circlename, userid));
            returnMap.put("type_s", "createClub");
		}

		// 查看已加入的俱乐部列表及详情
		if ("ckclub".equals(type)) {
			int userid = Integer.parseInt(request.getParameter("userid"));
			returnMap.put("ckclub", gameDao.getClub(userid));
			returnMap.put("type_s", "ckclub");
			returnMap.put("state", "-1");
		}

        // 查看俱乐部详情
        if ("clubdetails".equals(type)) {
            returnMap.put("clubdetails", gameDao.ckclubPlay(Integer.parseInt(request.getParameter("circlenumber"))));
            returnMap.put("type_s", type);
            returnMap.put("state", "-1");
        }


        // 申请加入俱乐部
		if ("joinCircleid".equals(type)) {
			returnMap.put("type_s", type);
			int circlenumber = Integer.parseInt(request.getParameter("circlenumber"));
			int userid = Integer.parseInt(request.getParameter("userid"));
			returnMap.put("state", gameDao.joinCircleid(userid, circlenumber));
		}

		// 圈主查看俱乐部申请
		if ("circleApplication".equals(type)) {
			returnMap.put("type_s", type);
			returnMap.put("state", "-1");
			returnMap.put("circleApplication",

					gameDao.circleApplication(Integer.parseInt(request.getParameter("circlenumber")),
							Integer.parseInt(request.getParameter("userid"))));
		}

		// 同意加入
		if ("passjoinCard".equals(type)) {
			returnMap.put("state",
                    type+gameDao.passjoinCard(
                            Integer.parseInt(request.getParameter("userid")),
							Integer.parseInt(request.getParameter("applyid")),
							Integer.parseInt(request.getParameter("circlenumber"))));
            returnMap.put("type_s", type);
		}

		// 拒绝加入
		if ("downjoinCard".equals(type)) {
			returnMap.put("state", type+gameDao.downjoinCard(Integer.parseInt(request.getParameter("applyid"))));
            returnMap.put("type_s", type);
		}

		// 查看俱乐部的成员
		if ("selectcarduser".equals(type)) {
			int circlenumber = Integer.parseInt(request.getParameter("circlenumber"));
            returnMap.put("type_s", type);
			returnMap.put("selectcardUser", gameDao.selectcarduser(circlenumber));
            returnMap.put("state", "-1");
		}
        // 踢出俱乐部
        if ("downcricle".equals(type)) {
            // 牌友-俱乐部绑定id
            int id = Integer.parseInt(request.getParameter("id"));
            // 自身id
            int userid = Integer.parseInt(request.getParameter("userid"));
            // 俱乐部编号
            int circlenumber = Integer.parseInt(request.getParameter("circlenumber"));

            returnMap.put("state", gameDao.downcricle(id, userid, circlenumber));

            returnMap.put("type_s", "-1");
        }
		// 退出俱乐部0
		if ("exitClub".equals(type)) {
			int userid = Integer.parseInt(request.getParameter("userid"));
			int circlenumber = Integer.parseInt(request.getParameter("circlenumber"));
			returnMap.put("state", gameDao.exitClub(userid, circlenumber));
			returnMap.put("type_s", "exitClub");
		}
		/********************************查看战局结束战绩 ***********************************/
        if ("finalScore".equals(type)) {
            returnMap.put("type_s", type);
            returnMap.put("scorelist", gameDao.finalScore(Integer.parseInt(request.getParameter("roomno"))));
			returnMap.put("state","-1");
        }

		/********************************** 我的战绩 *****************************************/
        // 查看战绩列表
        if ("selectPK".equals(type)) {
            List<Map<String, Object>> list = gameDao.getRecordRoom(Integer.parseInt(request.getParameter("userid")));
            List<Map<String, Object>> lists = new ArrayList<Map<String,Object>>();
            int num = 0;
            for (int i = 0; i < list.size(); i++) {
                if(i+1 <list.size()){
                    if(list.get(i).get("roomno").equals(list.get(i+1).get("roomno")) && list.get(i).get("game_number").equals(list.get(i+1).get("game_number"))){
                        num++;
                    }else{
                        String userid = "",nickname="" ,number="";
                        for (int j = 0; j <= num; j++) {
                            if(j==num){
                                userid += list.get(i-num+j).get("userid");
                                nickname+=list.get(i-num+j).get("nickname");
                                number+=list.get(i-num+j).get("number");
                            }else{
                                userid += list.get(i-num+j).get("userid")+"|";
                                nickname+=list.get(i-num+j).get("nickname")+"|";
                                number+=list.get(i-num+j).get("number")+"|";
                            }
                        }
                        list.get(i-num).put("userid", userid);
                        list.get(i-num).put("nickname", nickname);
                        list.get(i-num).put("number", number);
                        lists.add(list.get(i-num));
                        num=0;
                    }
                }else{
                    String userid = "",nickname="" ,number="";
                    for (int j = 0; j <= num; j++) {
                        if(j==num){
                            userid += list.get(i-num+j).get("userid");
                            nickname+=list.get(i-num+j).get("nickname");
                            number+=list.get(i-num+j).get("number");
                        }else{
                            userid += list.get(i-num+j).get("userid")+"|";
                            nickname+=list.get(i-num+j).get("nickname")+"|";
                            number+=list.get(i-num+j).get("number")+"|";
                        }
                    }
                    list.get(i-num).put("userid", userid);
                    list.get(i-num).put("nickname", nickname);
                    list.get(i-num).put("number", number);
                    lists.add(list.get(i-num));
                    num=0;
                }

            }
            returnMap.put("type_s",type);
            returnMap.put("state","-1");
            returnMap.put("selectPK",lists);
        }
		baseDao.CloseAll();
		String json = gson.toJson(returnMap).toString();
		System.out.println(json);
		response.getWriter().println(json);
	}



	public void init() throws ServletException {
	}
}