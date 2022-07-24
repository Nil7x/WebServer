package com.xu.blog.service.impl;

import com.xu.blog.Controller.vo.BlogDetailVO;
import com.xu.blog.Controller.vo.BlogListVO;
import com.xu.blog.Controller.vo.SimpleBlogListVO;
import com.xu.blog.dao.BlogCategoryMapper;
import com.xu.blog.dao.BlogMapper;
import com.xu.blog.dao.BlogTagMapper;
import com.xu.blog.dao.BlogTagRelationMapper;
import com.xu.blog.entity.Blog;
import com.xu.blog.entity.BlogCategory;
import com.xu.blog.entity.BlogTag;
import com.xu.blog.entity.BlogTagRelation;
import com.xu.blog.service.BlogService;
import com.xu.blog.utils.MarkDownUtil;
import com.xu.blog.utils.PageQueryUtil;
import com.xu.blog.utils.PageResult;
import com.xu.blog.utils.PatternUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.swing.text.html.HTML;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl implements BlogService {
    @Resource
    private BlogMapper blogMapper;
    @Resource
    private BlogTagMapper tagMapper;
    @Resource
    private BlogTagRelationMapper blogTagRelationMapper;
    @Resource
    private BlogCategoryMapper categoryMapper;

    @Override
    public Blog getBlogId(Long blogId) {
        return blogMapper.selectByPrimaryKey(blogId);
    }

    @Override
    @Transactional
    public String updateBlog(Blog blog) {
        //根据主键查询，若查询为空返回修改失败
        //根据分类主键查询分类，如果分类为空，设置为默认分类，有分类就设置名称且排序++
        //处理标签数据,blog设置标签名,新增一个tag对象和所有tag对象，遍历所有标签数据且创建一个tag对象接收数据，无->设值并添加进新tag对象，有则直接添加
        //做一个判断，如果新增标签!=null则插入标签数据；创建一个标签关系list，新增的tag集合添加进所有tag对象中，做一个foreach，添加进标签关系list中
        //分类表更新，标签关系表删除原数据更新新数据，if blog表更新返回>0，成，否则return失败
        Blog blogForUpdate = blogMapper.selectByPrimaryKey(blog.getBlogId());
        if (blogForUpdate == null) {
            return "数据不存在";
        }
        blogForUpdate.setBlogTitle(blog.getBlogTitle());
        blogForUpdate.setBlogSubUrl(blog.getBlogSubUrl());
        blogForUpdate.setBlogContent(blog.getBlogContent());
        blogForUpdate.setBlogCoverImage(blog.getBlogCoverImage());
        blogForUpdate.setBlogStatus(blog.getBlogStatus());
        blogForUpdate.setEnableComment(blog.getEnableComment());
        BlogCategory blogCategory = categoryMapper.selectByPrimaryKey(blog.getBlogCategoryId());
        if (blogCategory == null) {
            blogCategory.setCategoryId(0);
            blogCategory.setCategoryName("默认分类");
        } else {
            blogForUpdate.setBlogCategoryId(blogCategory.getCategoryId());
            blogForUpdate.setBlogCategoryName(blogCategory.getCategoryName());
            blogCategory.setCategoryRank(blogCategory.getCategoryRank() + 1);
        }
        String[] tags = blog.getBlogTags().split(",");
        if (tags.length > 6) {
            return "最大标签数为6";
        }
        blogForUpdate.setBlogTags(blog.getBlogTags());
        List<BlogTag> tagListForInsert = new ArrayList<>();
        List<BlogTag> allTagList = new ArrayList<>();
        for (int i = 0; i < tags.length; i++) {
            BlogTag tag = tagMapper.selectByTagName(tags[i]);
            if (tag == null) {
                BlogTag tempTag = new BlogTag();
                tempTag.setTagName(tags[i]);
                tagListForInsert.add(tempTag);
            } else {
                allTagList.add(tag);
            }
        }
        if (!CollectionUtils.isEmpty(tagListForInsert)) {
            tagMapper.batchInsertBlogTag(tagListForInsert);
        }
        List<BlogTagRelation> blogTagRelations = new ArrayList<>();
        allTagList.addAll(tagListForInsert);
        for (BlogTag tag : allTagList) {
            BlogTagRelation blogTagRelation=new BlogTagRelation();
            blogTagRelation.setBlogId(blog.getBlogId());
            blogTagRelation.setTagId(tag.getTagId());
            blogTagRelations.add(blogTagRelation);
        }
        categoryMapper.updateByPrimaryKeySelective(blogCategory);
        blogTagRelationMapper.deleteByBlogId(blog.getBlogId());
        blogTagRelationMapper.batchInsert(blogTagRelations);
        if(blogMapper.updateByPrimaryKeySelective(blogForUpdate)>0){
            return "success";
        }
        return "修改失败";
}

    @Override
    @Transactional
    public String save(Blog blog) {
        //根据分类主键查询，不存在就设为默认分类
        BlogCategory blogCategory = categoryMapper.selectByPrimaryKey(blog.getBlogCategoryId());
        if(blogCategory==null){
            blog.setBlogCategoryId(0);
            blog.setBlogCategoryName("默认分类");
        }
        //设置博客分类名称
        //分类排序值加1
        else {
            blog.setBlogCategoryName(blogCategory.getCategoryName());
            blogCategory.setCategoryRank(blogCategory.getCategoryRank()+1);
        }
        //处理标签数据
        String [] tags=blog.getBlogTags().split(",");
        if(tags.length>6){
            return "标签数量最大为6个";
        }
        //保存文章
        if(blogMapper.insertSelective(blog)>0) {
            //新增tag对象
            List<BlogTag> tagListForInsert=new ArrayList<>();
            //所有标签对象，用于建立关系数据
            List<BlogTag> allTagsList=new ArrayList<>();
            //创建一个for循环，查询是否有这个标签并赋值给一个tag，如果tag==null就新增，存在则直接加入所有标签对象中
            for (int i = 0; i <tags.length; i++) {
                BlogTag tag=tagMapper.selectByTagName(tags[i]);
                if(tag==null){
                    BlogTag tempTag=new BlogTag();
                    tempTag.setTagName(tags[i]);
                    tagListForInsert.add(tempTag);
                }else {
                    allTagsList.add(tag);
                }
            }
            //新增标签数据并且修改分类排序值
            //做一个非空判断，如果非空则全部插入
            //分类表进行更新，创建一个分类关系表
            //新增tag对象添加进所有tag对象中
            //做一个增强for循环，再将关系对象添加进关系表中
            if(!CollectionUtils.isEmpty(tagListForInsert)){
                tagMapper.batchInsertBlogTag(tagListForInsert);
            }
            categoryMapper.updateByPrimaryKeySelective(blogCategory);
            List<BlogTagRelation> blogTagRelations=new ArrayList<>();
            allTagsList.addAll(tagListForInsert);
            for (BlogTag tag:allTagsList) {
                BlogTagRelation blogTagRelation=new BlogTagRelation();
                blogTagRelation.setBlogId(blog.getBlogId());
                blogTagRelation.setTagId(tag.getTagId());
                blogTagRelations.add(blogTagRelation);
            }
            if(blogTagRelationMapper.batchInsert(blogTagRelations)>0){
                return "success";
            }
        }
        return "保存失败";
    }

    @Override
    public PageResult getBlogsPage(PageQueryUtil pageUtil) {
        List<Blog> blogList = blogMapper.findBlogList(pageUtil);
        int totalBlogs = blogMapper.getTotalBlogs(pageUtil);
        PageResult pageResult=new PageResult(blogList,totalBlogs,pageUtil.getLimit(),pageUtil.getPage());
        return pageResult;
    }

    @Override
    public Boolean deleteBatch(Integer[] ids) {
        return blogMapper.deleteBatch(ids) > 0;
    }

    @Override
    public List<SimpleBlogListVO> getBlogListForIndexPage(int type) {
        List<SimpleBlogListVO> simpleBlogListVOS=new ArrayList<>();
        List<Blog> blogs = blogMapper.findBlogListByType(type, 9);
        if(!CollectionUtils.isEmpty(blogs)){
            for(Blog blog:blogs){
                SimpleBlogListVO simpleBlogListVO=new SimpleBlogListVO();
                BeanUtils.copyProperties(blog,simpleBlogListVO);
                simpleBlogListVOS.add(simpleBlogListVO);
            }
        }
        return simpleBlogListVOS;
    }

    @Override
    public PageResult getBlogsForIndexPage(int page) {
        Map params=new HashMap();
        params.put("page",page);
        params.put("limit",8);
        params.put("blogStatus",1);
        PageQueryUtil pageQueryUtil=new PageQueryUtil(params);
        List<Blog> blogList=blogMapper.findBlogList(pageQueryUtil);
        List<BlogListVO> blogListVOS = getBlogListVosByBlogs(blogList);
        int total=blogMapper.getTotalBlogs(pageQueryUtil);
        PageResult pageResult=new PageResult(blogListVOS,total,pageQueryUtil.getLimit(),pageQueryUtil.getPage());
        return pageResult;
    }

    private List<BlogListVO> getBlogListVosByBlogs(List<Blog> blogList){
        List<BlogListVO> blogListVOS=new ArrayList<>();
        if(!CollectionUtils.isEmpty(blogList)){
            List<Integer> categoryIds=blogList.stream().map(Blog::getBlogCategoryId).collect(Collectors.toList());
            Map<Integer,String> blogCategoryMap=new HashMap<>();
            if(!CollectionUtils.isEmpty(categoryIds)){
                List<BlogCategory> blogCategories = categoryMapper.selectByCategoryIds(categoryIds);
                if(!CollectionUtils.isEmpty(blogCategories)){
                    blogCategoryMap=blogCategories.stream().collect(Collectors.toMap(BlogCategory::getCategoryId,BlogCategory::getCategoryIcon,(key1,key2)->key2));
                }
            }
            for(Blog blog:blogList){
                BlogListVO blogListVO=new BlogListVO();
                BeanUtils.copyProperties(blog,blogListVO);
                if(blogCategoryMap.containsKey(blog.getBlogCategoryId())){
                    blogListVO.setBlogCategoryIcon(blogCategoryMap.get(blog.getBlogCategoryId()));
                }else {
                    blogListVO.setBlogCategoryId(0);
                    blogListVO.setBlogCategoryName("默认分类");
                    blogListVO.setBlogCategoryIcon("/admin/dist/img/category/1.png");
                }
                blogListVOS.add(blogListVO);
            }
        }
        return blogListVOS;
    }

    @Override
    public PageResult getBlogsPageBySearch(String keyword, int page) {
        if(page>0 && PatternUtil.validKeyword(keyword)){
            Map params=new HashMap();
            params.put("page",page);
            params.put("limit",9);
            params.put("keyword",keyword);
            params.put("blogStatus",1);
            PageQueryUtil pageQueryUtil=new PageQueryUtil(params);
            List<Blog> blogList = blogMapper.findBlogList(pageQueryUtil);
            List<BlogListVO> blogListVOS = getBlogListVosByBlogs(blogList);
            int totalBlogs = blogMapper.getTotalBlogs(pageQueryUtil);
            PageResult pageResult=new PageResult(blogListVOS,totalBlogs,pageQueryUtil.getLimit(),pageQueryUtil.getPage());
            return pageResult;
        }
        return null;
    }

    @Override
    public PageResult getBlogPageByCategory(String categoryName, int page) {
        if(PatternUtil.validKeyword(categoryName)){
            BlogCategory blogCategory = categoryMapper.selectByCategoryName(categoryName);
            if("默认分类".equals(categoryName) && blogCategory==null){
                blogCategory=new BlogCategory();
                blogCategory.setCategoryId(0);
            }
            if(blogCategory!=null && page>0){
                Map params=new HashMap();
                params.put("page",page);
                params.put("limit",9);
                params.put("blogCategoryId",blogCategory.getCategoryId());
                params.put("blogStatus",1);
                PageQueryUtil pageQueryUtil=new PageQueryUtil(params);
                List<Blog> blogList = blogMapper.findBlogList(pageQueryUtil);
                List<BlogListVO> blogListVOS = getBlogListVosByBlogs(blogList);
                int totalBlogs = blogMapper.getTotalBlogs(pageQueryUtil);
                PageResult pageResult=new PageResult(blogListVOS,totalBlogs,pageQueryUtil.getLimit(), pageQueryUtil.getPage());
                return pageResult;
            }
        }
        return null;
    }

    @Override
    public PageResult getBlogsPageByTag(String tagName, int page) {
        if(PatternUtil.validKeyword(tagName)){
            BlogTag blogTag = tagMapper.selectByTagName(tagName);
            if(blogTag!=null && page>0){
                Map params=new HashMap();
                params.put("page",page);
                params.put("limit",9);
                params.put("tagId",blogTag.getTagId());
                PageQueryUtil pageQueryUtil=new PageQueryUtil(params);
                List<Blog> blogList = blogMapper.getBlogsPageByTagId(pageQueryUtil);
                List<BlogListVO> blogListVOS = getBlogListVosByBlogs(blogList);
                int total = blogMapper.getTotalBlogsByTagId(pageQueryUtil);
                PageResult pageResult=new PageResult(blogListVOS,total,pageQueryUtil.getLimit(),pageQueryUtil.getPage());
                return pageResult;
            }
        }
        return null;
    }

    @Override
    public BlogDetailVO getBlogDetail(Long blogId) {
        Blog blog = blogMapper.selectByPrimaryKey(blogId);
        BlogDetailVO blogDetailVO=getBlogDetailVo(blog);
        if(blogDetailVO!=null){
            return blogDetailVO;
        }
        return null;
    }

    private BlogDetailVO getBlogDetailVo(Blog blog){
        if(blog!=null && blog.getBlogStatus()==1){
            blog.setBlogViews(blog.getBlogViews()+1);
            blogMapper.updateByPrimaryKey(blog);
            BlogDetailVO blogDetailVO=new BlogDetailVO();
            BeanUtils.copyProperties(blog,blogDetailVO);
            blogDetailVO.setBlogContent(MarkDownUtil.mdToHtml(blogDetailVO.getBlogContent()));
            BlogCategory blogCategory = categoryMapper.selectByPrimaryKey(blog.getBlogCategoryId());
            if(blog==null){
                blogCategory=new BlogCategory();
                blogCategory.setCategoryId(0);
                blogCategory.setCategoryName("默认分类");
                blogCategory.setCategoryIcon("/admin/dist/img/category/00.png");
            }
            blogDetailVO.setBlogCategoryIcon(blogCategory.getCategoryIcon());
            if(!StringUtils.isEmpty(blog.getBlogTags())){
                List<String> tags = Arrays.asList(blog.getBlogTags().split(","));
                blogDetailVO.setBlogTags(tags);
            }
            return blogDetailVO;
        }
        return null;
    }
}
