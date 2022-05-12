package com.example.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The interface Product content service.
 *
 * @author lambda
 */
public interface ProductContentService {

    /**
     * Parse product content boolean.
     *
     * @param keywords the keywords
     * @return the boolean
     * @throws Exception the exception
     */
    boolean parseProductContent(String keywords) throws Exception;

    /**
     * Search page list.
     *
     * @param keyword  the keyword
     * @param pageNo   the page no
     * @param pageSize the page size
     * @return the list
     * @throws IOException the io exception
     */
    List<Map<String,Object>> searchPage(String keyword,Integer pageNo,Integer pageSize) throws IOException;
}
