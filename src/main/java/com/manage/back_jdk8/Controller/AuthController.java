/*
本项目分布式锁说明：
【使用分布式锁而不是MySQL事务悲观锁的原因】
    看例子：

    （事例1）管理员A发出请求A1，取消普通用户B的权限
	过了0.1秒后，普通用户B通过该权限发出请求B1

	这个问题当然可以通过A1先删表，然后B1再查表看有没有权限，没权限再返回。
	那么再看一个例子：

	（事例2）管理员A发出请求A1，取消普通用户B,C,D的权限
	过了0.1秒后，普通用户D通过该权限发出请求D1

	如果A1开始删表，还没删到D，那么D1很快会运行完成（D1进入方法后查表发现D还有权限，于是快速执行完成）。
	如果依然用事务，那么A1应该开启事务，然后给B,C,D三者加悲观锁。但是要知道MySQL速度并不快。很可能没给D加完锁，D1也执行完成了。
	所以这边就需要一个快速加锁的工具，还得是分布式锁，因为通过Redis实现，速度比MySQL快一个等级。
	Redis可以快速给B,C,D同时加上锁，然后D1就会被拦截。

	所以总体来说还是分布式锁好点。
	部分方法里，我觉得用MySQL事务里的悲观锁也没问题。
	但总体项目的锁要保持一致，所以还是选分布式锁。
【方法里并发的3个等级，需对相应等级的并发风险设计分布式锁】
    （0级，无锁，或者内部有锁自己实现）只有一条SQL语句、没有简单共享变量、只有select语句且加@Transactional注解
    解决方法：无需加锁。
    （1级，相当于第三隔离级别，并且防止相同方法的并发问题）有多条SQL语句且不符上述标准，并且相同方法调用有并发风险，不同方法之间无风险
    解决方法：对相同方法相同参数设置一个分布式锁，相同方法并发的时候抢夺。
    （2级，相当于第三隔离级别，并且防止不同方法的并发问题）有多条SQL语句且不符上述标准，不同方法之间有并发风险。
    解决方法：case by case
【小问题】
    一个方法删除记录的同时，另一个再添加记录，结果新加的记录被删了，这算什么呢？
 */



package com.manage.back_jdk8.Controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.map.MapUtil;
import com.google.code.kaptcha.Producer;
import com.manage.back_jdk8.common.lang.Const;
import com.manage.back_jdk8.common.lang.Result;
import com.manage.back_jdk8.dao.sys_user;
import com.manage.back_jdk8.entity.SysUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@RestController
public class AuthController{

    @Autowired
    Producer producer;


    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    sys_user sys_user_selections;

    //并发等级：0级
    @GetMapping("/captcha")
    public Result captcha(HttpServletRequest request) throws IOException {

        String ip=request.getRemoteAddr();
        String cache=stringRedisTemplate.opsForValue().get(ip);
        if(cache!=null)
            return Result.fail("60s之内已经请求过");


        String code = producer.createText();

        // 为了测试
        //key = "aaaaa";
        //code = "11111";

        BufferedImage image = producer.createImage(code);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);


        String str = "data:image/jpeg;base64,";

        Base64.Encoder encoder = Base64.getEncoder();
//            return encoder.encode(md5);
        String base64Img=str+encoder.encodeToString(outputStream.toByteArray());
        stringRedisTemplate.opsForValue().set(ip,code,1, TimeUnit.MINUTES);

        return Result.succ(
                MapUtil.builder()
                        .put("captchaImg", base64Img)
                        .build()

        );
    }

    //并发等级：0级
    @GetMapping("/sys/userInfo")
    public Result userInfo() {

        SysUser sysUser = sys_user_selections.getById(Long.valueOf((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal()));

        return Result.succ(MapUtil.builder()
                .put("id", sysUser.getId())
                .put("username", sysUser.getUsername())
                .put("avatar", sysUser.getAvatar())
                .put("created", sysUser.getCreated())
                .map()
        );
    }
}
