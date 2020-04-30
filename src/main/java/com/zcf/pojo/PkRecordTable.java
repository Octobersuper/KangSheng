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
@TableName("pk_record_table")
public class PkRecordTable extends Model<PkRecordTable> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "recordid", type = IdType.AUTO)
    private Integer recordid;
    private Integer userid;
    /**
     * 分数
     */
    private Integer number;
    /**
     * 游戏小局数
     */
    private Integer game_number;
    /**
     * 1=庄家
     */
    private Integer banker;
    private Integer roomid;


    public Integer getRecordid() {
        return recordid;
    }

    public void setRecordid(Integer recordid) {
        this.recordid = recordid;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getGame_number() {
        return game_number;
    }

    public void setGame_number(Integer game_number) {
        this.game_number = game_number;
    }

    public Integer getBanker() {
        return banker;
    }

    public void setBanker(Integer banker) {
        this.banker = banker;
    }

    public Integer getRoomid() {
        return roomid;
    }

    public void setRoomid(Integer roomid) {
        this.roomid = roomid;
    }

    @Override
    protected Serializable pkVal() {
        return this.recordid;
    }

    @Override
    public String toString() {
        return "PkRecordTable{" +
        "recordid=" + recordid +
        ", userid=" + userid +
        ", number=" + number +
        ", game_number=" + game_number +
        ", banker=" + banker +
        ", roomid=" + roomid +
        "}";
    }
}
