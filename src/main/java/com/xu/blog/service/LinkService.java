package com.xu.blog.service;

import com.xu.blog.entity.BlogLink;
import com.xu.blog.utils.PageQueryUtil;
import com.xu.blog.utils.PageResult;

public interface LinkService {
    PageResult getBlogLinkPage(PageQueryUtil pageQueryUtil);

    Boolean saveLink(BlogLink blogLink);

    Boolean deleteBatch(Integer[] ids);

    BlogLink selectById(Integer id);

    Boolean updateLink(BlogLink blogLink);
}
