package com.manage.back_jdk8.security;

import com.manage.back_jdk8.dao.sys_user;
import com.manage.back_jdk8.entity.SysUser;

import com.manage.back_jdk8.service.impl.SysUserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

	@Autowired
	sys_user sys_user_selections;
	@Autowired
	SysUserServiceImpl sysUserService;
	@Autowired
	@Lazy
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		SysUser sysUser = sys_user_selections.getByUsername(username);
		if (sysUser == null) {//校验过程
			throw new UsernameNotFoundException("用户名或密码不正确");
		}
		sysUser.setPassword(bCryptPasswordEncoder.encode(sysUser.getPassword()));
		return new AccountUser(sysUser.getId(), username, sysUser.getPassword(), getUserAuthority(sysUser.getId()));
	}


	public List<GrantedAuthority> getUserAuthority(Long user_id){

		// 角色(ROLE_admin)、菜单操作权限 sys:user:list
		String authority = sysUserService.getUserAuthorityInfo(user_id);  // ROLE_admin,ROLE_normal,sys:user:list,....

		return AuthorityUtils.commaSeparatedStringToAuthorityList(authority);

	}
}
