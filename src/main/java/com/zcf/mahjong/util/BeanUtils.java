package com.zcf.mahjong.util;

import java.lang.reflect.Field;

/**
 * @program: Guangan_Mahjong
 * @description: asd
 * @author: Journey
 * @create: 2019-08-28 10:41
 */

public class BeanUtils {

    /**
     * 将一个实体类复制到另一个实体类中
     * @param fromBean
     * @param
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws Exception
     */
    public static void CopySameBean(Object fromBean, Object toBean) throws NullPointerException, IllegalArgumentException, IllegalAccessException {
        if(fromBean == toBean) {
            return;
        }
        if(fromBean != null) {
            // 得到类对象
            Class fromBeanClass = (Class) fromBean.getClass();
            Class toBeanClass = (Class) toBean.getClass();

            /**
             * 得到类中的所有属性集合
             */
            Field[] fbc = fromBeanClass.getDeclaredFields();
            Field[] tbc = toBeanClass.getDeclaredFields();
            for(int i = 0;i < fbc.length;i++) {
                Field f = fbc[i];

                f.setAccessible(true); // 设置些属性是可以访问的
                Object fVal = f.get(fromBean);// 得到此属性的值
                for(int j = 0;j < tbc.length;j++) {
                    Field t = tbc[i];

                    t.setAccessible(true); // 设置些属性是可以访问的
                    // 属性名称和属性类型必须全部相同，才能赋值
                    if(f.getName().equals(t.getName()) && f.getType().toString().equals(t.getType().toString())){
                        t.set(toBean, fVal);
                    }
                }
            }
        } else {
            throw new NullPointerException("FromBean is null");
        }
    }
}