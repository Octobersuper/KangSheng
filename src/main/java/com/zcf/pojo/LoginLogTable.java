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
@TableName("login_log_table")
public class LoginLogTable extends Model<LoginLogTable> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "logid", type = IdType.AUTO)
    private Integer logid;
    private Integer backuserid;
    private String date;
    private String IP;


    public Integer getLogid() {
        return logid;
    }

    public void setLogid(Integer logid) {
        this.logid = logid;
    }

    public Integer getBackuserid() {
        return backuserid;
    }

    public void setBackuserid(Integer backuserid) {
        this.backuserid = backuserid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    @Override
    protected Serializable pkVal() {
        return this.logid;
    }

    @Override
    public String toString() {
        return "LoginLogTable{" +
        "logid=" + logid +
        ", backuserid=" + backuserid +
        ", date=" + date +
        ", IP=" + IP +
        "}";
    }
}
