package com.yoursudoku;

/**
 * Generic pair class
 * 
 * @author truongduy134@gmail.com
 *
 * @param <T1> the type of the first object
 * @param <T2> the type of the second object
 */
public class Pair<T1, T2> {
	private T1 first;
	private T2 second;;
	
	/**
	 * 
	 * @param first
	 * @param second
	 */
	public Pair(T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}
	
	/**
	 * 
	 * @return
	 */
	public T1 getFirst() {
		return first;
	}
	
	public void setFirst(T1 val){
		this.first = val;
	}
	/**
	 * 
	 * @return
	 */
	public T2 getSecond() {
		return second;
	}
	
	public void setSecond(T2 val){
		this.second = val;
	}
}
