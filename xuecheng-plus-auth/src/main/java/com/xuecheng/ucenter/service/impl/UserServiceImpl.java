package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcMenuMapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcMenu;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 统一认证入口
 */
@Slf4j
@Component
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    XcMenuMapper xcMenuMapper;

    @Autowired
    ApplicationContext applicationContext;

    //前端登录时传入的参数就是AuthParamDto的json串
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //将传入的json串转成AuthParamDto对象
        AuthParamsDto authParamsDto = null;
        try {
            authParamsDto = JSON.parseObject(s,AuthParamsDto.class);
        }catch (Exception e){
            throw new RuntimeException("请求认证参数不符合要求");
        }

        //取出认证类型
        String authType = authParamsDto.getAuthType();

        //根据认证的类型取出相应认证类型的authService
        String beanName = authType + "_authservice";
        AuthService authService = applicationContext.getBean(beanName,AuthService.class);

        //开始认证
        XcUserExt xcUserExt = authService.execute(authParamsDto);

        //认证通过后将数据库返回的用户信息封装为UserDetails
        return getUserPrincipal(xcUserExt);
    }

    /**
     * @description 查询用户信息
     * @param user  用户id，主键
     * @return com.xuecheng.ucenter.model.po.XcUser 用户信息
     * @author Mr.M
     * @date 2022/9/29 12:19
     */
    public UserDetails getUserPrincipal(XcUserExt user){
        //用户权限,如果不加报Cannot pass a null GrantedAuthority collection
        String[] authorities = {"p1"};

        //根据用户id查询用户的权限
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(user.getId());

        if(xcMenus.size()>0){
            List<String> permissions = new ArrayList<>();
            xcMenus.forEach(m->{
                //拿出用户拥有的权限标识符
                permissions.add(m.getCode());
            });
            //将permissions转成数组
            authorities = permissions.toArray(new String[0]);
        }

        String password = user.getPassword();
        //为了安全在令牌中不放密码
        user.setPassword(null);
        //将user对象转json
        String userString = JSON.toJSONString(user);
        //创建UserDetails对象
        UserDetails userDetails = User.withUsername(userString)
                .password(password)
                .authorities(authorities)
                .build();
        return userDetails;
    }

}
