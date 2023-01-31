package com.manage.back_jdk8.Controller;


import cn.hutool.core.util.StrUtil;
import com.manage.back_jdk8.common.dto.Page;
import com.manage.back_jdk8.common.dto.Page1;
import com.manage.back_jdk8.common.dto.PassDto;
import com.manage.back_jdk8.common.lang.Const;
import com.manage.back_jdk8.common.lang.Result;
import com.manage.back_jdk8.entity.*;
import com.manage.back_jdk8.service.impl.SysRoleMenuServiceImpl;
import com.manage.back_jdk8.service.impl.SysRoleServiceImpl;
import com.manage.back_jdk8.service.impl.SysUserRoleServiceImpl;
import com.manage.back_jdk8.service.impl.SysUserServiceImpl;
import com.manage.back_jdk8.untils.Safe;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/sys/user")
public class SysUserController  {
	@Autowired
	Safe safe;
	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Autowired
	SysUserServiceImpl sysUserService;

	@Autowired
	SysRoleServiceImpl sysRoleService;

	@Autowired
	SysUserRoleServiceImpl sysUserRoleService;

	@Autowired
	SysRoleMenuServiceImpl sysRoleMenuService;

	@Autowired
	RedissonClient redissonClient;

	//并发等级：0级
	@GetMapping("/info/{id}")
	@Transactional
	public Result info(@PathVariable("id") Long id) {

		SysUser sysUser = sysUserService.getById(id);
		Assert.notNull(sysUser, "找不到该管理员");

		List<Long> roleIds = sysUserRoleService.getRoleIdsByUserId(id);
		List<SysRole>roles=new ArrayList<>();
		for(Long roldId:roleIds){
			roles.add(sysRoleService.getById(roldId));
		}

		sysUser.setSysRoles(roles);
		return Result.succ(sysUser);
	}

	//并发等级：0级
	@GetMapping("/list/{name}/{current}/{size}")
	@Transactional
	public Result list(@PathVariable("name") String name,@PathVariable("current") Long current,@PathVariable("size") Long size) {
		if(name.equals("undefined"))
			name="";
		Page1 ans=new Page1();
		List<SysUser> t=sysUserService.pageSelect(name,(current-1)*size,size);
		ans.setRecords(t);
		ans.setCurrent(current);
		ans.setSize(size);
		Long total=((Integer)sysRoleService.pageSelect(name,0L,10000000L).size()).longValue();
		ans.setTotal(total);




		ans.getRecords().forEach(sysUser -> {
			List<Long> roleIds = sysUserRoleService.getRoleIdsByUserId(sysUser.getId());
			List<SysRole>roles=new ArrayList<>();
			for(Long roldId:roleIds){
				roles.add(sysRoleService.getById(roldId));
			}

			sysUser.setSysRoles(roles);
		});

		return Result.succ(ans);
	}

	//并发等级：0级
	@PostMapping("/save")
	public Result save(@Validated @RequestBody SysUser sysUser) {

		sysUser.setCreated(LocalDateTime.now());
		sysUser.setStatu(Const.STATUS_ON);

		// 默认密码
		String password = Const.DEFULT_PASSWORD;
		sysUser.setPassword(password);

		// 默认头像
		sysUser.setAvatar(Const.DEFULT_AVATAR);

		sysUserService.save(sysUser);
		return Result.succ("");
	}

	//并发等级：0级
	@PostMapping("/update")
	public Result update(@Validated @RequestBody SysUser sysUser) {

		sysUser.setUpdated(LocalDateTime.now());

		sysUserService.updateById(sysUser);
		return Result.succ("");
	}


	//并发等级：2级，但此接口流量过低，故先不考虑加分布式锁。
	@PostMapping("/delete")
	public Result delete(@RequestBody Long[] ids) {
		for(Long id:ids) {
			sysUserService.deleteById(id);
			sysUserRoleService.deleteByUserId(id);
		}
		return Result.succ("");
	}

	//并发等级：1级
	@PostMapping("/role/{user_id}")
	public Result rolePerm(@PathVariable("user_id") Long user_id, @RequestBody Long[] role_ids) {
		RLock lock=redissonClient.getFairLock("sysUserRoleService:deleteByUserId:"+user_id.toString());
		lock.lock();
		List<SysUserRole> userRoles = new ArrayList<>();

		Arrays.stream(role_ids).forEach(r -> {
			SysUserRole sysUserRole = new SysUserRole();
			sysUserRole.setRole_id(r);
			sysUserRole.setUser_id(user_id);

			userRoles.add(sysUserRole);
		});
		sysUserRoleService.deleteByUserId(user_id);
		for(SysUserRole sysUserRole:userRoles){
			sysUserRoleService.save(sysUserRole);
		}

		lock.unlock();
		return Result.succ("");
	}
	//并发等级：1级，和下面的方法属于同一类
	@PostMapping("/repass")
	public Result repass(@RequestBody Long user_id) {
		RLock lock=redissonClient.getFairLock("sysUserService:updateById:"+user_id);
		lock.lock();

		SysUser sysUser = sysUserService.getById(user_id);

		sysUser.setPassword(Const.DEFULT_PASSWORD);
		sysUser.setUpdated(LocalDateTime.now());

		sysUserService.updateById(sysUser);
		lock.unlock();
		return Result.succ("");
	}

	//并发等级：1级
	//这边可以额外说一个锁粒度的问题。就是这个方法涉及2个变量：原密码，旧密码，应当一开始就锁住2个资源，而不是说等比较完再上锁。
	@PostMapping("/updatePass")
	public Result updatePass(@Validated @RequestBody PassDto passDto) {

		SysUser sysUser = sysUserService.getById(Long.valueOf((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));
		RLock lock = redissonClient.getFairLock("sysUserService:updateById:" + sysUser.getId());
		try {

			lock.lock();

			if (!safe.safeEquals(passDto.getCurrentPass(),sysUser.getPassword())) {
				return Result.fail("旧密码不正确");
			}

			sysUser.setPassword(passDto.getPassword());
			sysUser.setUpdated(LocalDateTime.now());

			sysUserService.updateById(sysUser);
		}
		finally {
			lock.unlock();
		}

		return Result.succ("");
	}
}
