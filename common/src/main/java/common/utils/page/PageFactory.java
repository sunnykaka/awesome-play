package common.utils.page;


import common.models.utils.EntityClass;
import common.utils.ParamUtils;
import org.apache.commons.lang3.StringUtils;
import play.mvc.Http;

import java.util.HashMap;
import java.util.Map;

/**
 * User: liubin
 * Date: 14-3-11
 */
public class PageFactory {

    public static <T extends EntityClass> Page<T> getPage(Http.Request request) {
        String page = ParamUtils.getByKey(request,"page");
        String limit = ParamUtils.getByKey(request,"limit");

        int pageNo = 1;
        int pageSize = Page.DEFAULT_PAGE_SIZE;
        try {
            pageNo = Integer.valueOf(page);
        }catch (Exception e){
            //异常不处理
        }
        try {
            pageSize = Integer.valueOf(limit);
        }catch (Exception e){
            //异常不处理
        }
        return new Page<>(pageNo, pageSize);
    }

}
