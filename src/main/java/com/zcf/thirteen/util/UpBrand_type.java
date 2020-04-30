/**
 * 
 */
package com.zcf.thirteen.util;

import com.zcf.thirteen.bean.T_UserBean;

/**
 * @author guolele
 * @date 2019年8月21日 上午9:26:10
 * 
 */
public class UpBrand_type {
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
	 * 
	 * @param brand
	 */
	public UpBrand_type(int[] brand, T_UserBean userBean) {
		setBrands_Type(brand);
		this.userBean = userBean;
	}

	/**
	 * 检测上墩有几张相同牌 （对子、三张、炸弹）
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
		}

		return -1;
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
	 * 获取手牌上墩内最大牌的下标
	 * 
	 * @return @throws
	 */
	public int getMaxBrand() {
		int maxBrand = 0;
		int[] brand_a = new int[] { -1, -1, -1 };
		int count = 0;
		for (int i = 0; i < brand_a.length; i++) {
			count++;
		}
		if (count == 0) {
			for (int i = 0; i < this.brand.length; i++) {
				brand_a[i] = this.brand[i];
			}
		}
		SortBrand(brand_a);
		maxBrand = brand_a[0];
		brand_a[0] = -1;
		return maxBrand;
	}
	
}
