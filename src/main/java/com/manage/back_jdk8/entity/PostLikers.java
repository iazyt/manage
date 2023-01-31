package com.manage.back_jdk8.entity;

import lombok.Data;

@Data
public class PostLikers extends BaseEntity{
    private Long id;
    private Long user_id;
}
