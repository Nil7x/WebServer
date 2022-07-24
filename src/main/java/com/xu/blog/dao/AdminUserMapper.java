package com.xu.blog.dao;

import com.xu.blog.entity.AdminUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface AdminUserMapper {
    AdminUser login(@Param("userName")String userName,@Param("password")String password);
    AdminUser selectByPrimaryKey(Integer adminUserId);
    AdminUser insert(AdminUser adminUser);
    AdminUser insertSelective(AdminUser adminUser);
    int updateByPrimaryKeySelective(AdminUser adminUser);
    AdminUser updateByPrimaryKey(AdminUser adminUser);

}
