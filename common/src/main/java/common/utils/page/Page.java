package common.utils.page;

import java.util.ArrayList;
import java.util.List;

public class Page<T> {

    /** 每页显示 15 条数据 */
    public static final int DEFAULT_PAGE_SIZE = 15;

    /** 页数, 默认显示第 1 页 */
    private int pageNo = 1;

    /** 每页显示条数 */
    private int pageSize = DEFAULT_PAGE_SIZE;


    /** 不包含分页查询的总条数 */
    private int totalCount;

    /** 分页数据 */
    private List<T> result = new ArrayList<T>();

    public Page(int pageNo, int pageSize) {
        setPageNo(pageNo);
        setPageSize(pageSize);
    }

    /** 总页数 */
    public int getTotalPage() {
        return (totalCount % pageSize == 0) ? (totalCount / pageSize) : (totalCount / pageSize + 1);
    }

    /** 数据库 select oo from xxx limit #start#, #limit# 中需要的 start 参数 */
    public int getStart() {
        return (pageNo -1) * pageSize;
    }

    /** 数据库 select oo from xxx limit #start#, #limit# 中需要的 limit 参数 */
    public int getLimit() {
        return pageSize;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo=pageNo;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = (totalCount < 0) ? 0 : totalCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = (pageSize < 1) ? DEFAULT_PAGE_SIZE : pageSize;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Page{" +
                "pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", totalCount=" + totalCount +
                ", result=" + result +
                '}';
    }
}