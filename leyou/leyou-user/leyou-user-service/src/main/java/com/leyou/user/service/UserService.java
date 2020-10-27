package com.leyou.user.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.NumberUtils;
import com.leyou.user.pojo.User;
import com.leyou.user.repository.IUserRepository;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "user:verify:";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    /**
     * 校验数据是否可用
     * @param data
     * @param type
     * @return
     */
    public Boolean checkUser(String data, Integer type) {
        Specification<User> spec = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (type == 1) {
                    return  criteriaBuilder.equal(root.get("username"), data);
                } else if (type == 2) {
                    return  criteriaBuilder.equal(root.get("phone"), data);
                } else {
                    return null;
                }
                
        };
        return this.userRepository.count(spec) == 0;
    }

    /**
     * mandar el código de verificación al móvil de usuario
     * @param phone
     * @return
     */
    public void sendVerifyCode(String phone) {
        if (StringUtils.isBlank(phone)) {
            return;
        }
        //generar código de verificación
        String code = NumberUtils.generateCode(6);
        try {
            //mandar mensaje a rabbitMQ
            Map<String, String> msg = new HashMap<>();
            msg.put("phone", phone);
            msg.put("code", code);
            this.amqpTemplate.convertAndSend("LEYOU.SMS.EXCHANGE", "verifycode.sms", msg);
            //guardar código en redis
            this.redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            LOGGER.error("Mensaje no se ha enviado. phone: {}, code{}", phone, code);
        }
    }

    /**
     * registrar usuario nuevo
     * @param user
     * @param code
     * @return
     */
    public void register(User user, String code) {
        //1.buscar el código en redis
        String redisCode = this.redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        //2. verificar el código
        if (!StringUtils.equals(code, redisCode)) {
            throw  new LyException(ExceptionEnum.INVALID_VERIFY_CODE);
        }
        //3.生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        //4.加盐加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));
        //5.新增用户
        user.setId(null);
        user.setCreated(new Date());
        this.userRepository.save(user);
        //6. elimina el código en redis
        this.redisTemplate.delete(KEY_PREFIX + user.getPhone());
    }

    /**
     * buscar el usuario por nombre
     * @param username
     * @param password
     * @return
     */
    public User queryUser(String username, String password) {
        Specification<User> spec = new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.get("username"), username);
            }
        };
        Optional<User> optionalUser = this.userRepository.findOne(spec);
        //判断user是否为空
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            //获取盐 对用户输入的密码加盐加密
            password = CodecUtils.md5Hex(password, user.getSalt());
            //和数据库中密码比较
            if (StringUtils.equals(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }
}
