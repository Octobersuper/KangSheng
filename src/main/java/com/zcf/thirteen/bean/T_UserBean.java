/**
 * 
 */
package com.zcf.thirteen.bean;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.Session;

import com.zcf.thirteen.comm.Public_State_t;
import com.zcf.thirteen.comm.WebSocket;
import com.zcf.thirteen.service.GameService;
import com.zcf.thirteen.util.BaseDao;
import com.zcf.thirteen.util.Util_Brand;

/**
 * @author guolele
 * @date 2019年2月20日 上午9:21:05
 * 
 */
@SuppressWarnings("unused")
public class T_UserBean {
	// 用户id
	private int userid;
	// 用户昵称
	private String nickname;
	// 钻石
	private int diamond;
	// 用户头像
	private String avatarurl;
	// 游戏中的状态0默认1游戏中-1不可游戏 2掉线
	private int gametype = -1;
	// 是否抢庄 0不抢庄 1抢庄
	private int Robbery = 0;
	// 用户手里的牌
	private Integer[] brand = new Integer[] { -1, -1 ,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
	// 充值 0未充值 1已充值
	private int isPay;
	// 金币
	private int money;
	// 当前局输赢的水数
	private int winnum;
	// 当前局输赢状态
	private int winType = 0;
	// 胜利者ID
	private int winId;
	// 操作状态默認0 下注1 开牌2
	private int brandstatus = 0;
	// 牌型算法
	private Util_Brand util_brand;
	// 是否开牌
	private int open_brand;
	// 手机号
	private String phone;
	// 用户session
	private Session session;
	// 是否继续坐庄
	private int branker_number;
	// 庄家底分
	private int user_fen;
	// 连续坐庄的次数
	private int branker_count;
	// 是否开启作弊
	private int type;
	// 作弊牌型
	private String[] Cheat;
	// 作弊牌型id
	private Long fId;
	// 用户游戏之前的金额
	private int start_money;
	// 用户输赢总金额
	private int win_money;
	//解散
	private int jiesan;
	//花色
	private int brand_color = 0;
	//上墩牌
	private Integer[] upBrand = new Integer[]{-1,-1,-1};
	private String upBrand_type;
	private String upBrand_type_a;
	//中墩牌 
	private Integer[] middleBrand = new Integer[]{-1,-1,-1,-1,-1};
	private String middleBrand_type;
	private String middleBrand_type_a;
	//下墩牌
	private Integer[] belowBrand = new Integer[]{-1,-1,-1,-1,-1};
	private String belowBrand_type;
	private String belowBrand_type_a;
	//三墩全赢的次数
    private int win_number;
    //被打枪时输的分数  用来计算最后的全垒打
	private Map<Integer,Integer> qiang_fen = new HashMap<>();

	//是否准备
	private int ready_state;
	//坐标
	private String log_lat;
	//语音文件路径
	private String voice;
	
	//用来存储用户手里牌型对子的数组
	private int[] shang_duizi = new int[]{-1,-1};
	private int[] zhong_duizi = new int[]{-1,-1};
	private int[] xia_duizi = new int[]{-1,-1};
	//存储用户手里牌型豹子的数组
	private int[] shang_baozi = new int[]{-1,-1,-1};
	private int[] zhong_baozi = new int[]{-1,-1,-1};
	private int[] xia_baozi = new int[]{-1,-1,-1};
	//存储用户手里牌型炸弹的数组
	private int[] zhong_tiezhi = new int[]{-1,-1,-1,-1};
	private int[] xia_tiezhi = new int[]{-1,-1,-1,-1};
	//五同
	private int[] zhong_wutong = new int[]{-1,-1,-1,-1,-1};
	private int[] xia_wutong = new int[]{-1,-1,-1,-1,-1};
	//同花
	private int zhong_tonghua;
	private int xia_tonghua;
	//顺子
	private int[] zhong_shunzi = new int[]{-1,-1,-1,-1,-1};
	private int[] xia_shunzi = new int[]{-1,-1,-1,-1,-1};

	//当前所在俱乐部房间
	private String club_number = "-1";
	//当前所在俱乐部楼层
	private int floor = -1;

	public String getClub_number() {
		return club_number;
	}

	public void setClub_number(String club_number) {
		this.club_number = club_number;
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public int getDiamond() {
		return diamond;
	}

	public void setDiamond(int diamond) {
		this.diamond = diamond;
	}

	public Map<Integer, Integer> getQiang_fen() {
		return qiang_fen;
	}

	public void setQiang_fen(Map<Integer, Integer> qiang_fen) {
		this.qiang_fen = qiang_fen;
	}

	public int getReady_state() {
		return ready_state;
	}

	public void setReady_state(int reday_state) {
		this.ready_state = reday_state;
	}

	public String getLog_lat() {
		return log_lat;
	}

	public void setLog_lat(String log_lat) {
		this.log_lat = log_lat;
	}

	public String getVoice() {
		return voice;
	}

	public void setVoice(String voice) {
		this.voice = voice;
	}

	/**
	 * 自定义获取观战列表
	 *
	 * @param table
	 * @param map    
	 * @throws
	 */
	public void getGuanZhan_Custom(String table,Map<String, Object> map){
		String[] names = table.split("-");
		for (String user:names) {
			if (user.equals("userid"))
				map.put(user, userid);
			if (user.equals("nickname"))
				map.put(user, nickname);
			if (user.equals("avatarurl"))
				map.put(user, avatarurl);
		}
	}

	/**
	 * 
	 * 获取自定义用户详细信息 @param table @param map @throws
	 */
	public void getUser_Custom(String table, Map<String, Object> map) {
		String[] names = table.split("-");
		for (String user : names) {
			if (user.equals("userid"))
				map.put(user, userid);
			if (user.equals("user_fen"))
				map.put(user, user_fen);
			if (user.equals("nickname"))
				map.put(user, nickname);
			if (user.equals("avatarurl"))
				map.put(user, avatarurl);
			if (user.equals("gametype"))
				map.put(user, gametype);
			if (user.equals("brand"))
				map.put(user, brand);
			if (user.equals("isPay"))
				map.put(user, isPay);
			if (user.equals("money"))
				map.put(user, money);
			if (user.equals("bets"))
				map.put(user, money);
			if (user.equals("winnum"))
				map.put(user, winnum);
			if (user.equals("branker_count"))
				map.put(user, branker_count);
			if (user.equals("branker_number"))
				map.put(user, branker_number);
			if (user.equals("type"))
				map.put(user, type);
			if (user.equals("Cheat"))
				map.put(user, Cheat);
			if (user.equals("fId"))
				map.put(user, fId);
			if (user.equals("start_money"))
				map.put(user,start_money);
			if (user.equals("win_money"))
				map.put(user, win_money);
			if (user.equals("brand_color"))
				map.put(user, brand_color);
			if (user.equals("upBrand"))
				map.put(user, upBrand);
			if (user.equals("belowBrand"))
				map.put(user, belowBrand);
			if (user.equals("middleBrand"))
				map.put(user, middleBrand);
			if (user.equals("upBrand_type"))
				map.put(user, upBrand_type);
			if (user.equals("belowBrand_type"))
				map.put(user, belowBrand_type);
			if (user.equals("middleBrand_type"))
				map.put(user, middleBrand_type);
			if (user.equals("ready_state"))
				map.put(user, ready_state);
		}
	}

	/**
	 * 自定义获取机器人列
	 *
	 * @param table @param map @throws
	 */
	public void getRobot_Custom(String table, Map<String, Object> map) {
		String[] names = table.split("-");
		for (String user : names) {
			if (user.equals("userid"))
				map.put(user, userid);
			if (user.equals("brand"))
				map.put(user, brand);
			if (user.equals("brand_color"))
				map.put(user, brand_color);
		}
	}

	/**
	 * 
	 * 初始化用户 @throws
	 */
	public void Initialization() {
		// 初始化手中的牌
		this.brand = new Integer[] { -1, -1 ,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
		// 初始化用户状态
		this.gametype = 0;
		this.brandstatus = 0;
		this.win_number = 0;
		this.winnum = 0;
		// 初始化用户的牌型
		this.upBrand_type = null;
		this.middleBrand_type = null;
		this.belowBrand_type =null;
		//初始化对子
		this.shang_duizi = new int[]{-1,-1};
		this.zhong_duizi = new int[]{-1,-1};
		this.xia_duizi = new int[]{-1,-1};
		//初始化三条
		this.shang_baozi = new int[]{-1,-1,-1};
		this.zhong_baozi = new int[]{-1,-1,-1};
		this.xia_baozi = new int[]{-1,-1,-1};
		//初始化铁支
		this.zhong_tiezhi = new int[]{-1,-1,-1,-1};
		this.xia_tiezhi = new int[]{-1,-1,-1,-1,-1};
		//初始化五同
		this.zhong_wutong = new int[]{-1,-1,-1,-1,-1};
		this.xia_wutong = new int[]{-1,-1,-1,-1,-1};
		//初始化同花
		this.zhong_tonghua = -1;
		this.xia_tonghua = -1;
		//初始化顺子
		this.zhong_shunzi = new int[]{-1,-1,-1,-1,-1};
		this.xia_shunzi = new int[]{-1,-1,-1,-1,-1};
		//初始化用户墩牌
		this.upBrand = new Integer[]{-1,-1,-1};
		this.middleBrand = new Integer[]{-1,-1,-1,-1,-1};
		this.belowBrand = new Integer[]{-1,-1,-1,-1,-1};
		//初始化用户开牌状态
		this.open_brand = -1;
		// 初始化胜利者信息
		this.winType = 0;
		this.winId = 0;
		// 底分
		this.user_fen = 0;
		// 初始化作弊牌型
		this.Cheat = null;
		ready_state = 0;
		this.qiang_fen.clear();
	}

	/**
	 * 
	 * 放入一张牌到用户手中 @param brands @throws
	 */
	public void setBrand(int brands) {
		int count = 0;
		for (int i : this.brand) {
			if (i != -1) {
				count++;
			}
		}
		if (count < this.brand.length) {
			this.brand[count] = brands;
		}
	}

	/**
	 * 返回 用户手里的最后一张牌的牌值或者下标 type=0返回下标 =1返回牌值
	 *
	 * @param type
	 * @return @throws
	 */
	public int getBrand_M(int type) {
		int count = -1;
		for (int i : this.brand) {
			if (i != -1) {
				count++;
			}
		}
		if (type == 0) {
			// 一副牌的索引
			return this.brand[count];
		} else {
			// 牌值
			int brand_m = this.brand[count] % 13 + 1;
			return brand_m;
		}
	}

	/**
	 * 
	 * 返回用户手里的牌型 @param number @return @throws
	 */
	public int BrandCount(T_RoomBean rb, int[] brand) {
		
		return 1;
	}

	public boolean ISMoney(int money) {
		return this.money >= money;
	}

	/********************* get\set ******************************/

	public int getBrandstatus() {
		return brandstatus;
	}

	public void setBrandstatus(int brandstatus) {
		this.brandstatus = brandstatus;
	}

	public Util_Brand getUtil_brand() {
		return util_brand;
	}

	public void setUtil_brand(Util_Brand util_brand) {
		this.util_brand = util_brand;
	}

	public int getWinnum() {
		return winnum;
	}

	public void setWinnum(int winnum) {
		this.winnum = winnum;
	}
	public int getGametype() {
		return gametype;
	}

	public void setGametype(int gametype) {
		this.gametype = gametype;
	}

	public int getIsPay() {
		return isPay;
	}

	public void setIsPay(int isPay) {
		this.isPay = isPay;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAvatarurl() {
		return avatarurl;
	}

	public void setAvatarurl(String avatarurl) {
		this.avatarurl = avatarurl;
	}

	public Integer[] getBrand() {
		return brand;
	}

	public void setBrand(Integer[] brand) {
		this.brand = brand;
	}

	public int getMoney() {
		return money;
	}

    public int getWin_number() {
        return win_number;
    }

    public void setWin_number(int win_number) {
        this.win_number = win_number;
    }

    public void setMoney(int money) {
		this.money = money;
	}
	public int getOpen_brand() {
		return open_brand;
	}

	public void setOpen_brand(int open_brand) {
		this.open_brand = open_brand;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getWinType() {
		return winType;
	}

	public void setWinType(int winType) {
		this.winType = winType;
	}

	public int getWinId() {
		return winId;
	}

	public void setWinId(int winId) {
		this.winId = winId;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public int getBranker_number() {
		return branker_number;
	}

	public void setBranker_number(int branker_number) {
		this.branker_number = branker_number;
	}

	public int getUser_fen() {
		return user_fen;
	}

	public void setUser_fen(int user_fen) {
		this.user_fen = user_fen;
	}

	public int getBranker_count() {
		return branker_count;
	}

	public void setBranker_count(int branker_count) {
		this.branker_count = branker_count;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String[] getCheat() {
		return Cheat;
	}

	public void setCheat(String[] cheat) {
		Cheat = cheat;
	}

	public Long getfId() {
		return fId;
	}

	public void setfId(Long fId) {
		this.fId = fId;
	}

	public int getRobbery() {
		return Robbery;
	}

	public void setRobbery(int robbery) {
		Robbery = robbery;
	}
	public int getStart_money() {
		return start_money;
	}

	public void setStart_money(int start_money) {
		this.start_money = start_money;
	}

	public int getWin_money() {
		return win_money;
	}
	public void setWin_money(int win_money) {
		this.win_money = win_money;
	}

	public int getJiesan() {
		return jiesan;
	}

	public void setJiesan(int jiesan) {
		this.jiesan = jiesan;
	}

	public int getBrand_color() {
		return brand_color;
	}

	public void setBrand_color(int brand_color) {
		this.brand_color = brand_color;
	}

	public Integer[] getUpBrand() {
		return upBrand;
	}

	public void setUpBrand(Integer[] upBrand) {
		this.upBrand = upBrand;
	}

	public Integer[] getMiddleBrand() {
		return middleBrand;
	}

	public void setMiddleBrand(Integer[] middleBrand) {
		this.middleBrand = middleBrand;
	}

	public Integer[] getBelowBrand() {
		return belowBrand;
	}

	public void setBelowBrand(Integer[] belowBrand) {
		this.belowBrand = belowBrand;
	}

	public int[] getShang_duizi() {
		return shang_duizi;
	}

	public void setShang_duizi(int[] shang_duizi) {
		this.shang_duizi = shang_duizi;
	}

	public int[] getZhong_duizi() {
		return zhong_duizi;
	}

	public void setZhong_duizi(int[] zhong_duizi) {
		this.zhong_duizi = zhong_duizi;
	}

	public int[] getXia_duizi() {
		return xia_duizi;
	}

	public void setXia_duizi(int[] xia_duizi) {
		this.xia_duizi = xia_duizi;
	}

	public int[] getShang_baozi() {
		return shang_baozi;
	}

	public void setShang_baozi(int[] shang_baozi) {
		this.shang_baozi = shang_baozi;
	}

	public int[] getZhong_baozi() {
		return zhong_baozi;
	}

	public void setZhong_baozi(int[] zhong_baozi) {
		this.zhong_baozi = zhong_baozi;
	}

	public int[] getXia_baozi() {
		return xia_baozi;
	}

	public void setXia_baozi(int[] xia_baozi) {
		this.xia_baozi = xia_baozi;
	}

	public int[] getZhong_tiezhi() {
		return zhong_tiezhi;
	}

	public void setZhong_tiezhi(int[] zhong_tiezhi) {
		this.zhong_tiezhi = zhong_tiezhi;
	}

	public int[] getXia_tiezhi() {
		return xia_tiezhi;
	}

	public void setXia_tiezhi(int[] xia_tiezhi) {
		this.xia_tiezhi = xia_tiezhi;
	}

	public String getUpBrand_type() {
		return upBrand_type;
	}

	public void setUpBrand_type(String upBrand_type) {
		this.upBrand_type = upBrand_type;
	}

	public String getUpBrand_type_a() {
		return upBrand_type_a;
	}

	public void setUpBrand_type_a(String upBrand_type_a) {
		this.upBrand_type_a = upBrand_type_a;
	}

	public String getMiddleBrand_type() {
		return middleBrand_type;
	}

	public void setMiddleBrand_type(String middleBrand_type) {
		this.middleBrand_type = middleBrand_type;
	}

	public String getMiddleBrand_type_a() {
		return middleBrand_type_a;
	}

	public void setMiddleBrand_type_a(String middleBrand_type_a) {
		this.middleBrand_type_a = middleBrand_type_a;
	}

	public String getBelowBrand_type() {
		return belowBrand_type;
	}

	public void setBelowBrand_type(String belowBrand_type) {
		this.belowBrand_type = belowBrand_type;
	}

	public String getBelowBrand_type_a() {
		return belowBrand_type_a;
	}

	public void setBelowBrand_type_a(String belowBrand_type_a) {
		this.belowBrand_type_a = belowBrand_type_a;
	}

	public int[] getZhong_wutong() {
		return zhong_wutong;
	}

	public void setZhong_wutong(int[] zhong_wutong) {
		this.zhong_wutong = zhong_wutong;
	}

	public int[] getXia_wutong() {
		return xia_wutong;
	}

	public void setXia_wutong(int[] xia_wutong) {
		this.xia_wutong = xia_wutong;
	}

	public int getZhong_tonghua() {
		return zhong_tonghua;
	}

	public void setZhong_tonghua(int zhong_tonghua) {
		this.zhong_tonghua = zhong_tonghua;
	}

	public int getXia_tonghua() {
		return xia_tonghua;
	}

	public void setXia_tonghua(int xia_tonghua) {
		this.xia_tonghua = xia_tonghua;
	}

	public int[] getZhong_shunzi() {
		return zhong_shunzi;
	}

	public void setZhong_shunzi(int[] zhong_shunzi) {
		this.zhong_shunzi = zhong_shunzi;
	}

	public int[] getXia_shunzi() {
		return xia_shunzi;
	}

	public void setXia_shunzi(int[] xia_shunzi) {
		this.xia_shunzi = xia_shunzi;
	}


	/**
	 *@ Author:ZhaoQi
	 *@ methodName:增加比牌分数
	 *@ Params:i：1头道  2中道  3尾道  isqiang 是否打枪  rule 规则模式  bean 输家bean
	 *@ Description:
	 *@ Return:
	 *@ Date:2020/3/18
	 */
	public void addWin_number(int i,int isqiang,T_RoomBean rb,T_UserBean bean) {
		int fen = 0;
		int fan = 1;
		switch (i){
			case 1://头道
				fen = getfanbei(Integer.valueOf(upBrand_type), i, isqiang, rb.getRule());
				break;
			case 2://中道
				fen = getfanbei(Integer.valueOf(middleBrand_type), i, isqiang,  rb.getRule());
				break;
			case 3://尾道
				fen = getfanbei(Integer.valueOf(belowBrand_type), i, isqiang,  rb.getRule());
				break;
		}
		if(isqiang==1){//打枪翻倍
			fan = fan*2;
		}
		if(isbaibian()){//马牌翻倍
			System.out.println("马牌翻倍");
			fan = fan*2;
		}
		System.out.println(nickname+"的头道牌型:"+upBrand_type+"的中道牌型:"+middleBrand_type+"的尾道牌型:"+belowBrand_type+"牌型基础分数:"+fen+"倍数:"+fan);
		winnum = winnum + (fen *fan);//增加赢家分数
		bean.setWinnum(bean.getWinnum()-(fen *fan));//减少输家分数
		if(isqiang==1){
			Integer integer = bean.getQiang_fen().get(userid);
			if (integer == null) {
				bean.getQiang_fen().put(userid,0);
			}
			bean.getQiang_fen().put(userid,bean.getQiang_fen().get(userid)+(fen*fan));
		}
	}

	public int getfanbei(int type,int tmd,int isqiang,int rule){
		int i = 0;
		switch (type){
			case -2://散牌
			case 0://对子
			case 1://两队
				i = 1;
				break;
			case 2://三条
				if(tmd==1){
					i = 3;
					break;
				}
			case 3://顺子
			case 4://同花
				i = 1;
				break;
			case 5://葫芦
				if(tmd==2) i = 2;
				if(tmd==3) i = 1;
				break;
			case 6://铁支
				if(tmd==2) i = 8;
				if(tmd==3) i = 4;
				break;
			case 7://同花顺   红波浪模式 三道全赢 尾道为同花顺时 加4
				if(rule==0){
					if(isqiang==1){
						if(tmd==3) i = 4;
					}else{
						if(tmd==3) i = 5;
					}
				}else{
					if(tmd==3) i = 5;
				}
				if(tmd==2) i = 10;
				break;
			case 8://五同
				if(tmd==2) i = 20;
				if(tmd==3) i = 10;
				break;
			case 9://一条龙
				i = 13;
				break;
			case 10://至尊一条龙
				i = 26;
				break;
		}
		return i;
	}

	/**
	 *@ Author:ZhaoQi
	 *@ methodName:判断用户手里是否有马牌
	 *@ Params:
	 *@ Description:
	 *@ Return:
	 *@ Date:2020/3/18
	 */
	private boolean isbaibian(){
		WebSocket webSocket = Public_State_t.clients_t.get(String.valueOf(userid));
		boolean a = Arrays.asList(upBrand).contains(webSocket.rb.getBaibian());
		boolean b = Arrays.asList(middleBrand).contains(webSocket.rb.getBaibian());
		boolean c = Arrays.asList(belowBrand).contains(webSocket.rb.getBaibian());
		if(a || b || c){
			return true;
		}
		return false;
	}

	/**
	 *@ Author:ZhaoQi
	 *@ methodName:转换成真是牌值并排序
	 *@ Params:
	 *@ Description:
	 *@ Return:
	 *@ Date:2020/3/17
	 */
	public Integer[] sort(Integer[] upBrand) {
		Integer[] arr = new Integer[upBrand.length];
		for (int i = 0; i < upBrand.length; i++) {
			arr[i] = (upBrand[i] % 13) +1;
			if(arr[i]==1){
				arr[i]=999;
			}
		}
		Arrays.sort(arr);
		return arr;
	}
}
