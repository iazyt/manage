package com.manage.back_jdk8.untils;

import io.jsonwebtoken.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@Component
@ConfigurationProperties(prefix = "manage.jwt")
public class JwtUtils {

    private long expire;
    private String secret;
    private String header;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    // 生成jwt
    public String generateToken(String username,String ip) {
        Date nowDate = new Date();
        Date expireDate1 = new Date(nowDate.getTime() + 1000 * 60);
        String jwt1=Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(username)
                .setAudience(ip)
                .setExpiration(expireDate1)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
        Date expireDate2 = new Date(nowDate.getTime() + 1000 * 180);
        String jwt2=Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(username)
                .setAudience(ip)
                .setExpiration(expireDate2)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
        String jwt=jwt1+" "+jwt2;


        return jwt;
    }

    public String generateTestToken(String username,String ip) {
        Date nowDate = new Date();
        Date expireDate = new Date(nowDate.getTime() + 1000 * 10);
        String jwt=Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(username)
                .setAudience(ip)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

        return jwt;
    }

    // 解析jwt
    public Claims getClaimByToken(String jwt) {
        try {
            Claims claims=Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(jwt)
                    .getBody();
            return claims;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }



}
