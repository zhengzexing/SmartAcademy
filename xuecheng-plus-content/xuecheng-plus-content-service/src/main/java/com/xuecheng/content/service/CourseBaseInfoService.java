package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;

/**
 * @description 课程基本信息管理业务接口
 * @author Mr.M
 * @date 2022/9/6 21:42
 * @version 1.0
 **/
public interface CourseBaseInfoService  {

/*
 * @description 课程查询接口
 * @param pageParams 分页参数
 * @param queryCourseParamsDto 条件条件
 * @return com.xuecheng.base.model.PageResult<com.xuecheng.content.model.po.CourseBase>
 * @author Mr.M
 * @date 2022/9/6 21:44
 */
  PageResult<CourseBase> queryCourseBaseList(Long companyId, PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

  /**
   * @description 添加课程基本信息
   * @param companyId  教学机构id
   * @param addCourseDto  课程基本信息
   * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
   * @author Mr.M
   * @date 2022/9/7 17:51
   */
  CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

  /**
   * 根据课程id查询课程信息
   * @param courseId
   * @return
   */
  CourseBaseInfoDto getCourseBaseInfo(Long courseId);

  /**
   * @description 修改课程信息
   * @param companyId  机构id
   * @param dto  课程信息
   * @return com.xuecheng.content.model.dto.CourseBaseInfoDto
   * @author Mr.M
   * @date 2022/9/8 21:04
   */
  CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto dto);


  void deleteCourse(Long companyId, Long courseId);
}
