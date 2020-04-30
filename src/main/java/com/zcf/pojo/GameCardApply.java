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
@TableName("game_card_apply")
public class GameCardApply extends Model<GameCardApply> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "applyid", type = IdType.AUTO)
    private Integer applyid;
    /**
     * 用户id
     */
    private Integer userid;
    /**
     * 俱乐部账号
     */
    private Integer circlenumber;
    /**
     * 申请时间
     */
    private String date;
    /**
     * 状态 0 未审核 1审核通过 2 拒绝
     */
    private Integer state;


    public Integer getApplyid() {
        return applyid;
    }

    public void setApplyid(Integer applyid) {
        this.applyid = applyid;
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Override
    protected Serializable pkVal() {
        return this.applyid;
    }

    @Override
    public String toString() {
        return "GameCardApply{" +
        "applyid=" + applyid +
        ", userid=" + userid +
        ", circlenumber=" + circlenumber +
        ", date=" + date +
        ", state=" + state +
        "}";
    }
}
