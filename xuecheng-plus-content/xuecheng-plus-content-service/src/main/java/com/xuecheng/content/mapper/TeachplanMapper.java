package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {

    /**
     * @description 查询某课程的课程计划，组成树型结构
     * @param courseId
     * @return com.xuecheng.content.model.dto.TeachplanDto
     * @author Mr.M
     * @date 2022/9/9 11:10
     */
    public List<TeachplanDto> selectTreeNodes(long courseId);

    /**
     * 查询课程树状列表中，最大的排序字段
     * @param courseId
     * @param parentId
     * @return
     */
    public Integer selectMaxOrder(@Param("courseId") long courseId,@Param("parentId") long parentId);

    /**
     * 查询课程树状列表中，最小的排序字段
     * @param courseId
     * @param parentId
     * @return
     */
    public Integer selectMinOrder(@Param("courseId") long courseId,@Param("parentId") long parentId);


    /**
     * 下移课程的过程中，查询比当前课程排序字段更大的所有实体
     * @param id
     * @return
     */
    public List<Teachplan> selectBigOrder(@Param("id") long id);

    /**
     * 上移课程的过程中，查询比当前课程排序字段更小的所有实体
     * @param id
     * @return
     */
    public List<Teachplan> selectSmallOrder(@Param("id") long id);

}
