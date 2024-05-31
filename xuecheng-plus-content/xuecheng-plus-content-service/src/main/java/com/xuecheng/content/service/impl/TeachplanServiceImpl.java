package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @description 课程计划service接口实现类
 * @author Mr.M
 * @date 2022/9/9 11:14
 * @version 1.0
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Transactional
    @Override
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {

        //课程计划id
        Long id = teachplanDto.getId();
        //修改课程计划
        if(id!=null){
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDto,teachplan);
            teachplanMapper.updateById(teachplan);
        }else{
            //取出同父同级别的课程计划数量
            int count = getTeachplanCount(teachplanDto.getCourseId(), teachplanDto.getParentid());
            Teachplan teachplanNew = new Teachplan();
            //设置排序号
            teachplanNew.setOrderby(count+1);
            BeanUtils.copyProperties(teachplanDto,teachplanNew);

            teachplanMapper.insert(teachplanNew);

        }

    }

    /**
     * @description 获取最新的排序号
     * @param courseId  课程id
     * @param parentId  父课程计划id
     * @return int 最新排序号
     * @author Mr.M
     * @date 2022/9/9 13:43
     */
    private int getTeachplanCount(long courseId,long parentId){
        Integer count = teachplanMapper.selectMaxOrder(courseId,parentId);
        if (count == null){
            count = 1;
        }
        System.out.println(count);
        return count;
    }

    /**
     * 删除课程计划
     * @param id
     */
    @Transactional
    @Override
    public void deleteTeachplan(long id) {
        //TODO:根据要删除的章节，要判断它的底下是否还有孩子结点，没有的话才可以删除
        if(existChilds(id)){
            XueChengPlusException.cast("课程计划信息还有子级信息，无法操作");
        }else{
            //TODO:可以删除该章节，需要将TeachplanMedia表中相关的数据也删除
            teachplanMapper.deleteById(id);

            LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeachplanMedia::getTeachplanId,id);
            teachplanMediaMapper.delete(queryWrapper);
        }
    }

    /**
     * 判断树状结构编号id下是否还存在子节点
     * @param id
     * @return
     */
    private boolean existChilds(long id){
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getParentid,id);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        if(count == 0){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 对树状结构的课程进行移动操作
     * @param moveType
     * @param id
     */
    @Transactional
    @Override
    public void moveTeachplan(String moveType, long id) {
        // 根据teachplan的id将实体查询出来
        Teachplan OriginalTeachplan = teachplanMapper.selectById(id);
        // 原始的顺序orderBy
        Integer OriginalOrder = OriginalTeachplan.getOrderby();
        // 最大的顺序orderBy
        Integer MaxOrder = teachplanMapper.selectMaxOrder(OriginalTeachplan.getCourseId(),OriginalTeachplan.getParentid());
        // 最小的顺序orderBy
        Integer MinOrder = teachplanMapper.selectMinOrder(OriginalTeachplan.getCourseId(),OriginalTeachplan.getParentid());

        if("movedown".equals(moveType)){
            //进行下移操作
            //如果处于最后一个不能再下移
            if(OriginalOrder == MaxOrder){
                XueChengPlusException.cast("已经处于最后一个，不能下移");
            }
            // 查询orderBy比自己大的实体列表
            List<Teachplan> bigOrderList = teachplanMapper.selectBigOrder(id);
            // 取出第一个
            Teachplan BiggerTeachplan = bigOrderList.get(0);
            // 将原始实体的orderBy与较大orderBy实体进行交换
            OriginalTeachplan.setOrderby(BiggerTeachplan.getOrderby());
            BiggerTeachplan.setOrderby(OriginalOrder);
            // TODO:开始更新数据库
            teachplanMapper.updateById(OriginalTeachplan);
            teachplanMapper.updateById(BiggerTeachplan);

        }else if("moveup".equals(moveType)){
            //进行上移操作
            //如果处于第一个不能再上移
            if(OriginalOrder == MinOrder){
                XueChengPlusException.cast("已经处于第一个，不能上移");
            }
            // 查询orderBy比自己小的实体列表
            List<Teachplan> smallOrderList = teachplanMapper.selectSmallOrder(id);
            // 取出最后一个
            Teachplan SmallerTeachplan = smallOrderList.get(smallOrderList.size()-1);
            // 将原始实体的orderBy与较小orderBy实体进行交换
            OriginalTeachplan.setOrderby(SmallerTeachplan.getOrderby());
            SmallerTeachplan.setOrderby(OriginalOrder);
            // TODO:开始更新数据库
            teachplanMapper.updateById(OriginalTeachplan);
            teachplanMapper.updateById(SmallerTeachplan);
        }
    }

    @Transactional
    @Override
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        //教学计划id
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan==null){
            XueChengPlusException.cast("教学计划不存在");
        }
        Integer grade = teachplan.getGrade();
        if(grade!=2){
            XueChengPlusException.cast("只允许第二级教学计划绑定媒资文件");
        }
        //课程id
        Long courseId = teachplan.getCourseId();

        //先删除原来该教学计划绑定的媒资
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId,teachplanId));

        //再添加教学计划与媒资的绑定关系
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setCourseId(courseId);
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);
        return teachplanMedia;
    }

    @Transactional
    @Override
    public void unbindingMedia(Long teachPlanId, String mediaId) {
        Teachplan teachplan = teachplanMapper.selectById(teachPlanId);
        if(teachplan==null){
            XueChengPlusException.cast("教学计划不存在");
        }
        Integer grade = teachplan.getGrade();
        if(grade!=2){
            XueChengPlusException.cast("只允许第二级教学计划解绑媒资文件");
        }
        //删除原来该教学计划绑定的媒资
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>()
                .eq(TeachplanMedia::getTeachplanId,teachPlanId)
                .eq(TeachplanMedia::getMediaId,mediaId));
    }
}

