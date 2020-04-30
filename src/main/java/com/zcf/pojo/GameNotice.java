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
@TableName("game_notice")
public class GameNotice extends Model<GameNotice> {

    private static final long serialVersionUID = 1L;

    /**
     * 公告表
     */
    @TableId(value = "noticeid", type = IdType.AUTO)
    private Integer noticeid;
    /**
     * 公告内容
     */
    private String value;


    public Integer getNoticeid() {
        return noticeid;
    }

    public void setNoticeid(Integer noticeid) {
        this.noticeid = noticeid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    protected Serializable pkVal() {
        return this.noticeid;
    }

    @Override
    public String toString() {
        return "GameNotice{" +
        "noticeid=" + noticeid +
        ", value=" + value +
        "}";
    }
}
