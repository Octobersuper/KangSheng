package com.zcf.mahjong.dao;

import com.zcf.mahjong.util.BaseDao;

import java.sql.SQLException;

public class ClubDao {
	private BaseDao baseDao;

	public ClubDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}
	/**
	 * 查询茶馆钻石数量
	 * @param circleid
	 * @return
	 */
	public int getClub_diamond(int circleid){
		String sql = "select diamond from game_card_circle where circlenumber=?";
		baseDao.executeAll(sql, new Object[]{circleid});
		try {
			if(baseDao.resultSet.next()){
				return baseDao.resultSet.getInt("diamond");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	/**
	 * 修改俱乐部钻石
	 * @param circleid
	 * @param diamond
	 * @param type
	 * @return
	 */
	public int Update_Club_Money(int circleid,int diamond,int type){
		String sql = "update game_card_circle set diamond=diamond"+(type==0?"-":"+")+"? where circlenumber=?";
		String state = baseDao.executeUpdate(sql, new Object[]{diamond,circleid});
		return state.equals("success")?0:-1;
	}
}
