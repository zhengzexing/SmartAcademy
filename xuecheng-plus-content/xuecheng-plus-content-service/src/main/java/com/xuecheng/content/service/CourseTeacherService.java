package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

public interface CourseTeacherService {

    List<CourseTeacher> selectList(Long courseId);

    CourseTeacher saveTeacher(Long companyId , CourseTeacher courseTeacher);

    void deleteTeacher(Long companyId , Long courseId, Long teacherId);
}
