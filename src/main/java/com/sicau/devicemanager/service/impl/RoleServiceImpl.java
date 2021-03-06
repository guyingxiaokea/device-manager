package com.sicau.devicemanager.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sicau.devicemanager.POJO.DO.Role;
import com.sicau.devicemanager.POJO.DO.RolePermission;
import com.sicau.devicemanager.POJO.DTO.QueryPage;
import com.sicau.devicemanager.POJO.DTO.RoleAddDTO;
import com.sicau.devicemanager.POJO.DTO.RoleDTO;
import com.sicau.devicemanager.dao.RoleMapper;
import com.sicau.devicemanager.dao.UserRoleMapper;
import com.sicau.devicemanager.service.RoleService;
import com.sicau.devicemanager.util.KeyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author BeFondOfTaro
 * Created at 19:08 2018/5/14
 */
@Service
public class RoleServiceImpl implements RoleService{

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void addRole(RoleAddDTO roleAddDTO) {
        //角色表写入
        Role role = new Role();
        String roleId = KeyUtil.genUniqueKey();
        BeanUtils.copyProperties(roleAddDTO,role);
        role.setId(roleId);
        roleMapper.insertRole(role);
        //角色权限写入
        RolePermission rolePermission = new RolePermission();
        for (String permissionId : roleAddDTO.getPermissionIdList()){
            rolePermission.setId(KeyUtil.genUniqueKey());
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            roleMapper.insertRolePermission(rolePermission);
        }
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void deleteRoleById(String roleId) {
        //删除用户与角色的关联信息
        userRoleMapper.deleteUserRoleByRoleId(roleId);
        //删除角色权限关联信息
        roleMapper.deleteRolePermissionByRoleId(roleId);
        //删除角色
        roleMapper.deleteRoleById(roleId);
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void updateRolePermission(String roleId, List<String> permissionIdList) {
        //删除原来的权限
        roleMapper.deleteRolePermissionByRoleId(roleId);
        //循环添加要更新权限
        RolePermission rolePermission = new RolePermission();
        for (String permissionId : permissionIdList){
            rolePermission.setId(KeyUtil.genUniqueKey());
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            roleMapper.insertRolePermission(rolePermission);
        }
    }

    @Override
    public PageInfo<RoleDTO> listRole(QueryPage queryPage) {
        PageHelper.startPage(queryPage.getPageNum(),queryPage.getPageSize(),"create_time");
        return new PageInfo<>(roleMapper.listRole());
    }
}
