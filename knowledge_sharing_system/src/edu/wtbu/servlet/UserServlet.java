//UserServlet 个人信息修改/密码修改 POST
package edu.wtbu.servlet;

import java.io.IOException;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import edu.wtbu.service.UserService;

@WebServlet("/user")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService = new UserService();
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        Long userId = (Long) request.getSession().getAttribute("userId");
        HashMap<String, Object> result = new HashMap<>();
        
        if (userId == null) {
            result.put("flag", "fail");
            result.put("data", "请先登录");
            response.getWriter().append(JSON.toJSONString(result));
            return;
        }
        
        String action = request.getParameter("action");
        
        // 修改个人信息（邮箱、手机号）
        if ("updateInfo".equals(action)) {
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            
            boolean success = userService.updateProfile(userId, email, phone);
            if (success) {
                result.put("flag", "success");
                result.put("data", "修改成功");
            } else {
                result.put("flag", "fail");
                result.put("data", "修改失败");
            }
        }
        // 修改密码
        else if ("updatePassword".equals(action)) {
            String oldPassword = request.getParameter("oldPassword");
            String newPassword = request.getParameter("newPassword");
            
            if (newPassword == null || newPassword.length() < 6) {
                result.put("flag", "fail");
                result.put("data", "新密码长度不能少于6位");
            } else {
                boolean success = userService.updatePassword(userId, oldPassword, newPassword);
                if (success) {
                    result.put("flag", "success");
                    result.put("data", "修改成功");
                } else {
                    result.put("flag", "fail");
                    result.put("data", "原密码错误");
                }
            }
        }
        else {
            result.put("flag", "fail");
            result.put("data", "未知操作");
        }
        
        response.getWriter().append(JSON.toJSONString(result));
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}