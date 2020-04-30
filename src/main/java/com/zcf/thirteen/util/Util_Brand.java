package com.zcf.thirteen.util;

/***
 * 牌型计算(扎金花)
 * 
 * @author Administrator
 *
 */

import com.zcf.thirteen.bean.T_UserBean;

/**
 * @author guolele
 * @date 2019年8月3日 下午1:38:43
 * 
 */
public class Util_Brand {
	T_UserBean userBean;
	// 用户手里的牌
	private int[] brand;

	public void setBrands_Type(int[] brand) {
		this.brand = new int[brand.length];
		for (int i = 0; i < brand.length; i++) {
			this.brand[i] = brand[i];
		}

	}

	/**
	 * 冒泡排序
	 * 
	 * @throws
	 */
	public void SortBrand(int[] brand) {
		for (int i = 0; i < this.brand.length; i++) {
			for (int j = i; j < this.brand.length; j++) {
				if ((j + 1) < this.brand.length) {
					if (this.brand[i] % 13 + 1 > this.brand[j + 1] % 13 + 1) {
						int maxbrand = this.brand[i];
						this.brand[i] = this.brand[j + 1];
						this.brand[j + 1] = maxbrand;
					}
				}
			}
		}
	}
	/**
	 * 铁支冒泡排序
	 * 
	 * @throws
	 */
	public void SortBrand1(int[] tiezhi) {
		for (int i = 0; i < tiezhi.length; i++) {
			for (int j = i; j < tiezhi.length; j++) {
				if ((j + 1) < tiezhi.length) {
					if (tiezhi[i] /13 > tiezhi[j + 1] /13) {
						int maxbrand = tiezhi[i];
						tiezhi[i] = tiezhi[j + 1];
						tiezhi[j + 1] = maxbrand;
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param brand
	 */
	public Util_Brand(int[] brand, T_UserBean userBean, int type) {
		setBrands_Type(brand);
		SortBrand(brand);
		this.userBean = userBean;
	}

	/**
	 * 检测有几张相同牌 （对子、三张、炸弹）
	 *
	 * @return @throws
	 */
	public int OneBrand() {
		int count = 0;
		for (int i = 0; i < this.brand.length; i++) {
			if ((i + 1) < brand.length) {
				if (this.brand[i] % 13 + 1 == brand[i + 1] % 13 + 1) {
					count++;
				}
			}
		}

		if (count == 1) {
			return 2;
		} else if (count == 2) {
			return 3;
		} else if (count == 3) {
			return 4;
		}

		return -1;
	}

	/**
	 * 检测顺子
	 *
	 * @return @throws
	 */
	public int getShunBrandType() {
		// 如果牌组里有一张A
		if (brand[0] % 13 + 1 == 1) {
			if (brand[1] % 13 + 1 == 2 && brand[2] % 13 + 1 == 3 && brand[3] % 13 + 1 == 4 && brand[4] % 13 + 1 == 5) {
				return 400;
			}
			if (brand[1] % 13 + 1 == 10 && brand[2] % 13 + 1 == 11 && brand[3] % 13 + 1 == 12
					&& brand[4] % 13 + 1 == 13) {
				return 400;
			}
		} else {
			if (brand[0] % 13 + 2 == brand[1] % 13 + 1 && brand[1] % 13 + 2 == brand[2] % 13 + 1
					&& brand[2] % 13 + 2 == brand[3] % 13 + 1 && brand[3] % 13 + 2 == brand[4] % 13 + 1) {
				return 400;
			}
		}
		return -1;
	}

	/**
	 * 获取手牌里相同花色的数量
	 *
	 * @param brand
	 * 			@param userBean @return @throws
	 */
	public int getColorCount(int[] brand, T_UserBean userBean) {
		int count = 0;
		for (int i = 0; i < brand.length; i++) {
			if (brand[i] / 13 == brand[0] / 13) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 判断葫芦
	 *
	 * @param brand
	 * 			@param userBean @return @throws
	 */
	public int getHuLu(int[] brand, T_UserBean userBean) {
		int count = 0;
		int brand_number = 0;
		for (int i = 0; i < brand.length; i++) {
			for (int j = i + 1; j < brand.length; j++) {
				if ((j + 1) < brand.length && brand[i] % 13 + 1 == brand[j] % 13 + 1) {
					count++;
				}
				if (count == 2) {
					brand_number = brand[i] % 13 + 1;
				}
			}
		}
		// 如果有三张一样的牌
		if (count == 2) {
			for (int i = 0; i < brand.length; i++) {
				for (int j = i + 1; j < brand.length; j++) {
					if ((j < brand.length && brand[i] % 13 + 1 != brand_number
							&& brand[i] % 13 + 1 == brand[j] % 13 + 1)) {
						return 500;// 是葫芦
					}
				}
			}
		}

		return -1;// 不是葫芦
	}

	/**
	 * 判断铁支
	 *
	 * @return @throws
	 */
	public int getTieZhi() {
		int count = 0;
		int brand_number = 0;
		for (int i = 0; i < brand.length; i++) {
			for (int j = i + 1; j < brand.length; j++) {
				if ((j + 1) < brand.length && brand[i] % 13 + 1 == brand[j] % 13 + 1) {
					count++;
				}
				if (count == 3) {
					brand_number = brand[i] % 13 + 1;
				}
			}
		}
		// 如果有四张相同的牌
		if (count == 3) {
			for (int i = 0; i < brand.length; i++) {
				if (brand[i] % 13 + 1 != brand_number) {
					return 600;// 是铁支
				}
			}
		}

		return -1;// 不是铁支
	}

	/**
	 * 检测五同如果是五同 则返回最大一张牌的索引
	 *
	 * @return    
	 * @throws
	 */
	public int WuTong() {
		int count = 0;
		for (int i = 0; i < brand.length; i++) {
			if (brand[0] % 13 + 1 == brand[i+1] % 13 + 1) {
				count++;
			}
		}
		if (count == 4) {
			return brand[0];
		}
		return -1;
	}
	
	/**
	 * 检测铁支 并返回铁支中四个相同牌里最大的一张牌
	 *    
	 * @throws
	 */
	public int TieZhi(){
		int count = 0;
		int brand_number = 0;
		for (int i = 1; i < brand.length; i++) {
			if (brand[0]%13+1 == brand[i]%13+1) {
				count++;
				if (count == 3) {
					brand_number = brand[i];
				}
			}
		}
		if (count == 3) {
			return brand_number;
		}
		return -1;
	}
	
	/**
	 * 解析铁支用于比牌
	 *
	 * @param type    
	 * @throws
	 */
	public void tiezhi(int type){
		int count = 0;
		int count_a = -1;
		int brands= 0;
		for (int i = 0; i < brand.length; i++) {
			for (int j = i+1; j < brand.length; j++) {
				if (j<brand.length && brand[i]%13+1 ==brand[j]%13+1) {
					count++;
					if (count == 6) {
						brands = brand[i]%13+1;
					}
				}
			}
		}
		
		for (int i = 0; i < brand.length; i++) {
			if (brands == brand[i]%13+1) {
				count_a ++;
				if (type == 2) {
					userBean.getZhong_tiezhi()[count_a] = brand[i];
					if (count_a == 3) {
						SortBrand1(userBean.getZhong_tiezhi());
					}
				}
				if (type == 3) {
					userBean.getXia_tiezhi()[count_a] = brand[i];
					if (count_a == 3) {
						SortBrand1(userBean.getXia_tiezhi());
					}
				}
			}
		}
		
	}
	
	/**
	 * 返回五同中最大的一张牌
	 *
	 * @return    
	 * @throws
	 */
	public int wutong(){
		for (int i = 0; i < brand.length; i++) {
			if (brand[i]/13 == 3) {
				return brand[i];
			}else if(brand[i]/13 == 2){
				return brand[i];
			}else if(brand[i]/13 == 1){
				return brand[i];
			}
		}
		
		return -1;
	}
	
	/**
	 * 解析葫芦
	 *    
	 * @throws
	 */
	public void hulu(int type){
		int count = 0;
		int count_a = -1;
		int brands= 0;
		for (int i = 0; i < brand.length; i++) {
			for (int j = i+1; j < brand.length; j++) {
				if (j<brand.length && brand[i]%13+1 ==brand[j]%13+1) {
					count++;
					if (count == 3) {
						brands = brand[i]%13+1;
					}
				}
			}
		}
		for (int i = 0; i < brand.length; i++) {
			if (brands == brand[i]%13+1) {
				count_a ++;
				if (type == 2) {
					userBean.getZhong_baozi()[count_a] = brand[i];
					if (count_a == 3) {
						SortBrand1(userBean.getZhong_baozi());
					}
				}
				if (type == 3) {
					userBean.getXia_baozi()[count_a] = brand[i];
					if (count_a == 3) {
						SortBrand1(userBean.getXia_baozi());
					}
				}
			}
		}
		
	}
	
	/**
	 * 获取葫芦的值
	 *    
	 * @throws
	 */
	public void getHuLu(){
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
