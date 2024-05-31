package com.xuecheng.learning.service.impl;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import com.xuecheng.learning.feignclient.MediaServiceClient;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.service.LearningService;
import com.xuecheng.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LearningServiceImpl implements LearningService {
    @Autowired
    MediaServiceClient mediaServiceClient;

    @Autowired
    MyCourseTablesService myCourseTablesService;

    @Autowired
    ContentServiceClient contentServiceClient;


    @Override
    public RestResponse<String> getVideo(String userId,Long courseId,Long teachplanId, String mediaId) {
        //查询课程信息是否发布
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        if(coursepublish==null){
            XueChengPlusException.cast("课程信息不存在");
        }

        //TODO：试学视频

        //校验学习资格
        //判断课程是否收费，如果免费，不管是否登录都可以直接学习
        String charge = coursepublish.getCharge();
        if(charge.equals("201000")){//免费可以正常学习
            return mediaServiceClient.getPlayUrlByMediaId(mediaId);
        }

        //课程不免费，如果登录
        if(StringUtils.isNotEmpty(userId)){

            //判断是否选课，根据选课情况判断学习资格
            XcCourseTablesDto xcCourseTablesDto = myCourseTablesService.getLearningStatus(userId, courseId);
            //学习资格状态 [{"code":"702001","desc":"正常学习"},{"code":"702002","desc":"没有选课或选课后没有支付"},{"code":"702003","desc":"已过期需要申请续期或重新支付"}]
            String learnStatus = xcCourseTablesDto.getLearnStatus();
            if(learnStatus.equals("702003")){
                return RestResponse.validfail("您的选课已过期需要申请续期或重新支付");
            }else if(learnStatus.equals("702002")) {
                return RestResponse.validfail("无法学习，没有选课或者选课后没有支付");
            }else{
                //有资格学习
                return mediaServiceClient.getPlayUrlByMediaId(mediaId);
            }
        }

        //课程不免费，且未登录
        return RestResponse.validfail("付费课程，请登录校验资格后继续学习");
    }

}
