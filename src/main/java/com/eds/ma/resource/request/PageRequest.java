package com.eds.ma.resource.request;

import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

/**
 * 分页查询条件
 * @Author gaoyan
 * @Date: 2018/3/31
 */
public class PageRequest {
    /**
     * 页码
     */
    @DefaultValue("1")
    @QueryParam("pageNo")
    protected Integer pageNo;
    /**
     * 每页条数
     */
    @NotNull(message="每页条数不允许为空")
    @QueryParam("pageSize")
    protected Integer pageSize;

    public PageRequest() {
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        if (pageNo == null || pageNo < 0) {
            this.pageNo = 1;
        } else {
            this.pageNo = pageNo;
        }
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        if (pageSize < 0 || pageSize > 200) {
            this.pageSize = 200;
        } else {
            this.pageSize = pageSize;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PageRequest{");
        sb.append("pageNo=").append(pageNo);
        sb.append(", pageSize=").append(pageSize);
        sb.append('}');
        return sb.toString();
    }
}