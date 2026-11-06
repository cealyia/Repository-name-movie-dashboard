package com.ctgu.movie.common;

import java.util.List;

/**
 * 分页返回结构。total、page、size、pages 一起返回，前端分页组件就不用自己算总页数。
 *
 * @param <T> 列表元素的类型
 */
public class PageResult<T> {

    /** 总记录数 */
    private long total;
    /** 当前页码，从 1 开始 */
    private int page;
    /** 每页条数 */
    private int size;
    /** 总页数 */
    private int pages;
    /** 当前页数据 */
    private List<T> list;

    public PageResult() {
    }

    public PageResult(long total, int page, int size, List<T> list) {
        this.total = total;
        this.page = page;
        this.size = size;
        this.pages = (int) Math.ceil((double) total / size);
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
