package com.zcf.mahjong.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zcf.mahjong.bean.RoomBean;
import com.zcf.mahjong.bean.UserBean;
import com.zcf.mahjong.dao.M_LoginDao;
import com.zcf.mahjong.mahjong.Public_State;
import com.zcf.mahjong.util.BaseDao;
import com.zcf.mahjong.util.BeanUtils;
import com.zcf.mahjong.util.MahjongUtils;
import com.zcf.mahjong.util.System_Mess;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.Lock;

import static com.zcf.mahjong.util.Mahjong_Util.mahjong_Util;

@ServerEndpoint("/Mahjong_Socket/{openid}")
public class Mahjong_Socket {
    private Gson gson = new Gson();// json转换
    public Session session;
    // 用户信息
    private UserBean userBean;
    // 自己进入的房间
    public RoomBean roomBean;
    private BaseDao baseDao = new BaseDao();
    // 登陆dao
    private M_LoginDao loginDao = new M_LoginDao(baseDao);
    // 游戏逻辑类
    public M_GameService gameService = new M_GameService(baseDao);
    private Map<String, Object> returnMap = new HashMap<String, Object>();

    /**
     * 连接
     *
     * @param openid
     * @param session
     * @throws IOException
     */
    @OnOpen
    public void onOpen(@PathParam("openid") String openid, Session session) {
        boolean bool = true;
        if (openid != null) {
            // 查询出用户信息t
            userBean = loginDao.getUser(openid);
            baseDao.CloseAll();
            if (userBean != null) {
                String openids = userBean.getOpenid();
                //验证是否已经在线
                if (Public_State.clients.get(openids) != null) {
                    returnMap.put("type", "Repeat");
                    this.session = session;
                    sendMessageTo(returnMap);
                    System_Mess.system_Mess.ToMessagePrint(userBean.getNickname() + "重复登陆");
                } else {
                    //将自己放入客户端集合中
                    Public_State.clients.put(openids, this);
                    this.session = session;
                    System_Mess.system_Mess.ToMessagePrint(userBean.getNickname() + "已连接(麻将)");
                    bool = false;
                }
            }
        }
        //如果没走正常业务则归类非法连接
        if (bool) {
            try {
                session.close();
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
                System.out.println("xiaoxi" + e.getMessage());
            }
            System_Mess.system_Mess.ToMessagePrint("非法连接");
        }
    }

    /**
     * 关闭
     *
     * @throws IOException
     */
    @OnClose
    public void onClose() {
        if (userBean != null) {
            //删除自己
            Public_State.clients.remove(userBean.getOpenid() + "");
//			if(roomBean!=null){
//			    //启动托管线程
//			    userBean.setThread_robot(new Thread_Robot(roomBean, userBean, gameService));
//			   }
            //标识自己已经掉线
            userBean.setExit_state(1);
            System_Mess.system_Mess.ToMessagePrint(userBean.getNickname() + "断开连接");
            //如果已加入房间则通知其他人自己退出
            Exit_Room();
        }
    }

    /**
     * 接收消息
     *
     * @param message
     * @throws IOException
     * @throws InterruptedException
     */
    @OnMessage
    public void onMessage(String message) {
        //session.setMaxIdleTimeout(10000);
        Lock lock = userBean.getLock();
        lock.lock();
        returnMap.clear();
        if (!message.contains("heartbeat")) {
            System_Mess.system_Mess.ToMessagePrint(userBean.getBrands().size() + "接收" + userBean.getNickname() + "(state:" + userBean.getHu_state() + ")" + "的手牌" + userBean.getBrands().toString());
            System_Mess.system_Mess.ToMessagePrint("接收" + userBean.getNickname() + "的消息" + message);
        }
        Map<String, String> jsonTo = gson.fromJson(message, new TypeToken<Map<String, String>>() {
        }.getType());

        // 心跳连接
        if ("heartbeat".equals(jsonTo.get("type"))) {
            returnMap.put("type", "heartbeat");
            sendMessageTo(returnMap);
        }
        // 创建房间
        if ("Establish".equals(jsonTo.get("type"))) {
            returnMap.put("type", "Establish");
            returnMap.put("exittype", 0);
            returnMap.put("state", "0");
            if (roomBean != null && Public_State.PKMap.get(roomBean.getRoomno()) != null) {
                // 重复创建
                returnMap.put("state", "106");
            } else {
                // 创建房间
                roomBean = gameService.Establish(jsonTo, userBean, Integer.parseInt(jsonTo.get("clubid")));
                if (roomBean == null) {
                    // 房卡不足
                    returnMap.put("state", "105");
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String format = sdf.format(new Date());
                    roomBean.setData(format);
                    roomBean.getRoomBean_Custom("roomno-user_positions-gps-adjust_brand-same_colour-assured_brand-aircraft-ranging-initiative_ranging-fen", returnMap,
                            "userid-nickname-avatarurl-ip-number-sex");
                    sendMessageTo(returnMap, roomBean);
                }
            }
            sendMessageTo(returnMap);
        }
        // 加入房间
        if ("Matching".equals(jsonTo.get("type"))) {
            returnMap.put("state", "0");
            // 经纬度
            userBean.setLog_lat(jsonTo.get("log_lat"));
            int state = gameService.ISMatching_Money(userBean, jsonTo.get("roomno"));
            if (state == 0) {
                roomBean = gameService.Matching(jsonTo, userBean);
                if (roomBean == null) {
                    returnMap.put("state", "104");// 房间已满
                } else {
                    // 将自己的信息推送给房间内其他玩家
                    userBean.getUser_Custom("userid-nickname-sex-avatarurl-ip-number-log_lat", returnMap);
                    roomBean.getRoomBean_Custom("user_positions", returnMap);
                    returnMap.put("type", "Matching_User");
                    sendMessageTo(returnMap, roomBean);
                    returnMap.clear();
                    returnMap.put("state", "0");
                    returnMap.put("exittype", 1);
                    roomBean.getRoomBean_Custom("roomno-max_person-user_positions-max_number-game_type-fen-adjust_brand-same_colour-assured_brand-aircraft-ranging-initiative_ranging-user_log", returnMap,
                            "userid-nickname-sex-avatarurl-ip-ready_state-log_lat-number");
                }
            } else {
                // 100=房间不存在114=重复加入106=需要定位
                returnMap.put("state", String.valueOf(state));
            }
            returnMap.put("type", "Matching");
            sendMessageTo(returnMap);
        }
        // 准备
        if ("ready".equals(jsonTo.get("type"))) {
            // 如果处于等待加入或准备阶段
            if (roomBean.getState() == 0 || roomBean.getState() == 1) {
                int count = gameService.Ready(userBean, roomBean);
                returnMap.put("type", "ready");
                if (count == roomBean.getMax_person()) {
                    returnMap.put("type", "startgame");
                    returnMap.put("exittype", 3);
                    // 执行游戏开始
                    int state = gameService.StartGame(roomBean);
                    if (state == 0) {
                        //roomBean.getExit_time().updateUser(roomBean.getUserBean(roomBean.getEnd_userid()));
                        roomBean.getRoomBean_Custom("game_number-max_number-brands_count-banker-brands-adjust_brand-same_colour-assured_brand-aircraft-ranging-initiative_ranging-fen", returnMap,
                                "brands-number-sex-userid-exchange_brands-exchange_state-receive_brands-lack_state-lack_type-lack_brands");
                    }
                } else {
                    returnMap.put("userid", userBean.getUserid());
                }
                Random random = new Random();
                int num;
                returnMap.put("dice", num = random.nextInt(12) + 2);
                roomBean.setDice(num);
                sendMessageTo(returnMap);
                sendMessageTo(returnMap, roomBean);
            }
        }
        // 退出房间
        if ("exit_room".equals(jsonTo.get("type"))) {
            Exit_Room();
        }
        // 解散房间
        if ("exit_all".equals(jsonTo.get("type"))) {
            // 房间在等待加入阶段并且自己是房主才可以解散
            if (roomBean.getState() == 0 && roomBean.getHouseid() == userBean.getUserid()) {
                Exit_All();
            }
        }
        // 游戏开始解散
        if ("exit_game".equals(jsonTo.get("type"))) {
            returnMap.put("type", "exit_game");
            // 添加自己的解散状态
            userBean.setExit_game(Integer.parseInt(jsonTo.get("exit_game")));
            // 301-发起 302-同意 303-不同意 304-解散
            int state = gameService.Exit_GameUser(userBean, roomBean);
            // 执行解散
            if (state == 304) {
                Exit_All();
            } else {
                if (state == 301) {
                    roomBean.getRoomBean_Custom("userList", returnMap, "userid-nickname-avatarurl-exit_game-sex");
                    returnMap.put("applyuserid", userBean.getUserid());
                } else {
                    returnMap.put("userid", userBean.getUserid());
                }
                returnMap.put("state", String.valueOf(state));
                sendMessageTo(returnMap);
                sendMessageTo(returnMap, roomBean);
            }
        }
        /*****************************************换三张***********************************************/
        // 换三张
        if ("select_brands".equals(jsonTo.get("type"))) {
            returnMap.put("type", "select_brands");
            List<Integer> exchange_brands = userBean.getExchange_brands();
            exchange_brands.add(Integer.parseInt(jsonTo.get("onebrand")));
            exchange_brands.add(Integer.parseInt(jsonTo.get("twobrand")));
            exchange_brands.add(Integer.parseInt(jsonTo.get("treebrand")));
            roomBean.getRoomBean_Custom("game_number-max_number-brands_count-banker", returnMap,
                    "brands-number-userid-exchange_brands-exchange_state-receive_brands-sex");
            sendMessageTo(returnMap);
            sendMessageTo(returnMap, roomBean);
            List<UserBean> userlist = roomBean.getGame_userlist();
            int count = 0;
            for (UserBean userBean : userlist) {
                if (userBean.getExchange_brands().size() == 3) {
                    count++;
                }
            }
            if (count == roomBean.getMax_person()) {
                // 执行换牌
                gameService.ExchangeBrand(roomBean);
                returnMap.put("type", "exchange_brands");
                roomBean.getRoomBean_Custom("game_number-max_number-brands_count-banker", returnMap,
                        "brands-sex-number-userid-exchange_brands-exchange_state-hu_state-gang_state-peng_state-pass_state-receive_brands");
                sendMessageTo(returnMap);
                sendMessageTo(returnMap, roomBean);
            }
        }

        /*****************************************定缺***********************************************/
        // 定缺
        if ("lack_brands".equals(jsonTo.get("type"))) {
            // 定缺牌型
            int lackType = Integer.parseInt(jsonTo.get("lack_type"));
            returnMap.put("type", "lack_brands");
            // 缺牌集合
            List<Integer> list = gameService.lackType(lackType, userBean);
            List<Integer> lack_brands = userBean.getLack_brands();
            for (Integer integer : list) {
                lack_brands.add(integer);
            }
            roomBean.getRoomBean_Custom("roomno-max_person-user_positions-max_number-fen-banker", returnMap,
                    "userid-nickname-avatarurl-ip-ready_state-log_lat-number-lack_state-lack_type-lack_brands-brands-sex");
            sendMessageTo(returnMap);
            sendMessageTo(returnMap, roomBean);
        }
        /****************************************出牌*******************************************/
        // 出牌
        if ("out_brand".equals(jsonTo.get("type"))) {
            // 0代表不是自己杠后出牌就清空
            if (roomBean.IS_Bar_Userid(userBean.getUserid()) == 0) {
                roomBean.RemoveAll_Bar();
            }
            roomBean.setRepair_baruserid(0);
            roomBean.setHucount(0);
            String[] ting_brands = jsonTo.get("ting_brand").toString().split("-");
            List<Integer> tingList = new ArrayList<>();
            for (int i = 0; i < ting_brands.length; i++) {
                if(ting_brands[i].equals("")){
                    continue;
                }
                tingList.add(Integer.parseInt(ting_brands[i]));
            }
            userBean.setTingCards(tingList);
            returnMap.put("type", "out_brand");
            int outbrand = Integer.parseInt(jsonTo.get("outbrand"));
            int before_type = Integer.parseInt(jsonTo.get("before_type"));
            roomBean.setLastBrand(outbrand);
            roomBean.setLastUserid(userBean.getUserid());
            // 出牌并且判定是否有碰或杠 0有碰 300可摸牌
            returnMap.put("before_type", before_type);
            // 出的牌
            returnMap.put("outbrand", outbrand);
            // 出牌人id
            returnMap.put("out_userid", userBean.getUserid());
            // 出牌类型
            returnMap.put("out_type", 0);

            //检测是否有人可胡 可胡的话发送胡牌消息   0没人能胡  1有人能胡
            //List<UserBean> checkHu = roomBean.checkHu(outbrand, userBean);
            int checkHu2 = 0;
            for (UserBean user :
                    roomBean.getGame_userlist()) {
                if(user.getTingCards().size()!=0 && user.getUserid()!=userBean.getUserid()){
                    List<Integer> list = new ArrayList<>();
                    for (Integer card :
                            user.getTingCards()) {
                        list.add(mahjong_Util.getBrand_Value(card));
                    }
                    if(list.contains(mahjong_Util.getBrand_Value(outbrand))){
                        int s = mahjong_Util.checkHu(user,outbrand);
                        if(s!=-1){
                            checkHu2++;
                        }
                    }
                }
            }
            System.err.println("checkHu2:"+checkHu2);
            if(checkHu2==0){
                int state = gameService.OutBrand(roomBean, outbrand, userBean, returnMap);
                System_Mess.system_Mess.ToMessagePrint("碰牌状态" + state);
                sendMessageTo(returnMap);
                sendMessageTo(returnMap, roomBean);
            }else{
                gameService.OutBrand(userBean, outbrand);
                sendMessageTo(returnMap);
                sendMessageTo(returnMap, roomBean);
            }
        }
        /****************************************碰*****************************************/
        // 碰
        if ("bump".equals(jsonTo.get("type"))) {
            // 碰的用户id
            int p_userid = Integer.parseInt(jsonTo.get("p_userid"));
            // 碰牌
            int p_brand = Integer.parseInt(jsonTo.get("brand"));
            //牌值转换
            int brand_value = mahjong_Util.getBrand_Value(p_brand);
            if (brand_value == 31) {
                userBean.getRecordMsgList().add("碰红中+1");
                userBean.setPower(1);
            }
            if (brand_value == 32) {
                userBean.getRecordMsgList().add("碰发财+1");
                userBean.setPower(1);
            }
            if (brand_value == 33) {
                userBean.getRecordMsgList().add("碰白板+1");
                userBean.setPower(1);
            }
            int state = 503;
            if (p_userid != 0) {
                // 0不胡牌 900=4朴一将
                gameService.End_Hu(userBean, Integer.parseInt(jsonTo.get("brand")), roomBean, p_userid);
                // 弃胡
                // 结算500=已经结算 501=等待别人胡牌操作 502=等待别人结算
                state = gameService.End_Game(userBean, roomBean, p_userid, 2);
            }
            // 弃胡可碰
            if (state == 503) {
                int[] brands = gameService.Bump_Brand(userBean, Integer.parseInt(jsonTo.get("userid")),
                        Integer.parseInt(jsonTo.get("brand")), roomBean);
                returnMap.put("type", "bump");
                returnMap.put("bumpuserid", jsonTo.get("userid"));
                returnMap.put("brand", Integer.parseInt(jsonTo.get("brand")));
                returnMap.put("brands", brands);
                returnMap.put("userid", userBean.getUserid());
                // 清空碰牌信息
                roomBean.getNextMap().clear();
                sendMessageTo(returnMap);
                sendMessageTo(returnMap, roomBean);
            }
            if (state == 0) {
                // 结算
                jsonTo.put("type", "end");
            }
        }

        // 弃碰
        if ("giveup_bump".equals(jsonTo.get("type"))) {
            userBean.getNoany().add(mahjong_Util.getBrand_Value(Integer.parseInt(jsonTo.get("brand"))));//一轮不能胡这张
            //
            gameService.OutBrand(roomBean.getUserBean(roomBean.getLastUserid()),roomBean.getLastBrand());
            jsonTo.put("type", "brand_nextid");
            roomBean.getNextMap().clear();
        }
        /****************************************胡牌**************************************/
        // 客户端推送胡牌
        if ("is_hu".equals(jsonTo.get("type"))) {
            int state = Integer.parseInt(jsonTo.get("state"));
            userBean.setIs_hustate(state);
            int count = gameService.Is_Hu(roomBean, state);
            System.out.println("hucount:"+count);
            boolean bool = false;
            if (count == 3)
                bool = true;
            if (count == 2 && roomBean.getGame_userlist().size() == 3)
                bool = true;
            if (count == 1 && roomBean.getGame_userlist().size() == 2)
                bool = true;
            if (bool) {
                if(roomBean.getBrands().size()==0 && state==0){
                    roomBean.setFlowNum(roomBean.getFlowNum()+1);
                    if(roomBean.getFlowNum()==roomBean.getGame_userlist().size()-1){
                        jsonTo.put("type", "flow");
                    }
                    System.out.println("11111111111111");
                }else{
                    if(Integer.valueOf(jsonTo.get("before_type"))==2){
                        returnMap.put("type","repair_bar_bump_pass");
                        returnMap.put("brand_nextid",roomBean.getRepair_baruserid());
                        sendMessageTo(returnMap);
                        sendMessageTo(returnMap, roomBean);
                        System.out.println("2222222222222222");
                    }else {
                        jsonTo.put("type", "brand_nextid");
                        roomBean.setHucount(0);
                        System.out.println("333333333333333333");
                    }
                }
            }else{
                System.out.println("444444444444444");
                if(roomBean.getBrands().size()==0 && state==0){
                    roomBean.setFlowNum(roomBean.getFlowNum()+1);
                    if(roomBean.getFlowNum()==roomBean.getGame_userlist().size()-1){
                        jsonTo.put("type", "flow");
                    }
                }
            }
        }

        // 胡牌_点炮
        if ("endhu".equals(jsonTo.get("type"))) {
            if(roomBean.getMax_person()!=2){
                //血战
                int p_userid = Integer.parseInt(jsonTo.get("p_userid"));
                // 胡牌
                userBean.setPower(1);
                userBean.getRecordMsgList().add("胡牌+1");
                userBean.setHunum(userBean.getHunum()+1);
                // 增加用户点炮
                roomBean.getUserBean(p_userid).getRecordMsgList().add("点炮");
                returnMap.put("type", "endhu");
                // 0不胡牌 900=4朴一将 890=1癞子3朴1将 880=1癞子4朴 缺将 777=小七对
                int state = gameService.End_Hu(userBean, Integer.parseInt(jsonTo.get("brand")), roomBean, p_userid);
                System.out.println(state);
                if (state != 0) {
                    //检测是否海底炮
                    // gameService.isHaidipao(roomBean,p_userid);
                    List<Integer> brandList = userBean.getBrands();
                    List<Integer> list2 = new ArrayList<Integer>();
                    for (Integer in : brandList) {
                        if (!list2.contains(in)) {
                            list2.add(in);
                        }
                    }
                    brandList.clear();
                    for (Integer in : list2) {
                        brandList.add(in);
                    }
                    //是否杠上炮
                    if (Integer.parseInt(jsonTo.get("before_type")) == 1) {
                        userBean.setPower(5);
                        userBean.getRecordMsgList().add("杠上炮+5");
                    }else  //是否抢杠胡
                        if (Integer.parseInt(jsonTo.get("before_type")) == 2) {
                            userBean.setPower(5);
                            userBean.getRecordMsgList().add("抢杠胡+5");
                        }
                    // 是否海底炮
                    if (roomBean.getBrands().size() == 0) {
                        userBean.setPower(5);
                        userBean.getRecordMsgList().add("海底炮+5");
                    }
                    //听牌
                    String[] ting_brands = jsonTo.get("ting_brand").toString().split("-");
                    List<Integer> tingList = new ArrayList<>();
                    for (int i = 0; i < ting_brands.length; i++) {
                        if(ting_brands[i].equals("")){
                            continue;
                        }
                        tingList.add(Integer.parseInt(ting_brands[i]));
                    }
                    //检测手中的中发白
                    userBean.getZFBnum(userBean.getBrands(),userBean,Integer.parseInt(jsonTo.get("brand")),0);
                    // 结算检测
                    MahjongUtils mahjongUtils = new MahjongUtils();
                    mahjongUtils.getBrandKe(roomBean, userBean, Integer.parseInt(jsonTo.get("brand")), tingList,0);

                    if (userBean.getPower_number()>=10) {
                        userBean.setTennum(userBean.getTennum()+1);
                    }

                    System.out.println(">>>>>>>牌型检测完成啦");
                    // 结算500=已经结算 501=等待别人胡牌操作 502=等待别人结算
                    state = gameService.End_Game(userBean, roomBean, p_userid, 1);
                    returnMap.put("state", String.valueOf(state));
                    System_Mess.system_Mess.ToMessagePrint("点炮状态" + state);
                    userBean.setIshu(1);
                    // 成功结算
                    if (state == 0) {
                        roomBean.getHu_user_list().clear();
                        //胡牌的人信息
                        if (userBean.getRecordMsgList().size() == 0) {
                            userBean.getRecordMsgList().add("无");
                        }
                        roomBean.getRoomBean_Custom_HU("", returnMap, "number-userid-recordMsgList-brands-show_brands");
                        //点炮用户的信息
                        if (roomBean.getUserBean(p_userid).getRecordMsgList().size() == 0) {
                            roomBean.getUserBean(p_userid).getRecordMsgList().add("无");
                        }
                        //整合碰杠吃
                        UserBean user = roomBean.getUserBean(p_userid);
                        user.getUser_Custom("userid-number-recordMsgList-brands-show_brands", returnMap);
                        returnMap.clear();
                        returnMap.put("type","hu");
                        returnMap.put("hu_type",0);
                        returnMap.put("userid",userBean.getUserid());
                        sendMessageTo(returnMap);
                        sendMessageTo(returnMap,roomBean);

                        int count = gameService.Is_Hunum(roomBean);
                        boolean bool = false;
                        if (count == 3)
                            bool = true;
                        if (count == 2 && roomBean.getGame_userlist().size() == 3)
                            bool = true;
                        if (count == 1 && roomBean.getGame_userlist().size() == 2)
                            bool = true;
                        if (bool) {
                            jsonTo.put("type", "end");
                            for (UserBean u:
                                 roomBean.getGame_userlist()) {
                                //整合碰杠吃
                                List<Integer> show_brands = u.getShow_brands();
                                show_brands.addAll(u.getBump_brands());
                                show_brands.addAll(u.getHide_brands());
                            }
                        }else{
                            jsonTo.put("type", "brand_nextid");
                        }
                    }
                }
            }else{
                int p_userid = Integer.parseInt(jsonTo.get("p_userid"));
                // 胡牌
                userBean.setPower(1);
                userBean.getRecordMsgList().add("胡牌+1");
                userBean.setHunum(userBean.getHunum()+1);
                // 增加用户点炮
                roomBean.getUserBean(p_userid).getRecordMsgList().add("点炮");
                returnMap.put("type", "endhu");
                // 0不胡牌 900=4朴一将 890=1癞子3朴1将 880=1癞子4朴 缺将 777=小七对
                int state = gameService.End_Hu(userBean, Integer.parseInt(jsonTo.get("brand")), roomBean, p_userid);
                if (state != 0) {
                    //检测是否海底炮
                    // gameService.isHaidipao(roomBean,p_userid);
                    List<Integer> brandList = userBean.getBrands();
                    List<Integer> list2 = new ArrayList<Integer>();
                    for (Integer in : brandList) {
                        if (!list2.contains(in)) {
                            list2.add(in);
                        }
                    }
                    brandList.clear();
                    for (Integer in : list2) {
                        brandList.add(in);
                    }
                    //是否杠上炮
                    if (Integer.parseInt(jsonTo.get("before_type")) == 1) {
                        userBean.setPower(5);
                        userBean.getRecordMsgList().add("杠上炮+5");
                    }else  //是否抢杠胡
                        if (Integer.parseInt(jsonTo.get("before_type")) == 2) {
                            userBean.setPower(5);
                            userBean.getRecordMsgList().add("抢杠胡+5");
                        }
                    // 是否海底炮
                    if (roomBean.getBrands().size() == 0) {
                        userBean.setPower(5);
                        userBean.getRecordMsgList().add("海底炮+5");
                    }

                    //检测手中的中发白
                    for (UserBean user: roomBean.getGame_userlist()){
                        user.getZFBnum(user.getBrands(),user,Integer.parseInt(jsonTo.get("brand")),0);
                    }
                    //听牌
                    String[] ting_brands = jsonTo.get("ting_brand").toString().split("-");
                    List<Integer> tingList = new ArrayList<>();
                    for (int i = 0; i < ting_brands.length; i++) {
                        if(ting_brands[i].equals("")){
                            continue;
                        }
                        tingList.add(Integer.parseInt(ting_brands[i]));
                    }
                    // 结算检测
                    MahjongUtils mahjongUtils = new MahjongUtils();
                    mahjongUtils.getBrandKe(roomBean, userBean, Integer.parseInt(jsonTo.get("brand")), tingList,0);

                    if (userBean.getPower_number()>=10) {
                        userBean.setTennum(userBean.getTennum()+1);
                    }

                    System.out.println(">>>>>>>牌型检测完成啦");
                    // 结算500=已经结算 501=等待别人胡牌操作 502=等待别人结算
                    state = gameService.End_Game(userBean, roomBean, p_userid, 1);
                    returnMap.put("state", String.valueOf(state));
                    System_Mess.system_Mess.ToMessagePrint("点炮状态" + state);
                    // 成功结算
                    if (state == 0) {
                        //胡牌的人信息
                        if (userBean.getRecordMsgList().size() == 0) {
                            userBean.getRecordMsgList().add("无");
                        }
                        //整合碰杠吃
                        List<Integer> show_brands = userBean.getShow_brands();
                        show_brands.addAll(userBean.getBump_brands());
                        show_brands.addAll(userBean.getHide_brands());
                        roomBean.getRoomBean_Custom_HU("", returnMap, "number-userid-recordMsgList-brands-show_brands");
                        //点炮用户的信息
                        if (roomBean.getUserBean(p_userid).getRecordMsgList().size() == 0) {
                            roomBean.getUserBean(p_userid).getRecordMsgList().add("无");
                        }
                        //整合碰杠吃
                        UserBean user = roomBean.getUserBean(p_userid);
                        List<Integer> show_brand = user.getShow_brands();
                        show_brand.addAll(user.getBump_brands());
                        show_brand.addAll(user.getHide_brands());
                        user.getUser_Custom("userid-number-recordMsgList-brands-show_brands", returnMap);
                        jsonTo.put("type", "end");
                    }
                } else {
                    returnMap.put("state", "999");
                    sendMessageTo(returnMap);
                }
            }
        }
        //胡牌_点炮_结算
        if ("end".equals(jsonTo.get("type"))) {
            if(roomBean.getMax_person()!=2){
                //血战玩法
                roomBean.getRoomBean_Custom("", returnMap, "number-userid-dqnumber-recordMsgList-brands-show_brands");
                roomBean.setVictoryid(userBean.getUserid());
                returnMap.put("type", "endhu");
                returnMap.put("room_type",4);
                sendMessageTo(returnMap);
                sendMessageTo(returnMap, roomBean);
                roomBean.addLog(returnMap);
                // 记录战绩
                gameService.addRecord(roomBean);
                roomBean.InItReady();
            }else{
                // 胡牌的人信息
                if (userBean.getRecordMsgList().size() == 0) {
                    userBean.getRecordMsgList().add("无");
                }
                roomBean.getRoomBean_Custom_HU("", returnMap, "number-userid-dqnumber-recordMsgList-brands-show_brands");
                roomBean.setVictoryid(userBean.getUserid());
                UserBean user = roomBean.getUserBean(Integer.parseInt(jsonTo.get("p_userid")));
                //胡牌的人信息
                if (user.getRecordMsgList().size() == 0) {
                    user.getRecordMsgList().add("无");
                }
                user.getUser_Custom("userid-number-dqnumber-recordMsgList-brands-show_brands",
                        returnMap);
                returnMap.put("type", "endhu");
                returnMap.put("room_type",2);
                sendMessageTo(returnMap);
                sendMessageTo(returnMap, roomBean);
                roomBean.addLog(returnMap);
                // 记录战绩
                gameService.addRecord(roomBean);
                roomBean.InItReady();
            }
        }

        // 胡牌_自摸
        if ("endhu_this".equals(jsonTo.get("type"))) {
            returnMap.put("type", "endhu_this");
            if(roomBean.getMax_person()!=2){
                // 检测自摸胡牌0不胡牌 900=4朴一将 777小七对
                int state = gameService.End_Hu_This(userBean, roomBean, Integer.parseInt(jsonTo.get("brand")));
                List<Integer> brandList = userBean.getBrands();
                List<Integer> list2 = new ArrayList<Integer>();
                for (Integer in : brandList) {
                    if (!list2.contains(in)) {
                        list2.add(in);
                    }
                }
                brandList.clear();
                for (Integer in : list2) {
                    brandList.add(in);
                }
                //听牌
                String[] ting_brands = jsonTo.get("ting_brand").toString().split("-");
                List<Integer> tingList = new ArrayList<>();
                for (int i = 0; i < ting_brands.length; i++) {
                    if(ting_brands[i].equals("")){
                        continue;
                    }
                    tingList.add(Integer.parseInt(ting_brands[i]));
                }
                //检测手中的中发白
                 userBean.getZFBnum(userBean.getBrands(),userBean,Integer.parseInt(jsonTo.get("brand")),1);

                // 自摸+1
                userBean.setPower(1);
                userBean.getRecordMsgList().add("自摸+1");

                userBean.setHunum(userBean.getHunum()+1);

                // 胡牌
                userBean.setPower(1);
                userBean.getRecordMsgList().add("胡牌+1");

                // 是否海底捞
                if (roomBean.getBrands().size() == 0) {
                    userBean.setPower(5);
                    userBean.getRecordMsgList().add("海底捞+5");
                }
                //杠上花
                int before_type = Integer.parseInt(jsonTo.get("before_type"));
                if (before_type == 1) {
                    userBean.setPower(5);
                    userBean.getRecordMsgList().add("杠上花+5");
                /*userBean.setPower(-1);
                System.err.println("自摸+1");
                userBean.getRecordMsgList().remove("自摸+1");*/
                }

                // 结算检测
                MahjongUtils mahjongUtils = new MahjongUtils();
                mahjongUtils.getBrandKe(roomBean, userBean, Integer.parseInt(jsonTo.get("brand")), tingList,1);

                if (userBean.getPower_number()>=10) {
                    userBean.setTennum(userBean.getTennum()+1);
                }
                System.out.println(">>>>>>>牌型检测完成啦");

                // 自摸结算
                state = gameService.End_Game_This(userBean, roomBean);
                userBean.setIshu(1);
                System_Mess.system_Mess.ToMessagePrint("自摸状态" + state);
                returnMap.clear();
                returnMap.put("type","hu");
                returnMap.put("hu_type",1);
                returnMap.put("userid",userBean.getUserid());
                sendMessageTo(returnMap);
                sendMessageTo(returnMap,roomBean);
                returnMap.clear();
                int count = gameService.Is_Hunum(roomBean);
                boolean bool = false;
                if (count == 3)
                    bool = true;
                if (count == 2 && roomBean.getGame_userlist().size() == 3)
                    bool = true;
                if (count == 1 && roomBean.getGame_userlist().size() == 2)
                    bool = true;
                if (bool) {
                    if (state == 0) {
                        returnMap.put("type", "endhu_this");
                        // 胡牌的人信息
                        if (userBean.getRecordMsgList().size() == 0) {
                            userBean.getRecordMsgList().add("无");
                        }
                        for (UserBean user :
                                roomBean.getGame_userlist()) {
                            //整合碰杠吃
                            List<Integer> show_brands = user.getShow_brands();
                            show_brands.addAll(user.getBump_brands());
                            show_brands.addAll(user.getHide_brands());
                        }
                        roomBean.getRoomBean_Custom("", returnMap, "number-userid-dqnumber-recordMsgList-brands-show_brands");
                        // 设置赢家id
                        roomBean.setVictoryid(userBean.getUserid());
                        returnMap.put("userid", userBean.getUserid());
                        returnMap.put("brand", Integer.parseInt(jsonTo.get("brand")));
                        returnMap.put("state", String.valueOf(state));
                        sendMessageTo(returnMap, roomBean);
                        roomBean.addLog(returnMap);
                        // 记录战绩
                        gameService.addRecord(roomBean);
                        roomBean.InItReady();
                    } else {
                        returnMap.put("state", "999");
                    }
                    sendMessageTo(returnMap);
                }else{
                    jsonTo.put("type", "brand_nextid");
                }
            }else{
                // 检测自摸胡牌0不胡牌 900=4朴一将 777小七对
                int state = gameService.End_Hu_This(userBean, roomBean, Integer.parseInt(jsonTo.get("brand")));
                List<Integer> brandList = userBean.getBrands();
                List<Integer> list2 = new ArrayList<Integer>();
                for (Integer in : brandList) {
                    if (!list2.contains(in)) {
                        list2.add(in);
                    }
                }
                brandList.clear();
                for (Integer in : list2) {
                    brandList.add(in);
                }
                //听牌
                String[] ting_brands = jsonTo.get("ting_brand").toString().split("-");
                List<Integer> tingList = new ArrayList<>();
                for (int i = 0; i < ting_brands.length; i++) {
                    if(ting_brands[i].equals("")){
                        continue;
                    }
                    tingList.add(Integer.parseInt(ting_brands[i]));
                }
                //检测手中的中发白
                for (UserBean user: roomBean.getGame_userlist()){
                    user.getZFBnum(user.getBrands(),user,Integer.parseInt(jsonTo.get("brand")),1);
                }

                // 自摸+1
                userBean.setPower(1);
                userBean.getRecordMsgList().add("自摸+1");

                userBean.setHunum(userBean.getHunum()+1);

                // 胡牌
                userBean.setPower(1);
                userBean.getRecordMsgList().add("胡牌+1");

                // 是否海底捞
                if (roomBean.getBrands().size() == 0) {
                    userBean.setPower(5);
                    userBean.getRecordMsgList().add("海底捞+5");
                }
                //杠上花
                int before_type = Integer.parseInt(jsonTo.get("before_type"));
                if (before_type == 1) {
                    userBean.setPower(5);
                    userBean.getRecordMsgList().add("杠上花+5");
                /*userBean.setPower(-1);
                System.err.println("自摸+1");
                userBean.getRecordMsgList().remove("自摸+1");*/
                }

                // 结算检测
                MahjongUtils mahjongUtils = new MahjongUtils();
                mahjongUtils.getBrandKe(roomBean, userBean, Integer.parseInt(jsonTo.get("brand")), tingList,1);

                if (userBean.getPower_number()>=10) {
                    userBean.setTennum(userBean.getTennum()+1);
                }
                System.out.println(">>>>>>>牌型检测完成啦");

                // 自摸结算
                state = gameService.End_Game_This(userBean, roomBean);
                System_Mess.system_Mess.ToMessagePrint("自摸状态" + state);
                if (state == 0) {
                    // 胡牌的人信息
                    if (userBean.getRecordMsgList().size() == 0) {
                        userBean.getRecordMsgList().add("无");
                    }
                    for (UserBean user:roomBean.getGame_userlist()) {
                        List<Integer> show_brands = user.getShow_brands();
                        show_brands.addAll(user.getBump_brands());
                        show_brands.addAll(user.getHide_brands());
                    }
                    roomBean.getRoomBean_Custom("", returnMap, "number-userid-dqnumber-recordMsgList-brands-show_brands");
                    // 设置赢家id
                    roomBean.setVictoryid(userBean.getUserid());
                    returnMap.put("userid", userBean.getUserid());
                    returnMap.put("brand", Integer.parseInt(jsonTo.get("brand")));
                    returnMap.put("state", String.valueOf(state));
                    sendMessageTo(returnMap, roomBean);
                    roomBean.addLog(returnMap);
                    // 记录战绩
                    gameService.addRecord(roomBean);
                    roomBean.InItReady();
                } else {
                    returnMap.put("state", "999");
                }
                sendMessageTo(returnMap);
            }
        }

        // 弃胡
        if ("giveup_hu".equals(jsonTo.get("type"))) {
            returnMap.put("type", "giveup_hu");
            int p_userid = Integer.parseInt(jsonTo.get("p_userid"));
            userBean.getNohu().add(Integer.parseInt(jsonTo.get("brand")));//一轮不能胡这张
            returnMap.put("nohu",userBean.getNohu());
            // 0不胡牌 900=4朴一将 890=1癞子3朴1将 880=1癞子4朴 缺将
            int state = gameService.End_Hu(userBean, Integer.parseInt(jsonTo.get("brand")), roomBean, p_userid);
            if (state != -1) {
                // 结算500=已经结算 501=等待别人胡牌操作 502=等待别人结算 503=弃胡
                state = gameService.End_Game(userBean, roomBean, p_userid, 2);
                System.out.println("state" + state);
                // 成功结算
                if (state == 0) {
                    jsonTo.put("type", "end");
                } else if (state == 503) {
                    sendMessageTo(returnMap);
                    //sendMessageTo(returnMap, roomBean);
                    state = gameService.OutBrand(roomBean, roomBean.getLastBrand(), roomBean.getUserBean(roomBean.getLastUserid()), returnMap);
                    System_Mess.system_Mess.ToMessagePrint("碰牌状态" + state);
                    if(state==300){
                        jsonTo.put("type", "brand_nextid");
                    }else{
                        returnMap.put("type", "out_brand");
                        // 出的牌
                        returnMap.put("outbrand", roomBean.getLastBrand());
                        // 出牌人id
                        returnMap.put("out_userid", roomBean.getLastUserid());
                        // 出牌类型0正常出牌   1弃胡检测
                        returnMap.put("out_type",1);
                        sendMessageTo(returnMap);
                        sendMessageTo(returnMap, roomBean);
                    }
                    roomBean.HuState_InIt();
                    roomBean.getNextMap().clear();
                }
            }
        }
        if("Settlement".equals(jsonTo.get("type"))){
            roomBean.getRoomBean_Custom("roomno-fen-data-max_number-houseid", returnMap, "hunum-gangnum-tennum-number-userid-nickname-avatarurl");
            returnMap.put("type","Settlement");
            sendMessageTo(returnMap);
        }


        /***********************************杠***************************************/
        // 暗杠
        if ("hide_bar".equals(jsonTo.get("type"))) {
            /*if(roomBean.getBrands().size()==0){
                jsonTo.put("type", "flow");
            }*/
            userBean.setGangnum(userBean.getGangnum()+1);
            roomBean.setHide_baruserid(userBean.getUserid());
            //触发杠的牌
            int gang_brand = Integer.parseInt(jsonTo.get("brand"));
            //牌值转换
            int brand_value = mahjong_Util.getBrand_Value(gang_brand);
            //暗杠中发白+3颗
            if (brand_value == 31) {
                userBean.setPower(3);
                userBean.getRecordMsgList().add("暗杠红中+3");
            } else if (brand_value == 32) {
                userBean.setPower(3);
                userBean.getRecordMsgList().add("暗杠发财+3");
            } else if (brand_value == 33) {
                userBean.setPower(3);
                userBean.getRecordMsgList().add("暗杠白板+3");
            } else {
                userBean.setPower(2);
                userBean.getRecordMsgList().add("暗杠普通牌+2");
            }
            int[] brands = gameService.Hide_Bar(userBean, gang_brand, roomBean);
            returnMap.put("type", "hide_bar");
            returnMap.put("brands", brands);
            returnMap.put("userid", userBean.getUserid());
            sendMessageTo(returnMap);
            sendMessageTo(returnMap, roomBean);
        }
        // 明杠
        if ("show_bar".equals(jsonTo.get("type"))) {
            /*if(roomBean.getBrands().size()==0){
                jsonTo.put("type", "flow");
            }*/
            userBean.setGangnum(userBean.getGangnum()+1);
            roomBean.setShow_baruserid(userBean.getUserid());
            //触发杠的牌
            int gang_brand = Integer.parseInt(jsonTo.get("brand"));
            //牌值转换
            int brand_value = mahjong_Util.getBrand_Value(gang_brand);
            //明杠中发白+2颗
            if (brand_value == 31) {
                userBean.setPower(2);
                userBean.getRecordMsgList().add("明杠红中+2");
            } else if (brand_value == 32) {
                userBean.setPower(2);
                userBean.getRecordMsgList().add("明杠发财+2");
            } else if (brand_value == 33) {
                userBean.setPower(2);
                userBean.getRecordMsgList().add("明杠白板+2");
            } else {
                userBean.setPower(1);
                userBean.getRecordMsgList().add("明杠普通牌+1");
            }
            gameService.Show_Bar(userBean, Integer.parseInt(jsonTo.get("userid")), gang_brand, roomBean);
            returnMap.put("type", "show_bar");
            returnMap.put("userid", userBean.getUserid());
            returnMap.put("baruserid", jsonTo.get("userid"));
            returnMap.put("brand", gang_brand);
            sendMessageTo(returnMap);
            sendMessageTo(returnMap, roomBean);
        }
        // 抢杠过胡
        if ("repair_bar_bump_pass".equals(jsonTo.get("type"))) {
            returnMap.put("type","repair_bar_bump_pass");
            userBean.getNohu().add(Integer.parseInt(jsonTo.get("brand")));//一轮不能胡这张
            returnMap.put("brand_nextid",roomBean.getRepair_baruserid());
            sendMessageTo(returnMap);
            sendMessageTo(returnMap, roomBean);
        }
        // 补杠
        if ("repair_bar_bump".equals(jsonTo.get("type"))) {
            /*if(roomBean.getBrands().size()==0){
                jsonTo.put("type", "flow");
            }*/
            userBean.setGangnum(userBean.getGangnum()+1);
            //触发杠的牌
            int gang_brand = Integer.parseInt(jsonTo.get("brand"));
            //牌值转换
            int brand_value = mahjong_Util.getBrand_Value(gang_brand);
            //明杠中发白+2颗
            if (brand_value == 31) {
                userBean.setPower(1);
                userBean.getRecordMsgList().add("明杠红中+2");
                userBean.getRecordMsgList().remove("碰红中+1");
            } else if (brand_value == 32) {
                userBean.setPower(1);
                userBean.getRecordMsgList().add("明杠发财+2");
                userBean.getRecordMsgList().remove("碰发财+1");
            } else if (brand_value == 33) {
                userBean.setPower(1);
                userBean.getRecordMsgList().add("明杠白板+2");
                userBean.getRecordMsgList().remove("碰白板+1");
            } else {
                userBean.setPower(1);
                userBean.getRecordMsgList().add("明杠普通牌+1");
            }
            roomBean.setRepair_baruserid(userBean.getUserid());
            gameService.Repair_Bar_Bump(userBean, gang_brand, roomBean);
            returnMap.put("type", "repair_bar_bump");
            returnMap.put("userid", userBean.getUserid());
            returnMap.put("brand", gang_brand);
            sendMessageTo(returnMap);
            sendMessageTo(returnMap, roomBean);
        }
        /*********************************************房间操作******************************************/

        // 摸牌
        if ("get_brand".equals(jsonTo.get("type"))) {
            // 摸牌
            int brand = roomBean.getBrand_Random();
            if(brand==-1){
                jsonTo.put("type", "flow");
            }else{
                userBean.getBrands().add(brand);
                returnMap.put("userid", userBean.getUserid());
                returnMap.put("brand", brand);
                returnMap.put("type", "get_brand");
                sendMessageTo(returnMap);
                sendMessageTo(returnMap, roomBean);
            }
        }
        if("flow".equals(jsonTo.get("type"))){
            // 流局
            if (roomBean.getBrands().size() == 0) {
                returnMap.put("type", "flow");
                // 设置流局
                roomBean.setFlow(1);
                // 查叫当两家或三家甚至四家摸完牌后仍然没有人胡牌，这时，每一家必须把牌亮出开始查叫（是否听牌），没有查叫的一家必须按最大算法赔给有叫的一家，若每一家都没有叫，则不用赔或这收入，此局荒牌。若查叫时某一家牌型中没有缺一门，无论有无叫，都要赔给有叫的一家总颗数的两倍。
                //(如果有一家没有叫牌，自己的牌为缺一门，则赔其余几家有叫牌的最大胡牌颗数，如果这家当前牌三门都还有，则赔最大胡牌颗数的两倍)
                //注：没叫牌的玩家要赔给有叫牌的每一位玩家
                MahjongUtils mahjongUtils = new MahjongUtils();
                int num = 0;//未听牌人数
                int tinguser = 0;//听牌人数
                for (UserBean user : roomBean.getGame_userlist()){
                    System.out.println(user.getNickname()+"听牌数:"+user.getTingCards().size());
                    if(user.getTingCards().size()==0){//未听牌玩家
                        if (roomBean.getAssured_brand() == 1 || roomBean.getMax_person()==2 || user.getIsque1()) {
                            user.setQ1(1);
                        }
                        num++;
                    }else{
                        if (roomBean.getAssured_brand() == 1 || roomBean.getMax_person()==2 || user.getIsque1()) {
                            user.setQ1(1);
                        }
                        int power = 0;
                        ArrayList<String> list = new ArrayList<>();
                        for (int i = 0; i < user.getTingCards().size(); i++) {
                            UserBean user2 = new UserBean();
                            try {
                                BeanUtils.CopySameBean(user,user2);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            ArrayList<Integer> objects = new ArrayList<>();https://xrz1.xyz/?id=kl4
                            objects.addAll(user.getBump_brands());
                            user2.setBump_brands(objects);

                            ArrayList<Integer> objects2 = new ArrayList<>();
                            objects2.addAll(user.getHide_brands());
                            user2.setHide_brands(objects2);

                            ArrayList<Integer> objects3 = new ArrayList<>();
                            objects3.addAll(user.getShow_brands());
                            user2.setShow_brands(objects3);

                            ArrayList<Integer> objects4 = new ArrayList<>();
                            objects4.addAll(user.getBrands());
                            user2.setBrands(objects4);

                            ArrayList<String> objects5 = new ArrayList<>();
                            objects5.addAll(user.getRecordMsgList());
                            user2.setRecordMsgList(objects5);


                            mahjongUtils.getBrandKe(roomBean,user2,user2.getTingCards().get(i),user2.getTingCards(),0);
                             if(user2.getPower_number()>power){
                                power = user2.getPower_number();
                                list.clear();
                                list.addAll(user2.getRecordMsgList());
                            }
                        }
                        user.setPower_number(power);
                        if(list.size()!=0){
                            user.getRecordMsgList().clear();
                            user.getRecordMsgList().addAll(list);
                        }
                    }
                }
                tinguser = roomBean.getGame_userlist().size()-num;
                if(tinguser!=roomBean.getGame_userlist().size()){
                    if(num!=roomBean.getGame_userlist().size()){

                        //检测手中的中发白
                        for (UserBean user1: roomBean.getGame_userlist()){
                            user1.getZFBnum(user1.getBrands(),user1,0,1);
                        }

                        for (UserBean user : roomBean.getGame_userlist()){
                            if(user.getTingCards().size()!=0){//听牌玩家
                                user.setPower(1);//胡牌+1
                                int fen = roomBean.getFen() * user.getPower_number();
                                int ns = 1;
                                int sumfen = 0;
                                for (UserBean u : roomBean.getGame_userlist()){
                                    if((user.getUserid()!=u.getUserid() && u.getTingCards().size()==0)){//为听牌的人
                                        int n = 1;
                                        if(u.getQ1()==0){
                                            n = 2;
                                            ns = 2;
                                        }
                                        //u.setNumber((-fen*(roomBean.getGame_userlist().size()-num)*n)+userBean.getNumber());
                                        u.setNumber((-fen*n)+u.getNumber());
                                        u.setDqnumber(-fen*n+(u.getDqnumber()));
                                        //u.getRecordMsgList().add("查叫"+u.getDqnumber());
                                        if(!u.getRecordMsgList().contains("查叫")){
                                            u.getRecordMsgList().add("查叫");
                                        }
                                        sumfen+=fen*n;
                                    }
                                }
                                user.setNumber(sumfen+user.getNumber());
                                user.setDqnumber(sumfen+user.getDqnumber());
                                //user.getRecordMsgList().add("查叫+"+user.getDqnumber());
                                user.getRecordMsgList().add("查叫+1");
                            }
                        }
                        roomBean.getRoomBean_Custom("", returnMap, "number-userid-dqnumber-recordMsgList-brands-show_brands");
                        roomBean.setVictoryid(userBean.getUserid());
                        returnMap.put("userid", userBean.getUserid());
                        returnMap.put("brand", -1);
                        returnMap.put("type", "endhu_this");
                    }
                }
            }

            roomBean.addLog(returnMap);
            // 记录战绩
            gameService.addRecord(roomBean);
            roomBean.InItReady();
            sendMessageTo(returnMap);
            sendMessageTo(returnMap, roomBean);
        }


        // 需要摸牌
        if ("brand_nextid".equals(jsonTo.get("type"))) {
            roomBean.setHucount(0);
            // 查找下一个摸牌用户
            returnMap.put("type", "brand_nextid");
            if(roomBean.getRepair_baruserid()!=0){
                returnMap.put("brand_nextid", roomBean.getRepair_baruserid());
            }else{
                returnMap.put("brand_nextid", roomBean.getNextUserId());
            }
            for (UserBean user :
                    roomBean.getGame_userlist()) {
                returnMap.put("nohu",user.getNohu());
                if(user.getUserid()==Integer.valueOf(String.valueOf(returnMap.get("brand_nextid")))){
                    user.getNoany().clear();
                    user.getNohu().clear();
                    returnMap.put("nohu",user.getNohu());
                }
                Public_State.clients.get(user.getOpenid()).sendMessageTo(returnMap);
            }
            //sendMessageTo(returnMap, roomBean);
        }
        // 记录操作日志
        if (roomBean != null) {
            for (String value : Public_State.types) {
                if (value.equals(jsonTo.get("type"))) {
                    roomBean.addLog(returnMap);
                    break;
                }
            }
        }
        // 消息通道
        if ("message".equals(jsonTo.get("type"))) {
            returnMap.put("type", "message");
            returnMap.put("userid", userBean.getUserid());
            returnMap.put("text", jsonTo.get("text"));
            sendMessageTo(returnMap, roomBean);
        }
        // 断线重连
        if ("end_wifi".equals(jsonTo.get("type"))) {
            roomBean = Public_State.PKMap.get(jsonTo.get("roomno"));
            if (roomBean == null) {
                returnMap.put("state", "100");
            } else {
                returnMap.put("type", "con_wifi");
                returnMap.put("userid", userBean.getUserid());
                // 告知其他人我已经在线
                userBean = roomBean.getUserBean(userBean.getUserid());
                userBean.setExit_state(0);

                sendMessageTo(returnMap, roomBean);
                // 查询出房间信息返回
                roomBean.getRoomBean_Custom(
                        "cannon-dice-exit_game-end_userid-roomno-user_positions-brands_count-fen-fan-money-gps-banker-game_number-max_number-max_person-state-end_userid-game_type-adjust_brand-same_colour-assured_brand-aircraft-ranging-initiative_ranging-user_log",
                        returnMap,
                        "userid-nickname-avatarurl-brands-eat_brands-bump_brands-show_brands-ishu-hide_brands-out_brands-ip-log_lat-number-is_hustate-hu_state-exit_game-lack_type-lack_brands-lack_state-exchange_state-exchange_brands-receive_brands-sex-recordMsgList");
                //关闭托管
                // userBean.getThread_robot().setBool(false);
            }
            returnMap.put("type", "end_wifi");
            sendMessageTo(returnMap);
        }
        baseDao.CloseAll();
        lock.unlock();
    }

    /***
     * 退出房间
     *
     * @throws IOException
     */
    public void Exit_Room() {
        returnMap.clear();
        // 已加入房间且房间在等待加入阶段则退出
        if (roomBean != null && roomBean.getState() == 0 && roomBean.getHouseid() != userBean.getUserid()) {
            // 将自己从房间内清除
            roomBean.User_Remove(userBean.getUserid());
            returnMap.put("type", "exit");
            returnMap.put("userid", userBean.getUserid());
            sendMessageTo(returnMap, roomBean);
            roomBean = null;
            userBean.Initialization();
            // sendMessageTo(returnMap);
        } else if (roomBean != null && roomBean.getState() != 0) {
            // 告知其他人我已经掉线
            returnMap.put("type", "toUser_exit");
            returnMap.put("end_user", userBean.getUserid());
            sendMessageTo(returnMap, roomBean);
            // 房间存在且房间为开始且自己是房主的情况则解散房间
        } else if (roomBean != null && roomBean.getState() == 0 && roomBean.getHouseid() == userBean.getUserid()) {
            Exit_All();
        }
        roomBean = null;
    }

    /**
     * 解散房间
     */
    public void Exit_All() {
        returnMap.put("type", "exit_all");
        sendMessageTo(returnMap);
        sendMessageTo(returnMap, roomBean);
        roomBean.Ready_InIt();
        roomBean.getExit_time().closeRoomBean();
        Public_State.PKMap.remove(roomBean.getRoomno());
        roomBean = null;
    }

    @OnError
    public void onError(Session session, Throwable error) {
        if (!"远程主机强迫关闭了一个现有的连接".equals(error.getMessage()) && error.getMessage() != null) {
            error.printStackTrace();
            System_Mess.system_Mess.ToMessagePrint(
                    userBean.getNickname() + "异常" + error.getLocalizedMessage() + "***" + error.getMessage());
        }
    }

    /**
     * 发送消息(房间所有人)
     *
     * @param returnMap
     * @param roomBean
     */
    public synchronized void sendMessageTo(Map<String, Object> returnMap, RoomBean roomBean) {
        for (UserBean user : roomBean.getGame_userlist()) {
            Mahjong_Socket webSocket = (Mahjong_Socket) Public_State.clients.get(user.getOpenid());
            if (webSocket != null && webSocket.session.isOpen()) {
                // 不等于自己的则发送消息
                if (!user.getOpenid().equals(userBean.getOpenid())) {
                    webSocket.sendMessageTo(returnMap);
                }
            }
            // else{
            // System.out.println("推送托管");
            // user.getThread_robot().setReturnMap(returnMap);
            // user.getThread_robot().setStatus(1);
            // }
        }
    }

    /**
     * 给自己返回信息
     *
     * @param returnMap
     */
    public synchronized void sendMessageTo(Map<String, Object> returnMap) {
        if (session.isOpen()) {
            String returnjson = gson.toJson(returnMap).toString();
            // 加密
            // returnjson=AES.encrypt(returnjson);
            try {
                session.getBasicRemote().sendText(returnjson);
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
            if(!returnjson.contains("heartbeat")){
                System_Mess.system_Mess.ToMessagePrint(userBean.getNickname() + "返回消息(自己)" + returnjson);
            }
        }
    }

    /**
     * 给指定用户发送
     *
     * @param returnMap
     * @param openid
     * @throws IOException
     */
    public synchronized void sendMessageTo(Map<String, Object> returnMap, String openid) throws IOException {
        Mahjong_Socket websocket = (Mahjong_Socket) Public_State.clients.get(openid);
        if (websocket != null && websocket.session.isOpen()) {
            String returnjson = gson.toJson(returnMap).toString();
            // 加密
            // returnjson=AES.encrypt(returnjson);
            websocket.session.getBasicRemote().sendText(returnjson);
            System_Mess.system_Mess.ToMessagePrint(userBean.getNickname() + "(指定)返回消息" + returnjson);
        }
    }
}
