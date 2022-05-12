package com.example.utils;

import com.example.pojo.ProductContents;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lambda
 */
@Component
public class HtmlParseUtil {

    /**
     * 通过指定关键字获取所有的相关信息
     * @param  keywords
     * @return List
     * @throws Exception
     */
    public static List parseHtml(String keywords) throws Exception {
        // 获取搜索请求https://search.jd.com/Search?keyword=关键词
        String url="https://search.jd.com/Search?keyword="+keywords;

        //解析网页 ,超过30s就会报错，返回的就是浏览器的document对象
        Document document = Jsoup.parse(new URL(url), 30000);
        //通过id来获取元素
        Element element = document.getElementById("J_goodsList");

        //获取所有的li标签(每个li标签就代表一件商品)
        Elements liElements= element.getElementsByTag("li");
        //创建一个集合用于保存所有的解析对象
        List<ProductContents> productLists  = new ArrayList<>();

        //获取每个li标签之后获取每个标签中的内容
        for (Element liElement : liElements) {
            //可以获取每个li标签中的具体内容,eq()表示获取第几个元素
            //获取图片 src-->data-lazy-img 有一个懒加载过程
            String img = liElement.getElementsByTag("img").eq(0).attr("data-lazy-img");
            //获取价格
            String price = liElement.getElementsByClass("p-price").eq(0).text();
            //获取标题
            String title = liElement.getElementsByClass("p-name").eq(0).text();

            productLists.add(new ProductContents().setImg(img).setPrice(price).setTitle(title));
        }

        return productLists;
    }
}
