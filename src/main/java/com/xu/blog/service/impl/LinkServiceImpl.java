package com.xu.blog.service.impl;

import com.xu.blog.dao.BlogLinkMapper;
import com.xu.blog.entity.BlogLink;
import com.xu.blog.service.LinkService;
import com.xu.blog.utils.PageQueryUtil;
import com.xu.blog.utils.PageResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LinkServiceImpl implements LinkService {
    @Resource
    private BlogLinkMapper blogLinkMapper;
    @Override
    public PageResult getBlogLinkPage(PageQueryUtil pageQueryUtil) {
        List<BlogLink> linkList = blogLinkMapper.findLinkList(pageQueryUtil);
        int totalLink = blogLinkMapper.getTotalLink(pageQueryUtil);
        PageResult pageResult=new PageResult(linkList,totalLink,pageQueryUtil.getLimit(),pageQueryUtil.getPage());
        return pageResult;
    }

    @Override
    public Boolean saveLink(BlogLink blogLink) {
        return blogLinkMapper.insertSelective(blogLink)>0;
    }

    @Override
    public Boolean deleteBatch(Integer[] ids) {
        return blogLinkMapper.deleteBatch(ids)>0;
    }

    @Override
    public BlogLink selectById(Integer id) {
        return blogLinkMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean updateLink(BlogLink blogLink) {
        return blogLinkMapper.updateByPrimaryKeySelective(blogLink)>0;
    }
}
