package com.zcf.thirteen.util;

import java.util.Collections;
import java.util.List;


/**
 * 麻将算法类
 * @author Administrator
 *
 */
public class Mahjong_Util {
	/**
	 * 构造器
	 * @param brands（用户手里的牌）
	 */
	public Mahjong_Util(){
	}
	/**
	 * 排序（升序）[万筒条东南西北中发白]
	 * @param list
	 */
	public void Order_Brands(List<Integer> list,int draw){
		//升序
		Collections.sort(list);
		//手里的牌进行混排序
		if(draw!=0){
			//混放在最前面
			for(int i=0;i<list.size();i++){
				if(list.get(i)==draw){
					for(int j=i;j>=0;j--){
						if((j-1)>=0){
							int index = list.get(j);
							list.set(j, list.get(j-1));
							list.set(j-1, index);
						}
					}
				}
			}
		}
	}
	/**
	 * 获取单幅牌
	 * @param index
	 * @return
	 */
	public int getBrand_Value(int index){
		return index%13+1;
	}
	/**
	 * 获取单张牌值和花色
	 * @param index
	 * @return
	 */
	public int[] ISUser_Mahjong(int index){
		int[] indexs = new int[2];
		int color = -1;
		//获取单幅
		int value = getBrand_Value(index);
		//小于27的是万筒条
		if(value<27){
			//获取牌值ֵ
			index = value-9*(value/9);
			//获取花色
			color = (value/9);
		}else{
			color = value;
		}
		indexs[0] = index;
		indexs[1] = color;
		return indexs;
	}
	/**
	 * 检测是否可吃
	 * @param list 用户手里的牌
	 * @param index 别人出的牌
	 * @param draw 当前混
	 * @return
	 */
	public int[] IS_Eat(List<Integer> list,int index,int draw){
		//获取牌值和花色
		int[] index_brand = ISUser_Mahjong(index);
		//上牌和上上牌
		int index_s=index_brand[0]>0&&index_brand[0]<10?index-1:-1;
		int index_ss=index_brand[0]>1&&index_brand[0]<10?index-2:-1;;
		//下牌
		int index_x=index_brand[0]<8?index+1:-1;;
		//下下牌
		int index_xx=index_brand[0]<7?index+2:-1;;
		//中发白另算
		if(index_brand[0]==31){
			index_x=32;
			index_xx=33;
		}else if(index_brand[0]==32){
			index_s=31;
			index_x=33;
		}else if(index_brand[0]==33){
			index_ss=31;
			index_s=32;
		}
		//上上牌-上牌-下牌-下下牌
		int[] eat = new int[]{-1,-1,-1,-1};
		//
		for(int i=0;i<list.size();i++){
			if(list.get(i)==draw)continue;
			//上牌
			if(list.get(i)==index_s)eat[1]=index_s;
			//上上牌
			if(list.get(i)==index_ss)eat[0]=index_ss;
			//下牌
			if(list.get(i)==index_x)eat[2]=index_x;
			//下下牌
			if(list.get(i)==index_xx)eat[3]=index_xx;
		}
		return eat;
	}
	/**
	 * 检测是否可碰/杠
	 * @param list
	 * @param index
	 * @param draw
	 * @return
	 */
	public int[] IS_Bump(List<Integer> list,int index,int draw){
		int[] bump = new int[]{-1,-1,-1};
		for(int i=0;i<list.size();i++){
			if(list.get(i)==index){
				for(int j=0;j<bump.length;j++){
					if(bump[j]==-1){
						bump[j]=index;
						break;
					}
				}
			}
		}
		return bump;
	}
	public void IS_Victory(List<Integer> list,int index,int draw){
		
	}
	/**
	 * 七对检测
	 * @param list
	 * @param index
	 * @param draw
	 */
	public void IS_Seven(List<Integer> list,int index,int draw){
		//将来牌放入用户手中
		list.add(index);
		//重新进行排序
		Order_Brands(list, draw);
		int count=0;
		int draw_count=0;
		for(int i=0;i<list.size();i++){
			if((i+1)<list.size()){
				if(list.get(i)==draw){
					draw_count++;
					continue;
				}
				if(list.get(i)==list.get(i+1)){
					count++;
					i++;
				}
			}
		}
		if(count==7)System.out.println("七对胡");
		if(draw_count==1&&count==6)System.out.println("七对胡[1混]");
		if(draw_count==2&&count==5)System.out.println("七对胡[2混]");
		if(draw_count==3&&count==4)System.out.println("七对胡[3混]");
	}
}
