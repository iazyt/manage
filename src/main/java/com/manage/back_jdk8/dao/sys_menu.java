package com.manage.back_jdk8.dao;

import com.manage.back_jdk8.entity.SysMenu;
import com.manage.back_jdk8.entity.SysUser;
import org.apache.ibatis.annotations.*;

import java.util.List;
@Mapper
public interface sys_menu {
    @Select("select * from sys_menu;")
    List<SysMenu> getAll();

    @Select("select * from sys_menu where id=#{id};")
    SysMenu getById(Long id);

    @Select("select * from sys_menu where id=#{id};")
    List<SysMenu> getListById(Long id);

    @Insert("insert into sys_menu values(#{id},#{parent_id},#{name},#{path},#{perms},#{component},#{type},#{icon},#{orderNum},#{created},#{updated},#{statu});")
    void save(SysMenu t);

    @Update("update sys_menu set parent_id=#{parent_id},name=#{name},path=#{path},perms=#{perms},component=#{component},type=#{type},icon=#{icon},orderNum=#{orderNum},created=#{created},updated=#{updated},statu=#{statu} where id=#{id};")
    void updateById(SysMenu t);

    @Select("select count(*) count from sys_menu where parent_id=#{id};")
    Long count(Long id);

    @Delete("delete from sys_menu where id=#{id};")
    void deleteById(Long id);

}
