package com.manage.back_jdk8.Controller;

import com.manage.back_jdk8.common.lang.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/a")
public class test {


    int cnt;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @GetMapping("/1")
    public Result test() {// python脚本连接手机;
        System.out.println("JAVA YYDS");
        return null;
    }

}