package com.xjh.common.vo;

import com.xjh.common.bean.Discuss;
import com.xjh.common.bean.Share;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class HostelVO {
    private Share share;

    private Discuss discuss;

    private String img;

    //分页所需字段
    Integer number;

    Integer size;

}
