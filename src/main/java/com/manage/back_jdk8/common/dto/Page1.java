package com.manage.back_jdk8.common.dto;

import com.manage.back_jdk8.entity.SysRole;
import com.manage.back_jdk8.entity.SysUser;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Page1 implements Serializable {


    List<SysUser> records;
    Long total;
    Long size;
    Long current;
}
