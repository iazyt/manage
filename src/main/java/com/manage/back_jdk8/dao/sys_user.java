package com.manage.back_jdk8.dao;
import com.manage.back_jdk8.entity.SysRole;
import com.manage.back_jdk8.entity.SysRoleMenu;
import com.manage.back_jdk8.entity.SysUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface sys_user {


    @Select("select * from sys_user;")
    List<SysUser> getAll();

    @Select("select * from sys_user where username=#{username}")
    SysUser getByUsername(String username);


    @Select("select * from sys_user where id=#{id}")
    SysUser getById(Long id);

    @Insert("insert into sys_user values(#{id},#{username},#{password},#{avatar},#{email},#{city},#{created},#{updated},#{last_login},#{statu});")
    void save(SysUser t);

    @Update("update sys_user set username=#{username},password=#{password},avatar=#{avatar},email=#{email},city=#{city},created=#{created},updated=#{updated},last_login=#{last_login},statu=#{statu} where id=#{id};")
    void updateById(SysUser t);

    @Delete("delete from sys_user where id=#{id}")
    void deleteById(Long id);
    @Select("SELECT DISTINCT rm.menu_id  FROM sys_user_role ur LEFT JOIN sys_role_menu rm ON ur.role_id = rm.role_id WHERE ur.user_id = #{userId}")
    List<Long> getNavMenuIds(Long userId);

    @Select("select * from sys_user where username like '%${username}%' limit ${begin},${size};")
    List<SysUser> pageSelect(String username, Long begin, Long size);


}