package com.zcf.controller;

import com.zcf.mahjong.bean.Service;
import com.zcf.mahjong.json.Body;
import com.zcf.mahjong.mahjong.Public_State;
import com.zcf.mahjong.service.FileService;
import com.zcf.mahjong.util.LayuiJson;
import com.zcf.pojo.User;
import com.zcf.service.impl.UserServiceImpl;
import com.zcf.thirteen.comm.Public_State_t;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;


/**
 * <p>
 *  用户控制器
 * </p>
 *
 * @author Zhaoqi
 * @since 2018-11-21
 */
@Controller
@CrossOrigin
@RequestMapping("/user/")
public class UserController {

    @Autowired
    UserServiceImpl UserService;
    @Autowired
    FileService iFileService;

    /*
     *@Author:ZhaoQi
     *@methodName:微信登录
     *@Params:
     *@Description:
     *@Return:
     *@Date:2019/2/25
     */
    @PostMapping("login")
    @ResponseBody
    public Body login(User user){
        return UserService.login(user);
    }


    /*
     *@Author:ZhaoQi
     *@methodName:绑定邀请码
     *@Params:
     *@Description:
     *@Return:
     *@Date:2019/3/26
     */
    @PostMapping("addFid")
    @ResponseBody
    public Body addFid(User user){
        return UserService.addFid(user);
    }

    /*
     *@Author:ZhaoQi
     *@methodName:个人信息
     *@Params:
     *@Description:
     *@Return:
     *@Date:2019/3/26
     */
    @PostMapping("getInfo")
    @ResponseBody
    public Body getInfo(User user){
        return UserService.getInfo(user);
    }

    /*
     *@Author:ZhaoQi
     *@methodName:添加账号
     *@Params:
     *@Description:
     *@Return:
     *@Date:2019/2/25
     */
    @PostMapping("add")
    @ResponseBody
    public Body add(String phone, String password,String nickname){
        return UserService.add(phone,password,nickname);
    }

    /*
     *@Author:ZhaoQi
     *@methodName:获取某用户的个人信息
     *@Params:
     *@Description:
     *@Return:
     *@Date:2019/2/26
     */
    @PostMapping("getUser")
    @ResponseBody
    public Body getUser(User user){
        return UserService.getUser(user);
    }

    /*
     *@Author:ZhaoQi
     *@methodName:查询所有用户
     *@Params:
     *@Description:
     *@Return:
     *@Date:2019/2/25
     */
    @GetMapping("getUsers")
    @ResponseBody
    public LayuiJson getUsers(User user, @RequestParam(value = "pageNum") int pageNum,
                              @RequestParam(value = "pageSize") int pageSize){
        return UserService.getUsers(user,pageNum,pageSize);
    }

    /*
     *@Author:ZhaoQi
     *@methodName:修改某个用户
     *@Params:
     *@Description:
     *@Return:
     *@Date:2019/2/25
     */
    @PostMapping("update")
    @ResponseBody
    public Body update(User user){
        return UserService.update(user);
    }

    /*
     *@Author:ZhaoQi
     *@methodName:删除某个用户
     *@Params:
     *@Description:
     *@Return:
     *@Date:2019/2/25
     */
    @PostMapping("delete")
    @ResponseBody
    public Body delete(User user){
        return UserService.delete(user);
    }


    /*
     *@Author:ZhaoQi
     *@methodName:退出登录
     *@Params:
     *@Description:
     *@Return:
     *@Date:2019/2/28
     */
    @PostMapping("exitLogin")
    @ResponseBody
    public Body exitLogin(User user){
        return UserService.exitLogin(user);
    }

    /**
     * 查询下级
     * @param user
     * @return
     */
    @PostMapping("getMyUsers")
    @ResponseBody
    public Body getMyUsers(User user){
        return UserService.getMyUsers(user);
    }


    /**
     *@ Author:ZhaoQi
     *@ methodName:发送语音
     *@ Params:
     *@ Description:
     *@ Return:
     *@ Date:2020/3/21
     */
    @RequestMapping(value="send_voice",method= RequestMethod.POST)
    @ResponseBody
    public Body update(int userid,@RequestParam(value = "voice",required = false) MultipartFile voice, HttpServletRequest request){

        if(voice!=null){
            String path = request.getSession().getServletContext().getRealPath("voice");
            String targetFileName = iFileService.upload(voice,path);
            targetFileName = "/voice/"+targetFileName;
            if(Public_State_t.clients_t.get(String.valueOf(userid))==null){
                return Body.BODY_451;
            }
            Public_State_t.clients_t.get(String.valueOf(userid)).userBean.setVoice(targetFileName);
            return Body.BODY_200;
        }
        return Body.BODY_451;
    }
}

