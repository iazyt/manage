package com.manage.back_jdk8;

import com.manage.back_jdk8.entity.Post;
import com.manage.back_jdk8.service.impl.PostServiceImpl;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class ApplicationTests {

	@Autowired
	StringRedisTemplate stringRedisTemplate;
	@Test
	void contextLoads() {

		System.out.println(stringRedisTemplate.opsForHash().get("JWT_Name","2"));
	}


}
