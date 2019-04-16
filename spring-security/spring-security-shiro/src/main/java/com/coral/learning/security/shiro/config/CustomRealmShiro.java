package com.coral.learning.security.shiro.config;


import com.coral.learning.security.shiro.dao.UserDao;
import com.coral.learning.security.shiro.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Sha1Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * AuthorizingRealm
 */
@Slf4j
public class CustomRealmShiro extends AuthorizingRealm {

    @Autowired
    private UserDao userDao;

    /**
     * 获取身份验证信息
     * Shiro中，最终是通过 Realm 来获取应用程序中的用户、角色及权限信息的。
     *
     * @param authenticationToken 用户身份信息 token
     * @return 返回封装了用户信息的 AuthenticationInfo 实例
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        User user = userDao.findByUserName(token.getUsername());
        String hash = new Sha1Hash(token.getCredentials(), token.getUsername()).toHex();
        token.setPassword(hash.toCharArray());
        return new SimpleAuthenticationInfo(user, user.getPassword(), getName());
    }

    /**
     * 获取授权信息
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        String role = "ADMIN";

        info.addRole(role);
        List<String> permission = new ArrayList<>();
        permission.add(role);
        info.addStringPermissions(permission);

        return info;
    }
}