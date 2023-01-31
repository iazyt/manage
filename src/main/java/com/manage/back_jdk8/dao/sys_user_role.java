package com.manage.back_jdk8.dao;

import com.manage.back_jdk8.entity.SysUser;
import com.manage.back_jdk8.entity.SysUserRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface sys_user_role {
    @Select("select * from sys_user_role;")
    List<SysUserRole> getAll();

    @Select("select role_id from sys_user_role where user_id=#{user_id};")
    List<Long> getRoleIdsByUserId(Long user_id);

    @Select("select * from sys_user_role where user_id=#{user_id};")
    List<SysUserRole> getListByUserId(Long user_id);

    @Delete("delete from sys_user_role where role_id=#{role_id};")
    void deleteByRoleId(Long role_id);

    @Delete("delete from sys_user_role where user_id=#{user_id};")
    void deleteByUserId(Long user_id);

    @Insert("insert into sys_user_role values(#{id},#{user_id},#{role_id});")
    void save(SysUserRole t);
}
