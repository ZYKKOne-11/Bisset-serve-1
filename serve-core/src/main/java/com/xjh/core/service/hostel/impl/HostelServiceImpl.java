package com.xjh.core.service.hostel.impl;

import com.xjh.common.utils.Base64ToMultipartFileUtil;
import com.xjh.common.bean.Share;
import com.xjh.common.consts.HostelListConst;
import com.xjh.common.enums.RankingTypeEnum;
import com.xjh.common.exception.CommonErrorCode;
import com.xjh.common.exception.CommonException;
import com.xjh.common.po.UserPO;
import com.xjh.common.utils.PropertyLoader;
import com.xjh.common.vo.HostelVO;
import com.xjh.core.mapper.HostelMapper;
import com.xjh.core.service.hostel.HostelService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class HostelServiceImpl implements HostelService {

    @Resource
    private HostelMapper hostelMapper;

    @Resource
    private HostelListConst hostelListConst;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");

    private String  uploadPath = PropertyLoader.getProperty("user.share.upload.path");

    private static final Long UPLOAD_FILE_MAX_SIZE = PropertyLoader.getLongProperty("upload.file.max.size");


    @Override
    public Map<RankingTypeEnum,List<UserPO>> queryRanking() {
        try {
            Map<RankingTypeEnum,List<UserPO>> res = new HashMap<>();
            res.put(RankingTypeEnum.OVER_ALL_RANK,hostelListConst.getOverAllRankingList());
            res.put(RankingTypeEnum.MONTH_RANK,hostelListConst.getMonthRankingList());
            res.put(RankingTypeEnum.WEEK_RANK,hostelListConst.getWeekRankingList());
            return res;
        }catch (Exception e){
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR,"用户排行榜查询异常，请稍后重试");
        }
    }

    @Override
    public Boolean userShare(HostelVO hostelVO,HttpServletRequest request) {
        try {
            logger.info("======= req uri: /hostel/share =======");
            MultipartFile uploadFile = converter(hostelVO.getImg());
            logger.info("Base64 转换 MultipartFile Successful");
            checkBindParam(uploadFile,hostelVO);
            String uploadFilePath = uploadFiles(uploadFile, request);
            Share share = hostelVO.getShare();
            share.setImg(uploadFilePath);
            //TODO 暂时默认通过管理员审核
            share.setIsReviewed(1);
            logger.info("添加思维导图分享信息：param: "+share.toString());
            Integer res = hostelMapper.insertUserShare(share);
            return res == 1;
        }catch (Exception e){
            logger.error("分享思维导图异常，异常信息："+e.getMessage());
            throw new CommonException(CommonErrorCode.UNKNOWN_ERROR,"分享学习思维导图异常，请稍后重试");
        }
    }

    private void checkBindParam(MultipartFile uploadFile, HostelVO hostelVO) throws Exception {
        if (uploadFile.isEmpty()){
            throw new CommonException(CommonErrorCode.VALIDATE_ERROR,"上传的思维导图不能为空");
        }

        if (hostelVO.getShare().getTitle() == null || hostelVO.getShare().getTitle().equals("")){
            throw new CommonException(CommonErrorCode.VALIDATE_ERROR,"思维导图分享标题为空");
        }

        if (hostelVO.getShare().getDetails() == null || hostelVO.getShare().getDetails().equals("")){
            throw new CommonException(CommonErrorCode.VALIDATE_ERROR,"思维导图分享简介为空");
        }

        if (hostelVO.getShare().getIsDiscuss() == null){
            hostelVO.getShare().setIsDiscuss(0);
        }
    }

    public String uploadFiles(MultipartFile uploadFile, HttpServletRequest request) throws Exception{

        // 对上传的文件重命名，避免文件重名
        String oldName = uploadFile.getOriginalFilename();
        String newName = UUID.randomUUID().toString() + oldName.substring(oldName.lastIndexOf("."));

        if (newName.contains(";")){
            newName = newName.substring(0, newName.lastIndexOf(";"));
        }

        //如果名称为空，返回一个文件名为空的错误
        if (StringUtils.isEmpty(oldName)){
            throw  new CommonException(CommonErrorCode.VALIDATE_ERROR,"上传思维导图文件名称为空");
        }
        //如果文件超过最大值，返回超出可上传最大值的错误
        if (uploadFile.getSize()/(1024*1024)>UPLOAD_FILE_MAX_SIZE){
            throw  new CommonException(CommonErrorCode.VALIDATE_ERROR,"思维导图过大");
        }

        String format = sdf.format(new Date());
        File folder = new File(uploadPath + format);

        //目录不存在则创建目录
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }
        logger.info("图片地址："+newName);
        // 文件保存
        uploadFile.transferTo(new File(folder, newName));
        // 返回上传文件的访问路径
        String filePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()+ uploadPath  + format + newName;
        logger.info("上传文件的访问路径 filePath= "+filePath);
        return filePath;
    }

    /**
     * @Function: base64编码图片转 MultipartFile
     * 注意 转码时 需要把data:image/png;base64,这个前缀给去掉
     */
    public static MultipartFile converter(String source){
        String [] charArray = source.split(",");
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] bytes = new byte[0];
        bytes = decoder.decode(charArray[1]);
        for (int i=0;i<bytes.length;i++){
            if(bytes[i]<0){
                bytes[i]+=256;
            }
        }
       return Base64ToMultipartFileUtil.multipartFile(bytes,charArray[0]);
    }

}
