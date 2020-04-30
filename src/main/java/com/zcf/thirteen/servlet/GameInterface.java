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
import com.zcf.thirteen.comm.Public_State_t;

import com.zcf.thirteen.util.BaseDao;

@WebServlet("/Thirteen_Water")
public class GameInterface extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Map<String,Object> returnMap = new HashMap<String,Object>(); 
	private Gson gson = new Gson();
	public GameInterface() {
		super();
	}

	public void destroy() {
		super.destroy(); 
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//输出转码
		response.setContentType("text/json;charset=UTF-8");
		//接受转码
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
		//接收参数(解密后返回)
		Map<String,Object> requestmap=null;
		returnMap.clear();
		if(type!=null){
			if(type.equals("gameCount")){
				int i = Public_State_t.clients_t.size();
				response.getWriter().println(i);
				baseDao.CloseAll();
				return;
			}
		}else{
			returnMap.put("error", "参数错误");
			returnMap.put("state", "100");
			type="";
		}
		
		//查询结算信息
		baseDao.CloseAll();
		String json = gson.toJson(returnMap).toString();
		response.getWriter().println(json);
	}
	public void init() throws ServletException {
	}

}
