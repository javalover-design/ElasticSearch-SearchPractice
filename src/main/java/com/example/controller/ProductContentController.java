package com.example.controller;

import com.example.service.ProductContentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author lambda
 */
@RestController
public class ProductContentController {

    @Resource
    private ProductContentService productContentService;

    @GetMapping("/parse/{keywords}")
    public boolean parseContent(@PathVariable("keywords") String keywords) throws Exception {
        return productContentService.parseProductContent(keywords);
    }

    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String, Object>> searchResults(@PathVariable("keyword") String keyword,
                                                   @PathVariable("pageNo") Integer pageNo,
                                                   @PathVariable("pageSize") Integer pageSize) throws IOException {
        return productContentService.searchPage(keyword, pageNo, pageSize);

    }
}
