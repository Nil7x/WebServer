package com.xu.blog.service;

import com.xu.blog.entity.BlogCategory;
import com.xu.blog.utils.PageQueryUtil;
import com.xu.blog.utils.PageResult;

import java.util.List;

public interface CategoryService {

    PageResult getBlogCategoryPage(PageQueryUtil pageUtil);
    int getCategories();
    Boolean saveCategory(String name,String icon);
    Boolean updateCategory(Integer id,String name,String icon);
    Boolean deleteBatch(Integer [] ids);
    List<BlogCategory> getAllCategories();
    BlogCategory selectById(Integer id);
}
