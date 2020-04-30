package com.zcf.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.gson.Gson;
import com.zcf.mahjong.bean.UserBean;
import com.zcf.mahjong.json.Body;
import com.zcf.mahjong.util.LayuiJson;
import com.zcf.mahjong.util.MD5Util;
import com.zcf.mahjong.util.UtilClass;
import com.zcf.mapper.*;
import com.zcf.pojo.*;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UserServiceImpl {

    @Autowired
    UserTableMapper um;
    @Autowired
    PkTableMapper pm;
    @Autowired
    PkRecordTableMapper prm;

    public UserBean selectByid(Long uid) {
        User user = new User();
        UserBean userBean = new UserBean();
        user.setUserid(uid);
        User user1 = user.selectById();
        if(user1!=null){
            BeanUtils.copyProperties(user1,userBean);
        }else{
            userBean = null;
        }
        return userBean;
    }

    public Body login(User user) {
        if(user.getOpenid()==null || user.getNickname() == null || user.getAvatarurl()==null){
            return Body.BODY_451;
        }
        String wxname = user.getNickname();
        String wximg = user.getAvatarurl();
        String openid = user.getOpenid();

        EntityWrapper<User> eq = new EntityWrapper<>();
        eq.eq("openid",user.getOpenid());
        user = user.selectOne(eq);
        if (user!=null){
            if(user.getType()!=null && user.getType()==1){
                return Body.newInstance(451,"账号已被冻结，请联系管理员");
            }else if(user.getIsLogin()!=null && user.getIsLogin()==1){
                return Body.newInstance(451,"账号已在其他设备登录，如有疑问，请联系管理员");
            }/*else{
                user.setIsLogin(1);
                user.setWxname(wxname);
                user.setWximg(wximg);
                user.updateById();
                String roomName;
                HashMap<String, Object> map = new HashMap<>();
                Map<String, RoomsParam> rooms = Game_WebSocket.rooms;
                for (RoomsParam room:rooms.values()) {
                    roomName = room.getRoomName();
                    Map<String, UserBean> users = room.getUsers();
                    for (UserBean u:users.values()) {
                        if(u.getId()==user.getId()){//如果
                            map.put("roomName",roomName);
                            map.put("user",user);
                            //map.put("gameType",2);
                            return Body.newInstance(new Meta(111,"账号正在游戏中，是否重连"),map);
                        }
                    }
                }
                return Body.newInstance(user);
            }*/
        }
        //用户不存在时自动创建当前用户
        User u = new User();
        u.setNickname(wxname);
        u.setAvatarurl(wximg);
        u.setOpenid(openid);
        u.setCreatetime(new Date());
        u.setMoney(0);
        u.insert();
        return Body.newInstance(u);
    }


    public Body add(String phone, String password, String wxname) {
        User u = new User();
        u.setPhone(phone);
        User user = um.selectOne(u);
        if(user != null){
            return Body.newInstance(1,"手机号已存在");
        }
        User VUser = new User();
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        VUser.setNickname(wxname);
        VUser.setPhone(phone);
        VUser.setDate(sdf.format(now));
        VUser.setPassword(MD5Util.MD5EncodeUtf8(password));
        VUser.setSex(1);
        VUser.setAvatarurl(String.valueOf((Math.random()*10+1)));
        VUser.setOpenid(UUID.randomUUID().toString());
        VUser.setMoney(0);
        VUser.setDiamond(0);
        VUser.setSdk(1);
        VUser.setStatetext("0");
        VUser.setCode(UtilClass.utilClass.getRandom(6));
        VUser.setCreatetime(new Date());
        VUser.setOpenid(UUID.randomUUID()+"Q");

        Integer insert = um.insert(VUser);
        if (insert != 0) {
            return Body.BODY_200;
        }
        return Body.BODY_451;
    }

    public LayuiJson getUsers(User user, int pageNum, int pageSize) {
        LayuiJson lj = new LayuiJson();
        Wrapper w = new EntityWrapper();
        List<String> list1 = new ArrayList<String>();
        list1.add("createtime");
        w.orderDesc(list1);
        if (user.getPhone()!=null&&user.getPhone()!=""){
            w.eq("phone",user.getPhone()).or().eq("nickname",user.getPhone()).or().eq("id",user.getPhone());
        }
        if(user.getfId()!=null){
            w.eq("fId",user.getfId());
        }
        List list = um.selectPage(new Page<>(pageNum, pageSize), w);
        Integer count = um.selectCount(w);
        if (list != null) {
            lj.setCode(0);
            lj.setCount(count);
            lj.setData(list);
            lj.setMsg("成功");
            return lj;
        }
        lj.setCode(1);
        lj.setCount(count);
        lj.setData(null);
        lj.setMsg("暂无数据");
        return lj;
    }

    public Body update(User user) {
        if(user.getPassword()!=null){
            user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        }
        boolean b = user.updateById();
        if (b){
            return Body.BODY_200;
        }
        return Body.BODY_451;
    }

    public Body delete(User user) {
        boolean b = user.deleteById();
        if (b){
            return Body.BODY_200;
        }
        return Body.BODY_451;
    }


    public Body getUser(User user) {
        User user1 = user.selectById();
        if (user1 != null) {
            return Body.newInstance(user1);
        }
        return Body.BODY_451;
    }

    //1600+120+200+1000
    public Body exitLogin(User user) {
        user.setIsLogin(0);
        Integer integer = um.updateById(user);
        if (integer!=0){
            return Body.BODY_200;
        }
        return Body.BODY_451;
    }

    public Body addFid(User user) {
        if(user.getCode()!=null){
            User u = new User();
            u.setCode(user.getCode());
            User one = um.selectOne(u);
            int num = 0;
            if (one.getNumber_1()==5){
                num = 300;
            }else if (one.getNumber_1()==4){
                num = 200;
            }

            EntityWrapper<User> ew = new EntityWrapper<>();
            ew.eq("fid",one.getUserid());
            List<User> list = um.selectList(ew);
            if(num!=0){
                if(list.size()>=num){
                    return Body.newInstance(451,"邀请人数已满");
                }
            }

            if (one == null) {
                return Body.newInstance(451,"邀请码不存在");
            }else{
                user.setfId(one.getUserid());
                user.setCode(null);
                um.updateById(user);
                return Body.BODY_200;
            }
        }else{
            return Body.newInstance(451,"邀请码不能为空");
        }
    }

    //430+840+1500
    public Body getInfo(User user) {
        User one = um.selectOne(user);
        HashMap<Object, Object> map = new HashMap<>();
        if (one != null) {
            map.put("id",one.getUserid());
            map.put("nickname",one.getNickname());
            map.put("code",one.getCode());
            map.put("avatarurl",one.getAvatarurl());

            //查询一周内的对局信息
            List<PkTable> list = pm.selectOneWeek();
            Integer weekNum= 0;
            Integer monthNum = 0;
            Integer monthWin = 0;
            if (list != null && list.size()!=0) {
                //通过对局信息查询出来当前用户的一周内的参战次数
                for (PkTable p:list) {
                    EntityWrapper<PkRecordTable> ew = new EntityWrapper<>();
                    ew.eq("roomid",p.getPkid());
                    List<PkRecordTable> pklist = prm.selectList(ew);
                    if (pklist != null) {
                        for (PkRecordTable pt: pklist) {
                            if(pt.getUserid().equals(Integer.valueOf(String.valueOf(one.getUserid())))){
                                weekNum +=1;
                            }
                        }
                    }
                }
            }
            //查询一月内的对局信息
            List<PkTable> mlist = pm.selectOneMonth();
            if (mlist != null && mlist.size()!=0) {
                //通过对局信息查询出来当前用户的一周内的参战次数
                for (PkTable p:mlist) {
                    if (p == null) {
                        continue;
                    }
                    EntityWrapper<PkRecordTable> ew = new EntityWrapper<>();
                    ew.eq("roomid",p.getPkid());
                    List<PkRecordTable> pklist = prm.selectList(ew);
                    if (pklist != null) {
                        for (PkRecordTable pt: pklist) {
                            if(pt.getUserid().equals(Integer.valueOf(String.valueOf(one.getUserid())))){
                                monthWin +=pt.getNumber();
                                monthNum +=1;
                            }
                        }
                    }
                }
            }
            map.put("weekNum",weekNum);//本周盘数
            map.put("monthNum",monthNum);//本月盘数
            map.put("monthWin",monthWin);//本月输赢
            return Body.newInstance(map);
        }
        return Body.BODY_451;
    }

    public Body getMyUsers(User user) {
        // TODO Auto-generated method stub
        EntityWrapper<User> w = new EntityWrapper<User>();
        w.eq("fId", user.getUserid());
        List<User> list = um.selectList(w);
        if (list!=null) {
            for (User u:list) {
                int count=0;
                List<PkRecordTable> list2 = prm.selectList(null);
                for (PkRecordTable pkRecordTable:list2){
                    if(pkRecordTable.getUserid().equals(String.valueOf(u.getUserid()))){
                        count += 1;
                    }
                }
                //u.setPldNum(count);
            }
            return Body.newInstance(list);
        }
        return Body.newInstance(451,"无数据");
    }


    public void updateById(User u) {
        um.updateById(u);
    }


    private int getPan(Integer number1) {
        switch (number1){
            case 0:
                return 0;
            case 5:
                return 50000;
            case 4:
                return 8000;
            case 3:
                return 2000;
            case 2:
                return 300;
            case 1:
                return 20;
        }
        return 0;
    }

    private String getCommission(Integer number1) {
        switch (number1){
            case 0:
                return "0%";
            case 5:
                return "100%";
            case 4:
                return "75%";
            case 3:
                return "50%";
            case 2:
                return "25%";
            case 1:
                return "10%";
        }
        return "0%";
    }

    public List<User> selectList(EntityWrapper<User> ew1) {
        return um.selectList(ew1);
    }
}
