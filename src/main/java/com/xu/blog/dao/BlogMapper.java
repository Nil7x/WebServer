package com.xu.blog.dao;

import com.xu.blog.entity.Blog;
import com.xu.blog.utils.PageQueryUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface BlogMapper {
    int deleteByPrimaryKey(Long blogId);

    int insert(Blog record);

    int insertSelective(Blog record);

    Blog selectByPrimaryKey(Long blogId);

    int updateByPrimaryKeySelective(Blog record);

    int updateByPrimaryKeyWithBLOBs(Blog record);

    int updateByPrimaryKey(Blog record);

    List<Blog> findBlogList(PageQueryUtil pageUtil);

    int getTotalBlogs(PageQueryUtil pageUtil);

    int deleteBatch(Integer[] ids);

    List<Blog>findBlogListByType(@Param("type")int type,@Param("limit")int limit);

    List<Blog>getBlogsPageByTagId(PageQueryUtil pageQueryUtil);

    int getTotalBlogsByTagId(PageQueryUtil pageQueryUtil);
}