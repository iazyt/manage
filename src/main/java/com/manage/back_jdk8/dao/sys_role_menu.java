package com.manage.back_jdk8.dao;
import com.manage.back_jdk8.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface sys_role_menu {


    @Select("select * from sys_role_menu;")
    List<SysRoleMenu> getAll();



    @Select("select * from sys_role_menu where role_id=#{role_id};")
    List<SysRoleMenu> getListByRoleId(Long role_id);


    @Select("select * from sys_role_menu where user_id=#{user_id};")
    List<SysRoleMenu> getListByUserId(Long user_id);
    @Delete("delete from sys_role_menu where role_id=#{role_id};")
    void deleteByRoleId(Long role_id);

    @Delete("delete from sys_role_menu where menu_id=#{menu_id};")
    void deleteByMenuId(Long menu_id);

    @Insert("insert into sys_role_menu values(#{id},#{role_id},#{menu_id});")
    void save(SysRoleMenu t);


    @Select("select * from sys_role_menu where role_id=#{role_id} and menu_id=#{menu_id};")
    List<SysRoleMenu> getListByRoleIdAndMenuId(Long role_id,Long menu_id);
}