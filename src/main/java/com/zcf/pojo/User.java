package com.zcf.pojo;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
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
@TableName("user_table")
public class User extends Model<User> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(value = "userid", type = IdType.AUTO)
    private Long userid;
    private String openid;
    /**
     * 头像
     */
    private String avatarurl;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 手机号
     */
    private String phone;
    private String password;
    /**
     * 创建时间
     */
    private Date createtime;
    /**
     * 金币
     */
    private Integer money;
    /**
     * 房卡
     */
    private Integer diamond;
    /**
     * 账号状态 0正常 1冻结
     */
    private Integer state;
    /**
     * 用户类型  0普通用户  1游客
     */
    private Integer type;
    /**
     * 是否已登录
     */
    @TableField("isLogin")
    private Integer isLogin;
    /**
     * 签名
     */
    private String remard;
    /**
     * 0 女  1男
     */
    private Integer sex;
    /**
     * 地址
     */
    private String address;
    private String ip;
    /**
     * 上级ID  0无上级
     */
    @TableField("fId")
    private Long fId;
    /**
     * 角色  0普通玩家   1推广员
     */
    private Integer role;
    /**
     * 平台标识
     */
    private Integer sdk;
    /**
     * 微信注册时间
     */
    private String date;
    /**
     * state=1时需要写入
     */
    private String statetext;
    /**
     * 6位邀请码
     */
    private String code;
    @TableField("isPay")
    private Integer isPay;
    private String text_1;
    private String text_2;
    private String text_3;
    private String text_4;
    private String text_5;
    private Integer number_1;
    private Integer number_2;
    private Integer number_3;
    private Integer number_4;
    private Integer number_5;
    /**
     * 用户归属
     */
    private Integer zcf_user;
    private Integer award;


    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getAvatarurl() {
        return avatarurl;
    }

    public void setAvatarurl(String avatarurl) {
        this.avatarurl = avatarurl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Integer getDiamond() {
        return diamond;
    }

    public void setDiamond(Integer diamond) {
        this.diamond = diamond;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(Integer isLogin) {
        this.isLogin = isLogin;
    }

    public String getRemard() {
        return remard;
    }

    public void setRemard(String remard) {
        this.remard = remard;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getfId() {
        return fId;
    }

    public void setfId(Long fId) {
        this.fId = fId;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getSdk() {
        return sdk;
    }

    public void setSdk(Integer sdk) {
        this.sdk = sdk;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatetext() {
        return statetext;
    }

    public void setStatetext(String statetext) {
        this.statetext = statetext;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getIsPay() {
        return isPay;
    }

    public void setIsPay(Integer isPay) {
        this.isPay = isPay;
    }

    public String getText_1() {
        return text_1;
    }

    public void setText_1(String text_1) {
        this.text_1 = text_1;
    }

    public String getText_2() {
        return text_2;
    }

    public void setText_2(String text_2) {
        this.text_2 = text_2;
    }

    public String getText_3() {
        return text_3;
    }

    public void setText_3(String text_3) {
        this.text_3 = text_3;
    }

    public String getText_4() {
        return text_4;
    }

    public void setText_4(String text_4) {
        this.text_4 = text_4;
    }

    public String getText_5() {
        return text_5;
    }

    public void setText_5(String text_5) {
        this.text_5 = text_5;
    }

    public Integer getNumber_1() {
        return number_1;
    }

    public void setNumber_1(Integer number_1) {
        this.number_1 = number_1;
    }

    public Integer getNumber_2() {
        return number_2;
    }

    public void setNumber_2(Integer number_2) {
        this.number_2 = number_2;
    }

    public Integer getNumber_3() {
        return number_3;
    }

    public void setNumber_3(Integer number_3) {
        this.number_3 = number_3;
    }

    public Integer getNumber_4() {
        return number_4;
    }

    public void setNumber_4(Integer number_4) {
        this.number_4 = number_4;
    }

    public Integer getNumber_5() {
        return number_5;
    }

    public void setNumber_5(Integer number_5) {
        this.number_5 = number_5;
    }

    public Integer getZcf_user() {
        return zcf_user;
    }

    public void setZcf_user(Integer zcf_user) {
        this.zcf_user = zcf_user;
    }

    public Integer getAward() {
        return award;
    }

    public void setAward(Integer award) {
        this.award = award;
    }

    @Override
    protected Serializable pkVal() {
        return this.userid;
    }

    @Override
    public String toString() {
        return "UserTable{" +
        "userid=" + userid +
        ", openid=" + openid +
        ", avatarurl=" + avatarurl +
        ", nickname=" + nickname +
        ", phone=" + phone +
        ", password=" + password +
        ", createtime=" + createtime +
        ", money=" + money +
        ", diamond=" + diamond +
        ", state=" + state +
        ", type=" + type +
        ", isLogin=" + isLogin +
        ", remard=" + remard +
        ", sex=" + sex +
        ", address=" + address +
        ", ip=" + ip +
        ", fId=" + fId +
        ", role=" + role +
        ", sdk=" + sdk +
        ", date=" + date +
        ", statetext=" + statetext +
        ", code=" + code +
        ", isPay=" + isPay +
        ", text_1=" + text_1 +
        ", text_2=" + text_2 +
        ", text_3=" + text_3 +
        ", text_4=" + text_4 +
        ", text_5=" + text_5 +
        ", number_1=" + number_1 +
        ", number_2=" + number_2 +
        ", number_3=" + number_3 +
        ", number_4=" + number_4 +
        ", number_5=" + number_5 +
        ", zcf_user=" + zcf_user +
        ", award=" + award +
        "}";
    }
}
