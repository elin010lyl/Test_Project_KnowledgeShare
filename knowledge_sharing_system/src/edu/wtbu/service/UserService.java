package edu.wtbu.service;

import java.util.List;

import edu.wtbu.dao.FavoriteDao;
import edu.wtbu.dao.KnowledgeDao;
import edu.wtbu.dao.UserDao;
import edu.wtbu.pojo.Knowledge;
import edu.wtbu.pojo.User;

/**
 * 用户业务逻辑类
 * 作用：处理业务逻辑，调用DAO层
 */
public class UserService {
    
    private UserDao userDao = new UserDao();
    private KnowledgeDao knowledgeDao = new KnowledgeDao();
    private FavoriteDao favoriteDao = new FavoriteDao();
    
    /**
     * 用户登录
     */
    public User login(String username, String password) {
        return userDao.login(username, password);
    }
    
    /**
     * 用户注册
     */
    public boolean register(User user) {
        // 检查用户名是否已存在
        if (userDao.isUsernameExist(user.getUsername())) {
            return false;
        }
        // 检查邮箱是否已存在
        if (user.getEmail() != null && !user.getEmail().isEmpty() && userDao.isEmailExist(user.getEmail())) {
            return false;
        }
        // 检查手机号是否已存在
        if (user.getPhone() != null && !user.getPhone().isEmpty() && userDao.isPhoneExist(user.getPhone())) {
            return false;
        }
        return userDao.register(user);
    }
    
    /**
     * 根据ID查询用户
     */
    public User findById(Long id) {
        return userDao.findById(id);
    }
    
    /**
     * 查询所有用户
     */
    public java.util.List<User> findAllUsers() {
        return userDao.findAll();
    }
    
    /**
     * 更新用户信息
     */
    public boolean updateUser(User user) {
        return userDao.updateUser(user);
    }
    
    /**
     * 删除用户
     */
    public boolean deleteUser(Long userId) {
    	
        // 1. 删除该用户的所有收藏记录（直接删除 favorite）
        favoriteDao.removeAllByUser(userId);
        
        // 2. 删除该用户发布的所有知识
        List<Knowledge> userKnowledge = knowledgeDao.findByUserId(userId);
        /*
         * userKnowledge 是查出来的 “该用户发布的所有文章列表”，
        for 循环就是挨个遍历这些文章，一次拿一篇=》k
        */
        for (Knowledge k : userKnowledge) {
        	//传入当前这文章的ID
            knowledgeDao.deleteKnowledge(k.getId());  // 这里会触发级联删除收藏
        }
        
        // 3. 最后删除用户本人
        return userDao.deleteUser(userId);
    }
    
    // 修改个人信息
    public boolean updateProfile(Long userId, String email, String phone) {
        User user = userDao.findById(userId);
        if (user == null) return false;
        user.setEmail(email);
        user.setPhone(phone);
        return userDao.updateUser(user);
    }
    
    // 修改密码
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userDao.findById(userId);
        if (user == null || !user.getPassword().equals(oldPassword)) {
            return false;
        }
        return userDao.updatePassword(userId, newPassword);
    }
    
}
