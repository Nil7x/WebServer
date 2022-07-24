package com.xu.blog.Controller.admin;


import com.xu.blog.entity.AdminUser;
import com.xu.blog.service.AdminUserService;


import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Resource
    private AdminUserService adminUserService;
    @GetMapping({"/login"})
    public String login() {
        return "admin/login";
    }

    @PostMapping(value = "/login")
    public String login(@RequestParam("userName")String userName,
                        @RequestParam("password")String password,
                        @RequestParam("verifyCode")String verifyCode,
                        HttpSession session) {
        if (StringUtils.isEmpty(verifyCode)) {
            session.setAttribute("errorMsg", "验证码为空");
            return "admin/login";
        }
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            session.setAttribute("errorMsg", "填写用户密码");
            return "admin/login";
        }
        String kaptchaCode = session.getAttribute("verifyCode") + "";
        if (StringUtils.isEmpty(kaptchaCode) || !verifyCode.equals(kaptchaCode)) {
            session.setAttribute("errorMsg", "验证码错误");
            return "admin/login";
        }
        AdminUser adminUser = adminUserService.login(userName, password);

        if (adminUser != null) {
            session.setAttribute("loginUser", adminUser.getNickName());
            session.setAttribute("loginUserId", adminUser.getAdminUserId());
            session.setMaxInactiveInterval(60 * 60 * 1);
            return "redirect:/admin/index";
        }else {
            session.setAttribute("errorMsg","登录失败");
            return "admin/login";
        }
  }
    @GetMapping({"", "/", "/index", "/index.html"})
    public String index() {
        return "admin/index";
    }

    @GetMapping("/profile")
    public String profile(HttpServletRequest request ){
        Integer loginUserId = (Integer) request.getSession().getAttribute("loginUserId");
        AdminUser adminUser = adminUserService.getUserDetailById(loginUserId);
        if(adminUser==null){
            return "/admin/login";
        }
        request.setAttribute("path","profile");
        request.setAttribute("loginUserName","loginUserName");
        request.setAttribute("nickName","nickName");
        return "/admin/profile";
    }
    @PostMapping("/profile/name")
    @ResponseBody
    public String nameUpdate(HttpServletRequest request,@RequestParam("loginUserName")String loginUserName,
                             @RequestParam("nickName")String nickName){
        if(loginUserName==null || nickName==null){
            return "/admin/login";
        }
        Integer loginUserId = (Integer) request.getSession().getAttribute("loginUserId");
        if(adminUserService.updateName(loginUserId,loginUserName,nickName)){
            return "success";
        }
        return "修改失败";
    }
    @PostMapping("/profile/password")
    @ResponseBody
    public String updatePassword(HttpServletRequest request,@RequestParam("originalPassword")String originalPassword,
                                 @RequestParam("newPassword")String newPassword){
        if(StringUtils.isEmpty(originalPassword)||StringUtils.isEmpty(newPassword)){
            return "密码不能为空";
        }
        Integer loginUserID = (Integer) request.getSession().getAttribute("loginUserID");
        if(adminUserService.updatePassword(loginUserID,originalPassword,newPassword)){
            request.getSession().removeAttribute("loginUser");
            request.getSession().removeAttribute("loginUserId");
            request.getSession().removeAttribute("errorMsg");
            return "/admin/login";
        }
        return "修改失败";
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
            request.getSession().removeAttribute("loginUserId");
            request.getSession().removeAttribute("loginUser");
            request.getSession().removeAttribute("errorMsg");
            return "/admin/login";
    }
}


