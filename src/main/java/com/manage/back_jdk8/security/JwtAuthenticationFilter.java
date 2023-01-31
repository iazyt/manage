package com.manage.back_jdk8.security;

import cn.hutool.core.util.StrUtil;
import com.manage.back_jdk8.dao.sys_user;
import com.manage.back_jdk8.entity.SysUser;
import com.manage.back_jdk8.service.impl.SysRoleServiceImpl;
import com.manage.back_jdk8.service.impl.SysUserServiceImpl;
import com.manage.back_jdk8.untils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	UserDetailServiceImpl userDetailService;

	@Autowired
	SysUserServiceImpl sysUserService;


	public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

		String jwt = request.getHeader(jwtUtils.getHeader());
		if (StrUtil.isBlankOrUndefined(jwt)) {
			chain.doFilter(request, response);
			return;
		}
		Claims claim = jwtUtils.getClaimByToken(jwt);
		//System.out.println(claim.getAudience().equals(request.getRemoteAddr()));

		if (claim == null || !claim.getAudience().equals(request.getRemoteAddr())) {
			throw new JwtException("token异常或者已经失效");
		}


		String username = claim.getSubject();

		SysUser sysUser = sysUserService.getByUsername(username);

		UsernamePasswordAuthenticationToken token
				= new UsernamePasswordAuthenticationToken(sysUser.getId().toString(), null, userDetailService.getUserAuthority(sysUser.getId()));

		SecurityContextHolder.getContext().setAuthentication(token);

		chain.doFilter(request, response);
	}
}
