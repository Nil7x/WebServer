package com.xu.blog.Controller.admin;

import com.xu.blog.service.CommentService;
import com.xu.blog.utils.PageQueryUtil;
import com.xu.blog.utils.PageResult;
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
public class CommentController {
    @Resource
    private CommentService commentService;

    @GetMapping("/comments")
    public String list(HttpServletRequest request){
        request.setAttribute("path","comments");
        return "admin/comment";
    }

    @GetMapping("/comments/list")
    @ResponseBody
    public Result list(@RequestParam Map<String,Object>params){
        if(StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))){
            return ResultGenerator.genFailResult("参数异常");
        }
        PageQueryUtil pageQueryUtil=new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(commentService.getCommentsPage(pageQueryUtil));
    }

    @PostMapping("/comments/checkDone")
    @ResponseBody
    public Result checkDone(@RequestBody Integer[]ids){
        if(ids.length<1){
            return ResultGenerator.genFailResult("参数异常");
        }else if(commentService.checkDone(ids)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult("审核失败");
        }
    }

    @PostMapping("/comments/reply")
    @ResponseBody
    public Result checkDone(@RequestParam("commentId")Long commentId,
                            @RequestParam("replyBody")String replyBody){
        if(commentId==null || commentId<1 || StringUtils.isEmpty(replyBody)){
            return ResultGenerator.genFailResult("参数异常");
        }else if(commentService.reply(commentId,replyBody)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult("回复失败");
        }
    }

    @PostMapping("/comments/delete")
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids){
        if(ids.length<1){
            return ResultGenerator.genFailResult("参数异常");
        }else if(commentService.deleteBatch(ids)){
            return ResultGenerator.genSuccessResult();
        }else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }
}
