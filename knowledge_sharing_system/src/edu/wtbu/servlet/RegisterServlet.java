//注册 post方法
package edu.wtbu.servlet;

import java.io.IOException;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import edu.wtbu.pojo.User;
import edu.wtbu.service.UserService;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService = new UserService();// 业务层对象，处理注册逻辑
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        
        HashMap<String, Object> result = new HashMap<>();
        //.trim():去除首尾空格
        if (username == null || username.trim().isEmpty()) {
            result.put("flag", "fail");
            result.put("data", "用户名不能为空");
        } else if (password == null || password.length() < 6) {
            result.put("flag", "fail");
            result.put("data", "密码长度不能少于6位");
        } else if (!password.equals(confirmPassword)) {
            result.put("flag", "fail");
            result.put("data", "两次密码不一致");
        } else {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setPhone(phone);
            
            boolean success = userService.register(user);
            if (success) {
                result.put("flag", "success");
                result.put("data", "注册成功");
            } else {
                result.put("flag", "fail");
                result.put("data", "用户名、邮箱或手机号已被注册");
            }
        }
        
        response.getWriter().append(JSON.toJSONString(result));
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}