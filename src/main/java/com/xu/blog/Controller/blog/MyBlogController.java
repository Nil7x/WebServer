package com.xu.blog.Controller.blog;

import com.xu.blog.Controller.vo.BlogDetailVO;
import com.xu.blog.entity.BlogComment;
import com.xu.blog.service.BlogService;
import com.xu.blog.service.CommentService;
import com.xu.blog.service.TagService;
import com.xu.blog.utils.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class MyBlogController {
    @Resource
    private BlogService blogService;
    @Resource
    private TagService tagService;
    @Resource
    private CommentService commentService;

    //首页，获取第一页数据
    @GetMapping({"/", "/index", "index.html"})
    public String index(HttpServletRequest request) {
        return this.page(request, 1);
    }

    //首页，分页数据
    @GetMapping("/page/{pageNum}")
    public String page(HttpServletRequest request, @PathVariable("pageNum") int pageNum) {
        PageResult result = blogService.getBlogsForIndexPage(pageNum);
        if (result == null) {
            return "error/error_404";
        }
        request.setAttribute("blogPageResult", result);
        request.setAttribute("newBlogs", blogService.getBlogListForIndexPage(1));
        request.setAttribute("hotBlogs", blogService.getBlogListForIndexPage(0));
        request.setAttribute("hotTags", tagService.getBlogTagCountForIndex());
        request.setAttribute("pageName", "首页");
        return "blog/index";
    }

    @GetMapping("/search/{keyword}")
    public String search(HttpServletRequest request, @PathVariable("keyword") String keyword) {
        return search(request, keyword, 1);
    }

    @GetMapping("/search/{keyword}/{page}")
    public String search(HttpServletRequest request, @PathVariable("keyword") String keyword, @PathVariable("page") Integer page) {
        PageResult pageResult = blogService.getBlogsPageBySearch(keyword, page);
        request.setAttribute("blogPageResult", pageResult);
        request.setAttribute("pageName", "搜索");
        request.setAttribute("pageUrl", "search");
        request.setAttribute("keyword", keyword);
        return "blog/list";
    }

    @GetMapping("/category/{categoryName}")
    public String category(HttpServletRequest request, @PathVariable("categoryName") String categoryName) {
        return category(request, categoryName, 1);
    }

    @GetMapping("/category/{categoryName}/{page}")
    public String category(HttpServletRequest request, @PathVariable("categoryName") String categoryName, @PathVariable("page") Integer page) {
        PageResult pageResult = blogService.getBlogPageByCategory(categoryName, page);
        request.setAttribute("blogPageResult", pageResult);
        request.setAttribute("pageName", "分类");
        request.setAttribute("pageUrl", "category");
        request.setAttribute("keyword", categoryName);
        return "blog/list";
    }

    @GetMapping("/tag/{tagName}")
    public String tag(HttpServletRequest request, @PathVariable("tagName") String tagName) {
        return tag(request,tagName, 1);
    }

    @GetMapping("/tag/{tagName}/{page}")
    public String tag(HttpServletRequest request, @PathVariable("tagName") String tagName, @PathVariable("page") Integer page) {
        PageResult blog = blogService.getBlogsPageByTag(tagName, page);
        request.setAttribute("blogPageResult", blog);
        request.setAttribute("pageName", "标签");
        request.setAttribute("pageUrl", "tag");
        request.setAttribute("keyword", tagName);
        return "blog/list";
    }

    @GetMapping("/blog/{blogId}")
    public String detail(HttpServletRequest request,@PathVariable("blogId")Long blogId,@RequestParam(value = "commentPage",required = false,defaultValue = "1")Integer commentPage){
        BlogDetailVO blogDetail = blogService.getBlogDetail(blogId);
        if(blogDetail!=null){
            request.setAttribute("blogDetailVO",blogDetail);
            request.setAttribute("commentPageResult",commentService.getCommentPageByBlogIdAndPageNum(blogId,commentPage));
        }
        request.setAttribute("pageName","详情");
        return "blog/detail";
    }

    @PostMapping("/blog/comment")
    @ResponseBody
    public Result comment(HttpServletRequest request, HttpSession session,
                          @RequestParam Long blogId, @RequestParam String verifyCode,
                          @RequestParam String commentator, @RequestParam String email,
                          @RequestParam String websiteUrl, @RequestParam String commentBody){
        if(StringUtils.isEmpty(verifyCode)){
            return ResultGenerator.genFailResult("验证码不能为空");
        }
        String verifyCode1 = session.getAttribute("verifyCode") + "";
        if(StringUtils.isEmpty(verifyCode1)){
            return ResultGenerator.genFailResult("非法请求");
        }
        if(!verifyCode.equals(verifyCode1)){
            return ResultGenerator.genFailResult("验证码错误");
        }
        String referer = request.getHeader("Referer");
        if(StringUtils.isEmpty(referer)){
            return ResultGenerator.genFailResult("非法请求");
        }
        if(blogId==null || blogId<0){
            return ResultGenerator.genFailResult("非法请求");
        }
        if(StringUtils.isEmpty(commentator)){
            return ResultGenerator.genFailResult("请输入昵称");
        }
        if(StringUtils.isEmpty(email)){
            return ResultGenerator.genFailResult("请输入邮箱地址");
        }
        if(!PatternUtil.isEmail(email)){
            return ResultGenerator.genFailResult("请输入正确的邮箱地址");
        }
        if(StringUtils.isEmpty(commentBody)){
            return ResultGenerator.genFailResult("请输入评论内容");
        }
        if(commentBody.trim().length()>200){
            return ResultGenerator.genFailResult("评论内容大于200字");
        }
        BlogComment blogComment=new BlogComment();
        blogComment.setBlogId(blogId);
        blogComment.setCommentator(MyBlogUtils.cleanString(commentator));
        blogComment.setEmail(email);
        if(PatternUtil.isURL(websiteUrl)){
            blogComment.setWebsiteUrl(websiteUrl);
        }
        blogComment.setCommentBody(MyBlogUtils.cleanString(commentBody));
        return ResultGenerator.genSuccessResult(commentService.addComment(blogComment));
    }
}
