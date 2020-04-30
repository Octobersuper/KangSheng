package com.zcf.service.impl;

import com.zcf.mahjong.json.Body;
import com.zcf.mahjong.util.LayuiJson;
import com.zcf.mapper.ServiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ZhaoQi
 * @since 2019-02-26
 */
@Service
public class ServiceServiceImpl{
    @Autowired
    ServiceMapper sm;

    public LayuiJson getService() {
        LayuiJson lj = new LayuiJson();
        List<com.zcf.pojo.Service> list  = new ArrayList<>();
        com.zcf.pojo.Service service = sm.selectById(1);
        if (service != null) {
            lj.setCode(0);
            lj.setCount(1);
            lj.setMsg("yes");
            list.add(service);
            lj.setData(list);
            return lj;
        }
        lj.setCode(1);
        lj.setCount(0);
        lj.setMsg("no data");
        lj.setData(null);
        return lj;
    }

    public Body update(com.zcf.pojo.Service service) {
        service.setId((long)1);
        boolean b = service.updateById();
        if (b){
            return Body.BODY_200;
        }
        return Body.BODY_451;
    }

    public Body getServices() {
        com.zcf.pojo.Service service = sm.selectById(1);
        if (service == null) {
            return Body.BODY_451;
        }
        return Body.newInstance(service);
    }
}
