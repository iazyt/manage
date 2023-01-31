package com.manage.back_jdk8.entity;

import lombok.Data;

/*

create table post(id int,author int,content varchar(1000),likes int);
 */
@Data
public class Post extends BaseEntity{
    private Long id;
    private Long author;
    private String content;

}
