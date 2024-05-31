package com.xuecheng.content.api;

import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "教师管理编辑接口",tags = "教师管理编辑接口")
@RestController
public class CourseTeacherController {
    @Autowired
    CourseTeacherService courseTeacherService;

    @ApiOperation("根据课程编号查询担任该课程的教师")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> getTeacherList(@PathVariable("courseId") Long courseId){
        return courseTeacherService.selectList(courseId);
    }

    @ApiOperation("在指定课程下添加教师信息")
    @PostMapping("/courseTeacher")
    public CourseTeacher saveTeacher(@RequestBody CourseTeacher courseTeacher){
        //机构id，由于认证系统没有上线暂时硬编码
        Long companyId = 1232141425L;
        return courseTeacherService.saveTeacher(companyId,courseTeacher);
    }

    @ApiOperation("在指定课程下修改教师信息")
    @PutMapping("/courseTeacher")
    public CourseTeacher updateTeacher(@RequestBody CourseTeacher courseTeacher){
        //机构id，由于认证系统没有上线暂时硬编码
        Long companyId = 1232141425L;
        return courseTeacherService.saveTeacher(companyId,courseTeacher);
    }

    @ApiOperation("在指定课程下删除教师信息")
    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    public void deleteTeacher(@PathVariable("courseId") Long courseId,
                              @PathVariable("teacherId") Long teacherId){
        //机构id，由于认证系统没有上线暂时硬编码
        Long companyId = 1232141425L;
        courseTeacherService.deleteTeacher(companyId,courseId,teacherId);

    }


}
