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
@TableName("config_table")
public class ConfigTable extends Model<ConfigTable> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "configid", type = IdType.AUTO)
    private Integer configid;
    /**
     * 解散房间倒计时S
     */
    private Integer exit_time;
    /**
     * 4人房消耗房卡8局-12局
     */
    private String establish_four;
    private String establish_two;
    /**
     * 俱乐部的最大钻石数
     */
    private Integer card_diamond;
    /**
     * 俱乐部的最大上限
     */
    private Integer max_card;
    /**
     * 俱乐部创建人数上限
     */
    private Integer max_person;


    public Integer getConfigid() {
        return configid;
    }

    public void setConfigid(Integer configid) {
        this.configid = configid;
    }

    public Integer getExit_time() {
        return exit_time;
    }

    public void setExit_time(Integer exit_time) {
        this.exit_time = exit_time;
    }

    public String getEstablish_four() {
        return establish_four;
    }

    public void setEstablish_four(String establish_four) {
        this.establish_four = establish_four;
    }

    public String getEstablish_two() {
        return establish_two;
    }

    public void setEstablish_two(String establish_two) {
        this.establish_two = establish_two;
    }

    public Integer getCard_diamond() {
        return card_diamond;
    }

    public void setCard_diamond(Integer card_diamond) {
        this.card_diamond = card_diamond;
    }

    public Integer getMax_card() {
        return max_card;
    }

    public void setMax_card(Integer max_card) {
        this.max_card = max_card;
    }

    public Integer getMax_person() {
        return max_person;
    }

    public void setMax_person(Integer max_person) {
        this.max_person = max_person;
    }

    @Override
    protected Serializable pkVal() {
        return this.configid;
    }

    @Override
    public String toString() {
        return "ConfigTable{" +
        "configid=" + configid +
        ", exit_time=" + exit_time +
        ", establish_four=" + establish_four +
        ", establish_two=" + establish_two +
        ", card_diamond=" + card_diamond +
        ", max_card=" + max_card +
        ", max_person=" + max_person +
        "}";
    }
}
