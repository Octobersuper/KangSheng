/**
 * 
 */
package com.zcf.thirteen.dao;

import com.zcf.thirteen.bean.T_RoomBean;
import com.zcf.thirteen.util.BaseDao;

/**
 * @author guolele
 * @date 2019年2月20日 下午3:11:28
 * 
 */
public class GameDao {

	private BaseDao baseDao;
	
	public GameDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

/*	*//**
	 * 查询用户金币
	 * @param id
	 * @return    
	 * @throws
	 *//*
	public int getUserMoney(int id) {
		String sql = "select money from zcf_user.user_table where userid=?";
		baseDao.executeAll(sql, new Object[]{id});
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("money");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}*/
	
	/**
	 * 扣除用户金币
	 * @return 
	 * 
	 * @param id
	 * @param
	 * @param
	 * @throws
	 */
	public String UpdateUserMoney(int id, int money, int type) {
		String sql = "update user_table set money = money" + (type == 0 ?'-':'+') +"? where userid = ?";
		return baseDao.executeUpdate(sql, new Object[]{money,id});
	}
	
	/**
	 * 插入房间表
	 *
	 * @param record
	 * @return    
	 * @throws
	 */
	/*public String SaveRoomInfo(Record record){
		String sql = "insert into record(roomMoney,roomNum,uid,createTime,recordType)values(?,?,?,NOW(),0) ";
		return baseDao.executeUpdate(sql, new Object[]{record.getFen(),record.getRoom_number(),record.getRoom_id()});
	}*/
	
	/**
	 * 插入房间表
	 *
	 * @param
	 * @return    
	 * @throws
	 */
	public String SaveRoomsInfo(T_RoomBean rb){
		String sql = "insert into rooms(c_id,room_num,create_time,type)values(?,?,NOW(),0) ";
		return baseDao.executeUpdate(sql, new Object[]{rb.getRoom_number()});
	}

	
	/**
	 * 插入战绩详情
	 * @param record    
	 * @throws
	 */
	/*public String savaRecordInfo(Record record) {
		String sql = "insert into record_info(roomNum,gameId,uid,phone,username,money,cardType,type,createTime)values(?,?,?,?,?,?,?,?,NOW()) ";
		return baseDao.executeUpdate(sql, new Object[]{record.getRoom_number(),record.getGame_number(),record.getUserid(),record.getPhone(),record.getUsername(),record.getMoney(),record.getBrand_type(),record.getType()});
	}*/

	
}
