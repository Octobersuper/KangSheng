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
@TableName("pk_table")
public class PkTable extends Model<PkTable> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "pkid", type = IdType.AUTO)
    private Integer pkid;
    private String roomno;
    private String start_date;
    /**
     * 游戏人数
     */
    private Integer max_person;
    /**
     * 房主id
     */
    private Integer houseid;
    /**
     * 底分
     */
    private Integer fen;
    /**
     * 最大局数
     */
    private Integer max_number;
    /**
     * 回放记录
     */
    private String log;
    /**
     * 0=个人
     */
    private Integer clubid;
    private Integer game_number;


    public Integer getPkid() {
        return pkid;
    }

    public void setPkid(Integer pkid) {
        this.pkid = pkid;
    }

    public String getRoomno() {
        return roomno;
    }

    public void setRoomno(String roomno) {
        this.roomno = roomno;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public Integer getMax_person() {
        return max_person;
    }

    public void setMax_person(Integer max_person) {
        this.max_person = max_person;
    }

    public Integer getHouseid() {
        return houseid;
    }

    public void setHouseid(Integer houseid) {
        this.houseid = houseid;
    }

    public Integer getFen() {
        return fen;
    }

    public void setFen(Integer fen) {
        this.fen = fen;
    }

    public Integer getMax_number() {
        return max_number;
    }

    public void setMax_number(Integer max_number) {
        this.max_number = max_number;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Integer getClubid() {
        return clubid;
    }

    public void setClubid(Integer clubid) {
        this.clubid = clubid;
    }

    public Integer getGame_number() {
        return game_number;
    }

    public void setGame_number(Integer game_number) {
        this.game_number = game_number;
    }

    @Override
    protected Serializable pkVal() {
        return this.pkid;
    }

    @Override
    public String toString() {
        return "PkTable{" +
        "pkid=" + pkid +
        ", roomno=" + roomno +
        ", start_date=" + start_date +
        ", max_person=" + max_person +
        ", houseid=" + houseid +
        ", fen=" + fen +
        ", max_number=" + max_number +
        ", log=" + log +
        ", clubid=" + clubid +
        ", game_number=" + game_number +
        "}";
    }
}
