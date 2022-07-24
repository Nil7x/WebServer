package com.xu.blog.service;

import com.xu.blog.entity.BlogTagCount;
import com.xu.blog.utils.PageQueryUtil;
import com.xu.blog.utils.PageResult;
import com.xu.blog.utils.PageUtil;

import java.util.List;

public interface TagService {
    PageResult getBlogTagPage(PageQueryUtil pageUtil);
    Boolean saveTag(String tagName);
    Boolean deleteBatch(Integer[] ids);
    List<BlogTagCount> getBlogTagCountForIndex();
}
