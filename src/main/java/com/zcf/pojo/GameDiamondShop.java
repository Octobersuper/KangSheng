package com.zcf.pojo;

import com.baomidou.mybatisplus.enums.IdType;
import java.math.BigDecimal;
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
@TableName("game_diamond_shop")
public class GameDiamondShop extends Model<GameDiamondShop> {

    private static final long serialVersionUID = 1L;

    /**
     * 所需金币
     */
    @TableId(value = "diamondid", type = IdType.AUTO)
    private Integer diamondid;
    private String diamondname;
    private String realvalue;
    private BigDecimal price;


    public Integer getDiamondid() {
        return diamondid;
    }

    public void setDiamondid(Integer diamondid) {
        this.diamondid = diamondid;
    }

    public String getDiamondname() {
        return diamondname;
    }

    public void setDiamondname(String diamondname) {
        this.diamondname = diamondname;
    }

    public String getRealvalue() {
        return realvalue;
    }

    public void setRealvalue(String realvalue) {
        this.realvalue = realvalue;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    protected Serializable pkVal() {
        return this.diamondid;
    }

    @Override
    public String toString() {
        return "GameDiamondShop{" +
        "diamondid=" + diamondid +
        ", diamondname=" + diamondname +
        ", realvalue=" + realvalue +
        ", price=" + price +
        "}";
    }
}
