package com.zcf.mapper;

import com.zcf.pojo.PkTable;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ZhaoQi
 * @since 2019-07-08
 */
public interface PkTableMapper extends BaseMapper<PkTable> {

    List<PkTable> selectOneWeek();

    List<PkTable> selectOneMonth();
}
