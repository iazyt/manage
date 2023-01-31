package com.manage.back_jdk8.dao;

import com.manage.back_jdk8.entity.Post;
import com.manage.back_jdk8.entity.SysUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface post {
    @Insert("insert into post values(#{id},#{author},#{content});")
    void insert(Post t);

    @Delete("delete from post where id=#{id};")
    void deleteById(Long id);

    @Select("select * from post where id=#{id};")
    Post getById(Long id);

    @Select("select * from post;")
    List<Post> get();

    @Update("update post set content=#{content} where id=#{id};")
    void update(Post t);

    @Select("select * from post where content like '%${content}%' limit ${begin},${size};")
    List<Post> pageSelect(String content, Long begin, Long size);

}
