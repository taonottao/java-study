package com.atTao.team.service;
/**
 * @Description	表示员工的状态
 * @author T-wang  Email:1609832760@qq.com
 * @verson
 * @date 2023年1月13日下午2:26:18
 */
//public class Status {
//
//	private final String NAME;
//
//	private Status(String name) {
//		this.NAME = name;
//	}
//
//	public static final Status FREE = new Status("FREE");
//	public static final Status BUSY = new Status("BUSY");
//	public static final Status VOCATION = new Status("VOCATION");
//
//	public String getNAME() {
//		return NAME;
//	}
//
//	@Override
//		public String toString() {
//			return NAME;
//		}
//
//
//}

public enum Status {
	FREE,BUSY,VOCATION;
}
