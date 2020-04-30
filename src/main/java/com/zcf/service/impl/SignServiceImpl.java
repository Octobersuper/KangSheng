package com.zcf.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.zcf.mahjong.json.Body;
import com.zcf.mapper.SignDaysMapper;
import com.zcf.mapper.SignMapper;
import com.zcf.mapper.UserTableMapper;
import com.zcf.pojo.Sign;
import com.zcf.pojo.SignDays;
import com.zcf.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ZhaoQi
 * @since 2019-03-25
 */
@Service
public class SignServiceImpl {
    @Autowired
    SignMapper sm;
    @Autowired
    UserTableMapper um;
    @Autowired
    SignDaysMapper sdm;

    public Body sign(Sign sign) {
        if (sign.getUid() == null) {
            return Body.BODY_451;
        }
        //查询今天是否签到
        int i = sm.getIsSign(sign.getUid());
        if(i!=0){
            return Body.newInstance(452,"今天已签到");
        }

        Date now = new Date();
        EntityWrapper<Sign> w = new EntityWrapper<>();
        w.eq("uid",sign.getUid());
        int week_index = sm.selectCount(w);


        User user = um.selectById(sign.getUid());//获取用户信息
        SignDays signDays = sdm.selectById((long) week_index+1);
        user.setMoney(user.getMoney()+signDays.getMoney());

        um.update(user,null);

        sign.setCreateTime(now);
        boolean b = sign.insert();
        if (b) {
            return Body.BODY_200;
        }
        return Body.BODY_451;
    }

    public Body getSign(Sign s) {
        int[] arr = new int[15];
        int[] arr2 = new int[15];

        List<SignDays> signDays = sdm.selectList(null);
        for (int i = 0; i < signDays.size(); i++) {
            arr2[i] = signDays.get(i).getMoney();
            arr[i] = 0;
        }

        //获取今天是第几天
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int toDay = cal.get(Calendar.DAY_OF_YEAR);//今天

        //获取当前用户所有签到的日期  按时间倒叙  最后一天放最前面
        EntityWrapper<Sign> w = new EntityWrapper<>();
        w.eq("uid",s.getUid());
        w.orderBy("createTime",false);
        List<Sign> signs = sm.selectList(w);

        //取最后一次签到的日期
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(signs.get(0).getCreateTime());
        Long lastId = signs.get(0).getId();
        int lastDay = cal2.get(Calendar.DAY_OF_YEAR);//最后一天签到

        //如果是第16天  清空
        List<int[]> list = new ArrayList<int[]>();
        if (signs.size() == 15 && lastDay != toDay) {
            sm.delete(w);
            list.add(arr);
            list.add(arr2);
            return Body.newInstance(list);
        }else if((toDay!=lastDay) && (toDay - lastDay != 1)){
            sm.delete(w);
            list.add(arr);
            list.add(arr2);
            return Body.newInstance(list);
        }else{
            for (int i = 0; i < signs.size(); i++) {
                if(toDay==lastDay && (signs.get(i).getId().equals(lastId))){
                    arr[signs.size() - i -1] = 2;//2意思是今天已签到
                }else if(toDay!=lastDay && (signs.get(i).getId().equals(lastId))){
                    arr[signs.size() - i -1] = 1;//3意思是今=今天可签到但未签到
                    arr[signs.size() - i] = 3;//3意思是今=今天可签到但未签到
                }else{
                    arr[signs.size() - i -1] = 1;//已签到
                }
            }
            list.add(arr);
            list.add(arr2);
            return Body.newInstance(list);
        }
    }
}
