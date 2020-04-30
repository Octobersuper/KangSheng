package com.zcf.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.zcf.mahjong.json.Body;
import com.zcf.mahjong.util.LayuiJson;
import com.zcf.mapper.SignDaysMapper;
import com.zcf.pojo.Sign;
import com.zcf.pojo.SignDays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.Calendar;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ZhaoQi
 * @since 2019-03-25
 */
@CrossOrigin
@Controller
@RequestMapping("/signDays")
public class SignDaysController {

    @Autowired
    SignDaysMapper sm;

    @GetMapping("getDays")
    @ResponseBody
    public LayuiJson getDays(){
        EntityWrapper<SignDays> w = new EntityWrapper<>();
        List<SignDays> signDays = sm.selectList(w);

        LayuiJson lj = new LayuiJson();
        lj.setData(signDays);
        lj.setMsg("yes");
        lj.setCode(0);
        lj.setCount(7);

        return lj;
    }

    @PostMapping("update")
    @ResponseBody
    public Body update(SignDays signDays){
        Integer id = sm.updateById(signDays);
        if (id != 0) {
            return Body.BODY_200;
        }
        return Body.BODY_451;
    }
}

