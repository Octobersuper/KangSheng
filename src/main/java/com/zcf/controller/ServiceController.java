package com.zcf.controller;


import com.zcf.mahjong.json.Body;
import com.zcf.mahjong.util.LayuiJson;
import com.zcf.pojo.Service;
import com.zcf.service.impl.ServiceServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ZhaoQi
 * @since 2019-02-26
 */
@CrossOrigin
@Controller
@RequestMapping("/service/")
public class ServiceController {

    @Autowired
    ServiceServiceImpl ssi;

    /*
     *@Author:ZhaoQi
     *@methodName:后端获取客服信息
     *@Params:
     *@Description:
     *@Return:
     *@Date:2019/2/26
     */
    @PostMapping("getService")
    @ResponseBody
    public LayuiJson getService(){
        return ssi.getService();
    }

    /*
     *@Author:ZhaoQi
     *@methodName:前端获取客服信息
     *@Params:
     *@Description:
     *@Return:
     *@Date:2019/2/26
     */
    @PostMapping("getServices")
    @ResponseBody
    public Body getServices(){
        return ssi.getServices();
    }


    /*
     *@Author:ZhaoQi
     *@methodName:客服信息修改
     *@Params:
     *@Description:
     *@Return:
     *@Date:2019/2/26
     */
    @PostMapping("update")
    @ResponseBody
    public Body update(Service service){
        return ssi.update(service);
    }

}

