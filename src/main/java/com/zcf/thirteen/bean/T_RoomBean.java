/**
 *
 */
package com.zcf.thirteen.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.zcf.thirteen.comm.Public_State_t;
import com.zcf.thirteen.dao.GameDao;
import com.zcf.thirteen.dao.UserDao;
import com.zcf.thirteen.service.Time_Room;

/**
 * @author guolele
 * @date 2019年2月20日 上午9:20:50 房间类
 */
@SuppressWarnings("unused")
public class T_RoomBean {
    // 房间类型 0抢庄模式 1通比模式
    private int room_type;
    // 房间号
    private String room_number;
    // 参与游戏的用户集合
    private List<T_UserBean> game_userList;
    // 发牌集合
    private List<T_UserBean> rb_List;
    // 当前庄家的id
    private int branker_id;
    // 房主id 具备开始游戏的权限
    private int room_branker = 0;
    // 参与分数
    private int fen;
    // 最大人数
    private int foundation;
    // 最大回合数
    private int max_number;
    // 底分
    private int di_fen;
    // 房间状态 0准备  1理牌  2亮牌  3结算
    private int room_state = 0;
    // 1下注阶段 2搓牌阶段 3结算阶段
    private int room_state_a;
    // 当前游戏局数
    private int game_number;
    // 房间锁
    private Lock lock;
    // 房间当前注数
    private int bets = 0;
    // 游戏可加注数
    private int[] zbets;
    // 用户位置 用户位置0-7下标代表1-8的座位，值代表用户id
    private int[] user_positions = new int[]{-1, -1, -1, -1, -1, -1, -1, -1};
    // 房间局的牌
    private String brands;
    // 房间内用户每回合牌型的集合
    private String[] room_brandsList;
    // 每回合游戏结束的时间
    private Date game_time;

    // 房间计时器
    private Time_Room time_Room;
    // 用户计时器
    private int timer_user;
    // 用户信息
    private T_UserBean userBean;
    // 胜利得人
    private int victoryid;
    // 规则
    private int rule;
    //百变
    private int baibian;
    //是否全部理牌
    private int isli;
    //倒计时
    private int time;
    //俱乐部号
    private String club_number = "-1";
    //俱乐部状态
    private int club_state = -1;
    //房间所在楼层
    private int floor;

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getClub_number() {
        return club_number;
    }

    public void setClub_number(String club_number) {
        this.club_number = club_number;
    }

    public int getClub_state() {
        return club_state;
    }

    public void setClub_state(int club_state) {
        this.club_state = club_state;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    // 游戏状态 0 默认未开始 1下注阶段 2开牌阶段 3结算阶段
    private int game_state;

    public T_RoomBean() {
        // 房间锁
        this.lock = new ReentrantLock(true);
        // 玩家集合
        this.game_userList = new ArrayList<T_UserBean>();
        this.rb_List = new ArrayList<T_UserBean>();
        this.game_number = 0;
        this.room_branker = 0;
        // 初始化房间未开始
        this.room_state = 0;
        // 初始化庄家id
        this.branker_id = -1;
        this.fen = 0;
        // 游戏状态
        this.game_state = 0;
    }

    public void Room_Initialization() {
        for (int i = 0; i < rb_List.size(); i++) {
            rb_List.get(i).setBrand(new Integer[]{-1, -1});
        }
    }

    /**
     * 获取下一个庄家的Id
     *
     * @return
     */
    public int getNextBrankerId() {
        int userid = -1;
        int thisindex = -1;
        for (int i = 0; i < user_positions.length; i++) {
            if (user_positions[i] != -1 && getUserBean(user_positions[i]).getGametype() == -1) {
                continue;
            }
            if (user_positions[i] == branker_id) {
                thisindex = i;
            }
        }
        for (int i = 0; i < 8; i++) {
            if ((thisindex + 1) == user_positions.length) {
                thisindex = 0;
            } else {
                thisindex++;
            }
            if (user_positions[thisindex] != -1 && getUserBean(user_positions[thisindex]).getGametype() == -1) {
                continue;
            }
            if (user_positions[thisindex] != -1) {
                userid = user_positions[thisindex];
                break;
            }
        }
        // 为该用户创建一个倒计时线程
        // this.time_Room.setUser(getUserBean(userid));
        branker_id = userid;
        return userid;
    }

    // 找下一个房间拥有者
    public void getRoom_Branker(T_RoomBean rb, T_UserBean userBean) {
        for (int i = 0; i < rb.getUser_positions().length; i++) {
            if (rb.getUser_positions()[i] == userBean.getUserid()) {
                rb.getUser_positions()[i] = -1;
            }
            if (i == 0 && rb.getUser_positions()[i] == -1) {
                for (int j = 0; j < rb.getUser_positions().length; j++) {
                    if ((j + 1) < rb.getUser_positions().length) {
                        rb.getUser_positions()[j] = rb.getUser_positions()[j + 1];
                    }
                }
            }
        }

        for (int i = 0; i < rb.getUser_positions().length; i++) {
            if (rb.getUser_positions()[i] != -1 && rb.getUser_positions()[i] != rb.getRoom_branker()
                    && rb.getRoom_branker() == userBean.getUserid()) {
                rb.setRoom_branker(rb.getUser_positions()[i]);
                break;
            }
        }

    }


    /**
     * getrooms
     *
     * @param
     * @param
     * @param
     */
    public List<Map<String, Object>> getrooms(List<T_RoomBean> list,String table, String usertable) {
        List<Map<String, Object>> map = new ArrayList<>();
        if(list.size()!=0){
            for (T_RoomBean room:list) {
                Map<String, Object> returnMap = new HashMap<>();
                room.getRoomBean_Custom(table, returnMap);
                returnMap.put("userList", room.getuser_custom(usertable));
                map.add(returnMap);
            }
            return map;
        }else
            return map;
    }

    /**
     * 退出房间
     *
     * @return @throws
     */

    public void Exit_Room(T_RoomBean rb, T_UserBean userBean) {
        for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
            if (rb.getGame_userList(0).get(i).getUserid() == userBean.getUserid()) {
                rb.getGame_userList(0).remove(i);
            }
        }
        for (int i = 0; i < rb.getUser_positions().length; i++) {
            if (rb.getUser_positions()[i] == userBean.getUserid()) {
                rb.getUser_positions()[i] = -1;
            }
        }
        Public_State_t.clients_t.remove(String.valueOf(userBean.getUserid()));
    }

    /**
     * 返回某一个用户 @param i @return @throws
     */
    public T_UserBean getUserBean(int userid) {
        for (T_UserBean userBean : getGame_userList(0)) {
            if (userBean.getUserid() == userid) {
                return userBean;
            }
        }
        return null;
    }

    public T_UserBean getUserBean1(int userid) {
        for (T_UserBean userBean : rb_List) {
            if (userBean.getUserid() == userid) {
                return userBean;
            }
        }
        return null;
    }

    public int getIsli() {
        return isli;
    }

    public void setIsli(int isli) {
        this.isli = isli;
    }

    /**
     * 初始化房间
     */
    public void Initialization() {
        setIsli(0);
        // 初始化扑克牌
        setBrands(1);
        // 初始化游戏未开始
        setRoom_state(0);
        if (game_userList != null) {
            // 初始化用户
            for (T_UserBean userBean : game_userList) {
                userBean.Initialization();
            }
        }
    }

    /**
     * 获取自定义的房间信息和自定义的用户信息
     *
     * @param table
     * @param returnMap
     * 			@param usertable @throws
     */
    public void getRoomBean_Custom(String usertable, Map<String, Object> returnMap, String table) {
        getRoomBean_Custom(table, returnMap);
        returnMap.put("userlist", getGame_userList(usertable));
    }

    /**
     * 获取自定义的房间信息和自定义的用户信息
     *
     * @param table
     * @param returnMap
     * 			@param usertable @throws
     */
    public void getRoomBean_Custom2(String usertable, Map<String, Object> returnMap, String table) {
        getRoomBean_Custom(table, returnMap);
        returnMap.put("userlist", getuser_custom(usertable));
    }

    /**
     * 自定义获取观战用户列表
     *
     * @param usertable
     * @param returnMap
     * @param list
     * @throws
     */
	/*public void getGuanZhan_Custom(String usertable, Map<String, Object> returnMap ,List<UserBean> list) {
		returnMap.put("game_userlist", getGuanZhan_userList(usertable ,list));
	}*/
    /**
     * 自定义获取观战用户列
     */
/*	public List<Map<String, Object>> getGuanZhan_userList(String usertable ,List<UserBean> list) {
		List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			list1.add(map);
		}
		return list1;
	}*/

    /**
     * 自定义获取用户列
     */
    public List<Map<String, Object>> getGame_userList(String usertable) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < getGame_userList(0).size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            getGame_userList(0).get(i).getUser_Custom(usertable, map);
            list.add(map);
        }
        return list;
    }

    /**
     * 自定义获取用户列
     */
    public List<String> getuser_custom(String usertable) {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < getGame_userList(0).size(); i++) {
            list.add(getGame_userList(0).get(i).getAvatarurl());
        }
        return list;
    }

    /**
     * 自定义获取房间列
     *
     */
    public void getRoomBean_Custom(String usertable, Map<String, Object> returnMap) {
        String[] rooms = usertable.split("-");
        for (String room : rooms) {
            if (room.equals("room_type"))
                returnMap.put(room, room_type);
            if (room.equals("room_number"))
                returnMap.put(room, room_number);
            if (room.equals("game_number"))
                returnMap.put(room, game_number);
            if (room.equals("branker_id"))
                returnMap.put(room, branker_id);
            if (room.equals("room_branker"))
                returnMap.put(room, room_branker);
            if (room.equals("user_positions"))
                returnMap.put(room, user_positions);
            if (room.equals("brands"))
                returnMap.put(room, brands);
            if (room.equals("room_brandsList"))
                returnMap.put(room, room_brandsList);
            if (room.equals("max_number"))
                returnMap.put(room, max_number);
            if (room.equals("timer_user"))
                returnMap.put(room, timer_user);
            if (room.equals("rule"))
                returnMap.put(room, rule);
            if (room.equals("game_state"))
                returnMap.put(room, game_state);
            if (room.equals("baibian"))
                returnMap.put(room, baibian);
            if (room.equals("time"))
                returnMap.put(room, time);
            if (room.equals("room_state_a"))
                returnMap.put(room, room_state_a);
            if (room.equals("foundation"))
                returnMap.put(room, foundation);
        }
    }

    /**
     * 获取作弊牌型
     *
     * @return @throws
     */
    public void getBrand_a(UserDao ud, int id, T_UserBean userBean) {
        // 查询所有作弊牌型
        String cards = ud.getCheat(userBean.getfId());
        String[] card = cards.split(",");
        int a = (int) (Math.random() * card.length);
        String[] cards_a = card[a].split("-");
        userBean.setCheat(cards_a);
        // 作弊牌型数组
    }

    /**
     * 初始化扑克牌
     */
    public void setBrands(int number) {
        StringBuffer brand1 = new StringBuffer();

        if (this.foundation == 5) {
            for (int i = 0; i < 52 * number; i++) {
                if (i > 0) {
                    brand1.append("-");
                }
                brand1.append(i);
            }
            for (int i = 91; i < 104; i++) {
                brand1.append("-");
                brand1.append(i);
            }
        } else {
            for (int i = 0; i < 52 * number; i++) {
                if (i > 0) {
                    brand1.append("-");
                }
                brand1.append(i);
            }
        }
        this.brands = brand1.toString();
    }

    /**
     * 初始化用户位置
     *
     */
    public void setUser_positions(int[] user_positions) {
        this.user_positions = user_positions;
    }

    /**
     * 初始化用户操作状态
     *
     * @throws
     */
    public void Initialization_UserBrand() {
        for (T_UserBean user : getGame_userList(0)) {
            user.setBrandstatus(0);
        }
    }

    /**
     * 加入用户位置 按顺序加入 @param userBean @throws
     */
    public void setUser_positions(T_UserBean userBean) {
        for (int i = 0; i < user_positions.length; i++) {
            if (user_positions[i] == -1) {
                user_positions[i] = userBean.getUserid();
                break;
            }
        }
    }

    /**
     * 剔除指定用户的位置
     *
     */
    public void remove_options(int userid) {
        for (int i = 0; i < user_positions.length; i++) {
            if (user_positions[i] == userid) {
                user_positions[i] = -1;
            }
        }
    }

    /**
     * 删除一名用户 @param userid @throws
     */
    public void User_Remove(int userid) {
        for (int i = 0; i < game_userList.size(); i++) {
            if (game_userList.get(i).getUserid() == userid) {
                game_userList.remove(i);
                break;
            }
        }

    }

    /**
     * 扣除底注 @param gameDao @throws
     */
    public void DeductionBottom_Pan(GameDao gameDao) {
        for (T_UserBean userBean : game_userList) {
            if (userBean.getGametype() != -1) {
                gameDao.UpdateUserMoney(userBean.getUserid(), bets, 0);
                userBean.setMoney(userBean.getMoney() - bets);
            }
        }
    }

    /**
     * 用户下注
     *
     * @param userBean
     * @param bets
     * 			@param betstype @throws
     */
    public void DeductionBottom_Pan(T_UserBean userBean, int bets, int betstype) {
        // 减去用户金钱
        userBean.setMoney(userBean.getMoney() - bets);
        // 添加用户输赢总数
        userBean.setWinnum(userBean.getWinnum() + bets);
    }

    /**
     * 添加用户到用户集合
     *
     * @return @throws
     */
    /*
     * public List<UserBean> setGame_userList(){ List<UserBean> list = new
     * ArrayList<UserBean>(); for (UserBean userBean : game_userList) {
     * //没有弃牌且已参与游戏的用户 if(userBean.getGametype() != -1){ list.add(userBean); } }
     * return list; }
     */

    /**
     * 获取房间内用户集合 type=0 返回实例 1返回过滤 @param type @return @throws
     */
    public List<T_UserBean> getGame_userList(int type) {
        // 返回本实例
        if (type == 0) {
            return game_userList;
        }
        List<T_UserBean> list = new ArrayList<T_UserBean>();
        for (T_UserBean userBean : game_userList) {
            // 没有弃牌且已参与游戏的用户
            if (userBean.getBrandstatus() != 2 && userBean.getGametype() != -1) {
                list.add(userBean);
            }
        }
        return list;
    }

    /**
     * 发牌(通比模式)
     *
     * @param number @throws
     */
    public void GrantBrand(int number, T_RoomBean rb) {
        String[] brand = brands.split("-");
        // 作弊发牌
        for (int i = 0; i < this.getGame_userList(0).size(); i++) {
            if (this.getGame_userList(0).get(i).getType() == 1) {
                for (int j = 0; j < userBean.getCheat().length; j++) {
                    int brand_a = Integer.parseInt(userBean.getCheat()[j]);
                    int if_Brand = this.If_Brand(brand, brand_a);
                    if (if_Brand == 1) { // 如果牌组中有作弊牌则直接发作弊牌
                        this.RemoveBrand(brand_a, brand);
                        this.getGame_userList(0).get(i).getBrand()[j] = brand_a;
                    } else { // 没有作弊牌随机发一张牌
                        this.getGame_userList(0).get(i).getBrand()[j] = getBrand();
                    }
                }
            }
        }

        // 正常发牌
        for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
            T_UserBean bean = getUserBean(rb.getGame_userList(0).get(i).getUserid());
            for (int j = 0; j < number; j++) {
                bean.setBrand(getBrand());
            }
            /*int[] arr = {25, 19, 100, 37, 49, 28, 42, 21, 17, 40, 3, 5, 30};
            for (int j = 0; j < arr.length; j++) {
                bean.setBrand(arr[j]);
            }*/
        }
    }


    /**
     * 判断牌组内是否有作弊牌
     *
     * @param brand
     * @param brand_a
     * @return @throws
     */
    public int If_Brand(String[] brand, int brand_a) {
        int count = 0;
        for (int i = 0; i < brand.length; i++) {
            if (Integer.parseInt(brand[i]) == brand_a) {
                count++;
            }
        }
        return count;
    }

    /**
     * 随机获取一张牌
     *
     */
    public int getBrand() {
        String[] brand = brands.split("-");
        int index = (int) (Math.random() * brand.length);
        // 获取总牌的id
        int brands_index = Integer.parseInt(brand[index]);
        // 剔除此牌
        RemoveBrand(brands_index, brand);
        return brands_index;
    }

    /** 
     * 剔除一张牌 @param brands_index @param brand @throws
     */
    public void RemoveBrand(int index, String[] brand) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < brand.length; i++) {
            if (Integer.parseInt(brand[i]) != index) {
                if (!"".equals(sb.toString()))
                    sb.append("-");
                sb.append(brand[i]);
            }

        }
        this.brands = sb.toString();
    }

    /**
     * 排序用户实体类数组
     *
     * @return @throws
     */
    public Map<Object, Integer> getUserBandList() {
        int brand_type = 0;
        Map<Object, Integer> map = new HashMap<Object, Integer>();
        for (int i = 0; i < game_userList.size(); i++) {
            //brand_type = game_userList.get(i).getUser_brand_type();
            map.put(userBean, brand_type);
        }
        return map;
    }


    /**
     * 开始游戏 @param userBean2 @param rb @throws
     */
    public int Start_Game(T_UserBean userBean, T_RoomBean rb) {
        int count = 0;
        for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
            rb.getGame_userList(0).get(i).setGametype(1);
            if (rb.getGame_userList(0).get(i).getReady_state() == 1) {
                count++;
            }
        }
        if (count >= 2) {
            rb.setRoom_state(2); // 游戏开始
            return 2;
        }
        return 0;
    }


    /************************ get\set ****************************/

    public Date getGame_time() {
        return game_time;
    }

    public int getTimer_user() {
        return timer_user;
    }

    public void setTimer_user(int timer_user) {
        this.timer_user = timer_user;
    }

    public T_UserBean getUserBean() {
        return userBean;
    }

    public Time_Room getTime_Room() {
        return time_Room;
    }

    public void setTime_Room(Time_Room time_Room) {
        this.time_Room = time_Room;
    }

    public void setUserBean(T_UserBean userBean) {
        this.userBean = userBean;
    }

    public void setBrands(String brands) {
        this.brands = brands;
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

    public void setGame_time(Date game_time) {
        this.game_time = game_time;
    }

    public int getRoom_type() {
        return room_type;
    }

    public void setRoom_type(int room_type) {
        this.room_type = room_type;
    }

    public String getRoom_number() {
        return room_number;
    }

    public void setRoom_number(String room_number) {
        this.room_number = room_number;
    }

    public void setGame_userList(List<T_UserBean> game_userList) {
        this.game_userList = game_userList;
    }

    public int getBranker_id() {
        return branker_id;
    }

    public void setBranker_id(int branker_id) {
        this.branker_id = branker_id;
    }

    public int getFen() {
        return fen;
    }

    public void setFen(int fen) {
        this.fen = fen;
    }

    public int getRoom_state() {
        return room_state;
    }

    public void setRoom_state(int room_state) {
        this.room_state = room_state;
    }

    public int getGame_number() {
        return game_number;
    }

    public void setGame_number(int game_number) {
        this.game_number = game_number;
    }

    public int getBets() {
        return bets;
    }

    public void setBets(int bets) {
        this.bets = bets;
    }

    public int[] getZbets() {
        return zbets;
    }

    public void setZbets(int[] zbets) {
        this.zbets = zbets;
    }

    public int[] getUser_positions() {
        return user_positions;
    }

    public String getBrands() {
        return brands;
    }

    public String[] getRoom_brandsList() {
        return room_brandsList;
    }

    public void setRoom_brandsList(String[] room_brandsList) {
        this.room_brandsList = room_brandsList;
    }

    public List<T_UserBean> getGame_userList() {
        return game_userList;
    }

    public int getVictoryid() {
        return victoryid;
    }

    public void setVictoryid(int victoryid) {
        this.victoryid = victoryid;
    }

    public int getRoom_state_a() {
        return room_state_a;
    }

    public void setRoom_state_a(int room_state_a) {
        this.room_state_a = room_state_a;
    }

    public int getDi_fen() {
        return di_fen;
    }

    public void setDi_fen(int di_fen) {
        this.di_fen = di_fen;
    }

    public int getRoom_branker() {
        return room_branker;
    }

    public void setRoom_branker(int room_branker) {
        this.room_branker = room_branker;
    }

    public List<T_UserBean> getRb_List() {
        return rb_List;
    }

    public void setRb_List(List<T_UserBean> rb_List) {
        this.rb_List = rb_List;
    }

    public int getMax_number() {
        return max_number;
    }

    public void setMax_number(int max_number) {
        this.max_number = max_number;
    }

    public int getRule() {
        return rule;
    }

    public void setRule(int rule) {
        this.rule = rule;
    }

    public int getGame_state() {
        return game_state;
    }

    public void setGame_state(int game_state) {
        this.game_state = game_state;
    }

    public int getFoundation() {
        return foundation;
    }

    public void setFoundation(int foundation) {
        this.foundation = foundation;
    }

    public int getBaibian() {
        return baibian;
    }

    public void setBaibian(int baibian) {
        this.baibian = baibian;
    }

    /**
     * 解散房间 @param rb @param userBean2 @throws
     */
    public int getJiesan(T_RoomBean rb, T_UserBean userBean2) {
        int count = 0;
        int count_a = 0;
        for (int i = 0; i < rb.getGame_userList(0).size(); i++) {
            if (rb.getGame_userList(0).get(i).getJiesan() == 1) {
                count++;
            }
        }
        if (count == rb.getGame_userList().size()) {
            return 1;
        }
        return 0;

    }


    /**
     * 获取用户组合的三墩牌型
     * @param
     * @param
     * @param
     * @throws
     */
    public void setBrand(T_UserBean userBean, String upBrand, String middleBrand, String belowBrand) {
        String[] up = upBrand.split("-"); //上墩
        for (int i = 0; i < up.length; i++) {
            userBean.getUpBrand()[i] = Integer.parseInt(up[i]);
        }
        String[] middle = middleBrand.split("-"); //中墩
        for (int i = 0; i < middle.length; i++) {
            userBean.getMiddleBrand()[i] = Integer.parseInt(middle[i]);
        }
        String[] below = belowBrand.split("-"); //下墩
        for (int i = 0; i < below.length; i++) {
            userBean.getBelowBrand()[i] = Integer.parseInt(below[i]);
        }

    }

}
