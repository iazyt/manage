package com.manage.back_jdk8.service.impl;

import com.manage.back_jdk8.dao.sys_menu;
import com.manage.back_jdk8.dao.sys_role;
import com.manage.back_jdk8.dao.sys_user;
import com.manage.back_jdk8.entity.SysMenu;
import com.manage.back_jdk8.entity.SysRole;
import com.manage.back_jdk8.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysUserServiceImpl {
    @Autowired
    sys_user sys_user_selections;
    @Autowired
    sys_role sys_role_selections;

    @Autowired
    sys_menu sys_menu_selections;
    public List<SysUser> pageSelect(String username,Long current,Long size){
        return sys_user_selections.pageSelect(username,current,size);
    }

    public void save(SysUser t){
         sys_user_selections.save(t);
    }
    public void updateById(SysUser t){
        sys_user_selections.updateById(t);
    }
    public void deleteById(Long id){
        sys_user_selections.deleteById(id);
    }
    public SysUser getByUsername(String username){
        return sys_user_selections.getByUsername(username);
    }

    public SysUser getById(Long id){
        return sys_user_selections.getById(id);
    }
    public String getUserAuthorityInfo(Long id){
        SysUser sysUser =sys_user_selections.getById(id);
        String authority = "";
        List<SysRole> roles = sys_role_selections.getListById(id);


        if (roles.size() > 0) {
            String roleCodes = roles.stream().map(r -> "ROLE_" + r.getCode()).collect(Collectors.joining(","));
            authority = roleCodes.concat(",");
        }

        // 获取菜单操作编码
        List<Long> menuIds = sys_user_selections.getNavMenuIds(id);
        if (menuIds.size() > 0) {

            List<SysMenu> menus = new ArrayList<>();
            for(Long x:menuIds){
                menus.addAll(sys_menu_selections.getListById(x));
            }
            String menuPerms = menus.stream().map(m -> m.getPerms()).collect(Collectors.joining(","));

            authority = authority.concat(menuPerms);

        }
        return authority;
    }
}
