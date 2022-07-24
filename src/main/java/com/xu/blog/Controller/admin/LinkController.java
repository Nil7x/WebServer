package com.xu.blog.Controller.admin;

import com.xu.blog.entity.BlogLink;
import com.xu.blog.service.LinkService;
import com.xu.blog.utils.PageQueryUtil;
import com.xu.blog.utils.Result;
import com.xu.blog.utils.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class LinkController {
    @Resource
    private LinkService linkService;

    @GetMapping("/links")
    public String linkPage(HttpServletRequest request){
        request.setAttribute("path","links");
        return "/admin/link";
    }

    @GetMapping("/links/list")
    @ResponseBody
    public Result list(@RequestParam Map<String,Object> params){
        if(StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))){
            return ResultGenerator.genFailResult("参数异常");
        }
        PageQueryUtil pageQueryUtil=new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(linkService.getBlogLinkPage(pageQueryUtil));
    }

    @RequestMapping(value = "/links/save",method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestParam("linkType")Integer linkType,
                       @RequestParam("linkName")String linkName,
                       @RequestParam("linkUrl")String linkUrl,
                       @RequestParam("linkRank")Integer linkRank,
                       @RequestParam("linkDescription")String linkDescription){
    if(linkType == null || linkType < 0 || linkRank == null || linkRank < 0 || StringUtils.isEmpty(linkName) || StringUtils.isEmpty(linkName) || StringUtils.isEmpty(linkUrl) || StringUtils.isEmpty(linkDescription)){
        return ResultGenerator.genFailResult("参数异常");
    }
    BlogLink blogLink=new BlogLink();
    blogLink.setLinkType(linkType.byteValue());
    blogLink.setLinkName(linkName);
    blogLink.setLinkRank(linkRank);
    blogLink.setLinkUrl(linkUrl);
    blogLink.setLinkDescription(linkDescription);
    return ResultGenerator.genSuccessResult(linkService.saveLink(blogLink));
    }

    @RequestMapping(value = "/links/delete",method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids){
        if(ids.length<1){
            return ResultGenerator.genFailResult("参数异常");
        }
        if(linkService.deleteBatch(ids)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }

    @GetMapping("/links/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id")Integer id){
        BlogLink blogLink = linkService.selectById(id);
        return ResultGenerator.genSuccessResult(blogLink);
    }

    @RequestMapping(value = "/links/update",method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestParam("linkId")Integer linkId,
                         @RequestParam("linkType")Integer linkType,
                         @RequestParam("linkName")String linkName,
                         @RequestParam("linkUrl")String linkUrl,
                         @RequestParam("linkRank")Integer linkRank,
                         @RequestParam("linkDescription")String linkDescription){
        BlogLink blogLink = linkService.selectById(linkId);
        if(blogLink==null){
            return ResultGenerator.genFailResult("无数据");
        }
        if(linkType==null || linkType<0 ||linkRank==null || linkRank<0 || StringUtils.isEmpty(linkName) || StringUtils.isEmpty(linkUrl) || StringUtils.isEmpty(linkDescription)){
            return ResultGenerator.genFailResult("参数异常");
        }
        blogLink.setLinkType(linkType.byteValue());
        blogLink.setLinkName(linkName);
        blogLink.setLinkUrl(linkUrl);
        blogLink.setLinkRank(linkRank);
        blogLink.setLinkDescription(linkDescription);
        return ResultGenerator.genSuccessResult(linkService.updateLink(blogLink));
    }
}
