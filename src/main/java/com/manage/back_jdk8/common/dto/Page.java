package com.manage.back_jdk8.common.dto;

import com.manage.back_jdk8.entity.SysRole;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Page implements Serializable {


    List<SysRole> records;
    Long total;
    Long size;
    Long current;
}
