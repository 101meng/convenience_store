package com.lin101.store.interceptor;

import com.lin101.store.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 全局 JWT 安全拦截器
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 放行跨域的预检请求 (OPTIONS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 2. 从前端请求头中获取 Authorization: Bearer <token>
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 提取真正的 Token 字符串
            Integer userId = jwtUtils.getUserIdFromToken(token);

            // 3. 验证通过：将 userId 挂载到本次请求的域中，然后放行
            if (userId != null) {
                request.setAttribute("userId", userId);
                return true;
            }
        }

        // 4. 验证失败：直接将请求拦截，并打回 401 状态码
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\": 401, \"message\": \"未授权或登录已过期，请重新登录\"}");
        return false;
    }
}