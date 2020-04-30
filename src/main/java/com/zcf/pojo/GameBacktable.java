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
@TableName("game_backtable")
public class GameBacktable extends Model<GameBacktable> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "backuserid", type = IdType.AUTO)
    private Integer backuserid;
    /**
     * 昵称
     */
    private String backname;
    /**
     * 账号
     */
    private String account;
    /**
     * 密码
     */
    private String password;
    /**
     * 角色
     */
    private Integer role;


    public Integer getBackuserid() {
        return backuserid;
    }

    public void setBackuserid(Integer backuserid) {
        this.backuserid = backuserid;
    }

    public String getBackname() {
        return backname;
    }

    public void setBackname(String backname) {
        this.backname = backname;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    @Override
    protected Serializable pkVal() {
        return this.backuserid;
    }

    @Override
    public String toString() {
        return "GameBacktable{" +
        "backuserid=" + backuserid +
        ", backname=" + backname +
        ", account=" + account +
        ", password=" + password +
        ", role=" + role +
        "}";
    }
}
