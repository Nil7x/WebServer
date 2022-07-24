package com.xu.blog.Controller.admin;

import com.sun.deploy.net.HttpResponse;
import com.xu.blog.entity.Blog;
import com.xu.blog.service.BlogService;
import com.xu.blog.service.CategoryService;
import com.xu.blog.utils.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

@Controller
@RequestMapping("/admin")
public class BlogController {
    @Resource
    private BlogService blogService;
    @Resource
    private CategoryService categoryService;

    @GetMapping("/blogs")
    public String list(HttpServletRequest request){
        request.setAttribute("path","blogs");
        return "admin/blog";
    }

    @GetMapping("/blogs/list")
    @ResponseBody
    public Result list(@RequestParam Map<String,Object>params){
        if(StringUtils.isEmpty(params.get("page")) ||StringUtils.isEmpty(params.get("limit"))){
            return ResultGenerator.genFailResult("参数异常");
        }
        PageQueryUtil pageUtil=new PageQueryUtil(params);
        return  ResultGenerator.genSuccessResult(blogService.getBlogsPage(pageUtil));
    }


    @GetMapping("/blogs/edit")
    public String edit(HttpServletRequest request){
        request.setAttribute("path","edit");
        request.setAttribute("categories",categoryService.getAllCategories());
        return "admin/edit";
    }

    @GetMapping("/blogs/edit/{blogId}")
    public String edit(HttpServletRequest request,@PathVariable("blogId")Long blogId) {
        //先跳转到edit请求，调service层查询id赋值给一个blog，如果blog查询为空则无法修改，跳转错误页面，可以则设置路径回到edit页面
        request.setAttribute("path","edit");
        Blog blog = blogService.getBlogId(blogId);
        if(blog==null){
            return "error/error_400";
        }
        request.setAttribute("blog",blog);
        request.setAttribute("categories",categoryService.getAllCategories());
        return "admin/edit";
    }

    @PostMapping("/blogs/update")
    @ResponseBody
    public Result update(@RequestParam("blogId") Long blogId,
                         @RequestParam("blogTitle") String blogTitle,
                         @RequestParam(name = "blogSubUrl", required = false) String blogSubUrl,
                         @RequestParam("blogCategoryId") Integer blogCategoryId,
                         @RequestParam("blogTags") String blogTags,
                         @RequestParam("blogContent") String blogContent,
                         @RequestParam("blogCoverImage") String blogCoverImage,
                         @RequestParam("blogStatus") Byte blogStatus,
                         @RequestParam("enableComment") Byte enableComment) {
        if (StringUtils.isEmpty(blogTitle)) {
            return ResultGenerator.genFailResult("请输入文章标题");
        }
        if (blogTitle.trim().length() > 150) {
            return ResultGenerator.genFailResult("标题过长");
        }
        if (StringUtils.isEmpty(blogTags)) {
            return ResultGenerator.genFailResult("请输入文章标签");
        }
        if (blogTags.trim().length() > 150) {
            return ResultGenerator.genFailResult("标签过长");
        }
        if (blogSubUrl.trim().length() > 150) {
            return ResultGenerator.genFailResult("路径过长");
        }
        if (StringUtils.isEmpty(blogContent)) {
            return ResultGenerator.genFailResult("请输入文章内容");
        }
        if (blogTags.trim().length() > 100000) {
            return ResultGenerator.genFailResult("文章内容过长");
        }
        if (StringUtils.isEmpty(blogCoverImage)) {
            return ResultGenerator.genFailResult("封面图不能为空");
        }
        Blog blog = new Blog();
        blog.setBlogId(blogId);
        blog.setBlogTitle(blogTitle);
        blog.setBlogSubUrl(blogSubUrl);
        blog.setBlogCategoryId(blogCategoryId);
        blog.setBlogTags(blogTags);
        blog.setBlogContent(blogContent);
        blog.setBlogCoverImage(blogCoverImage);
        blog.setBlogStatus(blogStatus);
        blog.setEnableComment(enableComment);
        String updateBlogResult = blogService.updateBlog(blog);
        if ("success".equals(updateBlogResult)) {
            return ResultGenerator.genSuccessResult("修改成功");
        } else {
            return ResultGenerator.genFailResult(updateBlogResult);
        }
    }
    @PostMapping("/blogs/md/uploadfile")
    public void uploadFileByEditormd(HttpServletRequest request,
                                     HttpServletResponse response,
                                     @RequestParam(value = "editormd-image-file",required = true)
                                     MultipartFile file) throws URISyntaxException, IOException {
        String UPLOAD_FILE_DIC=file.getOriginalFilename();
        String fileName=file.getOriginalFilename();
        String suffixName=fileName.substring(fileName.lastIndexOf("."));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Random r = new Random();
        StringBuilder tempName = new StringBuilder();
        tempName.append(sdf.format(new Date())).append(r.nextInt(100)).append(suffixName);
        String newFileName = tempName.toString();
        File destFile=new File(UPLOAD_FILE_DIC+newFileName);
        String fileUrl= MyBlogUtils.getHost(new URI(request.getRequestURL()+""))+newFileName;
        File fileDirectory=new File(UPLOAD_FILE_DIC);
        try{
            if(!fileDirectory.exists()){
                if(!fileDirectory.mkdir()){
                    throw new IOException("文件上传失败，路径为:"+fileDirectory);
                }
            }
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
            try{
                request.setCharacterEncoding("utf-8");
                response.setHeader("Content-Type","text/html");
                response.getWriter().write("{\"success\": 1, \"message\":\"success\",\"url\":\"" + fileUrl + "\"}");
            } catch (UnsupportedEncodingException e) {
                response.getWriter().write("{\"success\":0}");
            } catch (IOException e) {
                response.getWriter().write("{\"success\":0}");
            }
    }

    @PostMapping("/blogs/save")
    @ResponseBody
    public Result save(@RequestParam("blogTitle") String blogTitle,
                       @RequestParam(name = "blogSubUrl", required = false) String blogSubUrl,
                       @RequestParam("blogCategoryId") Integer blogCategoryId,
                       @RequestParam("blogTags") String blogTags,
                       @RequestParam("blogContent") String blogContent,
                       @RequestParam("blogCoverImage") String blogCoverImage,
                       @RequestParam("blogStatus") Byte blogStatus,
                       @RequestParam("enableComment") Byte enableComment) {
        if (StringUtils.isEmpty(blogTitle)) {
            return ResultGenerator.genFailResult("请输入文章标题");
        }
        if (blogTitle.trim().length() > 150) {
            return ResultGenerator.genFailResult("标题过长");
        }
        if (StringUtils.isEmpty(blogTags)) {
            return ResultGenerator.genFailResult("请输入文章标签");
        }
        if (blogTags.trim().length() > 150) {
            return ResultGenerator.genFailResult("标签过长");
        }
        if (blogSubUrl.trim().length() > 150) {
            return ResultGenerator.genFailResult("路径过长");
        }
        if (StringUtils.isEmpty(blogContent)) {
            return ResultGenerator.genFailResult("请输入文章内容");
        }
        if (blogTags.trim().length() > 100000) {
            return ResultGenerator.genFailResult("文章内容过长");
        }
        if (StringUtils.isEmpty(blogCoverImage)) {
            return ResultGenerator.genFailResult("封面图不能为空");
        }
        Blog blog = new Blog();
        blog.setBlogTitle(blogTitle);
        blog.setBlogSubUrl(blogSubUrl);
        blog.setBlogCategoryId(blogCategoryId);
        blog.setBlogTags(blogTags);
        blog.setBlogContent(blogContent);
        blog.setBlogCoverImage(blogCoverImage);
        blog.setBlogStatus(blogStatus);
        blog.setEnableComment(enableComment);
        String saveBlogResult = blogService.save(blog);
        if ("success".equals(saveBlogResult)) {
            return ResultGenerator.genSuccessResult("添加成功");
        } else {
            return ResultGenerator.genFailResult(saveBlogResult);
        }
    }
    @PostMapping("/blogs/delete")
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids){
        if(ids.length<1){
            return ResultGenerator.genFailResult("参数异常");
        }if(blogService.deleteBatch(ids)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }
}
