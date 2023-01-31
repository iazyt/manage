package com.manage.back_jdk8.security;


import com.manage.back_jdk8.common.exception.CaptchaException;
import com.manage.back_jdk8.common.lang.Const;
import com.manage.back_jdk8.untils.Safe;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CaptchaFilter extends OncePerRequestFilter {
    @Autowired
    Safe safe;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    LoginFailureHandler loginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String url = httpServletRequest.getRequestURI();
        System.out.println(url+"   "+httpServletRequest.getRemoteAddr()+"    "+httpServletRequest.getRemotePort());
        if ("/login".equals(url) && httpServletRequest.getMethod().equals("POST")) {

            try{
                // 校验验证码

                validate(httpServletRequest);
            } catch (CaptchaException e) {

                // 交给认证失败处理器
                loginFailureHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, e);
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    // 校验验证码逻辑
    private void validate(HttpServletRequest request) {

        String code = request.getParameter("code");

        if (StringUtils.isBlank(code) ) {
            throw new CaptchaException("验证码错误");
        }
        String ip=request.getRemoteAddr();
        System.out.println(code+"    "+stringRedisTemplate.opsForValue().get(ip));
        if (!safe.safeEquals(code,(String) stringRedisTemplate.opsForValue().get(ip))) {
            throw new CaptchaException("验证码错误");
        }

        // 一次性使用
        //redisUtil.hdel(Const.CAPTCHA_KEY, key);
    }
}
