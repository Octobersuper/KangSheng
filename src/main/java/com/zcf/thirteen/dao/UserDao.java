/**
 * 
 */
package com.zcf.thirteen.dao;

import java.sql.SQLException;
import java.util.Arrays;

import com.zcf.mahjong.util.AES;
import com.zcf.thirteen.bean.T_RoomBean;
import com.zcf.thirteen.bean.T_UserBean;
import com.zcf.thirteen.util.BaseDao;
import com.zcf.thirteen.util.UtilClass;

/**
 * @author guolele
 * @date 2019年2月20日 下午4:59:43
 * 
 */
public class UserDao {
	private BaseDao baseDao = null;
	public UserDao(BaseDao baseDao){
		this.baseDao = baseDao;
	}
	/**
	 * 查询用户 
	 */
	public T_UserBean getUser(int id){
    	String sql = "select * FROM user_table WHERE userid = ?";
    	baseDao.executeAll(sql, new Object[]{id});
		try {
			if(baseDao.resultSet.next()){
				return (T_UserBean) UtilClass.utilClass.getSqlBean(T_UserBean.class,"/sql.properties", baseDao.resultSet, "sql_getUser");
			}
		} catch (SQLException e) {
    		System.out.println(e.getMessage());
		}
		return null;
    }
	/**
	 * 查詢金額
	 *
	 * @return
	 * @throws
	 */
	public int getMoney(int id) {
		String sql = "select money from user_table where userid=?";
		baseDao.executeAll(sql, new Object[] { id });
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getInt("money");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 获得牌友圈的参数
	 *
	 * @param circlenumber
	 * @return
	 */
	public String getRules(int circlenumber) {
		String sql = "SELECT rules from game_card_circle where circlenumber=?";
		baseDao.executeAll(sql, new Object[]{circlenumber});
		try {
			if (baseDao.resultSet.next()) {
				return baseDao.resultSet.getString("rules");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "-1";
	}
	
	/**
	 * 查询作弊牌型
	 * @param fId    
	 * @throws
	 */
	public String getCheat(Long fId) {
		String sql = "select cards from template_card where p_id=?";
		return baseDao.executeUpdate(sql, new Object[]{fId});
	}

	public int updateMoney(int winnum,int userid) {
		String sql = "update user_table SET money=money+? WHERE userid=?";
		String string = baseDao.executeUpdate(sql, new Object[] { winnum ,userid});
		if ("success".equals(string)) {
			return 0;
		}
		return -1;
	}

	/**
	 * 添加每局用户战绩
	 *
	 * @param roomBean
	 * @return
	 */
	public int addPK_Record(T_RoomBean roomBean,int type,int room_type) {
		if(roomBean.getGame_number()==1){
			//插入房间信息
			add_PK_Room(roomBean,type,room_type);
		}
		String sql = "insert into pk_record_table values(null,?,?,?,?,?,?,?,?)";
		for (T_UserBean userBean : roomBean.getGame_userList()) {
			baseDao.executeUpdate(sql, new Object[]{
					userBean.getUserid(),
					userBean.getWinnum(),
					roomBean.getRoom_number(),
					Arrays.toString(userBean.getUpBrand()),
					Arrays.toString(userBean.getMiddleBrand()),
					Arrays.toString(userBean.getBelowBrand()),
					Arrays.toString(userBean.getBrand()),
					roomBean.getGame_number()
			});
		}
		return 0;
	}
	/**
	 * 插入房间信息
	 *
	 * @param roomBean
	 * @return
	 */
	public int add_PK_Room(T_RoomBean roomBean,int type,int room_type) {
		String sql = "insert into pk_table values(null,?,?,NOW(),?)";
		String state = baseDao.executeUpdate(sql, new Object[]{
				roomBean.getRoom_number(),type,room_type
		});
		if ("success".equals(state)) {
			sql = "SELECT LAST_INSERT_ID() as id";
			baseDao.executeAll(sql, null);
			try {
				if (baseDao.resultSet.next()) {
					return baseDao.resultSet.getInt("id");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
}
