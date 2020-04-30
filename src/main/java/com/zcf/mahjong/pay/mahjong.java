package com.zcf.mahjong.pay;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.zcf.mahjong.dao.M_GameDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import com.zcf.mahjong.util.BaseDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: Guangan_Mahjong
 * @description: 支付
 * @author: Journey
 * @create: 2019-07-06 15:21
 */
@WebServlet("/mahjong_pay")
public class mahjong  extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Map<String, Object> returnMap = new HashMap<String, Object>();
    private Gson gson = new Gson();


    /**************************支付配置**************************/
    //接口地址
    private String url = "http://eee.hft-pay.cn/api/order";
    //商户号
    private String p1_merchant = "2019070509062";
    //订单号  自动生成
    private String p2_order = "";
    //回调地址
    private String p4_returnurl = "www.baidu.com";
    //异步通知地址
    private String p5_noticeurl = "http://103.69.17.136:8094/Guangan_Mahjong/mahjong_pay?type=notice";
    //终端账号
    private String p6_custom = "123";
    //签名类型（MD5）
    private String p7_signtype = "MD5";
    //支付场景编码
    private String p11_payscene = "WAP";
    //****非必填***
    //展示方式编码
    private String p12_showtype = "";
    //卡类或银行编码
    private String p13_bankcardcode = "";
    //备注
    private String p14_remark = "";
    //扩展参数
    private String p15_extend = "";
    //密钥
    private String key = "1629424956CEBA5490BFCA0208212913";
    //用户id
    private  int userid;
    //商品id
    private int diamondid;

    public mahjong() {
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
        //
        if ("index".equals(type)) {
            returnMap.put("index","nidex");
        }

        //购买商品
        if ("sendKV".equals(type)){
            userid = Integer.parseInt(request.getParameter("userid"));
            diamondid = Integer.parseInt(request.getParameter("diamondid"));
            String p3_money = request.getParameter("p3_money");
            String p10_paytype = request.getParameter("p10_paytype");
            if(StringUtils.isBlank(p3_money)){
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=utf-8");
                PrintWriter out = null;
                try {
                    out = response.getWriter();
                    out.append("金额不能为空");
                    out.flush();
                    out.close();

                } catch (IOException e) {
                    // 异常处理
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
                return ;
            }
            BigDecimal money = new BigDecimal(p3_money).multiply(new BigDecimal("100"));
            SimpleDateFormat simpleDateFormate = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            p2_order = simpleDateFormate.format(new Date()) + Utlis.genRandomNum(5);
            Map<String, String> hashMap = new HashMap<>();
            String p8_sign=(Utlis.MD5Encode("p1_merchant="+p1_merchant+
                    "&p2_order="+p2_order + "&p3_money="+money.toString()+
                    "&p4_returnurl="+p4_returnurl + "&p5_noticeurl="+p5_noticeurl+
                    "&p6_custom="+p6_custom + "&key="+key,null)).toUpperCase();
            hashMap.put("p1_merchant",p1_merchant);
            hashMap.put("p2_order",p2_order);
            hashMap.put("p3_money",money.toString());
            hashMap.put("p4_returnurl",p4_returnurl);
            hashMap.put("p5_noticeurl",p5_noticeurl);
            hashMap.put("p6_custom",p6_custom);
            hashMap.put("p7_signtype",p7_signtype);
            hashMap.put("p8_sign",p8_sign);
            hashMap.put("p10_paytype",p10_paytype);
            hashMap.put("p11_payscene",p11_payscene);

            hashMap.put("p12_showtype",p12_showtype);
            hashMap.put("p13_bankcardcode",p13_bankcardcode);
            hashMap.put("p14_remark",p14_remark);
            hashMap.put("p15_extend",p15_extend);
            //设置超时时间
            RequestConfig requestConfig =RequestConfig.custom()
                    .setConnectTimeout(65000).setConnectionRequestTimeout(15000)
                    .setSocketTimeout(65000).build();
            String doPost = Utlis.doPost(url, hashMap, null, requestConfig);
            if(doPost.contains("Flag")){
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=utf-8");
                PrintWriter out = null;
                try {
                    out = response.getWriter();
                    out.append(doPost);
                    out.flush();
                    out.close();

                } catch (IOException e) {
                    // e.printStackTrace();
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
                return ;
            }else{
                response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                try {
                    Map<String,String> header=new ObjectMapper().readValue(doPost, Map.class);
                    for (Map.Entry<String, String> entry :header.entrySet()) {
                        response.setHeader(entry.getKey(), entry.getValue());
                    }
                    // 重定向到上家url
                    response.sendRedirect(header.get("Location"));
                    return ;
                } catch (IOException e) {

                }
            }
            return ;
        }
        // 回调通知
        if ("notice".equals(type)){
            String postData = Utlis.getPostData(request);
            Map<String,String> map = new HashMap<>();
            if (StringUtils.isNotBlank(postData)) {
                // 按kv接收
                String[] strSplit = postData.split("&");
                for (String str : strSplit) {
                    if (str.contains("=")) {
                        // 取得参数名及对应的值
                        map.put(str.split("=")[0],str.split("=")[1]);
                    }
                }
            }

            String md5 = Utlis.MD5Encode("p1_usercode="+map.get("p1_usercode")+"&p2_order="+
                    map.get("p2_order")+"&p3_ordermoney="+map.get("p3_ordermoney")+"&p4_status="+
                    map.get("p4_status")+"&p5_tradeorder="+map.get("p5_tradeorder")+"&p6_paymethod="+
                    map.get("p6_paymethod")+"&p7_transamount="+map.get("p7_transamount")+"&key="+ key,null).toUpperCase();
            //验签
            if(!md5.equals(map.get("p10_sign"))){
                //returnMap.put("Msg","验签失败");
                gameDao.UpdateUserDiamonds(userid,diamondid);
                response.getWriter().print("SUCCESS");
            }else {
                //处理业务逻辑
                gameDao.UpdateUserDiamonds(userid,diamondid);
                response.getWriter().print("SUCCESS");
            }
        }
        if ("success".equals(type)){
            returnMap.put("Msg","SUCCESS");
        }
        baseDao.CloseAll();
        String returnjson = gson.toJson(returnMap).toString();
        System.out.println("返回的json数据为:" + returnjson);
    }

    public void init() throws ServletException {

    }

}
