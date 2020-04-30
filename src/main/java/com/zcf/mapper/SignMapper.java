package com.zcf.mapper;

import com.zcf.pojo.Sign;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ZhaoQi
 * @since 2019-03-25
 */
public interface SignMapper extends BaseMapper<Sign> {
    int getIsSign(Long uid);
}
