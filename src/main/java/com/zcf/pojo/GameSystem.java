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
@TableName("game_system")
public class GameSystem extends Model<GameSystem> {

    private static final long serialVersionUID = 1L;

    /**
     * 系统管理员表
     */
    @TableId(value = "backuserid", type = IdType.AUTO)
    private Integer backuserid;
    /**
     * 管理员名称
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
     * 添加时间
     */
    private String date;
    /**
     * 角色 超级管理员
     */
    private Integer role;
    /**
     * 游戏统计
     */
    private Integer menuone;
    /**
     * 金钱统计
     */
    private Integer menutwo;
    /**
     * 用户列表
     */
    private Integer menuthree;
    /**
     * 充值提现记录
     */
    private Integer menufour;
    /**
     * 提现申请
     */
    private Integer menufive;
    /**
     * 房间列表
     */
    private Integer menusix;
    /**
     * 牌友圈列表
     */
    private Integer menuseven;
    /**
     * 金币充值配置
     */
    private Integer menueight;
    /**
     * 钻石充值配置
     */
    private Integer menunine;
    /**
     * 轮播图管理
     */
    private Integer menuten;
    /**
     * 客服管理
     */
    private Integer eleven;
    /**
     * 消耗钻石数量设置
     */
    private Integer twelve;
    /**
     * 分享管理
     */
    private Integer thirteen;
    /**
     * 公告管理
     */
    private Integer fourteen;
    /**
     * 系统消息
     */
    private Integer fifteen;
    /**
     * 签到管理
     */
    private Integer sixteen;
    /**
     * 每日任务
     */
    private Integer seventeen;
    /**
     * 幸运转盘
     */
    private Integer eighteen;
    /**
     * 兑奖管理
     */
    private Integer nineteen;
    /**
     * 免房费时间设置
     */
    private Integer twenty;
    /**
     * 后台管理员列表
     */
    private Integer twentyone;
    /**
     * 后台权限列表
     */
    private Integer twentytwo;
    /**
     * 后台登录日志
     */
    private Integer twentythree;
    /**
     * 游戏规则
     */
    private Integer twentyfour;


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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getMenuone() {
        return menuone;
    }

    public void setMenuone(Integer menuone) {
        this.menuone = menuone;
    }

    public Integer getMenutwo() {
        return menutwo;
    }

    public void setMenutwo(Integer menutwo) {
        this.menutwo = menutwo;
    }

    public Integer getMenuthree() {
        return menuthree;
    }

    public void setMenuthree(Integer menuthree) {
        this.menuthree = menuthree;
    }

    public Integer getMenufour() {
        return menufour;
    }

    public void setMenufour(Integer menufour) {
        this.menufour = menufour;
    }

    public Integer getMenufive() {
        return menufive;
    }

    public void setMenufive(Integer menufive) {
        this.menufive = menufive;
    }

    public Integer getMenusix() {
        return menusix;
    }

    public void setMenusix(Integer menusix) {
        this.menusix = menusix;
    }

    public Integer getMenuseven() {
        return menuseven;
    }

    public void setMenuseven(Integer menuseven) {
        this.menuseven = menuseven;
    }

    public Integer getMenueight() {
        return menueight;
    }

    public void setMenueight(Integer menueight) {
        this.menueight = menueight;
    }

    public Integer getMenunine() {
        return menunine;
    }

    public void setMenunine(Integer menunine) {
        this.menunine = menunine;
    }

    public Integer getMenuten() {
        return menuten;
    }

    public void setMenuten(Integer menuten) {
        this.menuten = menuten;
    }

    public Integer getEleven() {
        return eleven;
    }

    public void setEleven(Integer eleven) {
        this.eleven = eleven;
    }

    public Integer getTwelve() {
        return twelve;
    }

    public void setTwelve(Integer twelve) {
        this.twelve = twelve;
    }

    public Integer getThirteen() {
        return thirteen;
    }

    public void setThirteen(Integer thirteen) {
        this.thirteen = thirteen;
    }

    public Integer getFourteen() {
        return fourteen;
    }

    public void setFourteen(Integer fourteen) {
        this.fourteen = fourteen;
    }

    public Integer getFifteen() {
        return fifteen;
    }

    public void setFifteen(Integer fifteen) {
        this.fifteen = fifteen;
    }

    public Integer getSixteen() {
        return sixteen;
    }

    public void setSixteen(Integer sixteen) {
        this.sixteen = sixteen;
    }

    public Integer getSeventeen() {
        return seventeen;
    }

    public void setSeventeen(Integer seventeen) {
        this.seventeen = seventeen;
    }

    public Integer getEighteen() {
        return eighteen;
    }

    public void setEighteen(Integer eighteen) {
        this.eighteen = eighteen;
    }

    public Integer getNineteen() {
        return nineteen;
    }

    public void setNineteen(Integer nineteen) {
        this.nineteen = nineteen;
    }

    public Integer getTwenty() {
        return twenty;
    }

    public void setTwenty(Integer twenty) {
        this.twenty = twenty;
    }

    public Integer getTwentyone() {
        return twentyone;
    }

    public void setTwentyone(Integer twentyone) {
        this.twentyone = twentyone;
    }

    public Integer getTwentytwo() {
        return twentytwo;
    }

    public void setTwentytwo(Integer twentytwo) {
        this.twentytwo = twentytwo;
    }

    public Integer getTwentythree() {
        return twentythree;
    }

    public void setTwentythree(Integer twentythree) {
        this.twentythree = twentythree;
    }

    public Integer getTwentyfour() {
        return twentyfour;
    }

    public void setTwentyfour(Integer twentyfour) {
        this.twentyfour = twentyfour;
    }

    @Override
    protected Serializable pkVal() {
        return this.backuserid;
    }

    @Override
    public String toString() {
        return "GameSystem{" +
        "backuserid=" + backuserid +
        ", backname=" + backname +
        ", account=" + account +
        ", password=" + password +
        ", date=" + date +
        ", role=" + role +
        ", menuone=" + menuone +
        ", menutwo=" + menutwo +
        ", menuthree=" + menuthree +
        ", menufour=" + menufour +
        ", menufive=" + menufive +
        ", menusix=" + menusix +
        ", menuseven=" + menuseven +
        ", menueight=" + menueight +
        ", menunine=" + menunine +
        ", menuten=" + menuten +
        ", eleven=" + eleven +
        ", twelve=" + twelve +
        ", thirteen=" + thirteen +
        ", fourteen=" + fourteen +
        ", fifteen=" + fifteen +
        ", sixteen=" + sixteen +
        ", seventeen=" + seventeen +
        ", eighteen=" + eighteen +
        ", nineteen=" + nineteen +
        ", twenty=" + twenty +
        ", twentyone=" + twentyone +
        ", twentytwo=" + twentytwo +
        ", twentythree=" + twentythree +
        ", twentyfour=" + twentyfour +
        "}";
    }
}
