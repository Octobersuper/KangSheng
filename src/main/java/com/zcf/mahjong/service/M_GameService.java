package com.zcf.mahjong.service;

import com.zcf.mahjong.bean.RoomBean;
import com.zcf.mahjong.bean.UserBean;
import com.zcf.mahjong.dao.ClubDao;
import com.zcf.mahjong.dao.M_GameDao;
import com.zcf.mahjong.mahjong.Establish_PK;
import com.zcf.mahjong.mahjong.Matching_PK;
import com.zcf.mahjong.mahjong.Public_State;
import com.zcf.mahjong.util.BaseDao;
import com.zcf.mahjong.util.Mahjong_Util;

import java.util.*;

import static com.zcf.mahjong.util.Mahjong_Util.mahjong_Util;

public class M_GameService {
	private M_GameDao gameDao;
	private ClubDao clubDao;

	public M_GameService(BaseDao baseDao) {
		this.gameDao = new M_GameDao(baseDao);
		this.clubDao = new ClubDao(baseDao);
	}

	/**
	 * 创建房间
	 * 
	 * @return
	 */
	public RoomBean Establish(Map<String, String> map, UserBean userBean, int clubid) {
		int index = -1;
		if (Integer.parseInt(map.get("max_number")) == 10)
			index = 0;
		if (Integer.parseInt(map.get("max_number")) == 20)
			index = 1;
		if (Integer.parseInt(map.get("max_number")) == 30)
			index = 2;
		// 创建房间所需房卡(8局-12局)
		int fangka = Integer.parseInt(map.get("max_person")) == 2 ? Integer.parseInt(Public_State.establish_two[index])
				: Integer.parseInt(Public_State.establish_four[index]);
		// 俱乐部房卡或用户房卡
		int diamond = clubid == 0 ? gameDao.getUserDiamond(userBean.getUserid()) : clubDao.getClub_diamond(clubid);
		// 房卡不足
		if (diamond < fangka) {
			return null;
		}
		// IP
		userBean.setIp(map.get("ip"));
		// 创建房间
		RoomBean roomBean = Establish_PK.Establish();
		// 指定房主
		roomBean.setHouseid(userBean.getUserid());
		// 房间消耗房卡
		roomBean.setRoom_card(fangka);
		// 加入自己
		roomBean.getGame_userlist().add(userBean);
		// 更新分数
		userBean.setNumber(0);
		// 加入座位
		roomBean.setUser_positions(userBean.getUserid());
		// 房间最大局数
		roomBean.setMax_number(Integer.parseInt(map.get("max_number")));
		// 底分
		roomBean.setFen(Integer.parseInt(map.get("fen")));
		// 人数
		roomBean.setMax_person(Integer.parseInt(map.get("max_person")));
		// 0为个人创建
		roomBean.setClubid(clubid);
		// 经纬度
		userBean.setLog_lat(map.get("log_lat"));
		// 换三张开关
		roomBean.setAdjust_brand(Integer.parseInt(map.get("adjust_brand")));
		// 是否同花色
		roomBean.setSame_colour(Integer.parseInt(map.get("same_colour")));
		// 是否定缺
		roomBean.setAssured_brand(Integer.parseInt(map.get("assured_brand")));
		// 是否字牌算飞机
		roomBean.setAircraft(Integer.parseInt(map.get("aircraft")));
		// 测距功能开关
		roomBean.setRanging(Integer.parseInt(map.get("ranging")));
		// 是否主动测距
		roomBean.setInitiative_ranging(Integer.parseInt(map.get("initiative_ranging")));
		// 开启gps定位才能加入游戏
		roomBean.setGps(Integer.parseInt(map.get("gps")));
		return roomBean;
	}

	/**
	 * 加入房间
	 * 
	 * @param map
	 * @param userBean
	 * @return
	 */
	public RoomBean Matching(Map<String, String> map, UserBean userBean) {
		// ip
		userBean.setIp(map.get("ip"));
		// 初始化分数
		userBean.setNumber(0);
		// 加入房间
		return Matching_PK.Matching(userBean, map.get("roomno"));
	}

	/**
	 * 检测房卡是否可以加入房间
	 * 
	 * @param userBean
	 * @param roomno
	 * @return
	 */
	public int ISMatching_Money(UserBean userBean, String roomno) {

		if (Public_State.PKMap.get(roomno) == null) {
			return 100;// 房间不存在
		}
		if (ISMatching(roomno, userBean)) {
			return 114;// 重复加入
		}
		// 检测房间类型
		RoomBean roomBean = Public_State.PKMap.get(roomno);
		// 检测经纬度
		if (roomBean.getGps() == 1) {
			// 如果未传入经纬度则返回错误码
			if (userBean.getLog_lat().equals("0"))
				return 106;
		}
		return 0;
	}


	/**
	 * 准备
	 * 
	 * @param userBean
	 * @param roomBean
	 * @return
	 */
	public int Ready(UserBean userBean, RoomBean roomBean) {
		// 准备
		userBean.setReady_state(1);
		int count = 0;
		// 判断是否所有人都准备
		roomBean.getLock().lock();
		for (UserBean user : roomBean.getGame_userlist()) {
			if (user.getReady_state() == 1)
				count++;
		}
		roomBean.getLock().unlock();
		return count;
	}

	/**
	 * 检测是否有人可以胡牌
	 * 
	 * @param roomBean
	 * @param state
	 * @return
	 */
	public int Is_Hu(RoomBean roomBean, int state) {
		roomBean.getLock().lock();
		if (state == 0) {
			roomBean.setHucount(roomBean.getHucount() + 1);
		}
		roomBean.getLock().unlock();
		return roomBean.getHucount();
	}

	/**
	 * 检测是否有人可以胡牌
	 *
	 * @param roomBean
	 * @param
	 * @return
	 */
	public int Is_Hunum(RoomBean roomBean) {
		roomBean.getLock().lock();
		roomBean.setHunum(roomBean.getHunum() + 1);
		roomBean.getLock().unlock();
		return roomBean.getHunum();
	}

	/**
	 * 胡检测(点炮)
	 * 
	 * @param userBean
	 * @param brand
	 * @param roomBean
	 * @param userid
	 * @return
	 */
	public int End_Hu(UserBean userBean, int brand, RoomBean roomBean, int userid) {
        //将点炮牌放入玩家手里
	    //userBean.getBrands().add(brand);
		// 检测胡牌人数(不包含点炮人自己)
		roomBean.IS_HU(userBean, brand, roomBean, userid);
		//userBean.Remove_Brands(brand);
		//返回胡牌状态
		return userBean.getHu_type();
	}

	/**
	 * 胡检测(自摸)
	 *
	 * @param userBean
	 * @param roomBean
	 * @return
	 */

	public int End_Hu_This(UserBean userBean, RoomBean roomBean,int brand) {
		return Mahjong_Util.mahjong_Util.IS_Victory(userBean.getBrands(), userBean,brand);
	}

	/**
	 * 结算(点炮)1
	 * @param userBean
	 * @param roomBean
	 * @param p_userid
	 * @param state
	 * @return
	 */
    public int End_Game(UserBean userBean, RoomBean roomBean, int p_userid, int state) {
    	roomBean.getLock().lock();
		// 设置自己已经同意结算
		userBean.setHu_state(state);
		if (roomBean.getState() == 4) {
			roomBean.getLock().unlock();
			return 500;// 已经结算
		}
		if (roomBean.getState() == 3) {
			roomBean.getLock().unlock();
			return 502;// 等待结算
		}
		// 胡牌人数大于1的时候需要检测
		if (roomBean.getHu_user_list().size() > 1) {
			for (int i=0;i<roomBean.getHu_user_list().size();i++){
				//当前用户的胡牌状态
				if (roomBean.getHu_user_list().get(i).getHu_state() == 0) {
					roomBean.getLock().unlock();
					return 501;// 等待胡牌
				}else if(roomBean.getHu_user_list().get(i).getHu_state()==2){
					roomBean.getHu_user_list().remove(roomBean.getHu_user_list().get(i));
					i--;
				}
			}
		}else{
			if(state==2){
				roomBean.getLock().unlock();
				return 503;//弃
			}
		}
		//弃胡
		if(roomBean.getHu_user_list().size()==0){
			return 503;
		}
		// 结算用户分数及总数
		int sum_num = 0;
		// 正在结算
		//roomBean.setState(3);
		for (UserBean user : roomBean.getHu_user_list()) {
			// 计算分数=房间底分*用户番数
			System.out.println("玩家"+user.getNickname()+"的番数为"+user.getPower_number());
			int fen = roomBean.getFen() * user.getPower_number();
			user.setNumber(user.getNumber() + fen);
			user.setDqnumber(fen+user.getDqnumber());
			sum_num += fen;
		}
		// 扣除失败者分数
		roomBean.getUserBean(p_userid).setNumber(roomBean.getUserBean(p_userid).getNumber() - sum_num);
		roomBean.getUserBean(p_userid).setDqnumber(-sum_num + roomBean.getUserBean(p_userid).getDqnumber());
		roomBean.getLock().unlock();
		return EndGame(roomBean,userBean);
    }

	/**
	 * 结算(自摸)
	 * 
	 * @param userBean
	 * @param roomBean
	 * @return
	 */
	public int End_Game_This(UserBean userBean, RoomBean roomBean) {
		// 计算分数=房间底分*用户番数
		int fen = roomBean.getFen() * userBean.getPower_number();
		userBean.setNumber(fen*(roomBean.getGame_userlist().size()-1-roomBean.getHunumList().size())+userBean.getNumber());
		userBean.setDqnumber(fen*(roomBean.getGame_userlist().size()-1-roomBean.getHunumList().size())+userBean.getDqnumber());
		for (UserBean user : roomBean.getGame_userlist()) {
			if (user.getUserid() != userBean.getUserid() && user.getIshu()==0) {
				// 扣除失败者分数
				user.setNumber(user.getNumber() - fen);
				user.setDqnumber(-fen + user.getDqnumber());
			}
		}
		return EndGame(roomBean, userBean);
	}

	/**
	 * 扣除开房房卡
	 * 
	 * @param roomBean
	 * @return
	 */
	private int EndGame(RoomBean roomBean, UserBean userBean) {
		int state = 0;
		// 判断是什么模式0个人
		if (roomBean.getClubid() == 0) {
			// 判断当前第一局则扣除房卡
			if (roomBean.getGame_number() == 1 && roomBean.getHunum()==1) {
                    // 扣除房主房卡
                    gameDao.UpdateUserDiamond(roomBean.getHouseid(), roomBean.getRoom_card(), 0);
			}
		} else {
			// 俱乐部模式扣除俱乐部房卡
			if (roomBean.getGame_number() == 1 && roomBean.getHunum()==1) {
				clubDao.Update_Club_Money(roomBean.getClubid(), roomBean.getRoom_card(), 0);
			}
		}
		// 更改结算状态
		//roomBean.setState(4);
		return state;
	}

	public void addRecord(RoomBean roomBean) {
		// 记录战绩
		gameDao.addPK_Record(roomBean);
	}

	/**
	 * 开始游戏
	 * 
	 * @param roomBean
	 * @return
	 */
	public int StartGame(RoomBean roomBean) {
		// 初始化
		roomBean.Initialization();
		// 房间状态
		roomBean.setState(2);
		// 不流局才选庄
		if (roomBean.getFlow() == 0) {
			// 选庄
			roomBean.Select_Banker();
		}
		// 发牌
		roomBean.Deal();
		return 0;
	}

	/**
	 * 出牌
	 * 
	 * @param roomBean
	 * @param outbrand
	 * @param userBean
	 * @return
	 */
	public int OutBrand(RoomBean roomBean, int outbrand, UserBean userBean, Map<String, Object> returnMap) {
		// 计算是否有人可碰
		for (UserBean user : roomBean.getGame_userlist()) {
			// 是自己则跳过
			if (user.getUserid() == userBean.getUserid())
				continue;

			if(user.getNoany().contains(mahjong_Util.getBrand_Value(outbrand))){//一轮不能碰
				continue;
			}
			if(user.getIshu()!=0){
				continue;
			}
			Map<String, Object> userMap = new HashMap<String, Object>();
			// 检测碰/杠
			int[] user_bump = mahjong_Util.IS_Bump(user.getBrands(), outbrand);
			//可碰牌玩家
			user.getUser_Custom("userid", userMap);
			// 碰/杠的牌
			userMap.put("bump", user_bump);
			if (user_bump != null) {
				// 判断房间是否定缺
				if (roomBean.getAssured_brand()==1){
					ArrayList<Integer> bumpList = new ArrayList<>();
					for (int i = 0; i <user_bump.length; i++) {
						if (user_bump[i]!=-1){
                            int brandValue = mahjong_Util.getBrand_Value(user_bump[i]);
                            bumpList.add(brandValue);
						}
					}
					//获取缺牌类型
					int lack_type = user.getLack_type();
					if (lack_type==1){
						ArrayList<Integer> list = new ArrayList<>();
						for (int i = 0; i < 9; i++) {//0-8  9-17   18-26
							list.add(i);
						}
						for (Integer bump : bumpList) {
							for (Integer bumps : list) {
								if (bump==bumps){
                                    // 出的牌
                                    returnMap.put("outbrand", outbrand);
                                    // 出牌人id
                                    returnMap.put("out_userid", userBean.getUserid());
                                    // 出牌
                                    OutBrand(userBean, outbrand);
									return  300;
								}
							}
						}
					}
					if (lack_type==2){
						ArrayList<Integer> list = new ArrayList<>();
						for (int i = 9; i < 18; i++) {//0-8  9-17   18-26
							list.add(i);
						}
						for (Integer bump : bumpList) {
							for (Integer bumps : list) {
								if (bump==bumps){
                                    // 出的牌
                                    returnMap.put("outbrand", outbrand);
                                    // 出牌人id
                                    returnMap.put("out_userid", userBean.getUserid());
                                    // 出牌
                                    OutBrand(userBean, outbrand);
									return  300;
								}
							}
						}
					}
					if (lack_type==3){
						ArrayList<Integer> list = new ArrayList<>();
						for (int i = 18; i < 27; i++) {//0-8  9-17   18-26
							list.add(i);
						}
						for (Integer bump : bumpList) {
							for (Integer bumps : list) {
								if (bump==bumps){
                                    // 出的牌
                                    returnMap.put("outbrand", outbrand);
                                    // 出牌人id
                                    returnMap.put("out_userid", userBean.getUserid());
                                    // 出牌
                                    OutBrand(userBean, outbrand);
									return  300;
								}
							}
						}
					}
				}
				returnMap.put("bump", userMap);
				roomBean.setNextMap(userMap);
				break;

			} else {
				roomBean.getNextMap().clear();
			}
		}
		// 出牌
		OutBrand(userBean, outbrand);
		if (returnMap.get("bump") == null) {
			// 需要摸牌
			return 300;
		}
		// 有碰
		return 0;
	}

	/**
	 * 碰牌
	 * 
	 * @param userBean
	 * @param userid
	 * @param brand
	 * @param roomBean
	 * @return
	 */
	public int[] Bump_Brand(UserBean userBean, int userid, int brand, RoomBean roomBean) {
		int[] brands = new int[2];
		// 获取对方用户
		UserBean user = roomBean.getUserBean(userid);
		// 删除出牌用户的牌
		user.Remove_Brands_Out(brand);
		// 将牌放入碰牌用户
		userBean.getBump_brands().add(brand);
		// 删除碰牌用户手里
		brand = mahjong_Util.getBrand_Value(brand);
		int count = 0;
		for (int i = 0; i < userBean.getBrands().size(); i++) {
			if (brand == mahjong_Util.getBrand_Value(userBean.getBrands().get(i))) {
				userBean.getBump_brands().add(userBean.getBrands().get(i));
				brands[count] = userBean.getBrands().get(i);
				userBean.Remove_Brands(userBean.getBrands().get(i));
				i--;
				count++;
				if (count == 2) {
					break;
				}
			}
		}
		// 设置当前操作用户为自己
		roomBean.setEnd_userid(userBean.getUserid());
		return brands;
	}

	/**
	 * 明杠（手中3张）
	 * 
	 * @param userBean
	 * @param userid
	 * @param brand
	 * @param roomBean
	 * @return
	 */
	public int Show_Bar(UserBean userBean, int userid, int brand, RoomBean roomBean) {
		// 获取出牌用户
		UserBean user = roomBean.getUserBean(userid);
		// 删除出牌用户出得牌
		user.Remove_Brands(brand);
		// 将牌放入自己明杠牌集合
		userBean.getShow_brands().add(brand);
		// 删除碰牌用户手里
		brand = mahjong_Util.getBrand_Value(brand);
		for (int i = 0; i < userBean.getBrands().size(); i++) {
			if (brand == mahjong_Util.getBrand_Value(userBean.getBrands().get(i))) {
				// 将牌放入自己明杠牌集合
				userBean.getShow_brands().add(userBean.getBrands().get(i));
				// 删除手中得牌
				userBean.Remove_Brands(userBean.getBrands().get(i));
				i--;
			}
		}
		// 设置当前操作用户为自己
		roomBean.setEnd_userid(userBean.getUserid());
		return 0;
	}

	/**
	 * 补杠（碰3张）
	 * 
	 * @param userBean
	 * @param brand
	 * @param roomBean
	 * @return
	 */
	public int Repair_Bar_Bump(UserBean userBean, int brand, RoomBean roomBean) {
		brand = mahjong_Util.getBrand_Value(brand);
		for (int i = 0; i < userBean.getBrands().size(); i++) {
			if (brand == mahjong_Util.getBrand_Value(userBean.getBrands().get(i))) {
				// 将牌放入自己得明杠牌集合
				userBean.getShow_brands().add(userBean.getBrands().get(i));
				// 删除用户手里得牌
				userBean.Remove_Brands(userBean.getBrands().get(i));
				i--;
			}
		}
		// 删除碰的牌
		for (int i = 0; i < userBean.getBump_brands().size(); i++) {
			if (brand == mahjong_Util.getBrand_Value(userBean.getBump_brands().get(i))) {
				// 将牌放入自己得明杠牌集合
				userBean.getShow_brands().add(userBean.getBump_brands().get(i));
				// 删除用户手里碰得牌
				userBean.Remove_Brands_Bump(userBean.getBump_brands().get(i));
				i--;
			}
		}
		// 设置当前操作用户为自己
		roomBean.setEnd_userid(userBean.getUserid());
		return 0;
	}

	/**
	 * 杠牌（暗）（手中4张牌）
	 * 
	 * @param userBean
	 * @param brand
	 * @param roomBean
	 * @return
	 */
	public int[] Hide_Bar(UserBean userBean, int brand, RoomBean roomBean) {
		int[] brands = new int[4];
		int count = 0;
		brand = mahjong_Util.getBrand_Value(brand);
		for (int i = 0; i < userBean.getBrands().size(); i++) {
			if (brand == mahjong_Util.getBrand_Value(userBean.getBrands().get(i))) {
				brands[count] = userBean.getBrands().get(i);
				count++;
				// 将牌放入自己得暗杠牌集合
				userBean.getHide_brands().add(userBean.getBrands().get(i));
				// 删除用户手里得牌
				userBean.Remove_Brands(userBean.getBrands().get(i));
				i--;
			}
		}
		return brands;
	}

	/**
	 * 出牌
	 * 
	 * @param userBean
	 * @param index
	 */
	public void OutBrand(UserBean userBean, int index) {
		if(!userBean.getOut_brands().contains(index)){
			userBean.getOut_brands().add(index);
		}
		if(userBean.getBrands().contains(index)){
			userBean.Remove_Brands(index);
		}
	}

	/**
	 * 检测是否重复加入
	 * 
	 * @param userBean
	 * @return
	 */
	public boolean ISMatching(String roomno, UserBean userBean) {
		RoomBean roomBean = Public_State.PKMap.get(roomno);
		for (UserBean user : roomBean.getGame_userlist()) {
			if (user.getUserid() == userBean.getUserid()) {
				return true;
			}
		}
		return false;
	}

	/***
	 * 发起/同意/解散房间
	 * 
	 * @param userBean
	 * @param roomBean
	 * @return
	 */
	public int Exit_GameUser(UserBean userBean, RoomBean roomBean) {
		roomBean.getLock().lock();
		// 不同意
		if (userBean.getExit_game() == 2) {
			roomBean.setExit_game(0);
			for (UserBean user : roomBean.getGame_userlist()) {
				user.setExit_game(0);
			}
			roomBean.getLock().unlock();
			// 取消解散
			return 303;
		} else {
			// 第一次发起
			if (roomBean.getExit_game() == 0) {
				roomBean.setExit_game(1);
				//開啟解散倒計時
                //roomBean.getExit_time().start();
				roomBean.getLock().unlock();
				// 发起解散
				return 301;
			}
			// 检测是否都同意解散
			for (UserBean user : roomBean.getGame_userlist()) {
				// 只要有一个用户未操作都返回同意
				if (user.getExit_game() == 0) {
					// user.setExit_game(1);
					roomBean.getLock().unlock();
					return 302;
				}
			}
			roomBean.getLock().unlock();
            roomBean.setExit_game(0);
			return 304;
		}
	}

	/**
	 * 换三張
 	 * @param roomBean
	 */
	public void ExchangeBrand(RoomBean roomBean) {
		List<UserBean> userlist = roomBean.getGame_userlist();
		for (UserBean user : userlist) {
			user.setExchange_state(1);
			// 玩家手里的牌
			List<Integer> brands = user.getBrands();
			// 玩家手里待交换的牌
			List<Integer> exchange_brand = user.getExchange_brands();
			//删除玩家手中要交换的牌
            brands.removeAll(exchange_brand);
		}
		// 2人房
		if (roomBean.getMax_person() == 2) {
			List<Integer> exchange_brands = userlist.get(0).getExchange_brands();
            for (Integer integer : exchange_brands) {
				userlist.get(1).getBrands().add(integer);
                userlist.get(1).getReceive_brands().add(integer);
            }
			List<Integer> exchange_brands2 = userlist.get(1).getExchange_brands();
			for (Integer integer : exchange_brands2) {
			    userlist.get(0).getBrands().add(integer);
                userlist.get(0).getReceive_brands().add(integer);
            }
        } else {
			// 4人房
			List<Integer> exchange_brand = userlist.get(0).getExchange_brands();
			for (Integer integer : exchange_brand) {
				userlist.get(2).getBrands().add(integer);
                userlist.get(2).getReceive_brands().add(integer);
            }
			List<Integer> exchange_brand3 = userlist.get(2).getExchange_brands();
			for (Integer integer : exchange_brand3) {
				userlist.get(0).getBrands().add(integer);
                userlist.get(0).getReceive_brands().add(integer);
			}
			List<Integer> exchange_brands = userlist.get(1).getExchange_brands();
			for (Integer integer : exchange_brands) {
				userlist.get(3).getBrands().add(integer);
                userlist.get(3).getReceive_brands().add(integer);
			}
			List<Integer> exchange_brand2 = userlist.get(3).getExchange_brands();
			for (Integer integer : exchange_brand2) {
				userlist.get(1).getBrands().add(integer);
                userlist.get(1).getReceive_brands().add(integer);
			}
		}
		for (UserBean userBean : userlist) {
			Collections.sort(userBean.getBrands());
        }
	}

	/**
	 * 定缺牌型
	 * 
	 * @param lack_type
	 * @param userBean
	 */
	public List<Integer> lackType(int lack_type, UserBean userBean) {
		// 设置定缺类型
		userBean.setLack_type(lack_type);
		// 更改定缺状态 已定缺
		userBean.setLack_state(1);
		// 玩家手里的牌
		List<Integer> brandsList = userBean.getBrands();
		// 万 1-9
		if (lack_type == 1) {
			List<Integer> wanlist = new ArrayList<Integer>();
			for (int i = 0; i <= 8; i++) {
				wanlist.add(i);
			}
			for (int i = 34; i <= 42; i++) {
				wanlist.add(i);
			}
			for (int i = 68; i <= 76; i++) {
				wanlist.add(i);
			}
			for (int i = 102; i <= 110; i++) {
				wanlist.add(i);
			}
			// 拆分缺牌
			return lackBrands(brandsList, wanlist);
			// 筒 1-9
		} else if (lack_type == 2) {
			List<Integer> tonglist = new ArrayList<Integer>();
			for (int i = 9; i <= 17; i++) {
				tonglist.add(i);
			}
			for (int i = 43; i <= 51; i++) {
				tonglist.add(i);
			}
			for (int i = 77; i <= 85; i++) {
				tonglist.add(i);
			}
			for (int i = 111; i <= 119; i++) {
				tonglist.add(i);
			}
			// 拆分缺牌
			return lackBrands(brandsList, tonglist);
		} else {
			// 条 1-9
			List<Integer> tiaolist = new ArrayList<Integer>();
			for (int i = 18; i <= 26; i++) {
				tiaolist.add(i);
			}
			for (int i = 52; i <= 60; i++) {
				tiaolist.add(i);
			}
			for (int i = 86; i <= 94; i++) {
				tiaolist.add(i);
			}
			for (int i = 120; i <= 128; i++) {
				tiaolist.add(i);
			}
			// 拆分缺牌
			return lackBrands(brandsList, tiaolist);
		}
	}

    /**
     * 拆分缺牌
     * @param brandsList  用户手里的牌
     * @param list 缺牌
     * @return
     */
	private List<Integer> lackBrands(List<Integer> brandsList, List<Integer> list) {
		List<Integer> cardslist = new ArrayList<Integer>();
		for (int j = 0; j < brandsList.size(); j++) {
			for (Integer i : list) {
				if (brandsList.get(j) == i) {
					cardslist.add(brandsList.get(j));
				}
			}
		}
        // 删除缺牌
        brandsList.removeAll(cardslist);
		// 缺牌非缺拆分完毕 重新放进玩家手中
		for (Integer i : cardslist) {
			brandsList.add(i);
		}
		return cardslist;
	}

	//添加房间记录
    public void addPkRoom(RoomBean roomBean) {
		gameDao.add_PK_Room(roomBean);
    }

	/**
	 * 是否海底炮
	 * @param roomBean
	 * @param p_userid
	 */
	public void isHaidipao(RoomBean roomBean, int p_userid) {
		List<UserBean> game_userlist = roomBean.getGame_userlist();
		for (UserBean userBean : game_userlist) {
			if (userBean.getUserid()==p_userid){
				userBean.setPower(5);
				userBean.getRecordMsgList().add("海底炮");
			}
		}
	}
}
