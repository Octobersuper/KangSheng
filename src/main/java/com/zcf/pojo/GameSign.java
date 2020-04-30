package com.zcf.pojo;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.activerecord.Model;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author ZhaoQi
 * @since 2020-04-03
 */
public class GameSign extends Model<GameSign> {

    private static final long serialVersionUID = 1L;

    /**
     * 每日签到
     */
    @TableId(value = "signid", type = IdType.AUTO)
    private Integer signid;
    /**
     * 第几天
     */
    private Integer num;
    /**
     * 第几天
     */
    private String value;


    public Integer getSignid() {
        return signid;
    }

    public void setSignid(Integer signid) {
        this.signid = signid;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    protected Serializable pkVal() {
        return this.signid;
    }

    @Override
    public String toString() {
        return "GameSign{" +
        "signid=" + signid +
        ", num=" + num +
        ", value=" + value +
        "}";
    }
}
