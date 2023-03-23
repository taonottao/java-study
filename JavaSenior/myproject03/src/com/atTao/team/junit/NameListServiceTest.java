package com.atTao.team.junit;

import org.junit.Test;

import com.atTao.team.domain.Employee;
import com.atTao.team.service.NameListService;
import com.atTao.team.service.TeamException;

/**
 * 对NameListService类的测试
 * @Description
 * @author T-wang  Email:1609832760@qq.com
 * @verson
 * @date 2023年1月13日下午7:24:14
 */
public class NameListServiceTest {
	
	@Test
	public void testGetAllEmployees() {
		NameListService service = new NameListService();
		Employee[] employees = service.getAllEmployees();
		for(int i = 0; i < employees.length; i++) {
			System.out.println(employees[i]);
		}
	}
	
	@Test
	public void testGetEmployee() {
		NameListService service = new NameListService();
		int id = 1;
		id = 10;
		try {
			Employee employee = service.getEmployee(id);
			System.out.println(employee);
		} catch (TeamException e) {
			System.out.println(e.getMessage());
		}
	}
	
}
