package com.yoursudoku;

public class Utility {
	public static boolean isSquareNumber(int num) {
		long squareRoot = Math.round(Math.floor(Math.sqrt((double) num)));
		if (squareRoot * squareRoot == num)
			return true;
		return false;
	}
}
