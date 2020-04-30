package com.zcf.controller;


import com.zcf.mahjong.json.Body;
import com.zcf.pojo.Sign;
import com.zcf.service.impl.SignServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author liuwei
 * @since 2018-11-22
 */
@CrossOrigin
@Controller
@RequestMapping("/sign")
public class SignController{
    @Autowired
    SignServiceImpl ssi;

    /*
     *@Author:ZhaoQi
     *@methodName:签到
     *@Params:
     *@Description:
     *@Return:
     *@Date:2019/3/6
     */
    @PostMapping("sign")
    @ResponseBody
    public Body sign(Sign sign){
        return ssi.sign(sign);
    }

    /*
     *@Author:ZhaoQi
     *@methodName:获取签到
     *@Params:
     *@Description:
     *@Return:
     *@Date:2019/3/6
     */
    @PostMapping("getSign")
    @ResponseBody
    public Body getSign(Sign s){
        return ssi.getSign(s);
    }
}

