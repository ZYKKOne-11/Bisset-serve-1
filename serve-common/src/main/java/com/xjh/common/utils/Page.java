package com.xjh.common.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Page<T> {

    private Integer pageNumber;//分页页数

    private Integer pageSize;//每页记录数

    private List<T> data;//返回的记录集合

    private Integer totalNumber;//总共多少页

    private Integer totalSize;//总记录条数

    public Page(List<T> data, Integer pageNumber, Integer pageSize, Integer count){
        this.data = data;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalNumber = count / pageSize + (count % pageSize == 0 ? 0 : 1);
        this.totalSize = count;
    }

    public Page(Integer pageNumber, Integer pageSize, Integer totalNumber, Integer totalSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalNumber = totalNumber;
        this.totalSize = totalSize;
    }
}
