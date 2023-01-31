package com.manage.back_jdk8.service.impl;

import cn.hutool.json.JSONUtil;
import com.manage.back_jdk8.common.dto.SysMenuDto;
import com.manage.back_jdk8.dao.sys_menu;
import com.manage.back_jdk8.dao.sys_user;
import com.manage.back_jdk8.entity.SysMenu;
import com.manage.back_jdk8.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 我的公众号：MarkerHub
 * @since 2021-04-05
 */
@Service
public class SysMenuServiceImpl{
	@Autowired
	sys_user sys_user_selections;

	@Autowired
	sys_menu sys_menu_selections;
	@Autowired
	SysUserServiceImpl sysUserService;

	public SysMenu getById(Long id){
		return sys_menu_selections.getById(id);
	}

	public void save(SysMenu t){
		sys_menu_selections.save(t);
	}
	public void updateById(SysMenu t){
		sys_menu_selections.updateById(t);
	}

	public Long count(Long id){
		return sys_menu_selections.count(id);
	}

	public void deleteById(Long id){
		sys_menu_selections.deleteById(id);
	}

	public List<SysMenu> tree(){
		return buildTreeMenu(sys_menu_selections.getAll());
	}
	public List<SysMenuDto> getCurrentUserNav() {
		String id = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		SysUser sysUser = sysUserService.getById(Long.valueOf(id));

		List<Long> menuIds = sys_user_selections.getNavMenuIds(sysUser.getId());

		List<SysMenu> menus = new ArrayList<>();
		for(Long x:menuIds){
			menus.addAll(sys_menu_selections.getListById(x));
		}
		// 转树状结构
		List<SysMenu> menuTree = buildTreeMenu(menus);

		// 实体转DTO
		return convert(menuTree);
	}



	private List<SysMenuDto> convert(List<SysMenu> menuTree) {
		List<SysMenuDto> menuDtos = new ArrayList<>();

		menuTree.forEach(m -> {
			SysMenuDto dto = new SysMenuDto();

			dto.setId(m.getId());
			dto.setName(m.getPerms());
			dto.setTitle(m.getName());
			dto.setComponent(m.getComponent());
			dto.setPath(m.getPath());

			if (m.getChildren().size() > 0) {

				// 子节点调用当前方法进行再次转换
				dto.setChildren(convert(m.getChildren()));
			}

			menuDtos.add(dto);
		});

		return menuDtos;
	}

	private List<SysMenu> buildTreeMenu(List<SysMenu> menus) {

		List<SysMenu> finalMenus = new ArrayList<>();

		// 先各自寻找到各自的孩子
		for (SysMenu menu : menus) {

			for (SysMenu e : menus) {
				if (menu.getId() == e.getParent_id())
					menu.getChildren().add(e);
				}


			// 提取出父节点
			if (menu.getParent_id() == 0) {
				finalMenus.add(menu);
			}
		}

		return finalMenus;
	}
}
