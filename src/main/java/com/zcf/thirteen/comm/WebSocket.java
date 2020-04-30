/**
 *
 */
package com.zcf.thirteen.comm;

import java.io.IOException;
import java.util.*;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.zcf.mahjong.mahjong.Public_State;
import com.zcf.mahjong.util.MapHelper;
import com.zcf.thirteen.bean.T_RoomBean;
import com.zcf.thirteen.bean.T_UserBean;
import com.zcf.thirteen.dao.UserDao;
import com.zcf.thirteen.service.GameService;

import com.zcf.thirteen.service.Time_Room;
import com.zcf.thirteen.util.BaseDao;
import com.zcf.thirteen.util.System_Mess;

/**
 * @author guolele
 * @date 2019年2月20日 下午3:02:10
 *
 */
@ServerEndpoint("/thirteen/{userid}")
public class WebSocket {
    // json转换
    private Gson gson = new Gson();
    public Session session;
    // 用户信息
    public T_UserBean userBean;
    // 自己进入的房间
    public T_RoomBean rb;
    private BaseDao baseDao = new BaseDao();

    // 用户dao
    private UserDao userDao = new UserDao(baseDao);
    // 游戏逻辑类
    private GameService gs = new GameService(baseDao);
    private Map<String, Object> returnMap = new HashMap<String, Object>();

    /**
     * 打开连接
     */
    @OnOpen
    public void onOpen(@PathParam("userid") Integer userid, Session session) throws IOException {
        boolean bool = true;
        System.out.println("id----------------------" + userid);
        if (userid != null) {
            // 查询用户信息
            userBean = userDao.getUser(Integer.valueOf(userid));
            // 用户金币比例1：100
            // userBean.setMoney(userBean.getMoney() * 100);
            baseDao.CloseAll();
            if (userBean != null) {
                userBean.setSession(session);
                // 将自己放入客户端集合
                Public_State_t.clients_t.put(String.valueOf(userBean.getUserid()), this);
                this.session = session;
                System_Mess.system_Mess.ToMessagePrint(userBean.getNickname() + "已连接(十三水)");
                bool = false;
            }
        }
        // 如果没走正常业务则归类非法连接
        if (bool) {
            session.close();
            System_Mess.system_Mess.ToMessagePrint("非法连接");
        }
    }

    /**
     * 关闭连接
     *
     */
    @OnClose
    public void onClose() throws IOException {
        if (userBean != null) {
            // 如果已加入房间则通知其他人自己退出
            if (rb != null) {
                if (rb.getRoom_state_a() == 0) {
                    returnMap.put("state", "110");// 退出房间
                    returnMap.put("id", userBean.getUserid());
                    returnMap.put("type", "exitGame");
                    sendMessageTo(returnMap, userBean);
                    sendMessageToAll(returnMap, rb);
                    Public_State_t.clients_t.remove(String.valueOf(userBean.getUserid()));
                    rb.remove_options(userBean.getUserid());
                    rb.getGame_userList().remove(userBean);
                    if(rb.getClub_state()!=-1){
                        room_change(rb,0);
                    }
                } else {
                    // 标识已经掉线
                    userBean.setGametype(2);
                    int count = 0;
                    for (int i = 0; i < rb.getGame_userList().size(); i++) {
                        if (rb.getGame_userList().get(i).getGametype() == 2) {
                            count++;
                        }
                    }
                    returnMap.put("state", "109");// 掉线
                    returnMap.put("id", userBean.getUserid());
                    returnMap.put("type", "exitGame");
                    sendMessageTo(returnMap, userBean);
                    sendMessageToAll(returnMap, rb);
                    if (count == rb.getGame_userList().size()) {
                        if(rb.getClub_state()==-1){
                            Public_State_t.PKMap1.remove(String.valueOf(rb.getRoom_number()));
                            rb = null;
                        }
                        room_change(rb,0);
                    }
                }
            }
            System_Mess.system_Mess.ToMessagePrint(userBean.getNickname() + "断开连接（十三水）");
        }
    }

    /**
     * 接收消息
     *
     * @throws InterruptedException
     */
    @OnMessage
    public void onMessage(String msg) {

        returnMap.clear();
        System_Mess.system_Mess.ToMessagePrint("接收" + userBean.getNickname() + "的消息" + msg);
        Map<String, String> jsonTo = gson.fromJson(msg, new TypeToken<Map<String, String>>() {
        }.getType());
        // 创建房间
        if ("CreateRoom".equals(jsonTo.get("type"))) {
            System.out.println("money---------------------" + userBean.getMoney());
            if (userBean.getDiamond() < 8) {
                returnMap.put("state", "101");// 金币不足
                returnMap.put("type", "CreateRoom");// 创建房间
                sendMessageTo(returnMap, userBean);
            } else {
                rb = gs.Esablish(jsonTo, userBean);
                rb.setRoom_state_a(0);
                userBean.setMoney(0);
                returnMap.put("state", "102");// 创建房间成功
                returnMap.put("type", "CreateRoom");// 创建房间
                rb.getRoomBean_Custom("userid-nickname-avatarurl-money", returnMap,
                        "room_number-baibian-user_positions-max_number-room_type-fen-game_number");
                sendMessageTo(returnMap, userBean);
                //sendMessageToAll(returnMap, rb);
                jsonTo.put("type", "Sit_down");
            }
        }
        // 加入房间
        if ("Matching".equals(jsonTo.get("type"))) {
            Integer state = Integer.valueOf(jsonTo.get("state"));//0普通房间  1俱乐部房间
            if ((state == 0 && Public_State_t.PKMap1.get(jsonTo.get("room_number")) == null) || (state == 0 && Public_State_t.PKMap1.get(jsonTo.get("room_number")) != null && Public_State_t.PKMap1.get(jsonTo.get("room_number")).getClub_state() != -1)) {
                returnMap.put("userid", userBean.getUserid());
                returnMap.put("state", "103");// 房间不存在
                returnMap.put("type", "Matching");
                sendMessageTo(returnMap, userBean);
            } else {
                rb = Public_State_t.PKMap1.get(jsonTo.get("room_number"));
                rb.setRoom_state_a(0);
                if (userBean.getDiamond() < rb.getFen()) {
                    returnMap.put("state", "107");// 金币不足
                    returnMap.put("type", "Matching");
                    sendMessageTo(returnMap, userBean);
                } else if (rb.getFoundation() == rb.getGame_userList().size()) {
                    returnMap.put("userid", userBean.getUserid());
                    returnMap.put("state", "104");// 房间已满
                    returnMap.put("type", "Matching");
                    sendMessageTo(returnMap, userBean);
                } else {
                    try {
                        rb = gs.Matching(jsonTo, userBean);
                        System.out.println(rb);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    userBean.setFloor(11);
                    // 经纬度
                    userBean.setLog_lat(jsonTo.get("log_lat"));
                    userBean.setMoney(0);
                    // 把自己信息推送给房间内其他玩家
                    returnMap.put("state", "106");// 加入房间成功
                    returnMap.put("userid", userBean.getUserid());
                    returnMap.put("type", "Matching");// 加入房间成功

                    rb.getRoomBean_Custom("userid-nickname-avatarurl-money-usertype-ready_state", returnMap,
                            "room_number-room_type-user_positions-max_number-baibian-game_number");
                    sendMessageToAll(returnMap, rb);
                    sendMessageTo(returnMap, userBean);
                    jsonTo.put("type", "Sit_down");
                }
            }
            returnMap.clear();
        }

        //查看所有房间
        if ("selectRooms".equals(jsonTo.get("type"))) {
            String club_number = jsonTo.get("club_number");
            Integer game_type = Integer.valueOf(jsonTo.get("game_type"));
            int floor = Integer.valueOf(jsonTo.get("floor"));//楼层
            int club_state = Integer.valueOf(jsonTo.get("club_state"));
            userBean.setClub_number(club_number);
            userBean.setFloor(floor);
            if(club_state==1){
                int clubrommnumber = gs.getclubrommnumber(club_number, game_type);
                if (clubrommnumber == 0) {
                    if (jsonTo.get("game_type").equals("0")) {
                        String rules = userDao.getRules(Integer.valueOf(club_number));
                        String[] split = rules.split("/");
                        Map<String, String> map = new HashMap<>();
                        map.put("foundation", split[0]);
                        map.put("max_number", split[1]);
                        map.put("rule", Integer.valueOf(split[0]) == 0 ? "-2" : split[2]);
                        map.put("room_type", split[3]);
                        map.put("baibian", split[4]);
                        for (int i = 1; i < 4; i++) {
                            for (int j = 0; j < 8; j++) {
                                T_RoomBean rb = gs.Esablish(map, null);
                                rb.setClub_state(1);
                                rb.setClub_number(club_number);
                                rb.setFloor(i);
                            }
                        }
                    } else {
                        System.out.println("麻将");
                    }
                }
                returnMap.clear();
                List<T_RoomBean> list = new ArrayList<>();
                for (String s : Public_State_t.PKMap1.keySet()) {
                    if (Public_State_t.PKMap1.get(s).getClub_number().equals(club_number) && Public_State_t.PKMap1.get(s).getFloor() == floor) {
                        list.add(Public_State_t.PKMap1.get(s));
                    }
                }
                T_RoomBean r = new T_RoomBean();
                List<Map<String, Object>> maps = r.getrooms(list, "foundation-room_number-setRoom_state_a-game_number-max_number",
                        "avatarurl");

                returnMap.put("rooms", maps);
                returnMap.put("type", "selectRooms");
                sendMessageTo(returnMap, userBean);
            }
        }

        // 坐下
        if ("Sit_down".equals(jsonTo.get("type"))) {
            int down = gs.Sit_down(userBean, rb);
            if (down == 0) {
                returnMap.put("room_branker", rb.getRoom_branker());
                returnMap.put("userid", userBean.getUserid());
                returnMap.put("user_positions", rb.getUser_positions());
                returnMap.put("type", "Sit_down");
                returnMap.put("state", "130"); // 坐下成功
                sendMessageTo(returnMap, userBean);
                sendMessageToAll(returnMap, rb);
                if (Integer.valueOf(jsonTo.get("state")) == 1) {//俱乐部坐下
                    returnMap.clear();
                    room_change(rb,0);
                }
            } else {
                returnMap.put("type", "Sit_down");
                returnMap.put("state", "131"); // 坐下失败
                sendMessageTo(returnMap, userBean);
                sendMessageToAll(returnMap, rb);
            }
            returnMap.clear();
        }


        // 准备
        if ("ready".equals(jsonTo.get("type"))) {
            returnMap.put("type", "ready");
            if (userBean.getDiamond() < 1) {
                returnMap.put("ready_state", "105");// 积分或钻石不足
                sendMessageTo(returnMap, userBean);
            } else {
                userBean.setReady_state(1);
                returnMap.put("userid", userBean.getUserid());
                returnMap.put("ready_state", 1);
                sendMessageTo(returnMap, userBean);
                sendMessageToAll(returnMap, rb);
                if (gs.check_positions(rb)) {
                    jsonTo.put("type", "start_game");
                }
            }
        }
        // 取消准备
        if ("offReady".equals(jsonTo.get("type"))) {
            userBean.setReady_state(0);
            returnMap.put("type", "ready");
            returnMap.put("ready_state", 0);
            returnMap.put("userid", userBean.getUserid());
            sendMessageTo(returnMap, userBean);
            sendMessageToAll(returnMap, rb);
        }

        // 获取位置距离
        if ("get_position".equals(jsonTo.get("type"))) {
            returnMap.put("type", "get_position");
            List<String> list = new ArrayList<String>();
            for (int i = 0; i < rb.getGame_userList().size(); i++) {
                T_UserBean user = rb.getGame_userList().get(i);
                for (int j = i + 1; j < rb.getGame_userList().size(); j++) {
                    T_UserBean user2 = rb.getGame_userList().get(j);
                    double distance = MapHelper.GetPointDistance(
                            user.getLog_lat(), user2.getLog_lat());
                    String re = user.getUserid() + "-"
                            + user2.getUserid() + "/"
                            + String.valueOf(distance);
                    list.add(re);
                }
            }
            returnMap.put("list", list);
            returnMap.put("position", rb.getUser_positions());
            sendMessageTo(returnMap, userBean);
        }

        // 消息通道
        if ("message".equals(jsonTo.get("type"))) {
            returnMap.put("type", "message");
            returnMap.put("userid", userBean.getUserid());
            returnMap.put("text", jsonTo.get("text"));
            sendMessageTo(returnMap, userBean);
            sendMessageToAll(returnMap, rb);
        }
        // 语音
        if ("send_voice".equals(jsonTo.get("type"))) {
            returnMap.put("type", "send_voice");
            returnMap.put("userid", userBean.getUserid());
            returnMap.put("voice", userBean.getVoice());
            sendMessageTo(returnMap, userBean);
            sendMessageToAll(returnMap, rb);
        }

        //获取三墩牌(开牌)
        if ("getBrand".equals(jsonTo.get("type"))) {
            rb.getLock().lock();
            String userid = String.valueOf(jsonTo.get("userid"));
            T_UserBean userBean = rb.getUserBean(Integer.valueOf(userid));

            rb.setBrand(userBean, String.valueOf(jsonTo.get("upBrand")), String.valueOf(jsonTo.get("middleBrand")),
                    String.valueOf(jsonTo.get("belowBrand")));
            //接收用于逻辑处理的三墩牌型
            userBean.setUpBrand_type(String.valueOf(jsonTo.get("upBrand_type")));
            userBean.setMiddleBrand_type(String.valueOf(jsonTo.get("middleBrand_type")));
            userBean.setBelowBrand_type(String.valueOf(jsonTo.get("belowBrand_type")));
            //接收用于转发的用户三墩牌型
            userBean.setUpBrand_type_a(String.valueOf(jsonTo.get("upBrand_type_a")));
            userBean.setMiddleBrand_type_a(String.valueOf(jsonTo.get("middleBrand_type_a")));
            userBean.setBelowBrand_type_a(String.valueOf(jsonTo.get("belowBrand_type_a")));
            returnMap.put("type", "getBrand");
            returnMap.put("userid", userid);
            sendMessageTo(returnMap, this.userBean);
            sendMessageToAll(returnMap, rb);
            returnMap.clear();
            if (gs.getUserstate(rb)) {
                rb.setIsli(1);
            }
            rb.getLock().unlock();
        }

        //邀请玩家加入俱乐部
        if("club_invite".equals(jsonTo.get("type"))){
            Integer userid = Integer.valueOf(jsonTo.get("userid"));//呗邀请人id
            String club_number = jsonTo.get("club_number");//俱乐部号
        }

        // 开始游戏
        if ("start_game".equals(jsonTo.get("type"))) {
            if (rb.getClub_state() == 1 || rb.getClub_state() == -1) {
                int start_Game = rb.Start_Game(userBean, rb);
                if (start_Game == 2) {
                    rb.setGame_number(rb.getGame_number() + 1);
                    returnMap.put("type", "start_game");
                    returnMap.put("timer", rb.getTimer_user());
                    returnMap.put("room_number", rb.getGame_number());
                    returnMap.put("state", "2"); // 游戏开始成功
                    // 房间计时器
                    rb.setTime_Room(new Time_Room(userBean, rb, gs, userDao));
                    rb.getTime_Room().start();
                    if(rb.getClub_state()!=-1){
                        room_change(rb,1);
                    }
                } else {
                    returnMap.put("type", "start_game");
                    returnMap.put("state", "0"); // 准备人数不足 无法开始游戏
                }
                sendMessageTo(returnMap, userBean);
                sendMessageToAll(returnMap, rb);
                returnMap.clear();
            } else {
                returnMap.put("type", "start_game");
                returnMap.put("state", "221"); // 俱乐部已打样
                sendMessageTo(returnMap, userBean);
                sendMessageToAll(returnMap, rb);
                returnMap.clear();
                Public_State_t.PKMap1.remove(String.valueOf(rb.getRoom_number()));
            }
        }

        // 发起解散
        if ("dissolve".equals(jsonTo.get("type"))) {
            int state = Integer.valueOf(jsonTo.get("dissolve_state"));
            int dissolve_userid = Integer.valueOf(jsonTo.get("dissolve_userid"));
            if (userBean.getUserid() == rb.getRoom_branker() && state == 1 && dissolve_userid == userBean.getUserid()
                    && rb.getRoom_state() == 0 && rb.getGame_number() == 1) { // 房主解散房间
                returnMap.put("type", "dissolve");
                returnMap.put("state", "400");// 房主解散房间
                returnMap.put("userid", userBean.getUserid());
                sendMessageTo(returnMap, userBean);
                sendMessageToAll(returnMap, rb);
                returnMap.clear();
                rb.setRoom_state(0);
                if(rb.getClub_state()==-1){
                    Public_State_t.PKMap1.remove(String.valueOf(rb.getRoom_number()));
                }else{
                    rb.remove_options(userBean.getUserid());
                    rb.getGame_userList().remove(userBean);
                }
                rb = null;
            } else {
                if (state == 1) {
                    userBean.setJiesan(1);
                    int jiesan = rb.getJiesan(rb, userBean);
                    if (jiesan == 1) {
                        returnMap.put("type", "dissolve");
                        returnMap.put("state", "400");// 房间解散
                        sendMessageTo(returnMap, userBean);
                        sendMessageToAll(returnMap, rb);
                        returnMap.clear();
                        returnMap.put("type", "big_settlement");
                        rb.getRoomBean_Custom("money-userid-nickname-winnum-win_money-avatarurl", returnMap, "");
                        sendMessageTo(returnMap, userBean);
                        sendMessageToAll(returnMap, rb);

                        rb.setRoom_state(0);
                        for (int i = rb.getGame_userList(0).size()-1; i >= 0; i--) {
                            rb.remove_options(rb.getGame_userList(0).get(i).getUserid());
                            rb.getGame_userList().remove(rb.getGame_userList().get(i));
                        }
                        rb.Initialization();
                        rb.setGame_number(0);
                        rb.setRoom_state_a(0);
                        if(rb.getClub_state()==-1){
                            Public_State_t.PKMap1.remove(String.valueOf(rb.getRoom_number()));
                        }
                        room_change(rb,0);
                    } else {
                        returnMap.put("type", "dissolve");
                        returnMap.put("state", "401");// 发起/同意解散
                        returnMap.put("dissolve_userid", dissolve_userid);
                        returnMap.put("userid", userBean.getUserid());
                        sendMessageTo(returnMap, userBean);
                        sendMessageToAll(returnMap, rb);
                        returnMap.clear();
                    }

                } else {
                    for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
                        rb.getGame_userList(0).get(i).setJiesan(0);
                    }
                    returnMap.put("userid", userBean.getUserid());
                    returnMap.put("type", "dissolve");
                    returnMap.put("state", "402");// 房间解散失败
                    sendMessageTo(returnMap, userBean);
                    sendMessageToAll(returnMap, rb);
                    returnMap.clear();
                }
            }
        }

        // 断线重连
        if ("reconnection".equals(jsonTo.get("type"))) {
            rb = Public_State_t.PKMap1.get(jsonTo.get("room_number"));
            if (rb != null) {
                T_UserBean user = rb.getUserBean(userBean.getUserid());
                user.setSession(userBean.getSession());
                userBean = user;
                user.setGametype(1);
                if (rb.getRoom_state_a() == 1 || rb.getRoom_state_a() == 2 || rb.getRoom_state_a() == 3) {
                    rb.getRoomBean_Custom("userid-nickname-avatarurl-money-brand-winnum-win_money-ready_state-upBrand" +
                                    "-belowBrand-middleBrand-upBrand_type-belowBrand_type-middleBrand_type",
                            returnMap,
                            "room_number-time-room_state_a-game_number-room_type-user_positions-max_number-foundation" +
                                    "-rule-baibian-room_branker");
                }
                returnMap.put("type", "reconnection");
                sendMessageTo(returnMap, userBean);
                returnMap.clear();
                returnMap.put("id", userBean.getUserid());
                returnMap.put("type", "reconnection_user");
                sendMessageToAll(returnMap, rb);
            }
        }

        // 退出房间
        if ("exit_room".equals(jsonTo.get("type"))) {
            if (rb.getGame_userList(0).size() == 0) {
                try {
                    session.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (rb != null && rb.getGame_userList(0).size() > 1) {
                rb.getRoom_Branker(rb, userBean);
                // returnMap.put("positions", rb.getUser_positions());
                returnMap.put("id", userBean.getUserid());
                returnMap.put("state", "1");// 房间还有别人的情况
                returnMap.put("type", "exitGame");
                returnMap.put("room_branker", rb.getRoom_branker());
                sendMessageTo(returnMap, userBean);
                sendMessageToAll(returnMap, rb);
                returnMap.clear();
                rb.Exit_Room(rb, userBean);
            } else {
                returnMap.put("id", userBean.getUserid());
                returnMap.put("state", "0");// 房间没人 直接解散
                returnMap.put("type", "exitGame");
                sendMessageTo(returnMap, userBean);
                sendMessageToAll(returnMap, rb);
                returnMap.clear();
                for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
                    Public_State_t.clients_t.remove(String.valueOf(rb.getGame_userList(0).get(i).getUserid()));
                }
                Public_State_t.PKMap1.remove(String.valueOf(rb.getRoom_number()));
                rb.setRoom_state(5);
                rb = null;
            }
        }

        // 正常退出或者强退
        if ("exit_room2".equals(jsonTo.get("type"))) {
            Integer exit_type = Integer.valueOf(jsonTo.get("exit_type"));
            // 退出
            Exit_Room(exit_type);
        }

        baseDao.CloseAll();
    }

    /**
     *@ Author:ZhaoQi
     *@ methodName:房间变化 及时推送
     *@ Params:type  0更新头像  1更新局数
     *@ Description:
     *@ Return:
     *@ Date:2020/3/30
     */
    private void room_change(T_RoomBean room,int type) {
        returnMap.clear();
        returnMap.put("type","room_change");
        if(type==0){
            room.getRoomBean_Custom2("avatarurl",returnMap,"room_number-Room_state_a-game_number-max_number");
        }else{
            room.getRoomBean_Custom2("",returnMap,"room_number-Room_state_a-game_number-max_number");
        }
        returnMap.put("change_type",type);
        for(Map.Entry<String, WebSocket> entry : Public_State_t.clients_t.entrySet()){
            WebSocket ws = entry.getValue();
            if(ws.userBean.getClub_number().equals(room.getClub_number()) && ws.userBean.getFloor()==room.getFloor() && room.getClub_state()==1){
                ws.sendMessageTo(returnMap,ws.userBean);
            }
        }
    }

    // 消息异常
    @OnError
    public void onError(Session session, Throwable error) throws IOException {
        if (error.getMessage() != null) {
            error.printStackTrace();
            System_Mess.system_Mess
                    .ToMessagePrint(userBean.getNickname() + "异常" + error.getLocalizedMessage() + "***********");
        }
    }

    /**
     * 退出房间 清除自己
     */

    public void Exit_Room(int type) {
        rb.getLock().lock();
        if (type == 1) {
            Public_State_t.PKMap1.remove(rb.getRoom_number());
            System_Mess.system_Mess.ToMessagePrint("房间清除");
            rb.setRoom_state(5);
            rb.getLock().unlock();
            rb = null;
        } else {
            userBean.setGametype(2);
            returnMap.put("id", userBean.getUserid());
            returnMap.put("state", "109");
            returnMap.put("type", "exitGame");
            sendMessageTo(returnMap, userBean);
            sendMessageToAll(returnMap, rb);
            rb.getLock().unlock();
        }
    }

    /**
     * 发送消息给除去自己以外的所有人
     *
     * @throws IOException
     */
    public synchronized void sendMessageToAll(Map<String, Object> returnMap, T_RoomBean rb) {
        for (T_UserBean user : rb.getGame_userList()) {
            WebSocket webSocket = Public_State_t.clients_t.get(user.getUserid() + "");
            if (webSocket != null && webSocket.session.isOpen() && user.getGametype() != 2) {
                // 不等于自己的则发送消息
                if (user.getUserid() != this.userBean.getUserid()) {
                    webSocket.sendMessageTo(returnMap, user);
                }
            }
        }
    }

    /**
     * 给自己返回消息
     *
     * @throws IOException
     *
     */
    public synchronized void sendMessageTo(Map<String, Object> returnMap, T_UserBean userBean) {
        if (session != null && session.isOpen()) {
            String returnjson = gson.toJson(returnMap).toString();
            try {
                session.getBasicRemote().sendText(returnjson);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System_Mess.system_Mess.ToMessagePrint(userBean.getUserid() + "(自己)返回消息" + returnjson);
        }
    }

}
