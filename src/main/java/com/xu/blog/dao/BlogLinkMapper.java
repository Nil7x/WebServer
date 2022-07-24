package com.xu.blog.dao;

import com.xu.blog.entity.BlogLink;
import com.xu.blog.utils.PageQueryUtil;

import java.util.List;

public interface BlogLinkMapper {
    int deleteByPrimaryKey(Integer linkId);

    int insert(BlogLink record);

    int insertSelective(BlogLink record);

    BlogLink selectByPrimaryKey(Integer linkId);

    int updateByPrimaryKeySelective(BlogLink record);

    int updateByPrimaryKey(BlogLink record);

    List<BlogLink> findLinkList(PageQueryUtil pageQueryUtil);

    int getTotalLink(PageQueryUtil pageQueryUtil);

    int deleteBatch(Integer[] ids);
}