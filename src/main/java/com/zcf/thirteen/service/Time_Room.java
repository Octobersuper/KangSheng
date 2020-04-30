package com.zcf.thirteen.service;

import java.lang.reflect.Array;
import java.util.*;

import com.zcf.thirteen.bean.T_RoomBean;
import com.zcf.thirteen.bean.T_UserBean;
import com.zcf.thirteen.comm.Public_State_t;
import com.zcf.thirteen.comm.WebSocket;
import com.zcf.thirteen.dao.UserDao;
import com.zcf.thirteen.util.System_Mess;

public class Time_Room extends Thread {
    private GameService gs;
    private T_RoomBean rb;
    private int timer;
    private T_UserBean userBean;
    private UserDao userDao;
    private HashMap<String, Object> returnMap = new HashMap<String, Object>();

    public Time_Room(T_UserBean userBean, T_RoomBean rb, GameService gs, UserDao userDao) {
        this.userDao = userDao;
        this.userBean = userBean;
        this.rb = rb;
        this.timer = 60;
        this.gs = gs;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (rb == null) {
                break;
            }
            System_Mess.system_Mess.ToMessagePrint("倒计时----------->" + timer);
            rb.setTime(timer - 25);
            // 发牌 60秒发牌  25秒理牌完毕  10秒推送理牌 0秒结算
            if (timer == 60) {
                rb.GrantBrand(13, rb);
                rb.setRoom_state_a(1);
                rb.setRoom_state(1);
                returnMap.put("type", "fapai");
                returnMap.put("game_number", rb.getGame_number());
                returnMap.put("time", rb.getTime());
                rb.getRoomBean_Custom("userid-brand-user_brand_type", returnMap, "");
                WebSocket userid = Public_State_t.clients_t.get(String.valueOf(rb.getRoom_branker()));
                userid.sendMessageToAll(returnMap, rb);
                userid.sendMessageTo(returnMap, userBean);
                returnMap.clear();
            }

            if (timer == 22) {//走到这说明有人掉线 需要后台自动理牌
                WebSocket ws = null;
                for (T_UserBean user :
                        rb.getGame_userList()) {
                    if (user.getGametype() == 1 && user.getUpBrand_type() != null) {
                        ws = Public_State_t.clients_t.get(String.valueOf(user.getUserid()));
                    }
                }
                if (ws != null) {
                    for (T_UserBean user :
                            rb.getGame_userList()) {
                        if (user.getGametype() == 2 && user.getUpBrand_type() == null) {
                            returnMap.put("type", "auto_ward");
                            returnMap.put("brand", user.getBrand());
                            returnMap.put("userid",user.getUserid());
                            ws.sendMessageTo(returnMap, userBean);
                            returnMap.clear();
                        }
                    }
                }
            }

            // 推送三墩牌  4:全部立牌完成
            if (rb.getIsli() == 1 && timer>=10) {
                rb.setRoom_state(2);
                rb.setRoom_state_a(2);
                returnMap.put("type", "send_brand");
                rb.getRoomBean_Custom("userid-upBrand-belowBrand-middleBrand-upBrand_type-belowBrand_type" +
                        "-middleBrand_type", returnMap, "");
                // returnMap.put("list", list);
                WebSocket userid = Public_State_t.clients_t.get(String.valueOf(rb.getRoom_branker()));

                T_UserBean bean = rb.getUserBean(rb.getRoom_branker());
                userid.sendMessageTo(returnMap, bean);
                userid.sendMessageToAll(returnMap, rb);
                returnMap.clear();
                timer = 10;
            }

            //总结算
            if (timer == 0 && rb.getIsli() == 1) {
                rb.setRoom_state_a(3);
                returnMap.clear();
                WebSocket userid = Public_State_t.clients_t.get(String.valueOf(rb.getRoom_branker()));
                //执行结算
                gs.EndGame(rb);
                // 记录战绩
                gs.addRecord(rb,1,rb.getClub_state()==-1?0:1);
                if (rb.getGame_number() == rb.getMax_number()) {
                    returnMap.put("type", "big_settlement");
                    rb.getRoomBean_Custom("money-userid-nickname-winnum-win_money-avatarurl", returnMap, "");
                } else {
                    returnMap.put("type", "small_settlement");
                    rb.getRoomBean_Custom("money-userid-nickname-winnum-win_money-avatarurl", returnMap, "");
                }
                userid.sendMessageTo(returnMap, userBean);
                userid.sendMessageToAll(returnMap, rb);
            }


            // 时间线程结束则更改房间状态并且开始下一局
            if (timer == 0) {
                if (rb.getGame_number() < rb.getMax_number()) {
                    rb.Initialization();
                    System_Mess.system_Mess.ToMessagePrint("开始游戏跳出线程");
                    break;
                }
            } else {
                if (timer == -3) {
                    WebSocket userid = Public_State_t.clients_t.get(String.valueOf(rb.getRoom_branker()));
                    userid.Exit_Room(1);
                    break;
                }
            }
            timer--;
            rb.setTime(rb.getTime() - 1);
        }
        System_Mess.system_Mess.ToMessagePrint("开始游戏线程结束");
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:自动理牌
     * @ Params:
     * @ Description:
     * @ Return:
     * @ Date:2020/3/23
     */
    private void auto_warid(T_UserBean user) {
        HashMap<String, Integer[]> map = new HashMap<>();
        warid_card(map, user);

        for (int i = 1; i < 4; i++) {
            if (map.get("8") != null) {
                switch (i) {
                    case 1:
                        user.setBelowBrand(map.get("8"));
                        user.setBelowBrand_type("8");
                    case 2:
                        break;
                    case 3:
                        break;
                }
                map.put("10", null);
            }
        }
    }

    private void warid_card(HashMap<String, Integer[]> map, T_UserBean user) {
        Integer[] cards = Arrays.copyOf(user.getBrand(), user.getBrand().length);
        Integer[] wt = wt(cards);//五同8
        if (wt != null) {
            map.put("8", wt);
            cards = remove_cards(wt, cards);
            //中道五同
            Integer[] wt2 = wt(cards);
            if (wt2 != null) {
                map.put("8_2", wt2);
                cards = remove_cards(wt2, cards);
            }
        }

        Integer[] ths = ths(cards);//同花顺7
        if (ths != null) {
            map.put("7", ths);
            cards = remove_cards(ths, cards);
        }
        Integer[] tz = tz(cards);//铁支6
        Integer[] hl = hl(cards);//葫芦5
        Integer[] th = th(cards);//同花4
        Integer[] ss = ss(cards);//顺子3
        Integer[] st = st(cards);//三条2
        Integer[] ld = ld(cards);//两对1
        Integer[] dz = dz(cards);//对子0
        Integer[] sp = sp(cards);//散牌 -2
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:散牌
     * @ Date:2020/3/23
     */
    private Integer[] sp(Integer[] cards) {
        return new Integer[0];
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:对子
     * @ Date:2020/3/23
     */
    private Integer[] dz(Integer[] cards) {
        return new Integer[0];
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:两对
     * @ Date:2020/3/23
     */
    private Integer[] ld(Integer[] cards) {
        return new Integer[0];
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:三条
     * @ Date:2020/3/23
     */
    private Integer[] st(Integer[] cards) {
        return new Integer[0];
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:顺子
     * @ Date:2020/3/23
     */
    private Integer[] ss(Integer[] cards) {
        return new Integer[0];
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:同花
     * @ Date:2020/3/23
     */
    private Integer[] th(Integer[] cards) {
        return new Integer[0];
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:葫芦
     * @ Date:2020/3/23
     */
    private Integer[] hl(Integer[] cards) {
        return new Integer[0];
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:铁支
     * @ Date:2020/3/23
     */
    private Integer[] tz(Integer[] cards) {
        return new Integer[0];
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:同花顺
     * @ Date:2020/3/23
     */
    private Integer[] ths(Integer[] cards) {
        return null;
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:五同  AAAAA
     * @ Params:
     * @ Description:
     * @ Return:
     * @ Date:2020/3/23
     */
    private Integer[] wt(Integer[] cards) {
        Integer[] wt = new Integer[5];
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < cards.length; i++) {
            int count = 0;
            for (int j = 0; j < cards.length; j++) {
                if(!list.contains((cards[i]%13)+1)){
                    if((cards[i]%13)+1==(cards[j]%13)+1){
                        count++;
                    }
                }
            }
            if(count==5){
                for (int j = 0; j < wt.length; j++) {
                    wt[j] = cards[i];
                }
                list.add((cards[i]%13)+1);
            }
        }
        if (list.size()==0) {
            return null;
        }
        return wt;
    }

    /**
     * @ Author:ZhaoQi
     * @ methodName:删除已搭配的牌型 返回剩下的牌值
     * @ Date:2020/3/23
     */
    private Integer[] remove_cards(Integer[] m, Integer[] n) {
        // 将较长的数组转换为set
        Set<Integer> set = new HashSet<Integer>(Arrays.asList(m.length > n.length ? m : n));
        // 遍历较短的数组，实现最少循环
        for (Integer i : m.length > n.length ? n : m) {// 如果集合里有相同的就删掉，如果没有就将值添加到集合
            if (set.contains(i)) {
                set.remove(i);
            } else {
                set.add(i);
            }
        }
        Integer[] arr = {};
        return set.toArray(arr);
    }

    public static void main(String[] args) {
        Integer[] m = {1,2,3,4,5};
        Integer[] n = {1,2,3,4,5,6,7,8,9,10,5,5,5,5};

        //Integer[] wt = wt(n);
        //Integer[] integers = remove_cards(m, n);
        //System.out.println(Arrays.toString(wt));
    }
}
