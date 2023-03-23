package com.atTao.team.service;
/**
 * 
 * @Description 自定义异常类
 * @author T-wang  Email:1609832760@qq.com
 * @verson
 * @date 2023年1月13日下午7:10:32
 */
public class TeamException extends Exception{
	static final long serialVersionUID = -3387516993122229948L;
	
	public TeamException() {
		super();
	}
	
	public TeamException(String msg) {
		super(msg);
	}
	
}
