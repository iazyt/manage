package com.manage.back_jdk8.dao;

import com.manage.back_jdk8.entity.SysRole;
import com.manage.back_jdk8.entity.SysUser;
import org.apache.ibatis.annotations.*;

import java.util.List;
@Mapper
public interface sys_role {
    @Select("select * from sys_role;")
    List<SysRole> getAll();

    @Select("select * from sys_role where id=#{id};")
    List<SysRole> getListById(Long id);

    @Select("select * from sys_role where id=#{id};")
    SysRole getById(Long id);


    @Select("select * from sys_role where name like '%${name}%' limit ${begin},${size};")
    List<SysRole> pageSelect(String name,Long begin,Long size);

    @Insert("insert into sys_role values(#{id},#{name},#{code},#{remark},#{created},#{updated},#{statu});")
    void save(SysRole t);

    @Update("update sys_role set name=#{name},code=#{code},remark=#{remark},created=#{created},updated=#{updated},statu=#{statu} where id=#{id};")
    void updateById(SysRole t);

    @Delete("delete from sys_role where id=#{id};")
    void deleteById(Long id);
}
