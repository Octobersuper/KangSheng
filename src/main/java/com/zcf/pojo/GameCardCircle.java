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
@TableName("game_card_circle")
public class GameCardCircle extends Model<GameCardCircle> {

    private static final long serialVersionUID = 1L;

    /**
     * 俱乐部表
     */
    @TableId(value = "circleid", type = IdType.AUTO)
    private Integer circleid;
    /**
     * 俱乐部名称
     */
    private String circlename;
    /**
     * 俱乐部编号
     */
    private String circlenumber;
    /**
     * 俱乐部创建时间
     */
    private String date;
    /**
     * 房卡数
     */
    private Integer diamond;
    /**
     * 创建人id
     */
    private Integer userid;
    /**
     * 最大人数
     */
    private Integer maxnum;


    public Integer getCircleid() {
        return circleid;
    }

    public void setCircleid(Integer circleid) {
        this.circleid = circleid;
    }

    public String getCirclename() {
        return circlename;
    }

    public void setCirclename(String circlename) {
        this.circlename = circlename;
    }

    public String getCirclenumber() {
        return circlenumber;
    }

    public void setCirclenumber(String circlenumber) {
        this.circlenumber = circlenumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getDiamond() {
        return diamond;
    }

    public void setDiamond(Integer diamond) {
        this.diamond = diamond;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getMaxnum() {
        return maxnum;
    }

    public void setMaxnum(Integer maxnum) {
        this.maxnum = maxnum;
    }

    @Override
    protected Serializable pkVal() {
        return this.circleid;
    }

    @Override
    public String toString() {
        return "GameCardCircle{" +
        "circleid=" + circleid +
        ", circlename=" + circlename +
        ", circlenumber=" + circlenumber +
        ", date=" + date +
        ", diamond=" + diamond +
        ", userid=" + userid +
        ", maxnum=" + maxnum +
        "}";
    }
}
