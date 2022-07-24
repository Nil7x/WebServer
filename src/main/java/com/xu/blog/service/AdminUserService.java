package com.xu.blog.service;


import com.xu.blog.entity.AdminUser;

public interface AdminUserService {
    AdminUser login(String userName,String password);
    //获取用户信息
    AdminUser getUserDetailById(Integer loginUserId);
    //修改密码
    Boolean updatePassword(Integer loginUserId, String originalPassword, String newPassword);
    //修改用户信息
    Boolean updateName(Integer loginUserId,String loginUserName,String nickName);
}
