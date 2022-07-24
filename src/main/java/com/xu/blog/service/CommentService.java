package com.xu.blog.service;

import com.xu.blog.entity.BlogComment;
import com.xu.blog.utils.PageQueryUtil;
import com.xu.blog.utils.PageResult;

public interface CommentService {

    PageResult getCommentsPage(PageQueryUtil pageQueryUtil);

    Boolean checkDone(Integer[] ids);

    Boolean reply(Long commentId,String replyBody);

    Boolean deleteBatch(Integer[] ids);

    Boolean addComment(BlogComment blogComment);

    PageResult getCommentPageByBlogIdAndPageNum(Long blogId,int page);
}
