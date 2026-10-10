//知识列表/发布/删除/审核  GET/POST
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

import edu.wtbu.pojo.Knowledge;
import edu.wtbu.service.FavoriteService;
import edu.wtbu.service.KnowledgeService;

@WebServlet("/knowledge")
public class KnowledgeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private KnowledgeService knowledgeService = new KnowledgeService();
    private FavoriteService favoriteService = new FavoriteService();
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        String action = request.getParameter("action");
        HashMap<String, Object> result = new HashMap<>();
        
        // 获取知识列表
        if ("list".equals(action)) {
            List<Knowledge> list = knowledgeService.getApprovedKnowledge();
            result.put("flag", "success");
            result.put("data", list);
        }
        // 获取我的收藏
        else if ("myFavorites".equals(action)) {
            Long userId = (Long) request.getSession().getAttribute("userId");
            if (userId == null) {
                result.put("flag", "fail");
                result.put("data", "请先登录");
            } else {
                List<Knowledge> favorites = favoriteService.getUserFavorites(userId);
                result.put("flag", "success");
                result.put("data", favorites);
            }
        }
        // 管理员获取待审核列表
        else if ("pending".equals(action)) {
            Integer role = (Integer) request.getSession().getAttribute("role");
            if (role == null || role != 1) {
                result.put("flag", "fail");
                result.put("data", "无权限");
            } else {
                List<Knowledge> pendingList = knowledgeService.getPendingKnowledge();
                result.put("flag", "success");
                result.put("data", pendingList);
            }
        }
        // 获取我发布的知识
        else if ("myKnowledge".equals(action)) {
            Long userId = (Long) request.getSession().getAttribute("userId");
            if (userId == null) {
                result.put("flag", "fail");
                result.put("data", "请先登录");
            } else {
                List<Knowledge> myKnowledge = knowledgeService.getMyKnowledge(userId);
                result.put("flag", "success");
                result.put("data", myKnowledge);
            }
        }
        // 获取单个知识详情
        else if ("detail".equals(action)) {
            String idStr = request.getParameter("id");
            if (idStr == null) {
                result.put("flag", "fail");
                result.put("data", "知识ID不能为空");
            } else {
                Long id = Long.parseLong(idStr);
                Knowledge knowledge = knowledgeService.getKnowledgeDetail(id);
                if (knowledge != null) {
                    result.put("flag", "success");
                    result.put("data", knowledge);
                } else {
                    result.put("flag", "fail");
                    result.put("data", "知识不存在");
                }
            }
        }
        else {
            result.put("flag", "fail");
            result.put("data", "未知操作");
        }
        
        response.getWriter().append(JSON.toJSONString(result));
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        String action = request.getParameter("action");
        Long userId = (Long) request.getSession().getAttribute("userId");
        HashMap<String, Object> result = new HashMap<>();
        
        // 发布知识
        if ("publish".equals(action)) {
            if (userId == null) {
                result.put("flag", "fail");
                result.put("data", "请先登录");
            } else {
                String title = request.getParameter("title");
                String content = request.getParameter("content");
                String category = request.getParameter("category");
                
                if (title == null || title.trim().isEmpty()) {
                    result.put("flag", "fail");
                    result.put("data", "标题不能为空");
                } else if (content == null || content.trim().isEmpty()) {
                    result.put("flag", "fail");
                    result.put("data", "内容不能为空");
                } else {
                    Knowledge knowledge = new Knowledge(title, content, category, userId);
                    boolean success = knowledgeService.publish(knowledge);
                    if (success) {
                        result.put("flag", "success");
                        result.put("data", "发布成功，等待审核");
                    } else {
                        result.put("flag", "fail");
                        result.put("data", "发布失败");
                    }
                }
            }
        }
        // 收藏/取消收藏
        else if ("favorite".equals(action)) {
            if (userId == null) {
                result.put("flag", "fail");
                result.put("data", "请先登录");
            } else {
                String favAction = request.getParameter("favAction");
                String knowledgeIdStr = request.getParameter("knowledgeId");
                
                if (knowledgeIdStr == null) {
                    result.put("flag", "fail");
                    result.put("data", "知识ID不能为空");
                } else {
                    Long knowledgeId = Long.parseLong(knowledgeIdStr);
                    if ("add".equals(favAction)) {
                        boolean success = favoriteService.addFavorite(userId, knowledgeId);
                        result.put("flag", success ? "success" : "fail");
                        result.put("data", success ? "收藏成功" : "已经收藏过了");
                    } else if ("remove".equals(favAction)) {
                        favoriteService.removeFavorite(userId, knowledgeId);
                        result.put("flag", "success");
                        result.put("data", "取消收藏成功");
                    } else {
                        result.put("flag", "fail");
                        result.put("data", "操作类型错误");
                    }
                }
            }
        }
        // 删除自己发布的知识
        else if ("delete".equals(action)) {
            if (userId == null) {
                result.put("flag", "fail");
                result.put("data", "请先登录");
            } else {
                String knowledgeIdStr = request.getParameter("id");
                if (knowledgeIdStr == null) {
                    result.put("flag", "fail");
                    result.put("data", "知识ID不能为空");
                } else {
                    Long knowledgeId = Long.parseLong(knowledgeIdStr);
                    boolean success = knowledgeService.deleteKnowledge(knowledgeId, userId);
                    if (success) {
                        result.put("flag", "success");
                        result.put("data", "删除成功");
                    } else {
                        result.put("flag", "fail");
                        result.put("data", "只能删除自己发布的知识");
                    }
                }
            }
        }
        
        // 管理员删除知识
        else if ("adminDelete".equals(action)) {
            Integer role = (Integer) request.getSession().getAttribute("role");
            if (role == null || role != 1) {
                result.put("flag", "fail");
                result.put("data", "无权限");
            } else {
                String knowledgeIdStr = request.getParameter("id");
                if (knowledgeIdStr == null) {
                    result.put("flag", "fail");
                    result.put("data", "知识ID不能为空");
                } else {
                    Long knowledgeId = Long.parseLong(knowledgeIdStr);
                    boolean success = knowledgeService.adminDeleteKnowledge(knowledgeId);
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
        
        // 管理员审核
        else if ("audit".equals(action)) {
            Integer role = (Integer) request.getSession().getAttribute("role");
            if (role == null || role != 1) {
                result.put("flag", "fail");
                result.put("data", "无权限");
            } else {
                String knowledgeIdStr = request.getParameter("id");
                String auditAction = request.getParameter("auditAction");
                
                if (knowledgeIdStr == null) {
                    result.put("flag", "fail");
                    result.put("data", "知识ID不能为空");
                } else {
                    Long knowledgeId = Long.parseLong(knowledgeIdStr);
                    int status = "approve".equals(auditAction) ? 1 : 2;
                    boolean success = knowledgeService.auditKnowledge(knowledgeId, status);
                    if (success) {
                        result.put("flag", "success");
                        result.put("data", status == 1 ? "审核通过" : "审核拒绝");
                    } else {
                        result.put("flag", "fail");
                        result.put("data", "审核失败");
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