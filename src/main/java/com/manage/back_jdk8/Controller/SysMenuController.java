package com.manage.back_jdk8.Controller;

import cn.hutool.core.map.MapUtil;
import com.manage.back_jdk8.common.dto.SysMenuDto;
import com.manage.back_jdk8.common.lang.Result;
import com.manage.back_jdk8.entity.SysMenu;
import com.manage.back_jdk8.entity.SysUser;
import com.manage.back_jdk8.service.impl.SysMenuServiceImpl;
import com.manage.back_jdk8.service.impl.SysRoleMenuServiceImpl;
import com.manage.back_jdk8.service.impl.SysUserServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.beans.Transient;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/sys/menu")
public class SysMenuController {
	@Autowired
	SysMenuServiceImpl sysMenuService;

	@Autowired
	SysUserServiceImpl sysUserService;

	@Autowired
	SysRoleMenuServiceImpl sysRoleMenuService;
	@Autowired
	RedissonClient redissonClient;


	//并发等级：0级
	@GetMapping("/nav")
	@Transactional
	public Result nav() {
		SysUser sysUser = sysUserService.getById(Long.valueOf((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));

		// 获取权限信息
		String authorityInfo = sysUserService.getUserAuthorityInfo(sysUser.getId());// ROLE_admin,ROLE_normal,sys:user:list,....
		String[] authorityInfoArray = StringUtils.tokenizeToStringArray(authorityInfo, ",");

		// 获取导航栏信息
		List<SysMenuDto> navs = sysMenuService.getCurrentUserNav();

		return Result.succ(MapUtil.builder()
				.put("authoritys", authorityInfoArray)
				.put("nav", navs)
				.map()
		);
	}

	//并发等级：0级
	@GetMapping("/info/{id}")
	public Result info(@PathVariable(name = "id") Long id) {
		return Result.succ(sysMenuService.getById(id));
	}

	//并发等级：0级
	@GetMapping("/list")
	public Result list() {
		List<SysMenu> menus = sysMenuService.tree();
		return Result.succ(menus);
	}

	//并发等级：0级
	@PostMapping("/save")
	public Result save(@Validated @RequestBody SysMenu sysMenu) {
		sysMenu.setCreated(LocalDateTime.now());

		sysMenuService.save(sysMenu);
		return Result.succ(sysMenu);
	}

	//并发等级：0级
	@PostMapping("/update")
	public Result update(@Validated @RequestBody SysMenu sysMenu) {
		sysMenu.setUpdated(LocalDateTime.now());

		sysMenuService.updateById(sysMenu);

		return Result.succ(sysMenu);
	}

	//并发等级：1级
	@PostMapping("/delete/{id}")
	public Result delete(@PathVariable("id") Long id) {
		RLock lock=redissonClient.getFairLock("sysMenuService:count:"+id.toString());
		lock.lock();

		Long count = sysMenuService.count(id);
		if (count > 0) {
			return Result.fail("请先删除子菜单");
		}


		sysMenuService.deleteById(id);

		// 同步删除中间关联表
		sysRoleMenuService.deleteByMenuId(id);
		lock.unlock();
		return Result.succ("");
	}

}
