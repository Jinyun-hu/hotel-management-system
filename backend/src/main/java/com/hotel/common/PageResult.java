package com.hotel.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果类
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据列表
     */
    private List<T> data;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页条数
     */
    private Integer size;

    public PageResult() {
    }

    public PageResult(List<T> data, Long total, Integer page, Integer size) {
        this.data = data;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    public static <T> PageResult<T> of(List<T> data, Long total, Integer page, Integer size) {
        return new PageResult<>(data, total, page, size);
    }
}
