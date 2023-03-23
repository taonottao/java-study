package com.atTao.team.service;

import com.atTao.team.domain.Architect;
import com.atTao.team.domain.Designer;
import com.atTao.team.domain.Employee;
import com.atTao.team.domain.Programmer;

/**
 * 
 * @Description	关于开发团队成员的管理：添加、删除等。
 * @author T-wang  Email:1609832760@qq.com
 * @verson
 * @date 2023年1月13日下午10:30:31
 */
public class TeamService {
	
	private static int counter = 1;//给memberId赋值使用
	private final int MAX_MEMBER = 5;//限制开发团队的人数
	private Programmer[] team = new Programmer[MAX_MEMBER];//保存开发团队成员
	private int total;//记录开发团队中实际的人数
	
	/**
	 * 
	 * @Description: 获取开发团队中的所有成员
	 * @auther: T-wang 
	 * @return
	 */
	public Programmer[] getTeam() {
		Programmer[] team = new Programmer[total];
		for(int i = 0; i < team.length; i++) {
			team[i] = this.team[i];
		}
		return team;
	}
	/**
	 * 
	 * @Description: 将指定的员工添加到开发团队中
	 * @auther: T-wang 
	 * @param e
	 * @throws TeamException 
	 */
	public void addMember(Employee e) throws TeamException {
		//成员已满，无法添加
		if(total >=	 MAX_MEMBER) {
			throw new TeamException("成员已满，无法添加");
		}
		//该成员不是开发人员，无法添加
		if(!(e instanceof Programmer)) {
			throw new TeamException("该成员不是开发人员，无法添加");
		}
		//该员工已在本开发团队中
		if(isExist(e)) {
			throw new TeamException("该成员已在本开发团队中");
		}
		//该员工已是某团队成员
		//该员工正在休假，无法添加
		Programmer p = (Programmer)e;//一定不会出现ClassCastException
//		if("BUSY".equals(p.getStatus().getNAME())){//if(p.getStatus().getNAME().equals("BUSY")) {
//			throw new TeamException("该员工已是某团队成员");
//		}else if("VOCATION".equals(p.getStatus().getNAME())) {
//			throw new TeamException("该员工正在休假，无法添加");
//		}
		switch (p.getStatus()){//byte/short/char/int/String/枚举类对象
			case BUSY :
				throw new TeamException("该员工已是某团队成员");
			case VOCATION:
				throw new TeamException("该员工正在休假，无法添加");


		}
		
		//团队中至多有一名架构师
		//团队中至多有两名设计师
		//团队中至多有三名程序员
		
		//获取team中已有的成员中架构师、设计师、程序员的人数
		int numOfArch = 0, numOfDes = 0, numOfPro = 0;
		for(int i = 0; i < total; i++) {
			if(team[i] instanceof Architect) {
				numOfArch++;
			}else if(team[i] instanceof Designer) {
				numOfDes++;
			}else {
				numOfPro++;
			}
		}
		
		if(p instanceof Architect) {
			if(numOfArch >= 1) {
				throw new TeamException("团队中至多有一名架构师");
			}
		}else if(p instanceof Designer) {
			if(numOfDes >= 2) {
				throw new TeamException("团队中至多有两名设计师");
			}
		}else {
			if(numOfPro >= 3) {
				throw new TeamException("团队中至多有三名程序员");
			}
		}
		
		//将p（或e）添加到现有的team中
		team[total++] = p;
		//p的属性赋值
		p.setStatus(Status.BUSY);
		p.setMemberId(counter++);
		
	}
	/**
	 * 
	 * @Description: 判断指定的员工是否已经存在于现有的开发团队中
	 * @auther: T-wang 
	 * @param e
	 * @return
	 */
	private boolean isExist(Employee e) {
		for(int i = 0; i < total; i++) {
			if(team[i].getId()==e.getId()) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 
	 * @Description: 从团队中，删除成员
	 * @auther: T-wang 
	 * @param memberId
	 * @throws TeamException 
	 */
	public void removeMember(int memberId) throws TeamException {
		int i = 0;
		for(; i< total; i++) {
			if(team[i].getMemberId() == memberId) {
				team[i].setStatus(Status.FREE);
				break;
			}
		}
		
		//未找到指定memberId的情况
				if(i == total) {
					throw new TeamException("找不到指定memberID的员工，删除失败");
				}
		
		//后一个元素覆盖前一个元素，实现删除操作
		for(int j = i+1; j < total; j++) {
			team[j-1] = team[j];
		}
		
//		team[total-1] = null;
//		total--;
		//或
		team[--total] = null;
		
	}

}
