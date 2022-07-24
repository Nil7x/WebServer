package com.xu.blog.service;

import com.xu.blog.Controller.vo.BlogDetailVO;
import com.xu.blog.Controller.vo.SimpleBlogListVO;
import com.xu.blog.entity.Blog;
import com.xu.blog.utils.PageQueryUtil;
import com.xu.blog.utils.PageResult;

import java.util.List;

public interface BlogService {
    String save(Blog blog);

    Blog getBlogId(Long blogId);

    String updateBlog(Blog blog);

    PageResult getBlogsPage(PageQueryUtil pageUtil);

    Boolean deleteBatch(Integer[] ids);

    List<SimpleBlogListVO> getBlogListForIndexPage(int type);

    PageResult getBlogsForIndexPage(int page);

    PageResult getBlogsPageBySearch(String keyword,int page);

    PageResult getBlogPageByCategory(String categoryId,int page);

    PageResult getBlogsPageByTag(String tagName,int page);

    BlogDetailVO getBlogDetail(Long blogId);
}
