package com.xu.blog.service.impl;

import com.xu.blog.dao.BlogCategoryMapper;
import com.xu.blog.entity.BlogCategory;
import com.xu.blog.service.CategoryService;
import com.xu.blog.utils.PageQueryUtil;
import com.xu.blog.utils.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
@Service
public class CategoryServiceImpl implements CategoryService {
    @Resource
    BlogCategoryMapper blogCategoryMapper;
    @Override
    public PageResult getBlogCategoryPage(PageQueryUtil pageUtil) {
        List<BlogCategory> categoryList = blogCategoryMapper.findCategoryList(pageUtil);
        int total=blogCategoryMapper.getTotalCategories(pageUtil);
        PageResult pageResult=new PageResult(categoryList,total,pageUtil.getLimit(),pageUtil.getPage());
        return pageResult;
    }

    @Override
    public int getCategories() {
        return blogCategoryMapper.getTotalCategories(null);
    }

    @Override
    public Boolean saveCategory(String name, String icon) {
        BlogCategory blogCategory = blogCategoryMapper.selectByCategoryName(name);
        if(blogCategory==null){
            BlogCategory blogCategory1=new BlogCategory();
            blogCategory1.setCategoryName(name);
            blogCategory1.setCategoryIcon(icon);
            return blogCategoryMapper.insertSelective(blogCategory1)>0;
        }
        return false;
    }

    @Override
    @Transactional
    public Boolean updateCategory(Integer id, String name, String icon) {
        BlogCategory blogCategory = blogCategoryMapper.selectByPrimaryKey(id);
        if(blogCategory!=null){
            blogCategory.setCategoryIcon(icon);
            blogCategory.setCategoryName(name);
            return blogCategoryMapper.updateByPrimaryKeySelective(blogCategory)>0;
        }
        return false;
    }

    @Override
    @Transactional
    public Boolean deleteBatch(Integer[] ids) {
        if(ids.length<1){
            return false;
        }
        return blogCategoryMapper.deleteBatch(ids)>0;
    }

    @Override
    public List<BlogCategory> getAllCategories() {
        return blogCategoryMapper.findCategoryList(null);
    }

    @Override
    public BlogCategory selectById(Integer id) {
        return blogCategoryMapper.selectByPrimaryKey(id);
    }
}
