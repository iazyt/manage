package com.manage.back_jdk8.Controller;

import com.manage.back_jdk8.common.lang.Result;
import com.manage.back_jdk8.untils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/jwt")
public class JwtController {
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @GetMapping("/getNewTokens/{jwt}")
    public Result get(@PathVariable("jwt") String jwt){
        Claims claims=jwtUtils.getClaimByToken(jwt);
        String username=claims.getSubject();
        String ip=claims.getAudience();
        return Result.succ(jwtUtils.generateToken(username,ip));
    }

    @GetMapping("/getTestTokens/{username}")
    public Result getTest(@PathVariable("username") String username,HttpServletRequest request){
        String ip=request.getRemoteAddr();

        return Result.succ(jwtUtils.generateTestToken(username,ip));
    }
}
