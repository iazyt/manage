package com.manage.back_jdk8.Controller;


import cn.hutool.core.util.StrUtil;
import com.manage.back_jdk8.common.dto.Page;
import com.manage.back_jdk8.common.lang.Const;
import com.manage.back_jdk8.common.lang.Result;
import com.manage.back_jdk8.entity.SysRole;
import com.manage.back_jdk8.entity.SysRoleMenu;
import com.manage.back_jdk8.service.impl.SysRoleMenuServiceImpl;
import com.manage.back_jdk8.service.impl.SysRoleServiceImpl;
import com.manage.back_jdk8.service.impl.SysUserRoleServiceImpl;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.lettuce.core.RedisClient;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/sys/role")
public class SysRoleController {
	@Autowired
	SysRoleServiceImpl sysRoleService;

	@Autowired
	SysRoleMenuServiceImpl sysRoleMenuService;

	@Autowired
	SysUserRoleServiceImpl sysUserRoleService;

	@Autowired
	RedissonClient redissonClient;



	//并发等级：0级
	@GetMapping("/info/{id}")
	@Transactional
	public Result info(@PathVariable("id") Long id) {

		SysRole sysRole = sysRoleService.getById(id);

		// 获取角色相关联的菜单id
		List<SysRoleMenu> roleMenus = sysRoleMenuService.getListByRoleId(id);

		List<Long> menuIds = roleMenus.stream().map(p -> p.getMenu_id()).collect(Collectors.toList());

		sysRole.setMenuIds(menuIds);
		return Result.succ(sysRole);
	}


	//并发等级：0级
	@GetMapping("/list/{name}/{current}/{size}")
	@Transactional
	public Result list(@PathVariable("name") String name,@PathVariable("current") Long current,@PathVariable("size") Long size) {
		if(name.equals("undefined"))
			name="";
		Page ans=new Page();
		List<SysRole> t=sysRoleService.pageSelect(name,(current-1)*size,size);
		ans.setRecords(t);
		ans.setCurrent(current);
		ans.setSize(size);
		Long total=((Integer)sysRoleService.pageSelect(name,0L,10000000L).size()).longValue();
		ans.setTotal(total);
		return Result.succ(ans);
	}

	//并发等级：0级
	@PostMapping("/save")
	public Result save(@Validated @RequestBody SysRole sysRole) {

		sysRole.setCreated(LocalDateTime.now());
		sysRole.setStatu(Const.STATUS_ON);

		sysRoleService.save(sysRole);
		return Result.succ(sysRole);
	}

	//并发等级：0级
	@PostMapping("/update")
	public Result update(@Validated @RequestBody SysRole sysRole) {

		sysRole.setUpdated(LocalDateTime.now());

		sysRoleService.updateById(sysRole);


		return Result.succ(sysRole);
	}

	//并发等级：2级
	//通过判断是否已经加锁防止死锁，同时兼顾并发
	@PostMapping("/delete")
	public Result info(@RequestBody Long[] ids) {
		//判断是否


		for(Long id:ids){
			RReadWriteLock lock=redissonClient.getReadWriteLock("sysRoleMenuService:deleteByRoleId:"+id.toString());
			lock.writeLock().lock();
		}
		for(Long id:ids) {
			// 删除中间表
			RLock lock=redissonClient.getFairLock("sysUserRoleService:deleteByRoleId:"+id.toString());
			lock.lock();
			lock=redissonClient.getFairLock("sysRoleService:deleteById:"+id.toString());
			lock.lock();

		}
		for(Long id:ids) {
			sysRoleMenuService.deleteByRoleId(id);
		}
		for(Long id:ids) {
			// 删除中间表
			sysUserRoleService.deleteByRoleId(id);
			sysRoleService.deleteById(id);
		}
		for(Long id:ids){
			RReadWriteLock lock=redissonClient.getReadWriteLock("sysRoleMenuService:deleteByRoleId:"+id.toString());
			lock.writeLock().unlock();
		}
		for(Long id:ids) {
			// 删除中间表
			RLock lock=redissonClient.getFairLock("sysUserRoleService:deleteByRoleId:"+id.toString());

				lock.unlock();
			lock=redissonClient.getFairLock("sysRoleService:deleteById:"+id.toString());

				lock.unlock();

		}
		return Result.succ("");
	}
	@GetMapping("/{role_id}/{menu_id}")
	public Boolean isHasRole(@PathVariable("role_id") Long role_id,@PathVariable("menu_id") Long menu_id){
		RReadWriteLock lock=redissonClient.getReadWriteLock("sysRoleMenuService:deleteByRoleId:"+role_id.toString());
		lock.readLock().lock();
		Boolean res=sysRoleMenuService.isHasRole(role_id,menu_id);
		lock.readLock().unlock();
		return res;
	}

	//并发等级：1级
	@PostMapping("/perm/{role_id}")
	public Result info(@PathVariable("role_id") Long role_id, @RequestBody Long[] menu_ids) {
		RLock lock=redissonClient.getFairLock("sysRoleMenuService:deleteByRoleId:"+role_id.toString());
		List<SysRoleMenu> sysRoleMenus = new ArrayList<>();

		Arrays.stream(menu_ids).forEach(menu_id -> {
			SysRoleMenu roleMenu = new SysRoleMenu();
			roleMenu.setMenu_id(menu_id);
			roleMenu.setRole_id(role_id);

			sysRoleMenus.add(roleMenu);
		});

		// 先删除原来的记录，再保存新的

		sysRoleMenuService.deleteByRoleId(role_id);
		for(SysRoleMenu sysRoleMenu:sysRoleMenus)
			sysRoleMenuService.save(sysRoleMenu);
		lock.unlock();
		return Result.succ("");
	}
}
