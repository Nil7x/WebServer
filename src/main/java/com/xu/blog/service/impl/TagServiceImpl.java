package com.xu.blog.service.impl;

import com.xu.blog.dao.BlogTagMapper;
import com.xu.blog.dao.BlogTagRelationMapper;
import com.xu.blog.entity.BlogTag;
import com.xu.blog.entity.BlogTagCount;
import com.xu.blog.service.TagService;
import com.xu.blog.utils.PageQueryUtil;
import com.xu.blog.utils.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    @Resource
    private BlogTagMapper blogTagMapper;
    @Resource
    private BlogTagRelationMapper blogTagRelationMapper;

    @Override
    public PageResult getBlogTagPage(PageQueryUtil pageUtil) {
        List<BlogTag> tagList = blogTagMapper.findTagList(pageUtil);
        int totalTags = blogTagMapper.getTotalTags(pageUtil);
        PageResult pageResult=new PageResult(tagList,totalTags,pageUtil.getLimit(),pageUtil.getPage());
        return pageResult;
    }

    @Override
    public Boolean saveTag(String tagName) {
        BlogTag blogTag = blogTagMapper.selectByTagName(tagName);
        if(blogTag==null){
            BlogTag blogTag1=new BlogTag();
            blogTag1.setTagName(tagName);
            return blogTagMapper.insertSelective(blogTag1)>0;
        }
        return false;
    }

    @Override
    public Boolean deleteBatch(Integer[] ids) {
        List<Long> longs = blogTagRelationMapper.selectDistinctTagIds(ids);
        if(!CollectionUtils.isEmpty(longs)){
            return false;
        }
        return blogTagMapper.deleteBatch(ids)>0;
    }

    @Override
    public List<BlogTagCount> getBlogTagCountForIndex() {
        return blogTagMapper.getTagCount();
    }
}
