package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {
    @Autowired
    CourseTeacherMapper courseTeacherMapper;

    @Autowired
    CourseBaseMapper courseBaseMapper;

    /**
     * 查询指定课程下的所有教师
     * @param courseId
     * @return
     */
    @Override
    public List<CourseTeacher> selectList(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId,courseId);
        return courseTeacherMapper.selectList(queryWrapper);
    }

    /**
     * 为指定的课程添加/修改教师信息
     * @param teacherInfo
     * @return
     */
    @Transactional
    @Override
    public CourseTeacher saveTeacher(Long operatorCompanyId, CourseTeacher teacherInfo) {
        // 获取课程id
        Long courseId = teacherInfo.getCourseId();
        // 根据课程id获取companyId
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        Long companyId = courseBase.getCompanyId();
        System.out.println(companyId+"  "+operatorCompanyId);
        if(companyId.longValue() != operatorCompanyId.longValue()){
            XueChengPlusException.cast("只允许向机构自己的课程中添加/修改教师信息");
            return null;
        }

        Long id = teacherInfo.getId();
        if(id==null){
            // 插入
            courseTeacherMapper.insert(teacherInfo);
            return teacherInfo;
        }else{
            // 修改
            CourseTeacher courseTeacher = courseTeacherMapper.selectById(id);
            BeanUtils.copyProperties(teacherInfo,courseTeacher);
            courseTeacherMapper.updateById(courseTeacher);
            return courseTeacher;
        }
    }

    /**
     * 删除教师信息
     * @param courseId
     * @param teacherId
     */
    @Transactional
    @Override
    public void deleteTeacher(Long operatorCompanyId, Long courseId, Long teacherId) {
        // 根据课程id获取companyId
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        Long companyId = courseBase.getCompanyId();
        if(companyId.longValue() != operatorCompanyId.longValue()){
            XueChengPlusException.cast("只允许向机构自己的课程中删除教师信息");
            return;
        }

        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId,courseId)
                    .eq(CourseTeacher::getId,teacherId);
        courseTeacherMapper.delete(queryWrapper);
    }

}
