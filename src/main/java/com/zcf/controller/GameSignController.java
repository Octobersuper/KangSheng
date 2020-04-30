package com.zcf.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.zcf.mahjong.json.Body;
import com.zcf.mahjong.util.LayuiJson;
import com.zcf.mapper.GameSignMapper;
import com.zcf.pojo.GameSign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

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
@RequestMapping("/gameSign")
public class GameSignController {

    @Autowired
    GameSignMapper gm;

    @GetMapping
    public LayuiJson getDays(){
        EntityWrapper<GameSign> w = new EntityWrapper<>();
        List<GameSign> signDays = gm.selectList(w);

        LayuiJson lj = new LayuiJson();
        lj.setData(signDays);
        lj.setMsg("yes");
        lj.setCode(0);
        lj.setCount(7);
        return lj;
    }

    @PutMapping
    public Body update(GameSign signDays){
        Integer id = gm.updateById(signDays);
        if (id != 0) {
            return Body.BODY_200;
        }
        return Body.BODY_451;
    }
}

