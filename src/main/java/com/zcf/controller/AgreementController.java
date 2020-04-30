package com.zcf.controller;


import com.zcf.mahjong.json.Body;
import com.zcf.mapper.AgreementMapper;
import com.zcf.pojo.Agreement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ZhaoQi
 * @since 2020-04-01
 */
@RestController
@CrossOrigin
@RequestMapping("/agreement")
public class AgreementController {

    @Autowired
    AgreementMapper am;

    @GetMapping("get")
    public Body get(){
        return Body.newInstance(am.selectById(1));
    }

    @PostMapping("put")
    public Body update(Agreement agreement){
        return Body.newInstance(am.updateById(agreement));
    }
}

