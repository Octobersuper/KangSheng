package com.zcf.thirteen.servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import com.zcf.thirteen.bean.T_RoomBean;
import com.zcf.thirteen.bean.T_UserBean;
import com.zcf.thirteen.comm.Public_State_t;

@WebServlet("/HttpRequest")
public class HttpRequest extends HttpServlet {
	private static final long serialVersionUID = 2L;
	private Map<String,Object> returnMap = new HashMap<String,Object>(); 
	private Gson gson = new Gson();
	public HttpRequest() {
		super();
	}

	public void destroy() {
		super.destroy(); 
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaa");
		//输出转码
		response.setContentType("text/json;charset=UTF-8");
		//接受转码
		request.setCharacterEncoding("UTF-8");
		String user_id = request.getParameter("id");
		int room_type = Integer.valueOf(request.getParameter("room_type"));
		System.out.println(">>>>>>>>>"+user_id);
		/* 允许跨域的主机地址 
		response.setHeader("Access-Control-Allow-Origin", "*");  
		 允许跨域的请求方法GET, POST, HEAD 等 
		response.setHeader("Access-Control-Allow-Methods", "*");  
		 重新预检验跨域的缓存时间 (s) 
		response.setHeader("Access-Control-Max-Age", "3600");  
		 允许跨域的请求头 
		response.setHeader("Access-Control-Allow-Headers", "*");  
		 是否携带cookie 
		response.setHeader("Access-Control-Allow-Credentials", "true");  
		BaseDao baseDao = new BaseDao();
		//接收参数(解密后返回)
		Map<String,Object> requestmap=null;
		returnMap.clear();*/
		Map<String, T_RoomBean> PKMap = Public_State_t.PKMap1;
		Map<String, T_RoomBean> PKMap1 = Public_State_t.PKMap2;
		//Map<String, RoomBean> PKMap2 = Public_State.PKMap2;
		if (room_type == 0) {
			for(String key:PKMap.keySet()){
				T_RoomBean roomBean = PKMap.get(key);
				for( T_UserBean userBean:roomBean.getGame_userList(0)){
					String id = String.valueOf(userBean.getUserid());
					if(user_id.equals(id)){
						if (userBean.getGametype()==2) {
							returnMap.put("state", "1");//断线重连
							returnMap.put("gameType", "0");
							returnMap.put("room_number", roomBean.getRoom_number());
							String jsonTo = gson.toJson(returnMap).toString();
							response.getWriter().println(jsonTo);
							return;
						}
					}
				}
			}
		}else {
			for(String key:PKMap1.keySet()){
				T_RoomBean roomBean = PKMap1.get(key);
				for(T_UserBean userBean:roomBean.getGame_userList(0)){
					String id = String.valueOf(userBean.getUserid());
					if(user_id.equals(id)){
						if (userBean.getGametype()==2) {
							returnMap.put("state", "1");//断线重连
							returnMap.put("gameType", "0");
							returnMap.put("room_number", roomBean.getRoom_number());
							String jsonTo = gson.toJson(returnMap).toString();
							response.getWriter().println(jsonTo);
							return;
						}
					}
				}
			}
		}
		
		returnMap.put("state", "0");//不需要重连
		String jsonTo = gson.toJson(returnMap).toString();
		response.getWriter().println(jsonTo);
	}

}
