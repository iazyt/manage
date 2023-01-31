package com.manage.back_jdk8.dao;

import com.manage.back_jdk8.entity.PostLikers;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface postLikers {

    @Select("select * from postLikers where id=#{id};")
    List<PostLikers> getListById(Long id);

    @Insert("insert into postLikers values(#{id},#{user_id});")
    void save(PostLikers t);
    @Delete("delete from postLikers where user_id=#{user_id};")
    void deleteByUserId(Long user_id);
}
