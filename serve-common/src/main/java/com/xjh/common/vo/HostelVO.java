package com.xjh.common.vo;

import com.xjh.common.bean.Share;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class HostelVO {
    private Share share;
    private String img;
}
