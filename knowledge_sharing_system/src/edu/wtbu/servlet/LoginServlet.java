//登录  post方法
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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserService userService = new UserService();
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        HashMap<String, Object> result = new HashMap<>();
        
//       trim()=》 用户只输入一堆空格/前后多打了空格的情况，单isempty无法查到
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            result.put("flag", "fail");
            result.put("data", "用户名/邮箱/手机号和密码不能为空");
        } else {
            User user = userService.login(username, password);
            if (user != null) {
            	//登陆成功
//            	Session 提供=》setAttribute(String key, Object value)
                request.getSession().setAttribute("userId", user.getId());
                request.getSession().setAttribute("username", user.getUsername());
                request.getSession().setAttribute("role", user.getRole());
                
                result.put("flag", "success");
                HashMap<String, Object> data = new HashMap<>();
                data.put("id", user.getId());
                data.put("username", user.getUsername());
                data.put("role", user.getRole());
                result.put("data", data);
            } else {
                result.put("flag", "fail");
                result.put("data", "用户名或密码错误");
            }
        }
        
        response.getWriter().append(JSON.toJSONString(result));
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}