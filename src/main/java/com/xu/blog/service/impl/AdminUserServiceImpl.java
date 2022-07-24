package com.xu.blog.service.impl;
import com.xu.blog.dao.AdminUserMapper;
import com.xu.blog.entity.AdminUser;
import com.xu.blog.service.AdminUserService;
import com.xu.blog.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class AdminUserServiceImpl implements AdminUserService {
    @Resource
    private AdminUserMapper adminUserMapper;
    @Override
    public AdminUser login(String userName, String password) {
        String passwordMd5 = "123456";
        return adminUserMapper.login(userName,passwordMd5);
    }

    @Override
    public AdminUser getUserDetailById(Integer loginUserId) {
        return adminUserMapper.selectByPrimaryKey(loginUserId);
    }

    @Override
    public Boolean updatePassword(Integer loginUserId, String originalPassword, String newPassword) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(loginUserId);
        //判断用户是否为空
        if(adminUser!=null){
            String oldPassword="123456";
            String newPassword1 = MD5Util.MD5Encode("newPassword", "UTF-8");
            //判断旧密码是否是登录进去的密码
            if(oldPassword.equals(adminUser.getLoginPassword())){
                //成功则设置密码
                adminUser.setLoginPassword(newPassword1);
                if(adminUserMapper.updateByPrimaryKeySelective(adminUser)>0){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Boolean updateName(Integer loginUserId, String loginUserName, String nickName) {
        AdminUser adminUser = adminUserMapper.selectByPrimaryKey(loginUserId);
        if(adminUser!=null){
            adminUser.setLoginUserName(loginUserName);
            adminUser.setNickName(nickName);
            if(adminUserMapper.updateByPrimaryKeySelective(adminUser)>0){
                return true;
            }
        }
        return false;
    }
}
