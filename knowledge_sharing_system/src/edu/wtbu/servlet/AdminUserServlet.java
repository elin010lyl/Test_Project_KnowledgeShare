//管理员用户管理（列表/修改/删除） GET/POST
package edu.wtbu.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import edu.wtbu.pojo.User;
import edu.wtbu.service.UserService;

@WebServlet("/admin/user")
public class AdminUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService = new UserService();
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        Integer role = (Integer) request.getSession().getAttribute("role");
        HashMap<String, Object> result = new HashMap<>();
        
        if (role == null || role != 1) {
            result.put("flag", "fail");
            result.put("data", "无权限");
        } else {
            List<User> userList = userService.findAllUsers();
            result.put("flag", "success");
            result.put("data", userList);
        }
        
        response.getWriter().append(JSON.toJSONString(result));
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        Integer role = (Integer) request.getSession().getAttribute("role");
        HashMap<String, Object> result = new HashMap<>();
        
        if (role == null || role != 1) {
            result.put("flag", "fail");
            result.put("data", "无权限");
            response.getWriter().append(JSON.toJSONString(result));
            return;
        }
        
        String action = request.getParameter("action");
        Long currentUserId = (Long) request.getSession().getAttribute("userId");
        
        // 修改用户信息
        if ("update".equals(action)) {
            String userIdStr = request.getParameter("id");
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            
            if (userIdStr == null) {
                result.put("flag", "fail");
                result.put("data", "用户ID不能为空");
            } else {
                User user = new User();
                user.setId(Long.parseLong(userIdStr));
                user.setUsername(username);
                user.setEmail(email);
                user.setPhone(phone);
                
                boolean success = userService.updateUser(user);
                if (success) {
                    result.put("flag", "success");
                    result.put("data", "修改成功");
                } else {
                    result.put("flag", "fail");
                    result.put("data", "修改失败");
                }
            }
        }
        // 删除用户
        else if ("delete".equals(action)) {
            String userIdStr = request.getParameter("id");
            
            if (userIdStr == null) {
                result.put("flag", "fail");
                result.put("data", "用户ID不能为空");
            } else {
                Long deleteUserId = Long.parseLong(userIdStr);//返回基础类型 long才能进行处理
                if (deleteUserId.equals(currentUserId)) {
                    result.put("flag", "fail");
                    result.put("data", "不能删除自己");
                } else {
                    boolean success = userService.deleteUser(deleteUserId);
                    if (success) {
                        result.put("flag", "success");
                        result.put("data", "删除成功");
                    } else {
                        result.put("flag", "fail");
                        result.put("data", "删除失败");
                    }
                }
            }
        }
        else {
            result.put("flag", "fail");
            result.put("data", "未知操作");
        }
        
        response.getWriter().append(JSON.toJSONString(result));
    }
}