package com.atTao.team.service;

import com.atTao.team.domain.Architect;
import com.atTao.team.domain.Designer;
import com.atTao.team.domain.Employee;
import com.atTao.team.domain.Equipment;
import com.atTao.team.domain.NoteBook;
import com.atTao.team.domain.PC;
import com.atTao.team.domain.Printer;
import com.atTao.team.domain.Programmer;

import static com.atTao.team.service.Data.*;


/**
 * 
 * @Description 负责将Data中的数据封装到Employee[]数组中，同时提供相关操作Employee[]的方法
 * @author T-wang  Email:1609832760@qq.com
 * @verson v1.0
 * @date 2023年1月13日下午3:27:18
 */
public class NameListService {
	
	private Employee[] employees;
	
	/**
	 * 给employees及数组元素进行初始化
	 */
	public NameListService() {
//		1.根据项目提供的Data类构建相应大小的employees数组
//		2.再根据Data类中的数据构建不同的对象，包括Employee、Programmer、Designer和Architect对象，以及相关联的Equipment子类的对象
//		3.将对象存于数组中
		employees = new Employee[EMPLOYEES.length];
		for(int i = 0; i <EMPLOYEES.length; i++) {
			//获取员工的类型
			int type = Integer.parseInt(EMPLOYEES[i][0]);
			
			//获取Employee的4个基本信息
			int id = Integer.parseInt(EMPLOYEES[i][1]);
			String name = EMPLOYEES[i][2];
			int age = Integer.parseInt(EMPLOYEES[i][3]);
			double salary = Double.parseDouble(EMPLOYEES[i][4]);
			
			Equipment equipment;
			double bonus;
			int stock;
			
			switch(type){
				case EMPLOYEE:
					employees[i] = new Employee(id, name, age, salary);
					break;
				case PROGRAMMER:
					equipment = createEquipment(i);
					employees[i] = new Programmer(id, name, age, salary, equipment);
					break;
				case DESIGNER:
					equipment = createEquipment(i);
					bonus = Double.parseDouble(EMPLOYEES[i][5]);
					employees[i] = new Designer(id, name, age, salary, equipment, bonus);
					break;
				case ARCHITECT:
					equipment = createEquipment(i);
					bonus = Double.parseDouble(EMPLOYEES[i][5]);
					stock = Integer.parseInt(EMPLOYEES[i][6]);
					employees[i] = new Architect(id, name, age, salary, equipment, bonus, stock);
					break;
			}
		}

	}
	
	/**
	 * 
	 * @Description: 获取指定index位置上的员工的设备
	 * @auther: T-wang 
	 * @param index
	 * @return
	 */
	private Equipment createEquipment(int index) {
		int key = Integer.parseInt(EQUIPMENTS[index][0]);
		
		String modelOrName = EQUIPMENTS[index][1];
		switch(key) {
		case PC://21
			
			return new PC(modelOrName, EQUIPMENTS[index][2]);
		case NOTEBOOK://22
			double price = Double.parseDouble(EQUIPMENTS[index][2]);
			return new NoteBook(modelOrName, price);
		case PRINTER://23
			String type = EQUIPMENTS[index][2];
			return new Printer(modelOrName, type);
		}
		return null;
	}
	/**
	 * 
	 * @Description: 获取当前所有的员工
	 * @auther: T-wang 
	 * @return
	 */
	public Employee[] getAllEmployees() {
		return employees;
	}
	/**
	 * 
	 * @Description: 获取指定ID的员工对象。
	 * @auther: T-wang 
	 * @param id
	 * @return
	 * @throws TeamException 
	 */
	public Employee getEmployee(int id) throws TeamException {
		for(int i = 0; i < employees.length; i++) {
			if(employees[i].getId() == id) {
				return employees[i];
			}
		}
		
		throw new TeamException("找不到指定员工"); 
	}
	
}
