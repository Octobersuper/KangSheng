/**
 *
 */
package com.zcf.thirteen.service;

import java.util.*;

import com.zcf.mahjong.bean.RoomBean;
import com.zcf.mahjong.mahjong.Public_State;
import com.zcf.thirteen.bean.T_RoomBean;
import com.zcf.thirteen.bean.T_UserBean;
import com.zcf.thirteen.comm.CreatRoom;
import com.zcf.thirteen.comm.MatchingRoom;
import com.zcf.thirteen.comm.Public_State_t;
import com.zcf.thirteen.dao.GameDao;
import com.zcf.thirteen.dao.UserDao;
import com.zcf.thirteen.util.BaseDao;

/**
 * @author guolele
 * @date 2019年2月20日 下午3:08:07
 *
 */
@SuppressWarnings("unused")
public class GameService extends Thread {
    private GameDao gd;
    private T_RoomBean rb;
    private T_UserBean userBean;
    private UserDao userDao;

    public GameService(BaseDao baseDao) {
        this.userDao = new UserDao(baseDao);
        this.gd = new GameDao(baseDao);
        this.rb = new T_RoomBean();
        this.userBean = new T_UserBean();
    }

    /**
     * 创建房间
     *
     * @param map
     * @param userBean
     * @return @throws
     */
    public T_RoomBean Esablish(Map<String, String> map, T_UserBean userBean) {
        // 创建房间
        T_RoomBean rb = CreatRoom.EcoSocket();
        // 最大回合数
        rb.setMax_number(Integer.parseInt(map.get("max_number")));
        //rb.setMax_number(3);
        // 最大参与人数
        rb.setFoundation(Integer.parseInt(map.get("foundation")));
        // 初始化游戏房间信息
        rb.Initialization();
        // 规则
        rb.setRule(Integer.parseInt(map.get("rule"))==0?-2:Integer.parseInt(map.get("rule")));
        // 马牌
        rb.setBaibian(Integer.parseInt(map.get("baibian")));
        // 游戏类型 0房主付费 1AA制
        rb.setRoom_type(Integer.parseInt(map.get("room_type")));

        rb.setUser_positions(new int[rb.getFoundation()]);
        // 往座位添加用户
        for (int i = 0; i < rb.getUser_positions().length; i++) {
            rb.getUser_positions()[i] = -1;
        }
        if(userBean!=null){
            // 加入自己
            rb.getGame_userList(0).add(userBean);
            userBean.setGametype(1);
            // 经纬度
            userBean.setLog_lat(map.get("log_lat"));
        }
        return rb;
    }

    /**
     * 匹配（如果匹配不到则创建一个房间返回） @param userBean @param jsonTo @return @throws
     */
    public T_RoomBean Matching3(T_UserBean userBean, int di_fen, int fen, int room_type, String brand_type) {

        int while_count = 5;
        // 所有房间最多匹配五次
        while (true) {
            if (while_count == 0) {
                break;
            }
            // 遍历所有房间查看是否有符合的房间
            for (String key : Public_State_t.PKMap2.keySet()) {
                rb = Public_State_t.PKMap2.get(key);
                // 初次验证房间人数未满
                if (rb.getGame_userList(0).size() < 11) {
                    // 检测房间内没有自己
                    boolean userbool = false;
                    for (T_UserBean user : rb.getGame_userList(0)) {
                        if (user.getUserid() == userBean.getUserid()) {
                            userbool = true;
                            break;
                        }
                    }
                    if (userbool) {
                        continue;
                    }
                    // 将自己加入到房间中
                    rb = MatchingRoom.Matching3(userBean, key);
                    // 代表没加入进去
                    if (rb == null) {
                        // 跳过此次房间继续匹配
                        continue;
                    } else {
                        // 返回加入的房间信息
                        return rb;
                    }
                }
            }
            while_count--;
            // 延迟500毫秒继续下一次匹配
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return CreatRoom.EcoSocket3(userBean, di_fen, fen, room_type, brand_type, this);
    }


    /**
     * 加入房间
     *
     * @throws InterruptedException
     */
    public T_RoomBean Matching(Map<String, String> map, T_UserBean userBean) throws InterruptedException {
        return MatchingRoom.Matching(userBean, String.valueOf(String.valueOf(map.get("room_number"))));
    }


    /**
     * 修改房间信息
     *
     * @param map
     * @param userBean
     * 			@param rb @return @throws
     */
    public T_RoomBean UpdateRoom(Map<String, String> map, T_UserBean userBean, T_RoomBean rb) {
        rb.setFoundation(Integer.parseInt(map.get("foundation")));
        rb.setRoom_type(Integer.valueOf(map.get("room_type")));
        return rb;
    }

    /**
     * 下注 @param money @param userBean @param rb @return @throws
     */
    public int bets(int money, T_UserBean userBean, T_RoomBean rb) {
        rb.getLock().lock();
        // 检测玩家金币
        if (userBean.getMoney() < money) {
            System.out.println("money:::::::::::::::::::::::" + userBean.getMoney() + "------>money:" + money);
            rb.getLock().unlock();
            return 809;
        }
        // 扣除下注
        gd.UpdateUserMoney(userBean.getUserid(), money, 0);
        userBean.setMoney(userBean.getMoney() - money);
        // 操作状态
        rb.getLock().unlock();
        return 0;
    }

    /**
     *  观战用户集合
     * @throws
     */
    public List<T_UserBean> getGuanZhan(T_RoomBean rb) {
        ArrayList<T_UserBean> list = new ArrayList<T_UserBean>();
        for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
            T_UserBean bean = rb.getUserBean(rb.getGame_userList(0).get(i).getUserid());
        }
        return list;
    }


    /**
     * 结算 @param id @param rb @return @throws
     */
    public void EndGame(T_RoomBean rb) {
        //通比
        for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
            T_UserBean bean_i = rb.getUserBean(rb.getGame_userList(0).get(i).getUserid());
            for (int j = i + 1; j < rb.getGame_userList(0).size(); j++) {
                T_UserBean bean_j = rb.getUserBean(rb.getGame_userList(0).get(j).getUserid());
                System.err.println(bean_i.getNickname() + "--------PK----------" + bean_j.getNickname());
                //比较头中尾三道 全赢叫打枪翻倍  记录每个玩家赢的次数 如果赢了所有玩家叫全垒打 翻倍
                pkUser(bean_i, bean_j, rb);
            }
            if (rb.getGame_userList().size() > 3 && bean_i.getWin_number() == rb.getGame_userList().size() - 1) {//如果全垒打
                System.err.println(bean_i.getNickname()+"-----------------------全垒打----------------------");
                for (T_UserBean bean :
                        rb.getGame_userList()) {
                    if (bean.getUserid() != bean_i.getUserid()) {
                        System.err.println(bean.getNickname()+"-----------------------全垒打扣除----------------------分数："+bean.getQiang_fen().get(bean_i.getUserid()));
                        bean.setWinnum(bean.getWinnum() - bean.getQiang_fen().get(bean_i.getUserid()));//全垒打分数翻倍
                        bean.setMoney(bean.getMoney()-bean.getQiang_fen().get(bean_i.getUserid()));
                        bean_i.setWinnum(bean_i.getWinnum() + bean.getQiang_fen().get(bean_i.getUserid()));
                        bean_i.setMoney(bean_i.getMoney() + bean.getQiang_fen().get(bean_i.getUserid()));
                    }
                }
            }
            /*int i1 = userDao.updateMoney(bean_i.getWinnum(), bean_i.getUserid());
            if (i1 == 0) {
                bean_i.setMoney(bean_i.getMoney() + bean_i.getWinnum());
            }*/
            bean_i.setMoney(bean_i.getMoney() + bean_i.getWinnum());
            //if win_number=玩家人数 基础分*（2（打枪）*玩家人数）*2（全垒打）*2（马牌）
            //if win_number<玩家人数&&>0 基础分*（2（打枪）*win_number）*2（马牌）
        }
    }

    /**
     * 比牌 @param branker_brand @param user_brand @throws
     */
    public void pkUser(T_UserBean bean_i, T_UserBean bean_j, T_RoomBean rb) {
        //比较头道 012
        int top = top_card_type(bean_i, bean_j);
        //比较中道
        int center = center_card_type(bean_i, bean_j);
        //比较尾道
        int bottom = bottom_card_type(bean_i, bean_j);
        //是否打枪
        int isqiang = 0;
        //打枪记录
        if (top == 1 && center == 1 && bottom == 1) {
            isqiang = 1;
            bean_i.setWin_number(bean_i.getWin_number() + 1);
        } else if (top == 2 && center == 2 && bottom == 2) {
            isqiang = 1;
            bean_j.setWin_number(bean_j.getWin_number() + 1);
        }
        System.out.println(bean_i.getNickname() + "PK" + bean_j.getNickname() + "结果-----" + "头道:" + top + "中道:" + center + "尾道:" + bottom + "是否打枪:" + isqiang);
        if(bean_i.getUpBrand_type().equals("9") || bean_i.getUpBrand_type().equals("10")){
            switch (top) {
                case 0://平局
                    break;
                case 1:
                    bean_i.addWin_number(1, isqiang, rb, bean_j);
                    break;
                case 2:
                    bean_j.addWin_number(1, isqiang, rb, bean_i);
                    break;
            }
        }else{
            switch (top) {
                case 0://平局
                    break;
                case 1:
                    bean_i.addWin_number(1, isqiang, rb, bean_j);
                    break;
                case 2:
                    bean_j.addWin_number(1, isqiang, rb, bean_i);
                    break;
            }
            switch (center) {
                case 0://平局
                    break;
                case 1:
                    bean_i.addWin_number(2, isqiang, rb, bean_j);
                    break;
                case 2:
                    bean_j.addWin_number(2, isqiang, rb, bean_i);
                    break;
            }
            switch (bottom) {
                case 0://平局
                    break;
                case 1:
                    bean_i.addWin_number(3, isqiang, rb, bean_j);
                    break;
                case 2:
                    bean_j.addWin_number(3, isqiang, rb, bean_i);
                    break;
            }
        }
    }

    /**
     *@ Author:ZhaoQi
     *@ methodName:头道牌型返回 1beani赢 2beanj赢 0和局
     *@ Params:
     *@ Description:
     *@ Return:
     *@ Date:2020/3/17
     */
    private int top_card_type(T_UserBean bean_i, T_UserBean bean_j) {
        int i;
        if (bean_i.getUpBrand_type().equals(bean_j.getUpBrand_type())) {
            //牌型相同比较牌值和花色
            i = pk_hua(bean_i.getUpBrand(), bean_j.getUpBrand(), Integer.valueOf(bean_j.getUpBrand_type()));
        } else {
            if (Integer.valueOf(bean_i.getUpBrand_type()) > Integer.valueOf(bean_j.getUpBrand_type())) {
                i = 1;
            } else {
                i = 2;
            }
        }
        return i;
    }

    private int pk_hua(Integer[] upBrand, Integer[] upBrand1, int type) {
        if (type == -2 || type == 3 || type == 4 || type == 7 || type == 9 || type == 10) {//-2 3 4 7 9 10   散牌比较
            Integer[] arr1 = sort(upBrand, type);
            Integer[] arr2 = sort(upBrand1, type);
            for (int i = arr1.length - 1; i >= 0; i--) {
                System.out.println("-------------牌值比较：" + arr1[i] + "   " + arr2[i]);
                if (arr1[i] > arr2[i]) {
                    return 1;
                } else if (arr1[i] < arr2[i]) {
                    return 2;
                } else {
                    continue;
                }
            }
            //比较花色
            for (int i = upBrand.length - 1; i >= 0; i--) {
                System.out.println("-------------花色比较：" + upBrand[i] / 13 + "   " + upBrand1[i] / 13);
                if (upBrand[i] / 13 > upBrand1[i] / 13) {
                    return 1;
                } else if (upBrand[i] / 13 < upBrand1[i] / 13) {
                    return 2;
                } else {
                    continue;
                }
            }
        } else {//带对子 或者 三条 五同 铁支的比牌
            Integer[] check = check(upBrand);//下表值  比花色
            Integer[] check1 = check(upBrand1);//下表值  比花色

            Integer[] sort = sort(check, type);//真实牌值  牌值比较时用到
            Integer[] sort2 = sort(check1, type);

            for (int i = sort.length - 1; i >= 0; i--) {
                System.out.println("-------------对子牌值比较：" + sort[i] + "    " + sort2[i]);
                if (sort[i] > sort2[i]) {
                    return 1;
                } else if (sort[i] < sort2[i]) {
                    return 2;
                } else {
                    continue;
                }
            }
            //比较花色
            for (int i = check.length - 1; i >= 0; i--) {
                System.out.println("-------------对子花色比较：" + check[i] / 13 + "   " + check1[i] / 13);
                if (check[i] / 13 > check1[i] / 13) {
                    return 1;
                } else if (check[i] / 13 < check1[i] / 13) {
                    return 2;
                } else {
                    continue;
                }
            }
        }
        return 0;
    }

    /**
     *@ Author:ZhaoQi
     *@ methodName:去除单张 只留重复的
     *@ Params:
     *@ Description:
     *@ Return:
     *@ Date:2020/3/18
     */
    private Integer[] check(Integer[] a) {
        for (int j = 0; j < a.length; j++) {
            int count = 0;
            for (int k = 0; k < a.length; k++) {
                int b = (a[j] % 13) + 1;
                int c = (a[k] % 13) + 1;
                if (b == c) {
                    count ++;
                }
            }
            if(count<2){
                a[j] = 0;
            }
        }
        Arrays.sort(a);
        return a;
    }

    /**
     *@ Author:ZhaoQi
     *@ methodName:转换成真是牌值并排序    type 为葫芦牌型时  只留三张 用作比较大小
     *@ Params:
     *@ Description:
     *@ Return:
     *@ Date:2020/3/17
     */
    public Integer[] sort(Integer[] upBrand, int type) {
        Integer[] arr = new Integer[upBrand.length];
        for (int i = 0; i < upBrand.length; i++) {
            arr[i] = (upBrand[i] % 13) + 1;
            if (arr[i] == 1) {
                arr[i] = 999;
            }
        }
        Arrays.sort(arr);
        if (type == 5) {
            int co = 1;
            for (int i = 0; i < arr.length - 1; i++) {
                if (arr[i].equals(arr[i + 1])) {
                    co++;
                } else {
                    if (co == 2) {
                        arr[i] = 0;
                        arr[i - 1] = 0;
                        break;
                    }
                    if (co == 3) {
                        arr[i + 1] = 0;
                        arr[i + 2] = 0;
                        break;
                    }
                }
            }
            Arrays.sort(arr);
            System.out.println(
                    "---------------------------------------------------------------------------------------------" + Arrays.toString(arr));
        }
        return arr;
    }

    private int top_type(int[] bean_i) {
        return 0;
    }

    /**
     *@ Author:ZhaoQi
     *@ methodName:中道牌型
     *@ Params:
     *@ Description:
     *@ Return:
     *@ Date:2020/3/17
     */
    private int center_card_type(T_UserBean bean_i, T_UserBean bean_j) {
        int i;
        if (bean_i.getMiddleBrand_type().equals(bean_j.getMiddleBrand_type())) {
            //牌型相同比较牌值和花色
            i = pk_hua(bean_i.getMiddleBrand(), bean_j.getMiddleBrand(), Integer.valueOf(bean_j.getMiddleBrand_type()));
        } else {
            if (Integer.valueOf(bean_i.getMiddleBrand_type()) > Integer.valueOf(bean_j.getMiddleBrand_type())) {
                i = 1;
            } else {
                i = 2;
            }
        }
        return i;
    }

    /**
     *@ Author:ZhaoQi
     *@ methodName:尾道牌型
     *@ Params:
     *@ Description:
     *@ Return:
     *@ Date:2020/3/17
     */
    private int bottom_card_type(T_UserBean bean_i, T_UserBean bean_j) {
        int i;
        if (bean_i.getBelowBrand_type().equals(bean_j.getBelowBrand_type())) {
            //牌型相同比较牌值和花色
            i = pk_hua(bean_i.getBelowBrand(), bean_j.getBelowBrand(), Integer.valueOf(bean_j.getBelowBrand_type()));
        } else {
            if (Integer.valueOf(bean_i.getBelowBrand_type()) > Integer.valueOf(bean_j.getBelowBrand_type())) {
                i = 1;
            } else {
                i = 2;
            }
        }
        return i;
    }

    /**
     * 開始游戲
     *
     * @param roomBean @throws
     */
    public void Game_Start(T_RoomBean roomBean, T_UserBean userBean) {
        roomBean.getLock().lock();
        if (roomBean.getGame_userList().size() >= 2) {
            roomBean.setRoom_state(2);
            roomBean.setTimer_user(61);
        }
        roomBean.getLock().unlock();

    }

    /**
     * 開始游戲
     *
     * @param  @throws
     */
    public void Game_Start2(T_RoomBean roomBean2, T_UserBean userBean) {
        roomBean2.getLock().lock();
        if (roomBean2.getGame_userList().size() >= 2) {
            roomBean2.setRoom_state(2);
            // roomBean2.setGame_number(roomBean2.getGame_number()+1);//回合数加1
        }
        roomBean2.getLock().unlock();

    }

    /**
     * 开牌比点 @param rb @return @throws
     */
    private int OpenBrand(T_RoomBean rb) {
        // 查询所有的
        List<T_UserBean> game_userList = rb.getGame_userList(1);
        rb.getGame_userList();
        return 0;
    }

    /**
     * 检测相同 @param brands @return @throws
     */
    private int isBrands(Object[] brands) {
        int brandcount = 0;
        // 判断是否有相同牌型
        for (int i = 0; i < brands.length; i++) {
            int[] brand_user = (int[]) brands[i];
            if ((i + 1) < brands.length) {
                int[] brand_user2 = (int[]) brands[i + 1];
                if (brand_user[1] == brand_user2[1]) {
                    brandcount++;
                } else {
                    break;
                }
            }
        }
        return brandcount;
    }

    /**
     * 查询倍率
     *
     * @param rb
     * @param
     * @param
     * @param
     * @return @throws
     */
    public int odds(int brand_type, T_RoomBean rb) {
        if (brand_type == 2) {
            return 3;
        }
        return 1;

    }

    /**
     * 坐下座位 @param userBean2 @param rb2 @throws
     */
    public int Sit_down(T_UserBean userBean, T_RoomBean rb) {
        for (int i = 0; i < rb.getUser_positions().length; i++) {
            if (rb.getUser_positions()[i] == -1) {
                // 如果座位未满 则按顺序添加用户到座位
                rb.getUser_positions()[i] = userBean.getUserid();
                if (i == 0) {
                    // 第一个坐下的人拥有开始游戏的权限
                    rb.setRoom_branker(userBean.getUserid());
                }
                userBean.setGametype(1);
                userBean.setStart_money(userBean.getMoney());
                return 0;
            }
        }
        return -1; // 房间已满 坐下失败
    }

    /**
     * 检测准备人数 @param userBean2 @param rb2 @throws
     */
    public boolean check_positions(T_RoomBean rb) {
        int count = 0;
        for (T_UserBean userBean : rb.getGame_userList()) {
            if (userBean.getReady_state() == 1) {
                count++;
            }
        }
        if (count == rb.getGame_userList().size() && count >= 2 && count == rb.getFoundation()) {
            return true;
        }
        return false;
    }


    public void addRecord(T_RoomBean roomBean,int type,int room_type) {
        // 记录战绩
        userDao.addPK_Record(roomBean,type,room_type);
    }

    /**
     * 结算 @param rb2 @param bean1 @param bean2 @throws
     */
    public void EndGame2(T_RoomBean rb2, T_UserBean bean1, T_UserBean bean2) {
        bean1.setMoney(bean1.getMoney() - rb2.getDi_fen());
        bean1.setWinnum(bean1.getWinnum() - rb2.getDi_fen());
        gd.UpdateUserMoney(bean1.getUserid(), rb.getDi_fen(), 0);

        bean2.setMoney(bean2.getMoney() + rb2.getDi_fen());
        bean2.setWinnum(bean2.getWinnum() + rb2.getDi_fen());
        gd.UpdateUserMoney(bean2.getUserid(), rb2.getDi_fen(), 1);
    }

    /**
     *@ Author:ZhaoQi
     *@ methodName:是否自动理牌了
     *@ Params:
     *@ Description:
     *@ Return:
     *@ Date:2020/3/23
     */
    public boolean getUserstate(T_RoomBean rb) {
        int count = 0;
        for (T_UserBean userbean :
                rb.getGame_userList()) {
            if (userbean.getUpBrand_type() != null) {
                count++;
            }
        }
        if (count == rb.getGame_userList().size()) {
            return true;
        }
        return false;
    }

    public int getclubrommnumber(String club_number,int game_type) {
        int count = 0;
        if(game_type==0){
            for(Map.Entry<String, T_RoomBean> entry : Public_State_t.PKMap1.entrySet()){
                T_RoomBean rb = entry.getValue();
                if(rb.getClub_number().equals(club_number)){
                    count++;
                }
            }
        }else{
            for(Map.Entry<String, RoomBean> entry : Public_State.PKMap.entrySet()){
                RoomBean rb = entry.getValue();
                if(rb.getClub_number().equals(club_number)){
                    count++;
                }
            }
        }
        return count;
    }
}
