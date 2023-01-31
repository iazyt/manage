package com.manage.back_jdk8.service.impl;

import com.manage.back_jdk8.dao.sys_role_menu;
import com.manage.back_jdk8.entity.SysRoleMenu;
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
public class SysRoleMenuServiceImpl {
    @Autowired
    sys_role_menu sys_role_menu_selections;


    public void deleteByMenuId(Long menu_id){
        sys_role_menu_selections.deleteByMenuId(menu_id);
    }

    public void deleteByRoleId(Long role_id){
        sys_role_menu_selections.deleteByRoleId(role_id);
    }
    public List<SysRoleMenu> getListByRoleId(Long role_id){
        return sys_role_menu_selections.getListByRoleId(role_id);
    }

    public List<SysRoleMenu> getListByUserId(Long user_id){
        return sys_role_menu_selections.getListByRoleId(user_id);
    }

    public void save(SysRoleMenu t){
        sys_role_menu_selections.save(t);
    }

    public boolean isHasRole(Long role_id,Long menu_id){
        return !sys_role_menu_selections.getListByRoleIdAndMenuId(role_id,menu_id).isEmpty();
    }


}
