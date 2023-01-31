package com.manage.back_jdk8.Controller;

import com.manage.back_jdk8.common.dto.Page1;
import com.manage.back_jdk8.common.dto.Page2;
import com.manage.back_jdk8.common.lang.Result;
import com.manage.back_jdk8.entity.*;
import com.manage.back_jdk8.service.impl.PostLikersServiceImpl;
import com.manage.back_jdk8.service.impl.PostServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import static cn.hutool.core.thread.ThreadUtil.sleep;

@RestController
@RequestMapping("/post")
public class PostController {
    /*
    主要对删除和点赞两个操作做分布式锁。
    点赞过程：①查询用户是否已经点赞过 ②如果没有点赞，就重新点赞。
    如果做完①，同时删除帖子，再做②，就有数据不一致的风险。
    所以对点赞过程加锁，获取这个锁才能点赞，删除也同样需要获取锁。
    
     */

    @Autowired
    RedissonClient redissonClient;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    PostServiceImpl postServiceImpl;

    @Autowired
    PostLikersServiceImpl postLikersService;

    @Scheduled(fixedRate = 5000)
    public void RedisToMySQL(){

        for(String id:stringRedisTemplate.opsForSet().members("ExistingPosts")){
            Set<String> copy=stringRedisTemplate.opsForSet().members("copy_"+id);
            Set<String> current=stringRedisTemplate.opsForSet().members(id);
            Set<String> all=new TreeSet<>();
            all.addAll(copy);
            all.addAll(current);
            for(String user_id :all){
                if(copy.contains(user_id)&&current.contains(user_id));
                else if(copy.contains(user_id)){
                    stringRedisTemplate.opsForSet().remove("copy_"+id,user_id);
                    postLikersService.deleteByUserId(Long.valueOf(user_id));

                }
                else {
                    stringRedisTemplate.opsForSet().add("copy_"+id,user_id);
                    PostLikers t=new PostLikers();
                    t.setId(Long.valueOf(id));
                    t.setUser_id(Long.valueOf(user_id));
                    postLikersService.save(t);
                }
            }
        }

    }
    @PostMapping("/save")
    public Result save(@Validated @RequestBody Post t){
        stringRedisTemplate.opsForSet().add("ExistingPosts", t.getId().toString());
        postServiceImpl.save(t);

        return Result.succ("");
    }
    @PostMapping("/del/{id}")
    public Result del(@PathVariable("id") Long id){
        RLock lk=redissonClient.getFairLock("Lock"+id.toString());
        try {
            stringRedisTemplate.opsForSet().remove("ExistingPosts",id.toString());
            lk.tryLock(5, 3, TimeUnit.SECONDS);
            sleep(2.1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            lk.unlock();
        }
        postServiceImpl.delete(id);
        return Result.succ("");
    }
    @PostMapping("/like/{id}")
    public Result likeBlog(@PathVariable("id") Long id) {
        // 1.获取登录用户
        if(!stringRedisTemplate.opsForSet().isMember("ExistingPosts",id.toString()))
            return Result.fail("帖子已被删除");

        RLock lk=redissonClient.getFairLock("Lock"+id.toString());

        try {
            Boolean isGetted = lk.tryLock(2, 1, TimeUnit.SECONDS);
            if (!isGetted)
                return Result.fail("帖子已被删除");
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        try {
            Long user_id=Long.valueOf((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            if (!stringRedisTemplate.opsForSet().isMember(id.toString(), user_id.toString())) {
                // 3.如果未点赞，可以点赞
                // 3.1.数据库点赞数 + 1
                // 3.2.保存用户到Redis的set集合  zadd key value score

                stringRedisTemplate.opsForSet().add(id.toString(), user_id.toString());

            } else {
                // 4.如果已点赞，取消点赞
                // 4.1.数据库点赞数 -1

                // 4.2.把用户从Redis的set集合移除

                stringRedisTemplate.opsForSet().remove(id.toString(), user_id.toString());

            }
        }
        finally {
            lk.unlock();
        }

        return Result.succ("");
    }
    @GetMapping("/total")
    public Result total() {
        return Result.succ(stringRedisTemplate.opsForSet().size("ExistingPosts"));
    }
    @GetMapping("/getLikes/{id}")
    public Result getLikes(@PathVariable("id") Long id){
        return Result.succ(stringRedisTemplate.opsForSet().size(id.toString()));
    }
    @GetMapping("/getLikers/{id}")
    public Result getLikers(@PathVariable("id") Long id){
        return Result.succ(stringRedisTemplate.opsForSet().members(id.toString()));
    }
    @GetMapping("/list/{content}/{current}/{size}")
    @Transactional
    public Result list(@PathVariable("content") String content,@PathVariable("current") Long current,@PathVariable("size") Long size) {
        if(content.equals("undefined"))
            content="";
        Page2 ans=new Page2();
        List<Post> t=postServiceImpl.pageSelect(content,(current-1)*size,size);
        ans.setRecords(t);
        ans.setCurrent(current);
        ans.setSize(size);
        Long total=((Integer)postServiceImpl.pageSelect(content,0L,10000000L).size()).longValue();
        ans.setTotal(total);

        return Result.succ(ans);
    }
    @PostMapping("/update")
    public Result update(@Validated @RequestBody Post t){
        postServiceImpl.update(t);
        return Result.succ("");
    }
    @GetMapping("/get/{id}")
    public Result get(@PathVariable("id") Long id){
        return Result.succ(postServiceImpl.getById(id));
    }



}
