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
public class GameIntroduce extends Model<GameIntroduce> {

    private static final long serialVersionUID = 1L;

    /**
     * 玩法介绍表
     */
    @TableId(value = "introduceid", type = IdType.AUTO)
    private Integer introduceid;
    /**
     * 详细介绍
     */
    private String value;


    public Integer getIntroduceid() {
        return introduceid;
    }

    public void setIntroduceid(Integer introduceid) {
        this.introduceid = introduceid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    protected Serializable pkVal() {
        return this.introduceid;
    }

    @Override
    public String toString() {
        return "GameIntroduce{" +
        "introduceid=" + introduceid +
        ", value=" + value +
        "}";
    }
}
