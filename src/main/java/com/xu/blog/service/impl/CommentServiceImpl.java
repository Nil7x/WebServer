package com.xu.blog.service.impl;

import com.xu.blog.dao.BlogCommentMapper;
import com.xu.blog.entity.BlogComment;
import com.xu.blog.service.CommentService;
import com.xu.blog.utils.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {
    @Resource
    private BlogCommentMapper blogCommentMapper;
    @Override
    public PageResult getCommentsPage(PageQueryUtil pageQueryUtil) {
        List<BlogComment> blogCommentList = blogCommentMapper.findBlogCommentList(pageQueryUtil);
        int totalBlogComments = blogCommentMapper.getTotalBlogComments(pageQueryUtil);
        PageResult pageResult=new PageResult(blogCommentList,totalBlogComments,pageQueryUtil.getLimit(),pageQueryUtil.getPage());
        return pageResult;
    }

    @Override
    public Boolean checkDone(Integer[] ids) {
        return blogCommentMapper.checkDone(ids)>0;
    }

    @Override
    public Boolean reply(Long commentId, String replyBody) {
        BlogComment blogComment = blogCommentMapper.selectByPrimaryKey(commentId);
        if(blogComment!=null && blogComment.getCommentStatus()==1){
            blogComment.setCommentBody(replyBody);
            blogComment.setCommentCreateTime(new Date());
            return blogCommentMapper.updateByPrimaryKeySelective(blogComment)>0;
        }
        return false;
    }

    @Override
    public Boolean deleteBatch(Integer[] ids) {
        return blogCommentMapper.deleteBatch(ids)>0;
    }

    @Override
    public Boolean addComment(BlogComment blogComment) {
        return blogCommentMapper.insertSelective(blogComment)>0;
    }

    @Override
    public PageResult getCommentPageByBlogIdAndPageNum(Long blogId, int page) {
        if(page<1){
            return null;
        }
        Map params=new HashMap();
        params.put("page",page);
        params.put("limit",8);
        params.put("blogId",blogId);
        params.put("commentStatus",1);
        PageQueryUtil pageQueryUtil=new PageQueryUtil(params);
        List<BlogComment> blogCommentList = blogCommentMapper.findBlogCommentList(pageQueryUtil);
        if(!CollectionUtils.isEmpty(pageQueryUtil)){
            int totalBlogComments = blogCommentMapper.getTotalBlogComments(pageQueryUtil);
            PageResult pageResult=new PageResult(blogCommentList,totalBlogComments,pageQueryUtil.getLimit(),pageQueryUtil.getPage());
            return pageResult;
        }
        return null;
    }
}
