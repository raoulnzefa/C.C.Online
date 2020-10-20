package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.JwtUtils;
import com.leyou.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * authentication login
     * @param username
     * @param password
     * @return
     */
    public String accredit(String username, String password) {
        //1. username + password -> user
        User user = this.userClient.queryUser(username, password);
        //2. si no existe
        if (user == null) {
            return null;
        }
        //3. JwtUtils -> jwt
        try {
            UserInfo userInfo = new UserInfo();
            userInfo.setUsername(user.getUsername());
            userInfo.setId(user.getId());
            return JwtUtils.generateToken(userInfo, this.jwtProperties.getPrivateKey(), this.jwtProperties.getExpire());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
