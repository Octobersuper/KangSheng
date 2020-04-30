package com.zcf.controller;


import com.zcf.mahjong.json.Body;
import com.zcf.mahjong.service.FileService;
import com.zcf.mapper.NoticeMapper;
import com.zcf.pojo.Notice;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

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
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    NoticeMapper nm;
    @Autowired
    FileService fileService;

    @GetMapping
    public Body get(Notice notice){
        return Body.newInstance(nm.selectById(notice.getId()));
    }

    @PutMapping
    public Body update(Notice notice){
        return Body.newInstance(nm.updateById(notice));
    }

    @RequestMapping(value = "/gg_img", method = RequestMethod.POST)
    public Map<String, Object> uploadImage(MultipartFile file, HttpServletRequest request) {
        Map<String, Object> res = new HashMap<>();
        Map<String, Object> date = new HashMap<>();
        try {
            String path = request.getSession().getServletContext().getRealPath("gg_img");
            String targetFileName = fileService.upload(file,path);
            res.put("code",0);
            res.put("msg",0);
            date.put("src","http://localhost:8099/KangSheng/gg_img/"+targetFileName);
            res.put("data",date);
        } catch (Exception e) {
            e.printStackTrace();
            res.put("code", -1);
            res.put("msg", "上传失败");
        }
        return res;
    }
}

