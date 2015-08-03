package com;

/**
 * 
 * @author rocca.peng@hunteron.com
 * @Description
 * @Date 2015年6月30日 上午11:31:48
 */
public class Atithmetic {

	public static void main(String[] args) {	
		
		
		
		System.out.println(230^10);
		
		
		
		
		int[] i = { 5, 6, 12, 4, 9, 3, 23, 1, 39, 403, 596, 87 };
		System.out.println("----冒泡排序的结果：");
		maoPao(i);
		System.out.println();
		System.out.println("----选择排序的结果：");
		xuanZe(i);
		System.out.println();
		System.out.println("----插入排序的结果：");
		chaRu(i);
		System.out.println();
		System.out.println("----希尔（Shell）排序的结果：");
		shell(i);
		
	}

	// 冒泡排序
	public static void maoPao(int[] x) {
		for (int i = 0; i < x.length; i++) {
			for (int j = i + 1; j < x.length; j++) {
				if (x[i] > x[j]) {
					int temp = x[i];
					x[i] = x[j];
					x[j] = temp;
				}
			}
		}
		for (int i : x) {
			System.out.print(i + " ");
		}
	}

	// 选择排序
	public static void xuanZe(int[] x) {
		for (int i = 0; i < x.length; i++) {
			int lowerIndex = i;
			// 找出最小的一个索引
			for (int j = i + 1; j < x.length; j++) {
				if (x[j] < x[lowerIndex]) {
					lowerIndex = j;
					break;
				}
			}
			// 交换
			int temp = x[i];
			x[i] = x[lowerIndex];
			x[lowerIndex] = temp;
		}
		for (int i : x) {
			System.out.print(i + " ");
		}
	}

	// 插入排序
	public static void chaRu(int[] x) {
		for (int i = 1; i < x.length; i++) {// i从一开始，因为第一个数已经是排好序的啦
			for (int j = i; j > 0; j--) {
				if (x[j] < x[j - 1]) {
					int temp = x[j];
					x[j] = x[j - 1];
					x[j - 1] = temp;
				}
			}
		}
		for (int i : x) {
			System.out.print(i + " ");
		}
	}

	// 希尔排序
	public static void shell(int[] x) {
		// 分组
		for (int increment = x.length / 2; increment > 0; increment /= 2) {
			// 每个组内排序
			for (int i = increment; i < x.length; i++) {
				int temp = x[i];
				int j = 0;
				for (j = i; j >= increment; j -= increment) {
					if (temp < x[j - increment]) {
						x[j] = x[j - increment];
					} else {
						break;
					}
				}
				x[j] = temp;
			}
		} 

		for (int i : x) {
			System.out.print(i + " ");
		}
	}

}
