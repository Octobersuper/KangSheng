package com.zcf.controller;


import com.zcf.mahjong.json.Body;
import com.zcf.mapper.GameIntroduceMapper;
import com.zcf.pojo.GameIntroduce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ZhaoQi
 * @since 2020-04-03
 */
@RestController
@CrossOrigin
@RequestMapping("/gameIntroduce")
public class GameIntroduceController {

    @Autowired
    GameIntroduceMapper gm;

    @GetMapping
    public Body get(GameIntroduce gi){
        return Body.newInstance(gm.selectById(gi.getIntroduceid()));
    }

    @PutMapping
    public Body update(GameIntroduce gi){
        return Body.newInstance(gm.updateById(gi));
    }
}

