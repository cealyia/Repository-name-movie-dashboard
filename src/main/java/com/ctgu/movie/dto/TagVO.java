package com.ctgu.movie.dto;

/**
 * 词云中的一个标签及其出现次数。
 */
public class TagVO {

    /** 标签文本 */
    private String name;

    /** 该标签被使用的次数 */
    private long value;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}
