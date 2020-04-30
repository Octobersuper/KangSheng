package com.zcf.pojo;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author ZhaoQi
 * @since 2020-01-09
 */
@TableName("game_card_user")
public class GameCardUser extends Model<GameCardUser> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户与俱乐部关联表
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 用户id
     */
    private Integer userid;
    /**
     * 俱乐部编号
     */
    private Integer circlenumber;
    /**
     * 加入时间
     */
    private String date;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getCirclenumber() {
        return circlenumber;
    }

    public void setCirclenumber(Integer circlenumber) {
        this.circlenumber = circlenumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "GameCardUser{" +
        "id=" + id +
        ", userid=" + userid +
        ", circlenumber=" + circlenumber +
        ", date=" + date +
        "}";
    }
}
