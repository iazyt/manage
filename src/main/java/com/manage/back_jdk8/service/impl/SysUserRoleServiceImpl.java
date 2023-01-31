package com.manage.back_jdk8.service.impl;

import com.manage.back_jdk8.dao.sys_user_role;
import com.manage.back_jdk8.entity.SysUser;
import com.manage.back_jdk8.entity.SysUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 我的公众号：MarkerHub
 * @since 2021-04-05
 */
@Service
public class SysUserRoleServiceImpl {
    @Autowired
    sys_user_role sys_user_role_selections;
    public void deleteByRoleId(Long role_id){
        sys_user_role_selections.deleteByRoleId(role_id);
    }

    public void deleteByUserId(Long user_id){
        sys_user_role_selections.deleteByUserId(user_id);
    }

    public List<SysUserRole> getListByUserId(Long user_id){
        return sys_user_role_selections.getListByUserId(user_id);
    }
    public List<Long> getRoleIdsByUserId(Long user_id){
        return sys_user_role_selections.getRoleIdsByUserId(user_id);
    }
    public void save(SysUserRole t){
        sys_user_role_selections.save(t);
    }
}
