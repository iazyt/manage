package com.manage.back_jdk8.common.dto;

import com.manage.back_jdk8.entity.Post;
import com.manage.back_jdk8.entity.SysUser;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Page2 implements Serializable {


    List<Post> records;
    Long total;
    Long size;
    Long current;
}
