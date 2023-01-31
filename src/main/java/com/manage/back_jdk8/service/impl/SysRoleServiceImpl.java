package com.manage.back_jdk8.service.impl;
import com.manage.back_jdk8.dao.sys_role;
import com.manage.back_jdk8.dao.sys_user_role;
import com.manage.back_jdk8.entity.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SysRoleServiceImpl{

	@Autowired
	sys_role sys_role_selections;


	public  SysRole getById(Long id){
		return sys_role_selections.getById(id);
	}

	public List<SysRole> pageSelect(String name,Long current,Long size){
		return sys_role_selections.pageSelect(name,current,size);
	}

	public void save(SysRole t){
		sys_role_selections.save(t);
	}

	public void updateById(SysRole t){
		sys_role_selections.updateById(t);
	}

	public void deleteById(Long id){
		sys_role_selections.deleteById(id);
	}
	public List<SysRole> getListById(Long id){
		return sys_role_selections.getListById(id);
	}
}
