package com.manage.back_jdk8.entity;

import lombok.Data;

@Data
public class Comment extends BaseEntity{
    private Long id;
    private Long author;
    private String content;
    private Long parent_id;
}