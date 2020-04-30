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
@TableName("game_recharge")
public class GameRecharge extends Model<GameRecharge> {

    private static final long serialVersionUID = 1L;

    /**
     * 充值提现记录表
     */
    @TableId(value = "rechargeid", type = IdType.AUTO)
    private Integer rechargeid;
    /**
     * 玩家id
     */
    private String userid;
    /**
     * 充值金额
     */
    private Double money;
    /**
     * 操作时间
     */
    private String date;
    /**
     * 1手动增加 2手动减少 3线上充值
     */
    private Integer type;


    public Integer getRechargeid() {
        return rechargeid;
    }

    public void setRechargeid(Integer rechargeid) {
        this.rechargeid = rechargeid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    protected Serializable pkVal() {
        return this.rechargeid;
    }

    @Override
    public String toString() {
        return "GameRecharge{" +
        "rechargeid=" + rechargeid +
        ", userid=" + userid +
        ", money=" + money +
        ", date=" + date +
        ", type=" + type +
        "}";
    }
}
