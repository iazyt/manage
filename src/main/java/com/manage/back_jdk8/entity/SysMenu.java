package com.manage.back_jdk8.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author 我的公众号：MarkerHub
 * @since 2021-04-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysMenu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 父菜单ID，一级菜单为0
     */

    private Long parent_id;


    private String name;

    /**
     * 菜单URL
     */
    private String path;

    /**
     * 授权(多个用逗号分隔，如：user:list,user:create)
     */

    private String perms;

    private String component;

    /**
     * 类型     0：目录   1：菜单   2：按钮
     */

    private Integer type;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 排序
     */

    private Integer orderNum;


    private List<SysMenu> children = new ArrayList<>();
}
